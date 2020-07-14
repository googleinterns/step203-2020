package com.google.step.model;

public class Comment {
  public final long id;
  public final long dealId;
  public final long userId;
  public final String content;
  public final String timestamp;

  public Comment(long id, long dealId, long userId, String content, String timestamp) {
    this.id = id;
    this.dealId = dealId;
    this.userId = userId;
    this.content = content;
    this.timestamp = timestamp;
  }
}
