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
    User other = (User) obj;
    if (other == this) {
      return true;
    }

    return other.id == this.id
        && ((other.username == null && this.username == null)
            || other.username.equals(this.username))
        && ((other.bio == null && this.bio == null) || other.bio.equals(this.bio))
        && ((other.email == null && this.email == null) || other.email.equals(this.email))
        && ((other.photoBlobKey == null && this.photoBlobKey == null)
            || other.photoBlobKey.equals(this.photoBlobKey));
  }
}
