package com.google.step.datamanager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DealTagManagerDatastore implements DealTagManager {
  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public List<Long> getTagIdsOfDeal(long id) {
    Iterable<Entity> results = getDealTagEntitiesOfDeal(id);
    List<Long> tagIds = new ArrayList<>();
    for (Entity entity : results) {
      long tagId = (long) entity.getProperty("tagId");
      tagIds.add(tagId);
    }
    return tagIds;
  }

  @Override
  public List<Long> getDealIdsWithTag(long id) {
    Query query =
        new Query("DealTag")
            .setFilter(new Query.FilterPredicate("tagId", Query.FilterOperator.EQUAL, id));

    Iterable<Entity> results = datastore.prepare(query).asIterable();
    List<Long> dealIds = new ArrayList<>();
    for (Entity entity : results) {
      long dealId = (long) entity.getProperty("dealId");
      dealIds.add(dealId);
    }
    return dealIds;
  }

  @Override
  public void updateTagsOfDeal(long dealId, List<Long> tagIds) {
    HashSet<Long> newTagIds = new HashSet<>(tagIds);
    Iterable<Entity> results = getDealTagEntitiesOfDeal(dealId);

    // Keeps tags in the new list and remove others
    for (Entity entity : results) {
      long tagId = (long) entity.getProperty("tagId");
      if (newTagIds.contains(tagId)) {
        newTagIds.remove(tagId);
        continue;
      }
      datastore.delete(entity.getKey());
    }

    // Adds remaining tags in the new list
    for (long tagId : newTagIds) {
      Entity entity = createDealTagEntity(dealId, tagId);
      datastore.put(entity);
    }
  }

  /**
   * Returns an iterable list of deal-tag entities with the deal id.
   *
   * @param dealId id of the deal.
   * @return an iterable list of deal-tag entities with the deal id.
   */
  private Iterable<Entity> getDealTagEntitiesOfDeal(long dealId) {
    Query query =
        new Query("DealTag")
            .setFilter(new Query.FilterPredicate("dealId", Query.FilterOperator.EQUAL, dealId));

    Iterable<Entity> results = datastore.prepare(query).asIterable();
    return results;
  }

  /**
   * Returns a deal tag entity with the given deal id and tag id.
   *
   * @param dealId id of the deal.
   * @param tagId id of the tag.
   * @return a deal tag entity with the given deal id and tag id.
   */
  private Entity createDealTagEntity(long dealId, long tagId) {
    Entity entity = new Entity("DealTag");
    entity.setProperty("dealId", dealId);
    entity.setProperty("tagId", tagId);
    return entity;
  }
}
