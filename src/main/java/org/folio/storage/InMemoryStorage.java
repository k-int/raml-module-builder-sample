package org.folio.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class InMemoryStorage<T> {
  private final Function<T, String> idGetter;
  private final Map<String, T> records = new HashMap<>();

  public InMemoryStorage(Function<T, String> idGetter) {
    this.idGetter = idGetter;
  }

  public void create(T record) {
    records.put(idGetter.apply(record), record);
  }

  public void replace(String id, T record) {
    records.replace(id, record);
  }

  public boolean exists(String id) {
    return records.containsKey(id);
  }

  public T getById(String id) {
    return records.get(id);
  }

  public MultipleRecords<T> getAll() {
    return new MultipleRecords<>(records.values(), records.size());
  }

  public void deleteById(String id) {
    records.remove(id);
  }

  public void deleteAll() {
    records.clear();
  }
}
