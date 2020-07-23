package com.google.step.model;

import static com.google.step.model.Util.isEqual;

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

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof Comment)) {
      return false;
    }
    Comment other = (Comment) obj;
    if (other == this) {
      return true;
    }

    return (other.id == this.id)
        && this.dealId == other.dealId
        && this.userId == other.userId
        && isEqual(this.content, other.content);
  }
}
