package com.google.step.datamanager;

import static com.google.step.TestConstants.BLOBKEY_A;
import static com.google.step.TestConstants.BLOBKEY_B;
import static com.google.step.TestConstants.RESTAURANT_ID_C;
import static com.google.step.TestConstants.RESTAURANT_NAME_A;
import static com.google.step.TestConstants.RESTAURANT_NAME_B;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.step.model.Restaurant;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class RestaurantManagerDatastoreTest {

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig().setApplyAllHighRepJobPolicy());

  private final RestaurantManagerDatastore restaurantManagerDatastore =
      new RestaurantManagerDatastore();

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testCreateRestaurant_success() throws Exception {
    Restaurant restaurant =
        restaurantManagerDatastore.createRestaurant(RESTAURANT_NAME_A, BLOBKEY_A);

    assertEquals(RESTAURANT_NAME_A, restaurant.name);
    assertEquals(BLOBKEY_A, restaurant.photoBlobkey);
  }

  @Test
  public void testReadRestaurant_success() throws Exception {
    Restaurant restaurantA =
        restaurantManagerDatastore.createRestaurant(RESTAURANT_NAME_A, BLOBKEY_A);
    Restaurant restaurantA_Test = restaurantManagerDatastore.readRestaurant(restaurantA.id);

    assertEquals(RESTAURANT_NAME_A, restaurantA_Test.name);
    assertEquals(BLOBKEY_A, restaurantA_Test.photoBlobkey);
  }

  @Test
  public void testReadRestaurant_doesNotExist() throws Exception {
    Restaurant restaurant = restaurantManagerDatastore.readRestaurant(1000);

    assertNull(restaurant);
  }

  @Test
  public void testUpdateRestaurant_name() throws Exception {
    Restaurant restaurantA =
        restaurantManagerDatastore.createRestaurant(RESTAURANT_NAME_A, BLOBKEY_A);
    Restaurant restaurantA_New = new Restaurant(restaurantA.id, RESTAURANT_NAME_B, null);
    Restaurant restaurantA_Updated = restaurantManagerDatastore.updateRestaurant(restaurantA_New);

    assertEquals(RESTAURANT_NAME_B, restaurantA_Updated.name);
    assertEquals(BLOBKEY_A, restaurantA_Updated.photoBlobkey);
  }

  @Test
  public void testUpdateRestaurant_blobKey() throws Exception {
    Restaurant restaurantA =
        restaurantManagerDatastore.createRestaurant(RESTAURANT_NAME_A, BLOBKEY_A);
    Restaurant restaurantA_New = new Restaurant(restaurantA.id, null, BLOBKEY_B);
    Restaurant restaurantA_Updated = restaurantManagerDatastore.updateRestaurant(restaurantA_New);

    assertEquals(RESTAURANT_NAME_A, restaurantA_Updated.name);
    assertEquals(BLOBKEY_B, restaurantA_Updated.photoBlobkey);
  }

  @Test
  public void testDeleteRestaurant() throws Exception {
    Restaurant restaurantA =
        restaurantManagerDatastore.createRestaurant(RESTAURANT_NAME_A, BLOBKEY_A);
    restaurantManagerDatastore.deleteRestaurant(restaurantA.id);

    assertNull(restaurantManagerDatastore.readRestaurant(restaurantA.id));
  }

  @Test
  public void testReadRestaurants() {
    Restaurant restaurantA =
        restaurantManagerDatastore.createRestaurant(RESTAURANT_NAME_A, BLOBKEY_A);
    Restaurant restaurantB =
        restaurantManagerDatastore.createRestaurant(RESTAURANT_NAME_B, BLOBKEY_B);
    List<Long> ids = Arrays.asList(restaurantA.id, restaurantB.id);
    List<Restaurant> users = restaurantManagerDatastore.readRestaurants(ids);

    assertThat(users, containsInAnyOrder(restaurantA, restaurantB));
  }

  @Test
  public void testReadRestaurants_idDoesNotExist() {
    Restaurant restaurantA =
        restaurantManagerDatastore.createRestaurant(RESTAURANT_NAME_A, BLOBKEY_A);
    Restaurant restaurantB =
        restaurantManagerDatastore.createRestaurant(RESTAURANT_NAME_B, BLOBKEY_B);
    List<Long> ids = Arrays.asList(restaurantA.id, restaurantB.id, RESTAURANT_ID_C);
    List<Restaurant> users = restaurantManagerDatastore.readRestaurants(ids);

    assertThat(users, containsInAnyOrder(restaurantA, restaurantB));
  }

  public void testSearchRestaurant() {
    Restaurant restaurantA = restaurantManagerDatastore.createRestaurant("abcde", BLOBKEY_A);
    Restaurant restaurantB = restaurantManagerDatastore.createRestaurant("abxyz", BLOBKEY_A);
    Restaurant restaurantC = restaurantManagerDatastore.createRestaurant("aqqq", BLOBKEY_A);

    List<Restaurant> restaurants = restaurantManagerDatastore.searchRestaurants("ab");

    assertEquals(2, restaurants.size());
    assertThat(restaurants, hasItems(restaurantA, restaurantB));
    assertThat(restaurants, not(hasItem(restaurantC)));
  }

  @Test
  public void testSearchRestaurant_caseInsensitive() {
    Restaurant restaurantA = restaurantManagerDatastore.createRestaurant("AbCdE", BLOBKEY_A);

    List<Restaurant> restaurants = restaurantManagerDatastore.searchRestaurants("abcde");

    assertEquals(1, restaurants.size());
    assertThat(restaurants, hasItem(restaurantA));
  }
}
