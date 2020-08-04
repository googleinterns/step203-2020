package com.google.step.datamanager;

import com.google.step.model.Comment;

public interface CommentManager {
  public Comment createComment(long dealId, long userId, String content);

  /**
   * Gets more comments with the given dealId from the pagination token. If pagination is set to
   * null, gets the initial list of comments.
   */
  public CommentsWithToken getCommentsForDeal(long dealId, String token);

  public void deleteAllCommentsOfDeal(long dealId);

  /** Gets the average comment sentiment of the deal */
  public double getAvgCommentSentiment(long dealId);

  public Comment updateComment(long id, String content);

  public void deleteComment(long id);
}
