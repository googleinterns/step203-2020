package com.google.step.datamanager;

import static com.google.step.TestConstants.BLOBKEY_A;
import static com.google.step.TestConstants.BLOBKEY_B;
import static com.google.step.TestConstants.BLOBKEY_C;
import static com.google.step.TestConstants.DATE_A;
import static com.google.step.TestConstants.DATE_B;
import static com.google.step.TestConstants.DATE_C;
import static com.google.step.TestConstants.DATE_D;
import static com.google.step.TestConstants.DEAL_A;
import static com.google.step.TestConstants.DEAL_ID_A;
import static com.google.step.TestConstants.DESCRIPTION_A;
import static com.google.step.TestConstants.DESCRIPTION_B;
import static com.google.step.TestConstants.DESCRIPTION_C;
import static com.google.step.TestConstants.RESTAURANT_ID_A;
import static com.google.step.TestConstants.RESTAURANT_ID_B;
import static com.google.step.TestConstants.RESTAURANT_ID_C;
import static com.google.step.TestConstants.SOURCE_A;
import static com.google.step.TestConstants.SOURCE_B;
import static com.google.step.TestConstants.SOURCE_C;
import static com.google.step.TestConstants.TAG_ID_A;
import static com.google.step.TestConstants.TAG_ID_B;
import static com.google.step.TestConstants.TAG_NAME_A;
import static com.google.step.TestConstants.USER_ID_A;
import static com.google.step.TestConstants.USER_ID_B;
import static com.google.step.TestConstants.USER_ID_C;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.step.model.Deal;
import com.google.step.model.Tag;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class DealManagerDatastoreTest {

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private final VoteManager voteManager = new VoteManagerDatastore();
  private final DealTagManager dealTagManager = new DealTagManagerDatastore();
  private static final List<String> EMPTY_LIST = new ArrayList<>();
  private static final List<String> TAG_LIST = Arrays.asList(TAG_NAME_A);

  private final DealSearchManager mockSearchManager = mock(DealSearchManager.class);
  private final DealManager dealManagerDatastore =
      new DealManagerDatastore(dealTagManager, voteManager, mockSearchManager);

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testCreate_success() {
    Deal deal =
        dealManagerDatastore.createDeal(
            DESCRIPTION_A,
            BLOBKEY_A,
            DATE_A,
            DATE_B,
            SOURCE_A,
            USER_ID_A,
            RESTAURANT_ID_A,
            EMPTY_LIST);
    assertEquals(DEAL_A, deal);
  }

  @Test
  public void testRead_success() {
    Deal createdDeal =
        dealManagerDatastore.createDeal(
            DESCRIPTION_A,
            BLOBKEY_A,
            DATE_A,
            DATE_B,
            SOURCE_A,
            USER_ID_A,
            RESTAURANT_ID_A,
            EMPTY_LIST);
    Deal deal = dealManagerDatastore.readDeal(createdDeal.id);
    assertEquals(DEAL_A, deal);
  }

  @Test
  public void testRead_invalidId_returnsNull() {
    Deal deal = dealManagerDatastore.readDeal(DEAL_ID_A);
    assertNull(deal);
  }

  @Test
  public void testDelete() {
    Deal createdDeal =
        dealManagerDatastore.createDeal(
            DESCRIPTION_A,
            BLOBKEY_A,
            DATE_A,
            DATE_B,
            SOURCE_A,
            USER_ID_A,
            RESTAURANT_ID_A,
            EMPTY_LIST);
    dealManagerDatastore.deleteDeal(createdDeal.id);
    Deal deal = dealManagerDatastore.readDeal(createdDeal.id);
    assertNull(deal);
  }

  @Test
  public void testUpdate_invalidId_returnsNull() {
    Deal deal = new Deal(DEAL_ID_A, null, null, null, null, null, -1, -1, null);
    Deal updatedDeal = dealManagerDatastore.updateDeal(deal, null);
    assertNull(updatedDeal);
  }

  @Test
  public void testUpdate_descriptionOnly() {
    Deal createdDeal =
        dealManagerDatastore.createDeal(
            DESCRIPTION_A,
            BLOBKEY_A,
            DATE_A,
            DATE_B,
            SOURCE_A,
            USER_ID_A,
            RESTAURANT_ID_A,
            new ArrayList<>());
    Deal dealToUpdate =
        new Deal(createdDeal.id, DESCRIPTION_B, null, null, null, null, -1, -1, null);
    Deal updatedDeal = dealManagerDatastore.updateDeal(dealToUpdate, null);

    // only description should change, everything else should remain
    Deal expected =
        new Deal(
            DEAL_ID_A,
            DESCRIPTION_B,
            BLOBKEY_A,
            DATE_A,
            DATE_B,
            SOURCE_A,
            USER_ID_A,
            RESTAURANT_ID_A,
            null);
    assertEquals(expected, updatedDeal);
  }

  @Test
  public void testUpdate_poster_noChange() {
    // method should not allow updating of poster
    Deal createdDeal =
        dealManagerDatastore.createDeal(
            DESCRIPTION_A,
            BLOBKEY_A,
            DATE_A,
            DATE_B,
            SOURCE_A,
            USER_ID_A,
            RESTAURANT_ID_A,
            new ArrayList<>());
    Deal dealToUpdate = new Deal(createdDeal.id, null, null, null, null, null, USER_ID_B, -1, null);
    Deal updatedDeal = dealManagerDatastore.updateDeal(dealToUpdate, null);
    assertEquals(DEAL_A, updatedDeal);
  }

  @Test
  public void testUpdate_allFields() {
    Deal createdDeal =
        dealManagerDatastore.createDeal(
            DESCRIPTION_A,
            BLOBKEY_A,
            DATE_A,
            DATE_B,
            SOURCE_A,
            USER_ID_A,
            RESTAURANT_ID_A,
            EMPTY_LIST);
    Deal dealToUpdate =
        new Deal(
            createdDeal.id,
            DESCRIPTION_B,
            BLOBKEY_B,
            DATE_C,
            DATE_D,
            SOURCE_B,
            -1,
            RESTAURANT_ID_B,
            null);
    Deal updatedDeal = dealManagerDatastore.updateDeal(dealToUpdate, TAG_LIST);

    Deal expected =
        new Deal(
            createdDeal.id,
            DESCRIPTION_B,
            BLOBKEY_B,
            DATE_C,
            DATE_D,
            SOURCE_B,
            USER_ID_A,
            RESTAURANT_ID_B,
            null);
    assertEquals(expected, updatedDeal);
  }

  @Test
  public void testGetDealPublishedByFollowedUsers_success() {
    // Add deals published by USER_ID_A and USER_ID_B
    Deal dealA =
        dealManagerDatastore.createDeal(
            DESCRIPTION_A,
            BLOBKEY_A,
            DATE_A,
            DATE_B,
            SOURCE_A,
            USER_ID_A,
            RESTAURANT_ID_A,
            EMPTY_LIST);
    Deal dealB =
        dealManagerDatastore.createDeal(
            DESCRIPTION_A,
            BLOBKEY_A,
            DATE_A,
            DATE_B,
            SOURCE_A,
            USER_ID_B,
            RESTAURANT_ID_A,
            EMPTY_LIST);
    Deal dealC =
        dealManagerDatastore.createDeal(
            DESCRIPTION_A,
            BLOBKEY_A,
            DATE_A,
            DATE_B,
            SOURCE_A,
            USER_ID_C,
            RESTAURANT_ID_A,
            EMPTY_LIST);

    // Get deals published by the users followed by USER_ID_A
    List<Deal> dealsForA =
        dealManagerDatastore.getDealsPublishedByFollowedUsers(Arrays.asList(USER_ID_B, USER_ID_C));
    assertEquals(2, dealsForA.size());
    assertEquals(dealsForA.get(0), dealB);
    assertEquals(dealsForA.get(1), dealC);

    // Get deals published by the users followed by USER_ID_B
    List<Deal> dealsB =
        dealManagerDatastore.getDealsPublishedByFollowedUsers(Arrays.asList(USER_ID_A));
    assertEquals(1, dealsB.size());
    assertEquals(dealsB.get(0), dealA);
  }

  @Test
  public void testGetDealPublishedByFollowedRestaurants_success() {
    // Add deals published by RESTAURANT_ID_A and RESTAURANT_ID_B
    Deal dealA =
        dealManagerDatastore.createDeal(
            DESCRIPTION_A,
            BLOBKEY_A,
            DATE_A,
            DATE_B,
            SOURCE_A,
            USER_ID_A,
            RESTAURANT_ID_A,
            EMPTY_LIST);
    Deal dealB =
        dealManagerDatastore.createDeal(
            DESCRIPTION_A,
            BLOBKEY_A,
            DATE_A,
            DATE_B,
            SOURCE_A,
            USER_ID_A,
            RESTAURANT_ID_B,
            EMPTY_LIST);

    // Get deals published by the restaurants followed by USER_ID_B
    List<Deal> deals =
        dealManagerDatastore.getDealsPublishedByFollowedRestaurants(
            Arrays.asList(RESTAURANT_ID_A, RESTAURANT_ID_B));
    assertEquals(2, deals.size());
    assertEquals(deals.get(0), dealA);
    assertEquals(deals.get(1), dealB);
  }

  @Test
  public void testGetDealPublishedByFollowedTags_success() {
    // Add deals published by RESTAURANT_ID_A and RESTAURANT_ID_B
    Deal dealA =
        dealManagerDatastore.createDeal(
            DESCRIPTION_A,
            BLOBKEY_A,
            DATE_A,
            DATE_B,
            SOURCE_A,
            USER_ID_A,
            RESTAURANT_ID_A,
            EMPTY_LIST);
    Deal dealB =
        dealManagerDatastore.createDeal(
            DESCRIPTION_A,
            BLOBKEY_A,
            DATE_A,
            DATE_B,
            SOURCE_A,
            USER_ID_A,
            RESTAURANT_ID_B,
            EMPTY_LIST);

    // Add tags to the deals
    dealTagManager.updateTagsOfDeal(dealA.id, Arrays.asList(TAG_ID_A, TAG_ID_B));
    dealTagManager.updateTagsOfDeal(dealB.id, Arrays.asList(TAG_ID_B));

    // Get deals published by the tags followed by USER_ID_A
    List<Deal> dealsForA =
        dealManagerDatastore.getDealsPublishedByFollowedTags(Arrays.asList(TAG_ID_A, TAG_ID_B));
    assertEquals(2, dealsForA.size());
    assertEquals(dealsForA.get(0), dealA);
    assertEquals(dealsForA.get(1), dealB);

    // Get deals published by the tags followed by USER_ID_B
    List<Deal> dealsForB =
        dealManagerDatastore.getDealsPublishedByFollowedTags(Arrays.asList(TAG_ID_A));
    assertEquals(1, dealsForB.size());
    assertEquals(dealsForB.get(0), dealA);

    // Get deals published by the tags followed by USER_ID_C
    List<Deal> dealsForC =
        dealManagerDatastore.getDealsPublishedByFollowedTags(Arrays.asList(TAG_ID_B));
    assertEquals(2, dealsForC.size());
    assertEquals(dealsForC.get(0), dealA);
    assertEquals(dealsForC.get(1), dealB);
  }

  @Test
  public void testSortDealsByVotes() {
    // Create 3 Deals
    Deal dealA =
        dealManagerDatastore.createDeal(
            DESCRIPTION_A,
            BLOBKEY_A,
            DATE_A,
            DATE_B,
            SOURCE_A,
            USER_ID_A,
            RESTAURANT_ID_A,
            EMPTY_LIST);
    Deal dealB =
        dealManagerDatastore.createDeal(
            DESCRIPTION_B,
            BLOBKEY_B,
            DATE_B,
            DATE_C,
            SOURCE_B,
            USER_ID_B,
            RESTAURANT_ID_B,
            EMPTY_LIST);
    Deal dealC =
        dealManagerDatastore.createDeal(
            DESCRIPTION_C,
            BLOBKEY_C,
            DATE_C,
            DATE_D,
            SOURCE_C,
            USER_ID_C,
            RESTAURANT_ID_C,
            EMPTY_LIST);

    // Add votes to the deals

    // 1 net upvote for dealA
    voteManager.vote(USER_ID_A, dealA.id, 1);
    voteManager.vote(USER_ID_B, dealA.id, -1);
    voteManager.vote(USER_ID_C, dealA.id, 1);

    // 3 net upvotes for dealB
    voteManager.vote(USER_ID_A, dealB.id, 1);
    voteManager.vote(USER_ID_B, dealB.id, 1);
    voteManager.vote(USER_ID_C, dealB.id, 1);

    // 1 net downvote for dealC
    voteManager.vote(USER_ID_A, dealC.id, -1);
    voteManager.vote(USER_ID_B, dealC.id, -1);
    voteManager.vote(USER_ID_C, dealC.id, 1);

    List<Deal> dealList = new ArrayList<>();
    dealList.add(dealA);
    dealList.add(dealB);
    dealList.add(dealC);

    // Expected: B, A, C (Descending order of votes)
    List<Deal> expectedList = new ArrayList<>();
    expectedList.add(dealB);
    expectedList.add(dealA);
    expectedList.add(dealC);

    List<Deal> dealSorted = dealManagerDatastore.sortDealsBasedOnVotes(dealList);
    assertThat(dealSorted, IsIterableContainingInOrder.contains(expectedList.toArray()));
  }

  @Test
  public void testSortDealsByNew() throws Exception {
    // Create 3 Deals with 1 second delay
    Deal dealA =
        dealManagerDatastore.createDeal(
            DESCRIPTION_A,
            BLOBKEY_A,
            DATE_A,
            DATE_B,
            SOURCE_A,
            USER_ID_A,
            RESTAURANT_ID_A,
            EMPTY_LIST);
    TimeUnit.SECONDS.sleep(1);
    Deal dealB =
        dealManagerDatastore.createDeal(
            DESCRIPTION_B,
            BLOBKEY_B,
            DATE_B,
            DATE_C,
            SOURCE_B,
            USER_ID_B,
            RESTAURANT_ID_B,
            EMPTY_LIST);
    TimeUnit.SECONDS.sleep(1);
    Deal dealC =
        dealManagerDatastore.createDeal(
            DESCRIPTION_C,
            BLOBKEY_C,
            DATE_C,
            DATE_D,
            SOURCE_C,
            USER_ID_C,
            RESTAURANT_ID_C,
            EMPTY_LIST);

    // Random order: A, C, B
    List<Deal> dealList = new ArrayList<>();
    dealList.add(dealA);
    dealList.add(dealC);
    dealList.add(dealB);

    // Expected order: C, B, A (Newest to Oldest)
    List<Deal> expectedList = new ArrayList<>();
    expectedList.add(dealC);
    expectedList.add(dealB);
    expectedList.add(dealA);

    List<Deal> dealSorted = dealManagerDatastore.sortDealsBasedOnNew(dealList);
    assertThat(dealSorted, IsIterableContainingInOrder.contains(expectedList.toArray()));
  }

  @Test
  public void testGetTrendingDeals() throws Exception {
    Deal dealA =
        dealManagerDatastore.createDeal(
            DESCRIPTION_A,
            BLOBKEY_A,
            DATE_A,
            DATE_B,
            SOURCE_A,
            USER_ID_A,
            RESTAURANT_ID_A,
            EMPTY_LIST);
    TimeUnit.SECONDS.sleep(2);
    Deal dealB =
        dealManagerDatastore.createDeal(
            DESCRIPTION_B,
            BLOBKEY_B,
            DATE_B,
            DATE_C,
            SOURCE_B,
            USER_ID_B,
            RESTAURANT_ID_B,
            EMPTY_LIST);
    TimeUnit.SECONDS.sleep(2);
    Deal dealC =
        dealManagerDatastore.createDeal(
            DESCRIPTION_C,
            BLOBKEY_C,
            DATE_C,
            DATE_D,
            SOURCE_C,
            USER_ID_C,
            RESTAURANT_ID_C,
            EMPTY_LIST);

    // Deal B most votes -> Most trending
    // Deal A and C equal number of votes, but deal A is the older deal -> less trending since
    // the votes were over a longer period of time

    voteManager.vote(USER_ID_A, dealA.id, 1);
    voteManager.vote(USER_ID_B, dealA.id, 1);

    // 3 net upvotes for dealB
    voteManager.vote(USER_ID_A, dealB.id, 1);
    voteManager.vote(USER_ID_B, dealB.id, 1);
    voteManager.vote(USER_ID_C, dealB.id, 1);

    // 1 net downvote for dealC
    voteManager.vote(USER_ID_A, dealC.id, 1);
    voteManager.vote(USER_ID_B, dealC.id, 1);

    List<Deal> expectedList = new ArrayList<>();
    expectedList.add(dealB);
    expectedList.add(dealC);
    expectedList.add(dealA);

    List<Deal> dealSorted = dealManagerDatastore.getTrendingDeals();
    for (int i = 0; i < 3; i++) {
      assertEquals(dealSorted.get(i), expectedList.get(i));
    }
  }

  @Test
  public void testCreateDealWithTags() {
    Deal deal =
        dealManagerDatastore.createDeal(
            DESCRIPTION_A,
            BLOBKEY_A,
            DATE_A,
            DATE_B,
            SOURCE_A,
            USER_ID_A,
            RESTAURANT_ID_A,
            TAG_LIST);
    List<Tag> tags = dealManagerDatastore.getTags(deal.id);

    assertEquals(1, tags.size());
    assertEquals(TAG_NAME_A, tags.get(0).name);
  }

  @Test
  public void testUpdateDealTags() {
    Deal createdDeal =
        dealManagerDatastore.createDeal(
            DESCRIPTION_A,
            BLOBKEY_A,
            DATE_A,
            DATE_B,
            SOURCE_A,
            USER_ID_A,
            RESTAURANT_ID_A,
            TAG_LIST);
    Deal dealToUpdate = new Deal(createdDeal.id, null, null, null, null, null, -1, -1, null);
    dealManagerDatastore.updateDeal(dealToUpdate, EMPTY_LIST);
    Deal updatedDeal = dealManagerDatastore.updateDeal(dealToUpdate, EMPTY_LIST);
    List<Tag> tags = dealManagerDatastore.getTags(createdDeal.id);

    assertEquals(0, tags.size());
  }
}
