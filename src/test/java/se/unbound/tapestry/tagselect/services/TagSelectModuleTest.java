package se.unbound.tapestry.tagselect.services;

import static org.junit.Assert.assertNotNull;

import org.apache.tapestry5.services.LibraryMapping;
import org.junit.Test;

import se.unbound.tapestry.tagselect.mocks.ConfigurationMock;
import se.unbound.tapestry.tagselect.mocks.LibraryMappingChecker;

public class TagSelectModuleTest {
    @Test
    public void instantiation() {
        final TagSelectModule module = new TagSelectModule();
        assertNotNull(module);
    }

    @Test
    public void testContributeComponentClassResolver() {
        final ConfigurationMock<LibraryMapping> configuration = new ConfigurationMock<LibraryMapping>();
        TagSelectModule.contributeComponentClassResolver(configuration);
        configuration.assertConfiguration(new LibraryMappingChecker("tag", "se.unbound.tapestry.tagselect"));
    }
}
