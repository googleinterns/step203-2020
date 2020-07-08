package com.google.step.datamanager;

import static com.google.step.TestConstants.BLOBKEY_A;
import static com.google.step.TestConstants.DATE_A;
import static com.google.step.TestConstants.DATE_B;
import static com.google.step.TestConstants.DESCRIPTION_A;
import static com.google.step.TestConstants.RESTAURANT_ID_A;
import static com.google.step.TestConstants.RESTAURANT_ID_B;
import static com.google.step.TestConstants.SOURCE_A;
import static com.google.step.TestConstants.TAG_ID_A;
import static com.google.step.TestConstants.TAG_ID_B;
import static com.google.step.TestConstants.USER_ID_A;
import static com.google.step.TestConstants.USER_ID_B;
import static com.google.step.TestConstants.USER_ID_C;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.step.model.Deal;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class DealManagerDatastoreMoreTest {

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private final FollowManager followManager = new FollowManagerDatastore();
  private final VoteManager voteManager = mock(VoteManager.class);
  private final DealTagManager dealTagManager = new DealTagManagerDatastore();
  private final DealSearchManager dealSearchManager = mock(DealSearchManager.class);
  private final DealManager dealManager =
      new DealManagerDatastore(followManager, dealTagManager, voteManager, dealSearchManager);

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testGetDealPublishedByFollowedUsers_success() {
    // Add deals published by USER_ID_A and USER_ID_B
    Deal dealA =
        dealManager.createDeal(
            DESCRIPTION_A, BLOBKEY_A, DATE_A, DATE_B, SOURCE_A, USER_ID_A, RESTAURANT_ID_A);
    Deal dealB =
        dealManager.createDeal(
            DESCRIPTION_A, BLOBKEY_A, DATE_A, DATE_B, SOURCE_A, USER_ID_B, RESTAURANT_ID_A);
    Deal dealC =
        dealManager.createDeal(
            DESCRIPTION_A, BLOBKEY_A, DATE_A, DATE_B, SOURCE_A, USER_ID_C, RESTAURANT_ID_A);

    // User_ID_A follows USER_ID_B and USER_ID_C, USER_ID_B follows USER_ID_A
    followManager.followUser(USER_ID_A, USER_ID_B);
    followManager.followUser(USER_ID_A, USER_ID_C);
    followManager.followUser(USER_ID_B, USER_ID_A);

    // Get deals published by the users followed by USER_ID_A
    List<Deal> dealsForA = dealManager.getDealsPublishedByFollowedUsers(USER_ID_A);
    assertEquals(2, dealsForA.size());
    assertThat(dealsForA.get(0), samePropertyValuesAs(dealB));
    assertThat(dealsForA.get(1), samePropertyValuesAs(dealC));

    // Get deals published by the users followed by USER_ID_B
    List<Deal> dealsB = dealManager.getDealsPublishedByFollowedUsers(USER_ID_B);
    assertEquals(1, dealsB.size());
    assertThat(dealsB.get(0), samePropertyValuesAs(dealA));
  }

  @Test
  public void testGetDealPublishedByFollowedRestaurants_success() {
    // Add deals published by RESTAURANT_ID_A and RESTAURANT_ID_B
    Deal dealA =
        dealManager.createDeal(
            DESCRIPTION_A, BLOBKEY_A, DATE_A, DATE_B, SOURCE_A, USER_ID_A, RESTAURANT_ID_A);
    Deal dealB =
        dealManager.createDeal(
            DESCRIPTION_A, BLOBKEY_A, DATE_A, DATE_B, SOURCE_A, USER_ID_A, RESTAURANT_ID_B);

    // USER_ID_B follows RESTAURANT_ID_A and RESTAURANT_ID_B
    followManager.followRestaurant(USER_ID_B, RESTAURANT_ID_A);
    followManager.followRestaurant(USER_ID_B, RESTAURANT_ID_B);

    // Get deals published by the restaurants followed by USER_ID_B
    List<Deal> deals = dealManager.getDealsPublishedByFollowedRestaurants(USER_ID_B);
    assertEquals(2, deals.size());
    assertThat(deals.get(0), samePropertyValuesAs(dealA));
    assertThat(deals.get(1), samePropertyValuesAs(dealB));
  }

  @Test
  public void testGetDealPublishedByFollowedTags_success() {
    // Add deals published by RESTAURANT_ID_A and RESTAURANT_ID_B
    Deal dealA =
        dealManager.createDeal(
            DESCRIPTION_A, BLOBKEY_A, DATE_A, DATE_B, SOURCE_A, USER_ID_A, RESTAURANT_ID_A);
    Deal dealB =
        dealManager.createDeal(
            DESCRIPTION_A, BLOBKEY_A, DATE_A, DATE_B, SOURCE_A, USER_ID_A, RESTAURANT_ID_B);

    // Add tags to the deals
    dealTagManager.updateTagsOfDeal(dealA.id, Arrays.asList(TAG_ID_A, TAG_ID_B));
    dealTagManager.updateTagsOfDeal(dealB.id, Arrays.asList(TAG_ID_B));

    // USER_ID_A follows TAG_ID_A and TAG_ID_B
    followManager.followTag(USER_ID_A, TAG_ID_A);
    followManager.followTag(USER_ID_A, TAG_ID_B);

    // USER_ID_B follows TAG_ID_A
    followManager.followTag(USER_ID_B, TAG_ID_A);

    // USER_ID_C follows TAG_ID_B
    followManager.followTag(USER_ID_C, TAG_ID_B);

    // Get deals published by the tags followed by USER_ID_A
    List<Deal> dealsForA = dealManager.getDealsPublishedByFollowedTags(USER_ID_A);
    assertEquals(2, dealsForA.size());
    assertThat(dealsForA.get(0), samePropertyValuesAs(dealA));
    assertThat(dealsForA.get(1), samePropertyValuesAs(dealB));

    // Get deals published by the tags followed by USER_ID_B
    List<Deal> dealsForB = dealManager.getDealsPublishedByFollowedTags(USER_ID_B);
    assertEquals(1, dealsForB.size());
    assertThat(dealsForB.get(0), samePropertyValuesAs(dealA));

    // Get deals published by the tags followed by USER_ID_C
    List<Deal> dealsForC = dealManager.getDealsPublishedByFollowedTags(USER_ID_C);
    assertEquals(2, dealsForC.size());
    assertThat(dealsForC.get(0), samePropertyValuesAs(dealA));
    assertThat(dealsForC.get(1), samePropertyValuesAs(dealB));
  }

  @Test
  public void testSortDealsByVotes_success() {}
}
