package com.google.step.model;

import java.util.Optional;

/** A class representing a user. */
public class User {
  public final long id;
  public final String username;
  public final String email;
  public final Optional<String> photoBlobKey;
  public final String bio;

  public User(long id, String email) {
    this.id = id;
    this.username = email;
    this.email = email;
    this.photoBlobKey = Optional.empty();
    this.bio = "";
  }

  public User(long id, String email, String username, Optional<String> photoBlobKey, String bio) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.photoBlobKey = photoBlobKey;
    this.bio = bio;
  }
}
