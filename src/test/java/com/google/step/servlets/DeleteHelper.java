package com.google.step.servlets;

import com.google.step.datamanager.CommentManager;
import com.google.step.datamanager.CommentManagerDatastore;
import com.google.step.datamanager.DealManager;
import com.google.step.datamanager.DealManagerDatastore;
import com.google.step.datamanager.DealTagManager;
import com.google.step.datamanager.DealTagManagerDatastore;
import com.google.step.datamanager.RestaurantManager;
import com.google.step.datamanager.RestaurantManagerDatastore;
import com.google.step.datamanager.VoteManager;
import com.google.step.datamanager.VoteManagerDatastore;
import com.google.step.model.Deal;
import com.google.step.model.Restaurant;

public class DeleteHelper {
  private DealManager dealManager = new DealManagerDatastore();
  private VoteManager voteManager = new VoteManagerDatastore();
  private DealTagManager dealTagManager = new DealTagManagerDatastore();
  private CommentManager commentManager = new CommentManagerDatastore();
  private RestaurantManager restaurantManager = new RestaurantManagerDatastore();

  public void deleteDeal(long id) {
    Deal deal = dealManager.readDeal(id);
    dealManager.deleteDeal(id);
    ImageUploader.deleteImage(deal.photoBlobkey);
    // Todo: delete vote,deal-tag, comments
  }

  public void deleteRestaurant(long id) {
    Restaurant restaurant = restaurantManager.readRestaurant(id);
    restaurantManager.deleteRestaurant(id);
    // Todo: delete image

  }
}
