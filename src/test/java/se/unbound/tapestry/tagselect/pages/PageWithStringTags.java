package se.unbound.tapestry.tagselect.pages;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;

public class PageWithStringTags {
    @Persist
    @Property
    private List<String> tags;

    void onActivate(final EventContext context) {
        if (this.tags == null) {
            this.tags = new ArrayList<String>();
        }
        if (context.getCount() > 0) {
            for (int i = 0; i < context.getCount(); i++) {
                final String tag = context.get(String.class, i);
                this.tags.add(tag);
            }
        }
    }

    SelectModel onProvideCompletionsFromTags(final String input) {
        return null;
    }
}
