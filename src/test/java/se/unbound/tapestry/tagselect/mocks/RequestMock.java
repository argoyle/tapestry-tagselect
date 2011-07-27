package se.unbound.tapestry.tagselect.mocks;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

/**
 * Mock implementation of the Request interface for unit testing.
 */
public class RequestMock implements Request {
    private String path;
    private String contextPath;
    private final Map<String, String> parameterMap = new HashMap<String, String>();

    public void setPath(final String path) {
        this.path = path;
    }

    public void setContextPath(final String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    public String getContextPath() {
        return this.contextPath;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    /**
     * Adds a parameter/value-pair to the request.
     * 
     * @param parameter The name of the parameter.
     * @param value The value of the parameter.
     */
    public void addParameter(final String parameter, final String value) {
        this.parameterMap.put(parameter, value);
    }

    @Override
    public String getParameter(final String name) {
        return this.parameterMap.get(name);
    }

    // Not implemented

    @Override
    public Object getAttribute(final String name) {
        return null;
    }

    @Override
    public long getDateHeader(final String name) {
        return 0;
    }

    @Override
    public String getHeader(final String name) {
        return null;
    }

    @Override
    public List<String> getHeaderNames() {
        return null;
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public String getMethod() {
        return null;
    }

    @Override
    public List<String> getParameterNames() {
        return null;
    }

    @Override
    public String[] getParameters(final String name) {
        return null;
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public Session getSession(final boolean create) {
        return null;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public boolean isXHR() {
        return false;
    }

    @Override
    public void setAttribute(final String name, final Object value) {
    }

    @Override
    public int getLocalPort() {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public int getServerPort() {
        throw new UnsupportedOperationException("Not yet implemented!");
    }
}
