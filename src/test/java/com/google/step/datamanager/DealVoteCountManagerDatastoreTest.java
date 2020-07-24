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

  private final DealVoteCountManagerDatastore mockDealVoteCountManager =
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
    assertEquals(0, mockDealVoteCountManager.getVotes(DEAL_ID_A));
  }

  @Test
  public void vote_incrementsVotesCorrectly() {
    mockDealVoteCountManager.updateDealVotes(DEAL_ID_A, 1);
    assertEquals(1, mockDealVoteCountManager.getVotes(DEAL_ID_A));
    mockDealVoteCountManager.updateDealVotes(DEAL_ID_A, 1);
    assertEquals(2, mockDealVoteCountManager.getVotes(DEAL_ID_A));
  }

  @Test
  public void vote_decrementsVotesCorrectly() {
    mockDealVoteCountManager.updateDealVotes(DEAL_ID_A, 1);
    assertEquals(1, mockDealVoteCountManager.getVotes(DEAL_ID_A));
    mockDealVoteCountManager.updateDealVotes(DEAL_ID_A, -1);
    assertEquals(0, mockDealVoteCountManager.getVotes(DEAL_ID_A));
  }

  @Test
  public void testManyVotes() {
    for (int i = 1; i <= 100; i++) {
      mockDealVoteCountManager.updateDealVotes(DEAL_ID_A, 1);
    }
    assertEquals(100, mockDealVoteCountManager.getVotes(DEAL_ID_A));
  }

  @Test
  public void testGetDealsInOrderOfVotesNoLimit() {
    mockDealVoteCountManager.updateDealVotes(DEAL_ID_A, 1);
    mockDealVoteCountManager.updateDealVotes(DEAL_ID_B, 2);
    List<Long> deals =
        mockDealVoteCountManager.getDealsInOrderOfVotes(
            Arrays.asList(DEAL_ID_A, DEAL_ID_B, DEAL_ID_C), -1);
    List<Long> expected = Arrays.asList(DEAL_ID_B, DEAL_ID_A, DEAL_ID_C);
    assertEquals(expected, deals);
  }

  @Test
  public void testGetDealsInOrderOfVotesLimit() {
    mockDealVoteCountManager.updateDealVotes(DEAL_ID_A, 1);
    mockDealVoteCountManager.updateDealVotes(DEAL_ID_B, 2);
    List<Long> deals =
        mockDealVoteCountManager.getDealsInOrderOfVotes(
            Arrays.asList(DEAL_ID_A, DEAL_ID_B, DEAL_ID_C), 1);
    List<Long> expected = Arrays.asList(DEAL_ID_B);
    assertEquals(1, expected.size());
    assertThat(deals, hasItem(DEAL_ID_B));
  }

  @Test
  public void testGetDealsInOrderOfVotesLimitMoreThanDeals() {
    mockDealVoteCountManager.updateDealVotes(DEAL_ID_A, 1);
    mockDealVoteCountManager.updateDealVotes(DEAL_ID_B, 2);
    List<Long> deals =
        mockDealVoteCountManager.getDealsInOrderOfVotes(
            Arrays.asList(DEAL_ID_A, DEAL_ID_B, DEAL_ID_C), 4);
    List<Long> expected = Arrays.asList(DEAL_ID_B, DEAL_ID_A, DEAL_ID_C);
    assertEquals(expected, deals);
  }
}
