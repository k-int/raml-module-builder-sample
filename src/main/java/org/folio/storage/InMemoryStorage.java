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

  @Override
  public T replace(String id, T record) {
    return super.replace(id, record);
  }

  public boolean exists(String id) {
    return containsKey(id);
  }

  public T getById(String id) {
    return get(id);
  }

  public MultipleRecords<T> getAll() {
    return new MultipleRecords<>(this.values(), this.size());
  }

  public void deleteById(String id) {
    remove(id);
  }

  public void deleteAll() {
    clear();
  }
}
