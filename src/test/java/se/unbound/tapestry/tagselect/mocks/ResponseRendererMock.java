package se.unbound.tapestry.tagselect.mocks;

import java.io.IOException;

import org.apache.tapestry5.ContentType;
import org.apache.tapestry5.services.ResponseRenderer;

public class ResponseRendererMock implements ResponseRenderer {
    @Override
    public void renderPageMarkupResponse(final String pageName) throws IOException {
    }

    @Override
    public ContentType findContentType(final Object component) {
        return new ContentType("text/xhtml");
    }
}
