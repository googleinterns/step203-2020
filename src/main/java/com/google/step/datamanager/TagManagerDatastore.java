package com.google.step.datamanager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.step.model.Tag;

/** A class handling tag operations. */
public class TagManagerDatastore implements TagManager {

  private final DatastoreService datastore;

  public TagManagerDatastore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @Override
  public Tag getTag(String name) {
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
      return null;
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

    long id = entity.getKey().getId();
    return new Tag(id, name);
  }

  private Tag transformEntityToTag(Entity entity) {
    String name = (String) entity.getProperty("name");
    long id = (long) entity.getKey().getId();

    return new Tag(id, name);
  }
}
