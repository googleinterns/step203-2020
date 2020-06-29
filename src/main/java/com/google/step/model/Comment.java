package com.google.step.model;

public class Comment {
  public final long id;
  public final long deal;
  public final long user;
  public final String content;

  public Comment(long id, long deal, long user, String content) {
    this.id = id;
    this.deal = deal;
    this.user = user;
    this.content = content;
  }
}
