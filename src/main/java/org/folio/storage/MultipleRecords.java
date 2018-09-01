package org.folio.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MultipleRecords<T> {
  private final List<T> records;
  private final Integer totalRecords;

  MultipleRecords(Collection<T> records, Integer totalRecords) {
    this.records = new ArrayList<>(records);
    this.totalRecords = totalRecords;
  }

  public List<T> getRecords() {
    return records;
  }

  public Integer getTotalRecords() {
    return totalRecords;
  }
}
