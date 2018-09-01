package org.folio.storage;

import java.util.HashMap;
import java.util.function.Function;

public class InMemoryStorage<T> extends HashMap<String, T> {
  private final Function<T, String> idGetter;

  public InMemoryStorage(Function<T, String> idGetter) {
    this.idGetter = idGetter;
  }

  public void create(T record) {
    put(idGetter.apply(record), record);
  }

  public boolean exists(String id) {
    return containsKey(id);
  }
}
