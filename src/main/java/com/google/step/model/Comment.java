package com.google.step.model;

public class Comment {
  public final long id;
  public final long dealId;
  public final long userId;
  public final String content;

  public Comment(long id, long dealId, long userId, String content) {
    this.id = id;
    this.dealId = dealId;
    this.userId = userId;
    this.content = content;
  }
}
