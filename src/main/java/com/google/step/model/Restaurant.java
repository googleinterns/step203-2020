package com.google.step.model;

import com.google.step.datamanager.RestaurantGenerator;

public class Restaurant {

  public final long id;
  public final String name;
  public final String photoUrl;

  private Restaurant(long id, String name, String photoUrl) {
    this.id = id;
    this.name = name;
    this.photoUrl = photoUrl;
  }

  public static Restaurant createRestaurantWithBlobkey(long id, String name, String photoBlobKey) {
    return new Restaurant(id, name, getImageUrlFromBlobKey(photoBlobKey));
  }

  public static Restaurant createRestaurantWithPhotoReference(
      long id, String name, String photoReference) {
    return new Restaurant(id, name, getImageUrlFromPhotoReference(photoReference));
  }

  public static Restaurant createRestaurantWithPhotoUrl(long id, String name, String photoUrl) {
    return new Restaurant(id, name, photoUrl);
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
