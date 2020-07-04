package com.google.step.datamanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.step.model.Restaurant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.ArrayList;

@RunWith(JUnit4.class)
public final class RestaurantManagerDatastoreTest {

  private static final String RESTAURANT_NAME_A = "A";
  private static final String BLOBKEY_A = "a_blob_key";
  

  private static final String RESTAURANT_NAME_B = "B";
  private static final String BLOBKEY_B = "b_blob_key";

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig().setApplyAllHighRepJobPolicy());

  private final RestaurantManagerDatastore restaurantManagerDatastore = new RestaurantManagerDatastore();

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testCreateRestaurant() {
    Restaurant restaurant = restaurantManagerDatastore.createRestaurant(RESTAURANT_NAME_A, BLOBKEY_A);
    assertEquals(RESTAURANT_NAME_A, restaurant.name);
    assertEquals(BLOBKEY_A, restaurant.photoBlobkey);
  }

  @Test
  public void testReadRestaurant() {
    Restaurant restaurant_A = restaurantManagerDatastore.createRestaurant(RESTAURANT_NAME_A, BLOBKEY_A);
    Restaurant restaurant_A_Test = restaurantManagerDatastore.readRestaurant(restaurant_A.id);
    assertEquals(RESTAURANT_NAME_A, restaurant_A_Test.name);
    assertEquals(BLOBKEY_A, restaurant_A_Test.photoBlobkey);
  }

  @Test
  public void testUpdateRestaurantName() {
    Restaurant restaurant_A = restaurantManagerDatastore.createRestaurant(RESTAURANT_NAME_A, BLOBKEY_A);
    Restaurant restaurant_A_New = new Restaurant(restaurant_A.id, RESTAURANT_NAME_B, BLOBKEY_A);
    Restaurant restaurant_A_Updated = restaurantManagerDatastore.updateRestaurant(restaurant_A_New);
    assertEquals(RESTAURANT_NAME_B, restaurant_A_Updated.name);
    assertEquals(BLOBKEY_A, restaurant_A_Updated.photoBlobkey);
  }

  @Test
  public void testDeleteRestaurant() {
    Restaurant restaurant_A = restaurantManagerDatastore.createRestaurant(RESTAURANT_NAME_A, BLOBKEY_A);
    restaurantManagerDatastore.deleteRestaurant(restaurant_A.id);
    assertEquals(null, restaurantManagerDatastore.readRestaurant(restaurant_A.id));
  }
}
