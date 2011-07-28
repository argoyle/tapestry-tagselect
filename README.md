Description
===========
Tapestry-tagselect is a select component for Tapestry 5 similar to the
version selector in recent JIRA-versions.

[Screenshot](https://github.com/argoyle/tapestry-tagselect/blob/master/tagselect.png)

Usage
=====
Add a dependency to your POM:

    <dependency>
      <groupId>se.unbound</groupId>
      <artifactId>tapestry-tagselect</artifactId>
      <version>1.1</version>
    </dependency>

Add the component to your template:

    <div t:type="tag/tagselect" t:id="tags" t:value="tags" t:encoder="encoder" />

In your page you need a property for the currently selected tags. If the tags are strings, no encoder is necessary, otherwise a 
ValueEncoder need to be provided via the encoder-attribute.

The component uses an Ajax.Autocompleter when retrieving the possible tag-values so a method for the provideCompletions-event that 
return a SelectModel is necessary in the page-class as well.

Example
=======
Page-class
----------
    public class Index {
        @Persist
        @Property
        private List<String> tags;

        public void onPrepare() {
            if (this.tags == null) {
                this.tags = new ArrayList<String>();
            }
        }

        SelectModel onProvideCompletionsFromTags(final String input) {
            final List<String> result = Arrays.asList("Milestone 1", "Milestone 2", "Milestone 3");
            return new StringSelectModel(result);
        }

        private static class StringSelectModel implements SelectModel {
            private final List<String> strings;

            public StringSelectModel(final List<String> strings) {
                this.strings = strings;
            }

            @Override
            public List<OptionModel> getOptions() {
                final List<OptionModel> options = new ArrayList<OptionModel>();

                for (final String string : this.strings) {
                    options.add(new OptionModelImpl(string));
                }

                return options;
            }

            @Override
            public List<OptionGroupModel> getOptionGroups() {
                return null;
            }

            @Override
            public void visit(final SelectModelVisitor visitor) {
            }
        }
    }


Template
--------
    <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
       "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
    <html xmlns:t="http://tapestry.apache.org/schema/tapestry_5_0_0.xsd">
      <head>
        <title>Example</title>
      </head>
      <body>
        <form t:type="form">
          <div t:type="tag/tagselect" t:id="tags" t:value="tags" style="width: 400px;"></div>
          <t:submit/>
        </form>
      </body>
    </html>

