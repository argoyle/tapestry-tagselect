package se.unbound.tapestry.tagselect.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentEventCallback;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.ContentType;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.Renderable;
import org.apache.tapestry5.SelectModel;
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

import se.unbound.tapestry.tagselect.AutoCompleteCallback;
import se.unbound.tapestry.tagselect.LabelAwareValueEncoder;

/**
 * Select component similar to the version select in Jira or the language select at Facebook. It's also possible to use
 * in a vertical mode where each tag is a separate input field.
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
    private Object value;

    @Parameter(required = false)
    private boolean dropdown;

    @Parameter(required = false)
    private boolean vertical;

    @Parameter(required = false, defaultPrefix = BindingConstants.LITERAL)
    private String blankLabel;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private double frequency;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private int minChars;

    /**
     * Allows a specific implementation of {@link LabelAwareValueEncoder} to be supplied. This is used to create
     * client-side string values and labels for the different options.
     */
    @Parameter
    private LabelAwareValueEncoder<Object> encoder;

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
        final String currentValues = this.request.getParameter("values");

        final ComponentEventCallback<SelectModel> callback = new AutoCompleteCallback(this.model,
                this.coercer);

        this.resources.triggerEvent(EventConstants.PROVIDE_COMPLETIONS, new Object[] { input }, callback);

        final ContentType contentType = this.responseRenderer.findContentType(this);

        final MarkupWriter writer = this.factory.newPartialMarkupWriter(contentType);

        this.generateResponseMarkup(writer, this.model.get(), currentValues);

        return new TextStreamResponse(contentType.toString(), writer.toString());
    }

    @Override
    protected void processSubmission(final String elementName) {
        final String parameterValue = this.request.getParameter(elementName + "-values");
        final String[] items = parameterValue.split(";");
        this.updateValue(items);
    }

    private void updateValue(final String[] items) {
        if (this.value instanceof Collection<?>) {
            @SuppressWarnings("unchecked")
            final Collection<Object> collection = (Collection<Object>) this.value;
            collection.clear();
            for (final String string : items) {
                if (StringUtils.isNotBlank(string)) {
                    collection.add(this.toValue(string));
                }
            }
        } else {
            this.value = this.toValue(items[0]);
        }
    }

    /**
     * Generates the markup response that will be returned to the client; this should be an &lt;ul&gt; element with
     * nested &lt;li&gt; elements. Subclasses may override this to produce more involved markup (including images and
     * CSS class attributes).
     * 
     * @param writer to write the list to.
     * @param selectModel to write each option.
     * @param currentValues The currently selected values in the tagselect.
     */
    protected void generateResponseMarkup(final MarkupWriter writer, final SelectModel selectModel,
            final String currentValues) {
        final List<String> values = Arrays.asList(currentValues.split(";"));
        writer.element("ul");
        if (selectModel != null) {
            for (final OptionModel o : selectModel.getOptions()) {
                final String clientValue = this.toClient(o.getValue());
                if (!values.contains(clientValue)) {
                    writer.element("li", "id", clientValue);

                    writer.element("span", "id", clientValue + "-label");
                    writer.write(o.getLabel());
                    writer.end();
                    writer.end();
                }
            }
        }
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
            final String menuId = this.clientId + ":menu";
            final String additionalClasses = this.getAdditionalClasses();

            // Tokens
            if (this.isSingleSelect(TagSelect.this.value)) {
                this.renderSingleSelect(writer, menuId);
            } else {
                this.renderMultiSelect(writer, menuId, additionalClasses);
            }

            // Initializes scripts
            TagSelect.this.javaScriptSupport.addScript(
                    "TagSelect.initialize('%s', '%s');", this.clientId,
                    this.getSelectedValue(TagSelect.this.value));

            TagSelect.this.resources.renderInformalParameters(writer);

            final Link link = TagSelect.this.resources.createEventLink(TagSelect.EVENT_NAME);

            final JSONObject config = new JSONObject();
            config.put("paramName", TagSelect.PARAM_NAME);
            config.put("minChars", String.valueOf(TagSelect.this.minChars));
            config.put("frequency", String.valueOf(TagSelect.this.frequency));

            final String methodAfterUpdate = "function (span) { TagSelect.addToken('" + this.clientId
                    + "', span); }";
            config.put("updateElement", methodAfterUpdate);
            final String callback = "function(field, query) { return query + '&values=' + $('"
                    + this.clientId + "-values').value; }";
            config.put("callback", callback);

            String configString = config.toString();
            configString = configString.replace("\"" + methodAfterUpdate + "\"", methodAfterUpdate);
            configString = configString.replace("\"" + callback + "\"", callback);

            TagSelect.this.javaScriptSupport.addScript(
                    "new Ajax.Autocompleter('%s', '%s', '%s', %s);", this.clientId, menuId,
                    link.toAbsoluteURI(), configString);
        }

        private String getAdditionalClasses() {
            final List<String> classes = new ArrayList<String>();

            if (TagSelect.this.dropdown) {
                classes.add("uiDropdown");
            }
            if (TagSelect.this.vertical) {
                classes.add("uiVertical");
            }

            return StringUtils.join(classes, " ");
        }

        private void renderMultiSelect(final MarkupWriter writer, final String menuId,
                final String additionalClasses) {
            writer.element("div", "id", this.clientId + "-uiTokenizer-container", "class",
                    "uiTokenizerCt clearfix uiTokenizerMulti " + additionalClasses);
            // Hidden Field
            writer.element("input", "type", "hidden", "id", this.clientId + "-values", "name",
                    this.controlName + "-values", "value", this.getSelectedValue(TagSelect.this.value));
            writer.end();
            writer.element("div", "id", this.clientId + "-tags", "class", "uiTokens");
            final Collection<Object> collection = this.getSelectedTags(TagSelect.this.value);
            for (final Object each : collection) {
                this.writeSelectedItem(writer, each, false);
            }
            writer.end();

            // Text Area
            writer.element("div", "id", this.clientId + "-uiTypeahead", "class", "uiTypeahead clearfix");
            if (TagSelect.this.dropdown && TagSelect.this.vertical) {
                this.writeDropdownArrow(writer, true);
            }
            if (TagSelect.this.blankLabel != null) {
                writer.element("div", "id", this.clientId + "-uiTokenLabel", "class", "uiTokenLabel");
                writer.write(TagSelect.this.blankLabel);
                writer.end();
            }
            writer.element("div", "class", "wrap");
            writer.element("div", "class", "innerWrap");
            writer.element("input", "type", "text", "autocomplete", "off", "id", this.clientId, "class",
                    "inputtext", "size", "1");
            if (!TagSelect.this.vertical) {
                writer.attributes("u:type", "multi");
            }
            writer.end();
            writer.end();
            writer.end();
            writer.end();

            writer.element("div", "id", menuId, "class", "uiTypeaheadView");
            writer.end();

            // Dropdown Arrow
            if (TagSelect.this.dropdown && !TagSelect.this.vertical) {
                this.writeDropdownArrow(writer, true);
            }
            writer.end();
        }

        private void renderSingleSelect(final MarkupWriter writer, final String menuId) {
            writer.element("div", "id", this.clientId + "-uiTokenizer-container", "class",
                    "uiTokenizerCt uiTypeahead uiTokenizerSingle");
            // hidden input field
            writer.element("input", "type", "hidden", "id", this.clientId + "-values", "name",
                    this.controlName + "-values", "value", this.getSelectedValue(TagSelect.this.value));
            writer.end();
            if (this.isSingleSelect(TagSelect.this.value)) {
                final Collection<Object> collection = this.getSelectedTags(TagSelect.this.value);

                if (collection.isEmpty()) {
                    this.writeSelectedItem(writer, TagSelect.this.value, true);
                }

                for (final Object each : collection) {
                    this.writeSelectedItem(writer, each, true);
                }
            }
            // Autocomplete menu
            writer.element("div", "id", menuId, "class", "uiTypeaheadView");
            writer.end();
            writer.end();
        }

        private boolean isSingleSelect(final Object selectedValue) {
            return !(selectedValue instanceof Collection<?>);
        }

        @SuppressWarnings("unchecked")
        private Collection<Object> getSelectedTags(final Object selectedValue) {
            final Collection<Object> selectedTags = new ArrayList<Object>();

            if (selectedValue instanceof Collection<?>) {
                selectedTags.addAll((Collection<Object>) selectedValue);
            } else if (selectedValue != null && this.isNotBlank(selectedValue)) {
                selectedTags.add(selectedValue);
            }

            return selectedTags;
        }

        private boolean isNotBlank(final Object selectedValue) {
            final String stringValue = String.valueOf(selectedValue);
            return !stringValue.trim().isEmpty();
        }

        @SuppressWarnings("unchecked")
        private String getSelectedValue(final Object selectedValue) {
            String result = "";
            if (selectedValue instanceof Collection<?>) {
                result = this.joinValue((Collection<Object>) selectedValue);
            } else if (selectedValue != null) {
                if (TagSelect.this.encoder != null) {
                    result = TagSelect.this.toClient(selectedValue);
                } else {
                    result = String.valueOf(selectedValue);
                }
            }
            return result;
        }

        private String joinValue(final Collection<Object> values) {
            final StringBuilder builder = new StringBuilder();

            for (final Object each : values) {
                if (builder.length() > 0) {
                    builder.append(";");
                }
                builder.append(TagSelect.this.toClient(each));
            }

            return builder.toString();
        }

        private void writeSelectedItem(final MarkupWriter writer, final Object item, final boolean single) {
            final String clientValue;
            if (item != null) {
                clientValue = TagSelect.this.toClient(item);
            } else {
                clientValue = "";
            }
            final String label;
            if (item != null) {
                label = this.getLabel(item);
            } else {
                label = "";
            }
            final Long itemId = System.nanoTime();
            final String selected;
            if (item != null) {
                selected = "selected";
            } else {
                selected = "";
            }

            if (single) {
                writer.element("div", "id", this.clientId + "-tags", "class", "wrap " + selected);
                writer.element("a", "onclick", "TagSelect.removeToken('"
                        + this.clientId + "', 'u-tag-" + itemId + "', '" + clientValue + "')");
                writer.end();
                if (TagSelect.this.dropdown) {
                    final boolean display = item == null;
                    this.writeDropdownArrow(writer, display);
                }
                if (TagSelect.this.blankLabel != null) {
                    writer.element("div", "id", this.clientId + "-uiTokenLabel", "class", "uiTokenLabel");
                    writer.write(TagSelect.this.blankLabel);
                    writer.end();
                }
                writer.element("input", "type", "text", "value", label, "u:type", "single", "autocomplete",
                        "off", "id", this.clientId, "class", "inputtext");
                writer.end();
                writer.end();
            } else {
                writer.element("span", "title", label, "id", "u-tag-" + itemId);
                writer.write(label);

                writer.element("a", "onclick", "TagSelect.removeToken('"
                        + this.clientId + "', 'u-tag-" + itemId + "', '" + clientValue + "')");
                writer.end();
                writer.end();
            }
        }

        private void writeDropdownArrow(final MarkupWriter writer, final boolean display) {
            String cssStyle = "block";

            if (!display) {
                cssStyle = "none";
            }

            writer.element("div", "id", this.clientId + "-trigger", "style", "display:" + cssStyle, "class",
                    "uiTokenDropdown", "onclick",
                    "TagSelect.triggerCompletion($('" + this.clientId + "'))");
            writer.element("div");
            writer.end();
            writer.end();
        }

        private String getLabel(final Object item) {
            if (TagSelect.this.encoder != null) {
                return TagSelect.this.encoder.getLabel(item);
            }
            return String.valueOf(item);
        }
    }
}
