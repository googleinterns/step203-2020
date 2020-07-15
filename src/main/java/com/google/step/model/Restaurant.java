package com.google.step.model;

public class Restaurant {
  public final long id;
  public final String name;
  public final String photoBlobkey;

  public Restaurant(long id, String name, String photoBlobkey) {
    this.id = id;
    this.name = name;
    this.photoBlobkey = photoBlobkey;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof Restaurant)) {
      return false;
    }
    Restaurant other = (Restaurant) obj;
    return id == other.id && name.equals(other.name) && photoBlobkey.equals(other.photoBlobkey);
  }
}
