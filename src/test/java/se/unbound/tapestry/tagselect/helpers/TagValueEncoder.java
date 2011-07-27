package se.unbound.tapestry.tagselect.helpers;

import org.apache.tapestry5.ValueEncoder;

import se.unbound.tapestry.tagselect.services.TagSource;

public class TagValueEncoder implements ValueEncoder<Tag> {
    @Override
    public String toClient(final Tag value) {
        return String.valueOf(value.getId());
    }

    @Override
    public Tag toValue(final String clientValue) {
        for (final Tag tag : TagSource.TAGS) {
            if (String.valueOf(tag.getId()).equals(clientValue)) {
                return tag;
            }
        }
        return null;
    }
}
