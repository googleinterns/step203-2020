package com.google.step.model;

public class User {
  public final long id;
  public final String name;
  public final String username;
  public final String email;
  public final String photoBlobkey;
  public final String bio;

  public User(
      long id, String name, String username, String email, String photoBlobkey, String bio) {
    this.id = id;
    this.name = name;
    this.username = username;
    this.email = email;
    this.photoBlobkey = photoBlobkey;
    this.bio = bio;
  }
}
