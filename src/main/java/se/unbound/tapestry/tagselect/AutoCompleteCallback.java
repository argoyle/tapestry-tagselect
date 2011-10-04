package se.unbound.tapestry.tagselect;

import java.util.concurrent.atomic.AtomicReference;

import org.apache.tapestry5.ComponentEventCallback;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ioc.services.TypeCoercer;

/**
 * ComponentEventCallback for the autocomplete.
 */
public final class AutoCompleteCallback implements ComponentEventCallback<SelectModel> {
    private final AtomicReference<SelectModel> model;
    private final TypeCoercer coercer;

    /**
     * Constructs a new AutoCompleteCallback.
     * 
     * @param model The model to hold the autocomplete result.
     * @param coercer The {@link TypeCoercer} to use to coerce the result to the correct type.
     */
    public AutoCompleteCallback(final AtomicReference<SelectModel> model, final TypeCoercer coercer) {
        this.model = model;
        this.coercer = coercer;
    }

    @Override
    public boolean handleResult(final SelectModel result) {
        final SelectModel matches = this.coercer.coerce(result, SelectModel.class);

        this.model.set(matches);

        return true;
    }
}