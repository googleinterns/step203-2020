package com.google.step.datamanager;

import com.google.step.model.Comment;
import java.util.List;

public interface CommentManager {
  public Comment createComment(long dealId, long userId, String content);

  public List<Comment> getComments(long dealId);

  public Comment updateComment(long id, String content);

  public void deleteComment(long id);
}
