package com.google.step.datamanager;

import static com.google.step.TestConstants.RESTAURANT_ID_A;
import static com.google.step.TestConstants.RESTAURANT_ID_B;
import static com.google.step.TestConstants.RESTAURANT_ID_C;
import static com.google.step.TestConstants.TAG_ID_A;
import static com.google.step.TestConstants.TAG_ID_B;
import static com.google.step.TestConstants.TAG_ID_C;
import static com.google.step.TestConstants.USER_ID_A;
import static com.google.step.TestConstants.USER_ID_B;
import static com.google.step.TestConstants.USER_ID_C;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
  public void testGetFollowedUserIdsDefault_noUserIdsReturned() {
    assertEquals(new ArrayList<>(), manager.getFollowedUserIds(USER_ID_A));
  }

  @Test
  public void testGetFollowedRestaurantIdsDefault_noRestaurantIdsReturned() {
    assertEquals(new ArrayList<>(), manager.getFollowedRestaurantIds(USER_ID_A));
  }

  @Test
  public void testGetFollowedTagIdsDefault_noTagIdsReturned() {
    assertEquals(new ArrayList<>(), manager.getFollowedTagIds(USER_ID_A));
  }

  @Test
  public void testFollowUser_returnsAllFollowedUsers() {
    // Act
    manager.followUser(USER_ID_A, USER_ID_B);
    manager.followUser(USER_ID_A, USER_ID_C);
    List<Long> followedUsers = manager.getFollowedUserIds(USER_ID_A);

    // Assert
    assertEquals(2, followedUsers.size());
    assertThat(followedUsers, hasItems(USER_ID_B, USER_ID_C));
  }

  @Test
  public void testUnfollowUser_unfollowedUsersNotReturned() {
    // Arrange
    manager.followUser(USER_ID_A, USER_ID_B);
    manager.followUser(USER_ID_A, USER_ID_C);

    // Act
    manager.unfollowUser(USER_ID_A, USER_ID_C);
    List<Long> followedUsers = manager.getFollowedUserIds(USER_ID_A);

    // Assert
    assertEquals(1, followedUsers.size());
    assertThat(followedUsers, hasItems(USER_ID_B));
  }

  @Test
  public void testFollowRestaurant_returnsAllFollowedRestaurants() {
    // Act
    manager.followRestaurant(RESTAURANT_ID_A, RESTAURANT_ID_B);
    manager.followRestaurant(RESTAURANT_ID_A, RESTAURANT_ID_C);
    List<Long> followedRestaurants = manager.getFollowedRestaurantIds(RESTAURANT_ID_A);

    // Assert
    assertEquals(2, followedRestaurants.size());
    assertThat(followedRestaurants, hasItems(RESTAURANT_ID_B, RESTAURANT_ID_C));
  }

  @Test
  public void testUnfollowRestaurant_unfollowedRestaurantsNotReturned() {
    // Arrange
    manager.followRestaurant(RESTAURANT_ID_A, RESTAURANT_ID_B);
    manager.followRestaurant(RESTAURANT_ID_A, RESTAURANT_ID_C);

    // Act
    manager.unfollowRestaurant(RESTAURANT_ID_A, RESTAURANT_ID_C);
    List<Long> followedRestaurants = manager.getFollowedRestaurantIds(RESTAURANT_ID_A);

    // Assert
    assertEquals(1, followedRestaurants.size());
    assertThat(followedRestaurants, hasItems(RESTAURANT_ID_B));
  }

  @Test
  public void testFollowTag_returnsAllFollowedTags() {
    // Act
    manager.followTag(TAG_ID_A, TAG_ID_B);
    manager.followTag(TAG_ID_A, TAG_ID_C);
    List<Long> followedTags = manager.getFollowedTagIds(TAG_ID_A);

    // Assert
    assertEquals(2, followedTags.size());
    assertThat(followedTags, hasItems(TAG_ID_B, TAG_ID_C));
  }

  @Test
  public void testUnfollowTag_unfollowedTagsNotReturned() {
    // Arrange
    manager.followTag(TAG_ID_A, TAG_ID_B);
    manager.followTag(TAG_ID_A, TAG_ID_C);

    // Act
    manager.unfollowTag(TAG_ID_A, TAG_ID_C);
    List<Long> followedTags = manager.getFollowedTagIds(TAG_ID_A);

    // Assert
    assertEquals(1, followedTags.size());
    assertThat(followedTags, hasItems(TAG_ID_B));
  }

  @Test
  public void testIsFollowingUser() {
    manager.followUser(USER_ID_A, USER_ID_B);
    manager.followUser(USER_ID_A, USER_ID_C);
    manager.followUser(USER_ID_C, USER_ID_A);

    assertTrue(manager.isFollowingUser(USER_ID_A, USER_ID_B));
    assertFalse(manager.isFollowingUser(USER_ID_C, USER_ID_B));
    assertFalse(manager.isFollowingUser(USER_ID_B, USER_ID_A));
  }

  @Test
  public void testIsFollowingRestaurant() {
    manager.followRestaurant(RESTAURANT_ID_A, RESTAURANT_ID_B);
    manager.followRestaurant(RESTAURANT_ID_B, RESTAURANT_ID_C);
    manager.followRestaurant(RESTAURANT_ID_C, RESTAURANT_ID_A);

    assertTrue(manager.isFollowingRestaurant(RESTAURANT_ID_A, RESTAURANT_ID_B));
    assertFalse(manager.isFollowingRestaurant(RESTAURANT_ID_C, RESTAURANT_ID_B));
  }
}
