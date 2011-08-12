package se.unbound.tapestry.tagselect.helpers;

import se.unbound.tapestry.tagselect.LabelAwareValueEncoder;
import se.unbound.tapestry.tagselect.services.TagSource;

public class TagValueEncoder implements LabelAwareValueEncoder<Tag> {
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

    @Override
    public String getLabel(final Tag value) {
        return value.getValue();
    }
}
