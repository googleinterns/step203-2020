package com.google.step.datamanager;

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.step.model.Tag;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/** A class handling tag operations. */
public class TagManagerDatastore implements TagManager {

  private final DatastoreService datastore;

  public TagManagerDatastore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @Override
  public Tag readOrCreateTagByName(String name) {
    Query query =
        new Query("Tag")
            .setFilter(new Query.FilterPredicate("name", Query.FilterOperator.EQUAL, name));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();

    if (entity == null) {
      return createTag(name);
    } else {
      return transformEntityToTag(entity);
    }
  }

  @Override
  public Tag readTag(long id) {
    Key key = KeyFactory.createKey("Tag", id);
    Entity tagEntity;
    try {
      tagEntity = datastore.get(key);
    } catch (EntityNotFoundException e) {
      throw new IllegalArgumentException("Tag id does not exist");
    }
    return transformEntityToTag(tagEntity);
  }

  /**
   * Creates a tag entity with the name and returns the tag object.
   *
   * @param name tag name.
   * @return a tag object with the name.
   */
  private Tag createTag(String name) {
    Entity entity = new Entity("Tag");
    entity.setProperty("name", name);
    datastore.put(entity);

    return transformEntityToTag(entity);
  }

  /**
   * Returns a tag object transformed from the entity.
   *
   * @param entity tag entity.
   * @return a tag object transformed from the entity.
   */
  private Tag transformEntityToTag(Entity entity) {
    String name = (String) entity.getProperty("name");
    long id = (long) entity.getKey().getId();

    return new Tag(id, name);
  }

  @Override
  public List<Tag> readTags(List<Long> ids) {
    List<Key> keys =
        ids.stream().map(id -> KeyFactory.createKey("Tag", id)).collect(Collectors.toList());
    Collection<Entity> tagEntities;
    try {
      tagEntities = datastore.get(keys).values();
    } catch (IllegalArgumentException | DatastoreFailureException e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
    List<Tag> tags =
        tagEntities.stream()
            .map(entity -> transformEntityToTag(entity))
            .collect(Collectors.toList());
    return tags;
  }
}
