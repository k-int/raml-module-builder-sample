package org.folio.storage;

import java.util.HashMap;
import java.util.function.Function;

public class InMemoryStorage<T> extends HashMap<String, T> {
  public void create(T record, Function<T, String> idGetter) {
    put(idGetter.apply(record), record);
  }
}
