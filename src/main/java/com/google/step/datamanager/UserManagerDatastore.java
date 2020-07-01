package com.google.step.datamanager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.step.model.User;
import java.util.Optional;

/** A data manager handling datastore operations on user. */
public class UserManagerDatastore implements UserManager {

  private final DatastoreService datastore;

  /** Constructs a user manager with a datastore service. */
  public UserManagerDatastore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @Override
  public User readUser(String email) {
    // Checks if the user exists.
    Query query =
        new Query("User")
            .setFilter(new Query.FilterPredicate("email", Query.FilterOperator.EQUAL, email));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity == null) {
      // Creates a user entity if it does not exists.
      return createUser(email);
    } else {
      return transformEntityToUser(entity);
    }
  }

  @Override
  public User createUser(String email) {
    Entity entity = new Entity("User");
    entity.setProperty("email", email);
    // The username is set to be email by default.
    entity.setProperty("username", email);
    entity.setProperty("bio", "");
    Key key = datastore.put(entity);
    long id = key.getId();
    User user = new User(id, email);
    return user;
  }

  @Override
  public User readUser(long id) {
    Key key = KeyFactory.createKey("User", id);
    Entity dealEntity;
    try {
      dealEntity = datastore.get(key);
    } catch (EntityNotFoundException e) {
      return null;
    }
    return transformEntityToUser(dealEntity);
  }

  /**
   * Returns a User object transformed from a user entity.
   *
   * @param entity User entity.
   * @return a User object transformed from the entity.
   */
  private User transformEntityToUser(Entity entity) {
    String email = (String) entity.getProperty("email");
    String username = (String) entity.getProperty("username");
    String bio = (String) entity.getProperty("bio");
    String photoBlobKey = (String) entity.getProperty("photoBlobKey");
    long id = entity.getKey().getId();
    User user;
    if (photoBlobKey != null) {
      user = new User(id, email, username, photoBlobKey, bio);
    } else {
      user = new User(id, email, username, bio);
    }
    return user;
  }

  @Override
  public void updateUser(
      long id, String email, String username, Optional<String> photoBlobKey, String bio) {

    Entity entity = new Entity("User", id);

    entity.setProperty("email", email);
    entity.setProperty("username", username);
    entity.setProperty("bio", bio);
    if (photoBlobKey.isPresent()) {
      entity.setProperty("photoBlobkey", photoBlobKey.get());
    }
    datastore.put(entity);
  }

  @Override
  public void deleteUser(long id) {
    Key key = KeyFactory.createKey("Deal", id);
    datastore.delete(key);
  }
}
