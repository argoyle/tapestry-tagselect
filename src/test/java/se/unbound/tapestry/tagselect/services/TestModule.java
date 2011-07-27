package se.unbound.tapestry.tagselect.services;

import org.apache.tapestry5.ioc.ServiceBinder;

public class TestModule {
    public static void bind(final ServiceBinder binder) {
        binder.bind(TagSource.class);
    }
}
