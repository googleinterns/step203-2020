package com.google.step.datamanager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.step.model.Comment;
import java.util.List;
import java.util.ArrayList;

public class CommentManagerDatastore implements CommentManager {
    
  private final DatastoreService datastore;

  public CommentManagerDatastore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @Override
  public Comment createComment(
    long dealId, 
    long userId, 
    String content) {
    Entity entity = new Entity("Comment");
    entity.setProperty("deal", dealId);
    entity.setProperty("user", userId);
    entity.setProperty("content", content);

    Key key = datastore.put(entity);
    long id = key.getId();

    Comment comment = new Comment(id, dealId, userId, content);

    return comment;
  }

  @Override
  public List<Comment> getComments(long dealId) {
    Filter propertyFilter = new FilterPredicate("deal", FilterOperator.EQUAL, dealId);
    Query query = new Query("Comment").setFilter(propertyFilter);
    PreparedQuery pq = datastore.prepare(query);
    List<Comment> comments = new ArrayList<>();
    for (Entity entity : pq.asIterable()) {
      long id = entity.getKey().getId();
      long userId = (long) entity.getProperty("user"); 
      String content = (String) entity.getProperty("content");
      comments.add(new Comment(id, dealId, userId, content));
    }
    return comments;
  }

  @Override
  public void deleteComment(long id) {
    Key key = KeyFactory.createKey("Comment", id);
    datastore.delete(key);
  }

  @Override
  public Comment updateComment(long id, String content) {
    Key key = KeyFactory.createKey("Comment", id);
    Entity commentEntity;
    try {
      commentEntity = datastore.get(key);
    } catch (EntityNotFoundException e) {
      return null;
    }
    if (content != null) {
      commentEntity.setProperty("content", content);
    }
    datastore.put(commentEntity);
    long dealId = (long) commentEntity.getProperty("deal");
    long userId = (long) commentEntity.getProperty("user");
    return new Comment(id, dealId, userId, content);
  }
}
