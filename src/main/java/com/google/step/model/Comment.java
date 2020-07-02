package com.google.step.model;

public class Comment {
  public final long id;
  public final long dealId;
  public final long userId;
  public final String content;

  public Comment(long id, long deal, long user, String content) {
    this.id = id;
    this.dealId = deal;
    this.userId = user;
    this.content = content;
  }
}
