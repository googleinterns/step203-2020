package com.google.step.datamanager;

import static com.google.step.TestConstants.RESTAURANT_ID_A;
import static com.google.step.TestConstants.RESTAURANT_ID_B;
import static com.google.step.TestConstants.RESTAURANT_ID_C;
import static com.google.step.TestConstants.USER_ID_A;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class UserManagesRestaurantManagerTest {

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig().setApplyAllHighRepJobPolicy());

  private final UserManagesRestaurantManagerDatastore userManagesRestaurantManagerDatastore =
      new UserManagesRestaurantManagerDatastore();

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testAddUserManagesRestaurant() {
    userManagesRestaurantManagerDatastore.addUserManagesRestaurant(USER_ID_A, RESTAURANT_ID_A);
    userManagesRestaurantManagerDatastore.addUserManagesRestaurant(USER_ID_A, RESTAURANT_ID_B);
    List<Long> managersOfA =
        userManagesRestaurantManagerDatastore.getManagerIdsOfRestaurant(RESTAURANT_ID_A);

    Long[] actual = managersOfA.toArray(new Long[0]);
    Long[] expected = {USER_ID_A};

    assertArrayEquals(expected, actual);

    List<Long> restaurantsManagedByA =
        userManagesRestaurantManagerDatastore.getRestaurantIdsManagedBy(USER_ID_A);
    actual = restaurantsManagedByA.toArray(new Long[0]);
    expected = new Long[] {RESTAURANT_ID_A, RESTAURANT_ID_B};
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testAddUserManagesRestaurant_duplicateEntry() {
    userManagesRestaurantManagerDatastore.addUserManagesRestaurant(USER_ID_A, RESTAURANT_ID_A);
    userManagesRestaurantManagerDatastore.addUserManagesRestaurant(USER_ID_A, RESTAURANT_ID_A);
    userManagesRestaurantManagerDatastore.addUserManagesRestaurant(USER_ID_A, RESTAURANT_ID_B);
    List<Long> managersOfA =
        userManagesRestaurantManagerDatastore.getManagerIdsOfRestaurant(RESTAURANT_ID_A);

    Long[] actual = managersOfA.toArray(new Long[0]);
    Long[] expected = {USER_ID_A};

    assertArrayEquals(expected, actual);

    List<Long> restaurantsManagedByA =
        userManagesRestaurantManagerDatastore.getRestaurantIdsManagedBy(USER_ID_A);
    actual = restaurantsManagedByA.toArray(new Long[0]);
    expected = new Long[] {RESTAURANT_ID_A, RESTAURANT_ID_B};
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testDeleteUserManagesRestaurant() {
    userManagesRestaurantManagerDatastore.addUserManagesRestaurant(USER_ID_A, RESTAURANT_ID_A);
    userManagesRestaurantManagerDatastore.addUserManagesRestaurant(USER_ID_A, RESTAURANT_ID_B);
    userManagesRestaurantManagerDatastore.deleteUserManagesRestaurant(USER_ID_A, RESTAURANT_ID_B);

    List<Long> managersOfB =
        userManagesRestaurantManagerDatastore.getManagerIdsOfRestaurant(RESTAURANT_ID_B);
    assertTrue(managersOfB.isEmpty());

    List<Long> restaurantsManagedByA =
        userManagesRestaurantManagerDatastore.getRestaurantIdsManagedBy(USER_ID_A);
    Long[] actual = restaurantsManagedByA.toArray(new Long[0]);
    Long[] expected = new Long[] {RESTAURANT_ID_A};
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testDeleteUserManagesRestaurant_doesNotExist() {
    userManagesRestaurantManagerDatastore.addUserManagesRestaurant(USER_ID_A, RESTAURANT_ID_A);
    userManagesRestaurantManagerDatastore.addUserManagesRestaurant(USER_ID_A, RESTAURANT_ID_B);
    userManagesRestaurantManagerDatastore.deleteUserManagesRestaurant(USER_ID_A, RESTAURANT_ID_C);

    List<Long> managersOfA =
        userManagesRestaurantManagerDatastore.getManagerIdsOfRestaurant(RESTAURANT_ID_A);

    Long[] actual = managersOfA.toArray(new Long[0]);
    Long[] expected = {USER_ID_A};

    assertArrayEquals(expected, actual);

    List<Long> restaurantsManagedByA =
        userManagesRestaurantManagerDatastore.getRestaurantIdsManagedBy(USER_ID_A);
    actual = restaurantsManagedByA.toArray(new Long[0]);
    expected = new Long[] {RESTAURANT_ID_A, RESTAURANT_ID_B};
    assertArrayEquals(expected, actual);
  }
}
