package com.google.step.datamanager;

import static org.junit.Assert.assertEquals;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class FollowManagerDatastoreTest {

  private static final long USER_ID_A = 123;
  private static final long USER_ID_B = 456;
  private static final long USER_ID_C = 789;

  private static final long RESTAURANT_ID = 111;
  private static final long TAG_ID = 222;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private final FollowManagerDatastore manager = new FollowManagerDatastore();

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testFollow() {
    // test follow no one
    assertEquals(new ArrayList<>(), manager.getFollowedUserIds(USER_ID_A));

    // test follow users
    manager.followUser(USER_ID_A, USER_ID_B);
    manager.followUser(USER_ID_A, USER_ID_C);
    List<Long> list_users = new ArrayList<>();
    list_users.add(USER_ID_B);
    list_users.add(USER_ID_C);
    assertEquals(list_users, manager.getFollowedUserIds(USER_ID_A));

    // test unfollow user
    manager.unfollowUser(USER_ID_A, USER_ID_C);
    list_users.remove((Long) USER_ID_C);
    assertEquals(list_users, manager.getFollowedUserIds(USER_ID_A));

    // test follow restaurant
    manager.followRestaurant(USER_ID_B, RESTAURANT_ID);
    List<Long> list_restaurant = new ArrayList<>();
    list_restaurant.add(RESTAURANT_ID);
    assertEquals(list_restaurant, manager.getFollowedRestaurantIds(USER_ID_B));

    // test unfollow restaurant
    manager.unfollowRestaurant(USER_ID_B, RESTAURANT_ID);
    assertEquals(new ArrayList<>(), manager.getFollowedUserIds(USER_ID_B));

    // test follow tag
    manager.followTag(USER_ID_B, TAG_ID);
    List<Long> list_tag = new ArrayList<>();
    list_tag.add(TAG_ID);
    assertEquals(list_tag, manager.getFollowedTagIds(USER_ID_B));

    // test unfollow tag
    manager.unfollowTag(USER_ID_B, RESTAURANT_ID);
    assertEquals(new ArrayList<>(), manager.getFollowedUserIds(USER_ID_B));
  }
}
