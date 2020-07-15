package com.google.step.datamanager;

import static com.google.step.servlets.ImageUploader.deleteImage;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.step.model.User;
import java.util.ArrayList;
import java.util.List;

/** A data manager handling datastore operations on user. */
public class UserManagerDatastore implements UserManager {

  private final DatastoreService datastore;

  /** Constructs a user manager with a datastore service. */
  public UserManagerDatastore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @Override
  public User readUserByEmail(String email) throws IllegalArgumentException {
    // Checks if the user exists.
    Query query =
        new Query("User")
            .setFilter(new Query.FilterPredicate("email", Query.FilterOperator.EQUAL, email));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity == null) {
      throw new IllegalArgumentException("User does not exist");
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
  public User readUser(long id) throws IllegalArgumentException {
    Entity entity = getUserEntity(id);
    return transformEntityToUser(entity);
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

  /**
   * Returns a user entity in datastore with the id.
   *
   * @param id id of the user entity.
   * @return a user entity in datastore with the id.
   * @throws IllegalArgumentException if the id does not exist.
   */
  private Entity getUserEntity(long id) throws IllegalArgumentException {
    Key key = KeyFactory.createKey("User", id);
    Entity userEntity;
    try {
      userEntity = datastore.get(key);
    } catch (EntityNotFoundException e) {
      throw new IllegalArgumentException("User with id: " + id + " does not exist.");
    }
    return userEntity;
  }

  @Override
  public void updateUser(User user) throws IllegalArgumentException {
    Entity entity = getUserEntity(user.id);
    if (user.username != null) {
      entity.setProperty("username", user.username);
    }
    if (user.bio != null) {
      entity.setProperty("bio", user.bio);
    }
    if (user.photoBlobKey != null) {
      discardImageOfUserEntity(entity);
      if (user.photoBlobKey.isPresent()) {
        entity.setProperty("photoBlobKey", user.photoBlobKey.get());
      } else {
        entity.removeProperty("photoBlobKey");
      }
    }
    datastore.put(entity);
  }

  @Override
  public void deleteUser(long id) {
    Entity entity = getUserEntity(id);
    discardImageOfUserEntity(entity);
    datastore.delete(entity.getKey());
  }

  /**
   * Discards the image of the user entity. If the user entity has a blob key, remove the file
   * identified by the blob key.
   *
   * @param entity the user entity.
   */
  private void discardImageOfUserEntity(Entity entity) {
    if (entity.hasProperty("photoBlobKey")) {
      deleteImage((String) entity.getProperty("photoBlobKey"));
    }
  }

  @Override
  public List<User> readUsers(List<Long> ids) {
    List<User> users = new ArrayList<>();
    for (long id : ids) {
      try {
        users.add(readUser(id));
      } catch (IllegalArgumentException e) {
        continue;
      }
    }
    return users;
  }
}
