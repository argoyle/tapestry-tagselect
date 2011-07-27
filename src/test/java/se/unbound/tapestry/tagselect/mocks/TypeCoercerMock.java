package se.unbound.tapestry.tagselect.mocks;

import org.apache.tapestry5.ioc.services.Coercion;
import org.apache.tapestry5.ioc.services.TypeCoercer;

public class TypeCoercerMock implements TypeCoercer {
    @Override
    public <S, T> T coerce(final S input, final Class<T> targetType) {
        return (T) input;
    }

    @Override
    public <S, T> String explain(final Class<S> inputType, final Class<T> targetType) {
        return null;
    }

    @Override
    public void clearCache() {
    }

    @Override
    public <S, T> Coercion<S, T> getCoercion(final Class<S> sourceType, final Class<T> targetType) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }
}
