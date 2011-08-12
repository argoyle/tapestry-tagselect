package se.unbound.tapestry.tagselect.pages;

import java.util.Arrays;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import se.unbound.tapestry.tagselect.LabelAwareValueEncoder;
import se.unbound.tapestry.tagselect.helpers.Tag;
import se.unbound.tapestry.tagselect.helpers.TagValueEncoder;
import se.unbound.tapestry.tagselect.services.TagSource;

public class PageWithSingleEncodedTag {
    @Inject
    private TagSource tagSource;

    @Persist
    @Property
    private Tag tag;

    void onPrepare() {
        if (!this.tagSource.getTags().isEmpty()) {
            this.tag = this.tagSource.getTags().get(0);
        }
    }

    public LabelAwareValueEncoder<Tag> getEncoder() {
        return new TagValueEncoder();
    }

    void onSuccess() {
        System.out.println(this.tag);
        this.tagSource.save(Arrays.asList(this.tag));
    }
}
