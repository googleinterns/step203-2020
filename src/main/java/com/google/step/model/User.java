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

  public User(long id, String email, String username, String photoBlobKey, String bio) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.bio = bio;
    if (photoBlobKey != null) {
      this.photoBlobKey = Optional.of(photoBlobKey);
    } else {
      this.photoBlobKey = null;
    }
  }

  public User(long id, String email, String username, String bio) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.bio = bio;
    this.photoBlobKey = Optional.empty();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof User)) {
      return false;
    }
    User user = (User) obj;
    return user.id == this.id
        && ((user.email == null && this.email == null) || user.email.equals(this.email))
        && ((user.bio == null && this.bio == null) || user.bio.equals(this.bio))
        && ((user.email == null && this.email == null) || user.email.equals(this.email))
        && ((user.photoBlobKey == null && this.photoBlobKey == null)
            || user.photoBlobKey.equals(this.photoBlobKey));
  }
}
