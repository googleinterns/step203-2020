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
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
public final class FollowManagerDatastoreTest {

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private final FollowManagerDatastore followManager = new FollowManagerDatastore();

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
    assertTrue(followManager.getFollowedUserIds(USER_ID_A).isEmpty());
  }

  @Test
  public void testGetFollowedRestaurantIdsDefault_noRestaurantIdsReturned() {
    assertTrue(followManager.getFollowedRestaurantIds(USER_ID_A).isEmpty());
  }

  @Test
  public void testGetFollowedTagIdsDefault_noTagIdsReturned() {
    assertTrue(followManager.getFollowedTagIds(USER_ID_A).isEmpty());
  }

  @Test
  public void testFollowUser_returnsAllFollowedUsers() {
    // Act
    followManager.followUser(USER_ID_A, USER_ID_B);
    followManager.followUser(USER_ID_A, USER_ID_C);
    Set<Long> followedUsers = followManager.getFollowedUserIds(USER_ID_A);

    // Assert
    assertEquals(2, followedUsers.size());
    assertThat(followedUsers, hasItems(USER_ID_B, USER_ID_C));
  }

  @Test
  public void testUnfollowUser_unfollowedUsersNotReturned() {
    // Arrange
    followManager.followUser(USER_ID_A, USER_ID_B);
    followManager.followUser(USER_ID_A, USER_ID_C);

    // Act
    followManager.unfollowUser(USER_ID_A, USER_ID_C);
    Set<Long> followedUsers = followManager.getFollowedUserIds(USER_ID_A);

    // Assert
    assertEquals(1, followedUsers.size());
    assertThat(followedUsers, hasItems(USER_ID_B));
  }

  @Test
  public void testFollowRestaurant_returnsAllFollowedRestaurants() {
    // Act
    followManager.followRestaurant(RESTAURANT_ID_A, RESTAURANT_ID_B);
    followManager.followRestaurant(RESTAURANT_ID_A, RESTAURANT_ID_C);
    Set<Long> followedRestaurants = followManager.getFollowedRestaurantIds(RESTAURANT_ID_A);

    // Assert
    assertEquals(2, followedRestaurants.size());
    assertThat(followedRestaurants, hasItems(RESTAURANT_ID_B, RESTAURANT_ID_C));
  }

  @Test
  public void testUnfollowRestaurant_unfollowedRestaurantsNotReturned() {
    // Arrange
    followManager.followRestaurant(RESTAURANT_ID_A, RESTAURANT_ID_B);
    followManager.followRestaurant(RESTAURANT_ID_A, RESTAURANT_ID_C);

    // Act
    followManager.unfollowRestaurant(RESTAURANT_ID_A, RESTAURANT_ID_C);
    Set<Long> followedRestaurants = followManager.getFollowedRestaurantIds(RESTAURANT_ID_A);

    // Assert
    assertEquals(1, followedRestaurants.size());
    assertThat(followedRestaurants, hasItems(RESTAURANT_ID_B));
  }

  @Test
  public void testFollowTag_returnsAllFollowedTags() {
    // Act
    followManager.followTag(TAG_ID_A, TAG_ID_B);
    followManager.followTag(TAG_ID_A, TAG_ID_C);
    Set<Long> followedTags = followManager.getFollowedTagIds(TAG_ID_A);

    // Assert
    assertEquals(2, followedTags.size());
    assertThat(followedTags, hasItems(TAG_ID_B, TAG_ID_C));
  }

  @Test
  public void testUnfollowTag_unfollowedTagsNotReturned() {
    // Arrange
    followManager.followTag(TAG_ID_A, TAG_ID_B);
    followManager.followTag(TAG_ID_A, TAG_ID_C);

    // Act
    followManager.unfollowTag(TAG_ID_A, TAG_ID_C);
    Set<Long> followedTags = followManager.getFollowedTagIds(TAG_ID_A);

    // Assert
    assertEquals(1, followedTags.size());
    assertThat(followedTags, hasItems(TAG_ID_B));
  }

  @Test
  public void testIsFollowingUser() {
    followManager.followUser(USER_ID_A, USER_ID_B);
    followManager.followUser(USER_ID_A, USER_ID_C);
    followManager.followUser(USER_ID_C, USER_ID_A);

    assertTrue(followManager.isFollowingUser(USER_ID_A, USER_ID_B));
    assertFalse(followManager.isFollowingUser(USER_ID_C, USER_ID_B));
    assertFalse(followManager.isFollowingUser(USER_ID_B, USER_ID_A));
  }

  @Test
  public void testIsFollowingRestaurant() {
    followManager.followRestaurant(RESTAURANT_ID_A, RESTAURANT_ID_B);
    followManager.followRestaurant(RESTAURANT_ID_B, RESTAURANT_ID_C);
    followManager.followRestaurant(RESTAURANT_ID_C, RESTAURANT_ID_A);

    assertTrue(followManager.isFollowingRestaurant(RESTAURANT_ID_A, RESTAURANT_ID_B));
    assertFalse(followManager.isFollowingRestaurant(RESTAURANT_ID_C, RESTAURANT_ID_B));
  }

  public void testGetFollowerIdsOfUser() {
    followManager.followUser(USER_ID_A, USER_ID_B);
    followManager.followUser(USER_ID_C, USER_ID_B);
    followManager.followUser(USER_ID_C, USER_ID_A);

    Set<Long> ids = followManager.getFollowerIdsOfUser(USER_ID_B);

    assertThat(ids, containsInAnyOrder(USER_ID_A, USER_ID_C));
    assertTrue(followManager.getFollowerIdsOfUser(USER_ID_C).isEmpty());
  }

  public void testUpdateFollowedTagIds() {
    followManager.followTag(USER_ID_A, TAG_ID_A);
    followManager.followTag(USER_ID_A, TAG_ID_B);

    List<Long> tagIds = Arrays.asList(TAG_ID_A, TAG_ID_C);
    followManager.updateFollowedTagIds(USER_ID_A, tagIds);
    assertThat(followManager.getFollowedTagIds(USER_ID_A), hasItems(TAG_ID_A, TAG_ID_C));

    tagIds = Arrays.asList(TAG_ID_A, TAG_ID_B);
    followManager.updateFollowedTagIds(USER_ID_B, tagIds);
    assertThat(followManager.getFollowedTagIds(USER_ID_B), hasItems(TAG_ID_A, TAG_ID_B));

    assertTrue(followManager.getFollowedTagIds(USER_ID_C).isEmpty());
  }
}
