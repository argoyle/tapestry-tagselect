package se.unbound.tapestry.tagselect.mocks;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.MarkupWriterListener;
import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.dom.Element;

/**
 * Mock implementation of the MarkupWriter interface for unit testing.
 */
public class MarkupWriterMock implements MarkupWriter {
    private final Document document = new Document();
    private Element currentElement;

    /**
     * Creates a new MarkupWriterMock.
     */
    public MarkupWriterMock() {
        this.document.newRootElement("html");
        this.currentElement = this.document.getRootElement();
    }

    @Override
    public Element element(final String name, final Object... attributes) {
        final String[] namesAndValues = new String[attributes.length];
        int i = 0;
        for (final Object attribute : attributes) {
            if (attribute != null) {
                namesAndValues[i++] = attribute.toString();
            } else {
                namesAndValues[i++] = null;
            }
        }

        this.currentElement = this.currentElement.element(name, namesAndValues);
        return this.currentElement;
    }

    @Override
    public Element end() {
        this.currentElement = this.currentElement.getContainer();
        return this.currentElement;
    }

    @Override
    public void write(final String text) {
        if (text != null) {
            this.currentElement.text(text);
        }
    }

    @Override
    public void toMarkup(final PrintWriter writer) {
        this.document.toMarkup(writer);
    }

    @Override
    public String toString() {
        final StringWriter stringWriter = new StringWriter();
        this.toMarkup(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    // Not implemented

    @Override
    public void writef(final String format, final Object... args) {
    }

    @Override
    public void writeRaw(final String text) {
    }

    @Override
    public void comment(final String text) {
    }

    @Override
    public void cdata(final String content) {
    }

    @Override
    public void attributes(final Object... namesAndValues) {
    }

    @Override
    public Document getDocument() {
        return null;
    }

    @Override
    public Element getElement() {
        return null;
    }

    @Override
    public Element defineNamespace(final String namespace, final String namespacePrefix) {
        return null;
    }

    @Override
    public Element elementNS(final String namespace, final String elementName) {
        return null;
    }

    @Override
    public Element attributeNS(final String namespace, final String attributeName,
            final String attributeValue) {
        return null;
    }

    @Override
    public void addListener(final MarkupWriterListener listener) {
    }

    @Override
    public void removeListener(final MarkupWriterListener listener) {
    }
}