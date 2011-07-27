package se.unbound.tapestry.tagselect.services;

import java.util.ArrayList;
import java.util.List;

import se.unbound.tapestry.tagselect.helpers.Tag;

public class TagSource {
    public static final List<Tag> TAGS = new ArrayList<Tag>();
    public static List<Tag> savedTags;

    public void save(final List<Tag> tagsToSave) {
        TagSource.savedTags = tagsToSave;
    }

    public List<Tag> getTags() {
        return TagSource.TAGS;
    }
}
