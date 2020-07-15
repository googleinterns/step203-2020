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
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof Restaurant)) {
      return false;
    }
    Restaurant other = (Restaurant) obj;
    return (this.id == other.id)
        && ((this.name == null && other.name == null)
            || (this.name != null && this.name.equals(other.name)))
        && ((this.photoBlobkey == null && other.photoBlobkey == null)
            || (this.photoBlobkey != null && this.photoBlobkey.equals(other.photoBlobkey)));
  }
}
