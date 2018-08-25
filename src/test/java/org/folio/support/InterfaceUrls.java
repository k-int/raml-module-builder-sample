package org.folio.support;

import java.net.URL;

public enum InterfaceUrls {
  Records("/records");

  private final String basePath;

  InterfaceUrls(String basePath) {
    this.basePath = basePath;
  }

  public UrlMaker urlsBasedUpon(String baseUrl) {
    return path -> new URL(
      String.format("%s%s%s",
        baseUrl,
        basePath, path));
  }
}
