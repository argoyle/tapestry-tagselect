package se.unbound.tapestry.tagselect.mocks;

import org.apache.tapestry5.ContentType;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.services.MarkupWriterFactory;

public class MarkupWriterFactoryMock implements MarkupWriterFactory {
    @Override
    public MarkupWriter newMarkupWriter(final ContentType contentType) {
        return null;
    }

    @Override
    public MarkupWriter newPartialMarkupWriter(final ContentType contentType) {
        return new MarkupWriterMock();
    }

    @Override
    public MarkupWriter newMarkupWriter(final String pageName) {
        return null;
    }
}
