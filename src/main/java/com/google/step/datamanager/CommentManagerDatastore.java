package com.google.step.datamanager;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.step.model.Comment;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class CommentManagerDatastore implements CommentManager {

  private static final int PAGE_SIZE = 5;
  private final DatastoreService datastore;

  public CommentManagerDatastore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @Override
  public Comment createComment(long dealId, long userId, String content) {
    Entity entity = new Entity("Comment");

    String timestamp = LocalDateTime.now(ZoneId.of("Asia/Singapore")).toString();
    entity.setProperty("timestamp", timestamp);
    entity.setProperty("deal", dealId);
    entity.setProperty("user", userId);
    entity.setProperty("content", content);

    Key key = datastore.put(entity);
    long id = key.getId();

    return new Comment(id, dealId, userId, content, timestamp);
  }

  @Override
  public CommentsWithToken getCommentsForDeal(long dealId, String token)
      throws IllegalArgumentException {
    Filter propertyFilter = new FilterPredicate("deal", FilterOperator.EQUAL, dealId);

    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(PAGE_SIZE);
    if (token != null) {
      fetchOptions.startCursor(Cursor.fromWebSafeString(token));
    }

    Query query =
        new Query("Comment")
            .setFilter(propertyFilter)
            .addSort("timestamp", SortDirection.DESCENDING);

    PreparedQuery pq = datastore.prepare(query);
    QueryResultList<Entity> results;
    results = pq.asQueryResultList(fetchOptions);

    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results) {
      comments.add(transformEntitytoComment(entity));
    }

    String newToken = results.getCursor().toWebSafeString();

    return new CommentsWithToken(comments, newToken);
  }

  /** Deletes the comment with the given commentId */
  @Override
  public void deleteComment(long id) {
    Key key = KeyFactory.createKey("Comment", id);
    datastore.delete(key);
  }

  /** Updates the comment with the given commentId */
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
    return transformEntitytoComment(commentEntity);
  }

  /**
   * Returns a Comment object transformed from a comment entity.
   *
   * @param entity Comment entity.
   * @return a Comment object transformed from the entity.
   */
  private Comment transformEntitytoComment(Entity commentEntity) {
    long id = commentEntity.getKey().getId();
    long dealId = (long) commentEntity.getProperty("deal");
    long userId = (long) commentEntity.getProperty("user");
    String content = (String) commentEntity.getProperty("content");
    String timestamp = (String) commentEntity.getProperty("timestamp");
    return new Comment(id, dealId, userId, content, timestamp);
  }

  @Override
  public void deleteAllCommentsOfDeal(long dealId) {
    Filter propertyFilter = new FilterPredicate("deal", FilterOperator.EQUAL, dealId);
    Query query = new Query("Comment").setFilter(propertyFilter).setKeysOnly();
    PreparedQuery pq = datastore.prepare(query);
    List<Key> keys = new ArrayList<>();
    pq.asIterable().forEach(entity -> keys.add(entity.getKey()));
    datastore.delete(keys);
  }
}
