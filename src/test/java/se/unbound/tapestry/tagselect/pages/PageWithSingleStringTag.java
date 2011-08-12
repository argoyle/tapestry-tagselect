package se.unbound.tapestry.tagselect.pages;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;

public class PageWithSingleStringTag {
    @Persist
    @Property
    private String tag;

    void onActivate(final EventContext context) {
        if (context.getCount() > 0) {
            final String tag = context.get(String.class, 0);
            this.tag = tag;
        }
    }

    SelectModel onProvideCompletionsFromTag(final String input) {
        return null;
    }
}
