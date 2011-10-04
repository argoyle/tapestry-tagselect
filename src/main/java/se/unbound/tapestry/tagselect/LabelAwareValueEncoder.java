package se.unbound.tapestry.tagselect;

import org.apache.tapestry5.ValueEncoder;

/**
 * Used to convert server side values to client-side strings. This is an extension of Tapestrys {@link ValueEncoder}
 * with the addition of a getLabel-method.
 * 
 * @param <V> The type of value.
 */
public interface LabelAwareValueEncoder<V> extends ValueEncoder<V> {
    /**
     * Retrieves the label for the provided value.
     * 
     * @param value The value to retrieve a label for.
     * @return the label for the provided value.
     */
    String getLabel(V value);
}
