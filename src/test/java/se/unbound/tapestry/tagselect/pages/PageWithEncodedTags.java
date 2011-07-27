package se.unbound.tapestry.tagselect.pages;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import se.unbound.tapestry.tagselect.helpers.Tag;
import se.unbound.tapestry.tagselect.helpers.TagValueEncoder;
import se.unbound.tapestry.tagselect.services.TagSource;

public class PageWithEncodedTags {
    @Inject
    private TagSource tagSource;

    @Persist
    @Property
    private List<Tag> tags;

    void onPrepare() {
        if (this.tags == null) {
            this.tags = new ArrayList<Tag>();
            this.tags.addAll(this.tagSource.getTags());
        }
    }

    public ValueEncoder<Tag> getEncoder() {
        return new TagValueEncoder();
    }

    void onSuccess() {
        System.out.println(this.tags);
        this.tagSource.save(this.tags);
    }
}
