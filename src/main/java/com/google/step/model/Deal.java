package com.google.step.model;

import static com.google.step.model.Util.isEqual;

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
        && isEqual(this.description, other.description)
        && isEqual(this.photoBlobkey, other.photoBlobkey)
        && isEqual(this.start, other.start)
        && isEqual(this.end, other.end)
        && isEqual(this.source, other.source)
        && (this.posterId == other.posterId)
        && (this.restaurantId == other.restaurantId);
  }
}
