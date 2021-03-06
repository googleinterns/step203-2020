package com.google.step.datamanager;

import static com.google.step.TestConstants.DEAL_ID_A;
import static com.google.step.TestConstants.DEAL_ID_B;
import static com.google.step.TestConstants.DEAL_ID_C;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class DealVoteCountManagerDatastoreTest {

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private final DealVoteCountManagerDatastore dealVoteCountManager =
      new DealVoteCountManagerDatastore();

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void getVotes_returnsZeroAtFirst() {
    assertEquals(0, dealVoteCountManager.getVotes(DEAL_ID_A));
  }

  @Test
  public void vote_incrementsVotesCorrectly() {
    dealVoteCountManager.updateDealVotes(DEAL_ID_A, 1);
    assertEquals(1, dealVoteCountManager.getVotes(DEAL_ID_A));
    dealVoteCountManager.updateDealVotes(DEAL_ID_A, 1);
    assertEquals(2, dealVoteCountManager.getVotes(DEAL_ID_A));
  }

  @Test
  public void vote_decrementsVotesCorrectly() {
    dealVoteCountManager.updateDealVotes(DEAL_ID_A, 1);
    assertEquals(1, dealVoteCountManager.getVotes(DEAL_ID_A));
    dealVoteCountManager.updateDealVotes(DEAL_ID_A, -1);
    assertEquals(0, dealVoteCountManager.getVotes(DEAL_ID_A));
  }

  @Test
  public void testManyVotes() {
    for (int i = 1; i <= 100; i++) {
      dealVoteCountManager.updateDealVotes(DEAL_ID_A, 1);
    }
    assertEquals(100, dealVoteCountManager.getVotes(DEAL_ID_A));
  }

  @Test
  public void testSortDealsInOrderOfVotesAddCAtEnd() {
    dealVoteCountManager.updateDealVotes(DEAL_ID_A, 1);
    dealVoteCountManager.updateDealVotes(DEAL_ID_B, 2);
    List<Long> deals =
        dealVoteCountManager.sortDealsInOrderOfVotes(
            Arrays.asList(DEAL_ID_A, DEAL_ID_B, DEAL_ID_C), -1);
    List<Long> expected = Arrays.asList(DEAL_ID_B, DEAL_ID_A, DEAL_ID_C);
    assertEquals(expected, deals);
  }

  @Test
  public void testSortDealsInOrderOfVotesAddCInMiddle() {
    dealVoteCountManager.updateDealVotes(DEAL_ID_A, 1);
    dealVoteCountManager.updateDealVotes(DEAL_ID_B, -1);
    List<Long> deals =
        dealVoteCountManager.sortDealsInOrderOfVotes(
            Arrays.asList(DEAL_ID_A, DEAL_ID_B, DEAL_ID_C), -1);
    List<Long> expected = Arrays.asList(DEAL_ID_A, DEAL_ID_C, DEAL_ID_B);
    assertEquals(expected, deals);
  }

  @Test
  public void testSortDealsInOrderOfVotesAddCInMiddleLimit() {
    dealVoteCountManager.updateDealVotes(DEAL_ID_A, 1);
    dealVoteCountManager.updateDealVotes(DEAL_ID_B, -1);
    List<Long> deals =
        dealVoteCountManager.sortDealsInOrderOfVotes(
            Arrays.asList(DEAL_ID_A, DEAL_ID_B, DEAL_ID_C), 2);
    List<Long> expected = Arrays.asList(DEAL_ID_A, DEAL_ID_C);
    assertEquals(expected, deals);
  }

  @Test
  public void testSortDealsInOrderOfVotesAddCStart() {
    dealVoteCountManager.updateDealVotes(DEAL_ID_A, 0);
    dealVoteCountManager.updateDealVotes(DEAL_ID_B, -1);
    List<Long> deals =
        dealVoteCountManager.sortDealsInOrderOfVotes(
            Arrays.asList(DEAL_ID_A, DEAL_ID_B, DEAL_ID_C), -1);
    List<Long> expected = Arrays.asList(DEAL_ID_C, DEAL_ID_A, DEAL_ID_B);
    assertEquals(expected, deals);
  }

  @Test
  public void testSortDealsInOrderOfVotesAddCStartLimit() {
    dealVoteCountManager.updateDealVotes(DEAL_ID_A, 0);
    dealVoteCountManager.updateDealVotes(DEAL_ID_B, -1);
    List<Long> deals =
        dealVoteCountManager.sortDealsInOrderOfVotes(
            Arrays.asList(DEAL_ID_A, DEAL_ID_B, DEAL_ID_C), 2);
    List<Long> expected = Arrays.asList(DEAL_ID_C, DEAL_ID_A);
    assertEquals(expected, deals);
  }

  @Test
  public void testSortDealsInOrderOfVotesAddCEndLimit() {
    dealVoteCountManager.updateDealVotes(DEAL_ID_A, 1);
    dealVoteCountManager.updateDealVotes(DEAL_ID_B, 2);
    List<Long> deals =
        dealVoteCountManager.sortDealsInOrderOfVotes(
            Arrays.asList(DEAL_ID_A, DEAL_ID_B, DEAL_ID_C), 1);
    List<Long> expected = Arrays.asList(DEAL_ID_B);
    assertEquals(1, expected.size());
    assertThat(deals, hasItem(DEAL_ID_B));
  }

  @Test
  public void testSortDealsInOrderOfVotesLimitMoreThanDeals() {
    dealVoteCountManager.updateDealVotes(DEAL_ID_A, 1);
    dealVoteCountManager.updateDealVotes(DEAL_ID_B, 2);
    List<Long> deals =
        dealVoteCountManager.sortDealsInOrderOfVotes(
            Arrays.asList(DEAL_ID_A, DEAL_ID_B, DEAL_ID_C), 4);
    List<Long> expected = Arrays.asList(DEAL_ID_B, DEAL_ID_A, DEAL_ID_C);
    assertEquals(expected, deals);
  }
}
