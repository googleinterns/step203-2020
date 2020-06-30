package com.google.step.datamanager;

import com.google.step.model.Comment;

public interface CommentManager {
    public Comment createComment(
        long dealId, 
        long userId, 
        String content
    )
    public Comment getComments(long dealId);

    public Comment updateComment(Comment comment);

    public void deleteComment(long id);
}
