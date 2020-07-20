package com.google.step.model;

/** A class representing default restaurants in database. */
public class DefaultRestaurant extends Restaurant {

  public final String photoUrl;

  public DefaultRestaurant(long id, String name, String photoUrl) {
    super(id, name, null);
    this.photoUrl = photoUrl;
  }
}
