package com.google.step.model;

import com.google.step.datamanager.RestaurantGenerator;

public class Restaurant {

  public final long id;
  public final String name;
  public final String photoUrl;
  public final long posterId;

  private Restaurant(long id, String name, String photoUrl, long posterId) {
    this.id = id;
    this.name = name;
    this.photoUrl = photoUrl;
    this.posterId = posterId;
  }

  public static Restaurant createRestaurantWithBlobkey(
      long id, String name, String photoBlobKey, long posterId) {
    return new Restaurant(id, name, getImageUrlFromBlobKey(photoBlobKey), posterId);
  }

  public static Restaurant createRestaurantWithPhotoReference(
      long id, String name, String photoReference, long posterId) {
    return new Restaurant(id, name, getImageUrlFromPhotoReference(photoReference), posterId);
  }

  public static Restaurant createRestaurantWithPhotoUrl(
      long id, String name, String photoUrl, long posterId) {
    return new Restaurant(id, name, photoUrl, posterId);
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
        && (this.posterId == other.posterId)
        && ((this.name == null && other.name == null)
            || (this.name != null && this.name.equals(other.name)))
        && ((this.photoUrl == null && other.photoUrl == null)
            || (this.photoUrl != null && this.photoUrl.equals(other.photoUrl)));
  }

  public static String getImageUrlFromBlobKey(String blobKey) {
    return blobKey == null ? null : "/api/images/" + blobKey;
  }

  public static String getImageUrlFromPhotoReference(String photoReference) {
    if (photoReference == null) {
      return null;
    }
    String url =
        "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400"
            + "&photoreference="
            + photoReference
            + "&key="
            + RestaurantGenerator.API_KEY;
    return url;
  }
}
