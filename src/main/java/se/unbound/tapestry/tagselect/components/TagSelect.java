package se.unbound.tapestry.tagselect.components;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.ComponentEventCallback;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.ContentType;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.Renderable;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.base.AbstractField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.MarkupWriterFactory;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ResponseRenderer;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.util.TextStreamResponse;

/**
 * Select component similar to the version select in Jira.
 */
@Import(library = { "${tapestry.scriptaculous}/controls.js", "TagSelect.js" }, stylesheet = "TagSelect.css")
@Events(EventConstants.PROVIDE_COMPLETIONS)
public class TagSelect extends AbstractField {
    static final String EVENT_NAME = "autocomplete";
    private static final String PARAM_NAME = "u:input";

    @Inject
    private ComponentResources resources;

    @Inject
    private JavaScriptSupport javaScriptSupport;

    @Inject
    private Request request;

    @Inject
    private TypeCoercer coercer;

    @Inject
    private MarkupWriterFactory factory;

    @Inject
    private ResponseRenderer responseRenderer;

    @Parameter(required = true)
    private List<Object> value;

    /**
     * Allows a specific implementation of {@link ValueEncoder} to be supplied. This is used to create client-side
     * string values for the different options.
     */
    @Parameter
    private ValueEncoder<Object> encoder;

    @Persist
    private AtomicReference<SelectModel> model;

    /**
     * Called by Tapestry before rendering is started.
     */
    @SetupRender
    public void setupRender() {
        this.model = new AtomicReference<SelectModel>();
    }

    public Renderable getRenderer() {
        return new TagSelectRenderer(this.getClientId(), this.getControlName());
    }

    Object onAutocomplete() {
        final String input = this.request.getParameter(TagSelect.PARAM_NAME);

        final ComponentEventCallback<SelectModel> callback = new AutoCompleteCallback(this.model,
                this.coercer);

        this.resources.triggerEvent(EventConstants.PROVIDE_COMPLETIONS, new Object[] { input }, callback);

        final ContentType contentType = this.responseRenderer.findContentType(this);

        final MarkupWriter writer = this.factory.newPartialMarkupWriter(contentType);

        this.generateResponseMarkup(writer, this.model.get());

        return new TextStreamResponse(contentType.toString(), writer.toString());
    }

    @Override
    protected void processSubmission(final String elementName) {
        final String parameterValue = this.request.getParameter(elementName + "-values");
        final String[] items = parameterValue.split(";");
        this.value.clear();
        for (final String string : items) {
            if (StringUtils.isNotBlank(string)) {
                this.value.add(this.toValue(string));
            }
        }
    }

    /**
     * Generates the markup response that will be returned to the client; this should be an &lt;ul&gt; element with
     * nested &lt;li&gt; elements. Subclasses may override this to produce more involved markup (including images and
     * CSS class attributes).
     * 
     * @param writer to write the list to
     * @param selectModel to write each option
     */
    protected void generateResponseMarkup(final MarkupWriter writer, final SelectModel selectModel) {
        writer.element("ul");

        if (selectModel != null) {
            for (final OptionModel o : selectModel.getOptions()) {
                writer.element("li",
                        "id", this.toClient(o.getValue()));
                writer.write(o.getLabel());
                writer.end();
            }
        }

        // ul
        writer.end();
    }

    /**
     * Get the client value.
     * 
     * @param object The object to get the client value for.
     * @return The client value of the object.
     */
    public String toClient(final Object object) {
        if (this.encoder != null) {
            return this.encoder.toClient(object);
        }
        return String.valueOf(object);
    }

    private Object toValue(final String clientValue) {
        if (this.encoder != null) {
            return this.encoder.toValue(clientValue);
        }
        return clientValue;
    }

    /**
     * Renderable for creating the markup necessary for the select component.
     */
    public class TagSelectRenderer implements Renderable {
        private final String clientId;
        private final String controlName;

