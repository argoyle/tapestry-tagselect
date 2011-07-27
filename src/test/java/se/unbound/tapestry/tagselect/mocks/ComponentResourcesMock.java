package se.unbound.tapestry.tagselect.mocks;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentEventCallback;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.ioc.AnnotationProvider;
import org.apache.tapestry5.ioc.Location;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.model.ComponentModel;
import org.apache.tapestry5.runtime.Component;
import org.apache.tapestry5.runtime.PageLifecycleListener;
import org.slf4j.Logger;

/**
 * Mock implementation of ComponentResources for unit testing.
 */
public class ComponentResourcesMock implements ComponentResources {
    private String id;
    private Object eventResult;
    private final Map<String, Boolean> boundParameters = new HashMap<String, Boolean>();
    private final Map<String, Class<?>> boundTypes = new HashMap<String, Class<?>>();
    private Messages containerMessages;

    public void setId(final String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Link createEventLink(final String eventType, final Object... context) {
        return new LinkMock(eventType);
    }

    public void setEventResult(final Object eventResult) {
        this.eventResult = eventResult;
    }

    @Override
    public boolean triggerEvent(final String eventType, final Object[] contextValues,
            final ComponentEventCallback callback) {
        if (this.eventResult != null) {
            callback.handleResult(this.eventResult);
        }
        return false;
    }

    /**
     * Sets the provided parameter as bound in the component resources.
     * 
     * @param parameter The parameter to set as bound.
     */
    public void setBound(final String parameter) {
        this.boundParameters.put(parameter, Boolean.TRUE);
    }

    @Override
    public boolean isBound(final String parameterName) {
        return this.boundParameters.containsKey(parameterName);
    }

    /**
     * Sets the provided class as bound to the provided parameterName.
     * 
     * @param parameterName The name to bind the class to.
     * @param boundClass The class to bind.
     */
    public void setBoundType(final String parameterName, final Class<?> boundClass) {
        this.boundTypes.put(parameterName, boundClass);
    }

    @Override
    public Class getBoundType(final String parameterName) {
        return this.boundTypes.get(parameterName);
    }

    public void setContainerMessages(final Messages containerMessages) {
        this.containerMessages = containerMessages;
    }

    @Override
    public Messages getContainerMessages() {
        return this.containerMessages;
    }

    // Not implemented

    @Override
    public void addPageLifecycleListener(final PageLifecycleListener listener) {
    }

    @Override
    public void discardPersistentFieldChanges() {
    }

    @Override
    public AnnotationProvider getAnnotationProvider(final String parameterName) {
        return null;
    }

    @Override
    public Resource getBaseResource() {
        return null;
    }

    @Override
    public Block getBlockParameter(final String parameterName) {
        return null;
    }

    @Override
    public Component getComponent() {
        return null;
    }

    @Override
    public ComponentModel getComponentModel() {
        return null;
    }

    @Override
    public Component getContainer() {
        return null;
    }

    @Override
    public ComponentResources getContainerResources() {
        return null;
    }

    @Override
    public String getElementName() {
        return null;
    }

    @Override
    public Component getEmbeddedComponent(final String embeddedId) {
        return null;
    }

    @Override
    public <T> T getInformalParameter(final String name, final Class<T> type) {
        return null;
    }

    @Override
    public List<String> getInformalParameterNames() {
        return null;
    }

    @Override
    public Messages getMessages() {
        return null;
    }

    @Override
    public Component getPage() {
        return null;
    }

    @Override
    public <T extends Annotation> T getParameterAnnotation(final String parameterName,
            final Class<T> annotationType) {
        return null;
    }

    @Override
    public Object getRenderVariable(final String name) {
        return null;
    }

    @Override
    public void renderInformalParameters(final MarkupWriter writer) {
    }

    @Override
    public void storeRenderVariable(final String name, final Object value) {
    }

    @Override
    public Link createActionLink(final String eventType, final boolean forForm, final Object... context) {
        return null;
    }

    @Override
    public Link createFormEventLink(final String eventType, final Object... context) {
        return null;
    }

    @Override
    public Link createPageLink(final String pageName, final boolean override, final Object... context) {
        return null;
    }

    @Override
    public Link createPageLink(final Class pageClass, final boolean override, final Object... context) {
        return null;
    }

    @Override
    public Block findBlock(final String blockId) {
        return null;
    }

    @Override
    public Block getBlock(final String blockId) {
        return null;
    }

    @Override
    public Block getBody() {
        return null;
    }

    @Override
    public String getCompleteId() {
        return null;
    }

    @Override
    public String getElementName(final String defaultElementName) {
        return null;
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Logger getLogger() {
        return null;
    }

    @Override
    public String getNestedId() {
        return null;
    }

    @Override
    public String getPageName() {
        return null;
    }

    @Override
    public boolean hasBody() {
        return false;
    }

    @Override
    public boolean isRendering() {
        return false;
    }

    @Override
    public boolean triggerContextEvent(final String eventType, final EventContext context,
            final ComponentEventCallback callback) {
        return false;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public void removePageLifecycleListener(final PageLifecycleListener listener) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public boolean isMixin() {
        throw new UnsupportedOperationException("Not yet implemented!");
    }
}