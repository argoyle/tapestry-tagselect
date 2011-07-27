package se.unbound.tapestry.tagselect.mocks;

import java.util.List;

import org.apache.tapestry5.Link;

/**
 * Mock implementation of the Link interface for unit testing.
 */
public class LinkMock implements Link {
    private final String pageName;

    /**
     * Creates a new LinkMock.
     * 
     * @param pageName The name of the page to use for the link.
     */
    public LinkMock(final String pageName) {
        this.pageName = pageName;
    }

    @Override
    public String toAbsoluteURI() {
        return this.pageName;
    }

    @Override
    public String toRedirectURI() {
        return this.pageName;
    }

    @Override
    public String toURI() {
        return this.pageName;
    }

    // Not implemented

    @Override
    public void addParameter(final String parameterName, final String value) {
    }

    @Override
    public String getAnchor() {
        return null;
    }

    @Override
    public List<String> getParameterNames() {
        return null;
    }

    @Override
    public String getParameterValue(final String name) {
        return null;
    }

    @Override
    public void setAnchor(final String anchor) {
    }

    @Override
    public Link addParameterValue(final String parameterName, final Object value) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public void removeParameter(final String parameterName) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public String getBasePath() {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public Link copyWithBasePath(final String basePath) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public String toAbsoluteURI(final boolean secure) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }
}
