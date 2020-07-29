package com.google.step.datamanager;

import com.google.step.model.Comment;
import java.util.List;

public class CommentsWithToken {
  public final List<Comment> comments;
  public final String token;

  public CommentsWithToken(List<Comment> comments, String token) {
    this.comments = comments;
    this.token = token;
  }
}
