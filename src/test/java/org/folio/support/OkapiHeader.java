package org.folio.support;

public enum OkapiHeader {
  TENANT("X-Okapi-Tenant"),
  TOKEN("X-Okapi-Token"),
  USER_ID("X-Okapi-User-Id"),
  REQUEST_ID("X-Okapi-Request-Id");

  private final String value;

  OkapiHeader(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
