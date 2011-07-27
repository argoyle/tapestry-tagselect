package se.unbound.tapestry.tagselect.helpers;

public class Tag {
    private final Long id;
    private final String value;

    public Tag(final Long id, final String value) {
        this.id = id;
        this.value = value;
    }

    public Long getId() {
        return this.id;
    }

    public String getValue() {
        return this.value;
    }
}
