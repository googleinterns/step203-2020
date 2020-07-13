package com.google.step.model;

public class Deal {
  public final long id;
  public final String description;
  public final String photoBlobkey;
  public final String start;
  public final String end;
  public final String source;
  public final long posterId;
  public final long restaurantId;

  public Deal(
      long id,
      String description,
      String photoBlobkey,
      String start,
      String end,
      String source,
      long posterId,
      long restaurantId) {
    this.id = id;
    this.description = description;
    this.photoBlobkey = photoBlobkey;
    this.start = start;
    this.end = end;
    this.source = source;
    this.posterId = posterId;
    this.restaurantId = restaurantId;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof Deal)) {
      return false;
    }
    Deal other = (Deal) obj;
    if (other == this) {
      return true;
    }

    return (other.id == this.id)
        && ((other.description == null && this.description == null)
            || (this.description != null && this.description.equals(other.description)))
        && ((other.photoBlobkey == null && this.photoBlobkey == null)
            || (this.photoBlobkey != null && this.photoBlobkey.equals(other.photoBlobkey)))
        && ((other.start == null && this.start == null)
            || (this.start != null && this.start.equals(other.start)))
        && ((other.end == null && this.end == null)
            || (this.end != null && this.end.equals(other.end)))
        && ((other.source == null && this.source == null)
            || (this.source != null && this.source.equals(other.source)))
        && (this.posterId == other.posterId)
        && (this.restaurantId == other.restaurantId);
  }
}