        /**
         * Constructs a new {@link TagSelectRenderer}.
         * 
         * @param clientId The clientId of the component.
         * @param controlName The controlName of the component.
         */
        public TagSelectRenderer(final String clientId, final String controlName) {
            this.clientId = clientId;
            this.controlName = controlName;
        }

        @Override
        public void render(final MarkupWriter writer) {
            writer.element("input", "type", "hidden", "id", this.clientId + "-values", "name",
                    this.controlName + "-values", "value", this.joinValue(TagSelect.this.value));
            writer.end();
            writer.element("textarea", "autocomplete", "off", "id", this.clientId, "class", "u-textarea");
            writer.end();

            final String menuId = this.clientId + ":menu";

            writer.element("div",
                    "id", menuId,
                    "class", "u-autocomplete-menu");
            writer.end();

            writer.element("div", "id", this.clientId + "-tag-container", "class", "u-tag-container");
            writer.element("ul", "id", this.clientId + "-tags", "class", "u-tags");
            if (TagSelect.this.value != null) {
                for (final Object each : TagSelect.this.value) {
                    this.writeSelectedItem(writer, each);
                }
            }
            writer.end();
            writer.end();
            TagSelect.this.javaScriptSupport.addScript(
                    "TagSelect.updatePadding('%s');", this.clientId);

            writer.element("em", "id", this.clientId + "trigger", "class", "u-trigger", "onclick",
                    "TagSelect.triggerCompletion(" + this.clientId + ")");
            writer.write("▽");
            writer.end();

            TagSelect.this.resources.renderInformalParameters(writer);

            final Link link = TagSelect.this.resources.createEventLink(TagSelect.EVENT_NAME);

            final JSONObject config = new JSONObject();
            config.put("paramName", TagSelect.PARAM_NAME);
            // config.put("indicator", loaderId);
            config.put("minChars", "0");

            final String methodAfterUpdate = "function (li) { TagSelect.addSelection(" + this.clientId
                    + ", li); }";
            config.put("updateElement", methodAfterUpdate);

            String configString = config.toString();
            configString = configString.replace("\"" + methodAfterUpdate + "\"", methodAfterUpdate);

            TagSelect.this.javaScriptSupport.addScript(
                    "new Ajax.Autocompleter('%s', '%s', '%s', %s);", this.clientId, menuId,
                    link.toAbsoluteURI(), configString);
        }

        private String joinValue(final List<Object> values) {
            final StringBuilder builder = new StringBuilder();

            for (final Object each : values) {
                if (builder.length() > 0) {
                    builder.append(";");
                }
                builder.append(TagSelect.this.toClient(each));
            }

            return builder.toString();
        }

        private void writeSelectedItem(final MarkupWriter writer, final Object item) {
            final String clientValue = TagSelect.this.toClient(item);
            final Long itemId = System.nanoTime();
            writer.element("li", "class", "u-tag", "id", "u-tag-" + itemId);
            writer.element("button", "type", "button", "class", "u-tag-button");
            writer.element("span");
            writer.element("span", "class", "u-tag-value");
            writer.write(clientValue);
            writer.end();
            writer.end();
            writer.end();
            writer.element("em", "class", "u-tag-remove", "onclick", "TagSelect.removeSelection("
                    + this.clientId + ", 'u-tag-" + itemId + "', '" + clientValue + "')");
            writer.end();
            writer.end();
        }
    }

    /**
     * ComponentEventCallback for the autocomplete.
     */
    static final class AutoCompleteCallback implements ComponentEventCallback<SelectModel> {
        private final AtomicReference<SelectModel> model;
        private final TypeCoercer coercer;

        public AutoCompleteCallback(final AtomicReference<SelectModel> model, final TypeCoercer coercer) {
            this.model = model;
            this.coercer = coercer;
        }

        @Override
        public boolean handleResult(final SelectModel result) {
            final SelectModel matches = this.coercer.coerce(result, SelectModel.class);

            this.model.set(matches);

            return true;
        }
    }
}
