package com.google.step.servlets;

import com.google.step.datamanager.CommentManager;
import com.google.step.datamanager.CommentManagerDatastore;
import com.google.step.datamanager.DealManager;
import com.google.step.datamanager.DealManagerDatastore;
import com.google.step.datamanager.DealTagManager;
import com.google.step.datamanager.DealTagManagerDatastore;
import com.google.step.datamanager.RestaurantManager;
import com.google.step.datamanager.RestaurantManagerDatastore;
import com.google.step.model.Deal;
import java.util.List;

public class DeleteHelper {
  private DealManager dealManager = new DealManagerDatastore();
  private RestaurantManager restaurantManager = new RestaurantManagerDatastore();
  private DealTagManager dealTagManager = new DealTagManagerDatastore();
  private CommentManager commentManager = new CommentManagerDatastore();

  public DeleteHelper() {}

  public DeleteHelper(DealManager dealManager, RestaurantManager restaurantManager) {
    this.dealManager = dealManager;
    this.restaurantManager = restaurantManager;
  }

  public void deleteDeal(long id) {
    Deal deal = dealManager.readDeal(id);
    dealManager.deleteDeal(id);
    commentManager.deleteAllCommentsOfDeal(id);
    ImageUploader.deleteImage(deal.photoBlobkey);
    dealTagManager.deleteAllTagsOfDeal(id);
  }

  public void deleteRestaurant(long id) {
    restaurantManager.deleteRestaurant(id);
    // Todo: delete places, followers

    List<Deal> deals = dealManager.getDealsOfRestaurant(id);
    for (Deal deal : deals) {
      deleteDeal(deal.id);
    }
  }
}
