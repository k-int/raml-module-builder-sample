package org.folio.support;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Result<T> {
  static <T> Result<T> from(Supplier<T> supplier) {
    return new Success<>(supplier.get());
  }

  <R> Result<R> map(Function<T, R> mapper);

  Result<T> apply(Consumer<T> effect);

  class Success<T> implements Result<T> {
    private final T value;

    private Success(T value) {
      this.value = value;
    }

    @Override
    public <R> Result<R> map(Function<T, R> mapper) {
      return new Success<>(mapper.apply(value));
    }

    @Override
    public Result<T> apply(Consumer<T> effect) {
      effect.accept(value);

      return this;
    }
  }
}
