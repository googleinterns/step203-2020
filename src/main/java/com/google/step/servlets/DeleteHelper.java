package com.google.step.servlets;

import com.google.step.datamanager.DealManager;
import com.google.step.datamanager.DealManagerDatastore;
import com.google.step.datamanager.RestaurantManager;
import com.google.step.datamanager.RestaurantManagerDatastore;
import com.google.step.model.Deal;
import java.util.List;

public class DeleteHelper {
  private DealManager dealManager = new DealManagerDatastore();
  private RestaurantManager restaurantManager = new RestaurantManagerDatastore();

  public DeleteHelper(DealManager dealManager, RestaurantManager restaurantManager) {
    this.dealManager = dealManager;
    this.restaurantManager = restaurantManager;
  }

  public void deleteDeal(long id) {
    dealManager.deleteDeal(id);
    // Todo: delete vote,deal-tag, comments,image
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
