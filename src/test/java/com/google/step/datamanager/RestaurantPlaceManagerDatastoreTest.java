package com.google.step.datamanager;

import static com.google.step.TestConstants.PLACE_ID_A;
import static com.google.step.TestConstants.PLACE_ID_B;
import static com.google.step.TestConstants.PLACE_ID_C;
import static com.google.step.TestConstants.PLACE_ID_D;
import static com.google.step.TestConstants.RESTAURANT_ID_A;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class RestaurantPlaceManagerDatastoreTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  private final RestaurantPlaceManagerDatastore restaurantPlaceManager =
      new RestaurantPlaceManagerDatastore();

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testUpdatePlacesOfRestaurant() {
    List<String> placeIds = Arrays.asList(PLACE_ID_A, PLACE_ID_B);
    restaurantPlaceManager.updatePlacesOfRestaurant(RESTAURANT_ID_A, placeIds);

    Set<String> actual = restaurantPlaceManager.getPlaceIdsOfRestaurant(RESTAURANT_ID_A);
    assertThat(actual, containsInAnyOrder(PLACE_ID_A, PLACE_ID_B));
  }

  @Test
  public void testUpdatePlacesOfRestaurant_morePlaceIds() {
    List<String> placeIds = Arrays.asList(PLACE_ID_A, PLACE_ID_B);
    restaurantPlaceManager.updatePlacesOfRestaurant(RESTAURANT_ID_A, placeIds);
    placeIds = Arrays.asList(PLACE_ID_A, PLACE_ID_C, PLACE_ID_D);
    restaurantPlaceManager.updatePlacesOfRestaurant(RESTAURANT_ID_A, placeIds);

    Set<String> actual = restaurantPlaceManager.getPlaceIdsOfRestaurant(RESTAURANT_ID_A);
    assertThat(actual, containsInAnyOrder(PLACE_ID_A, PLACE_ID_C, PLACE_ID_D));
  }
}
