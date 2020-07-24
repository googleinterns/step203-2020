package com.google.step.datamanager;

import static com.google.step.TestConstants.DEAL_ID_A;
import static com.google.step.TestConstants.DEAL_ID_B;
import static com.google.step.TestConstants.DEAL_ID_C;
import static org.junit.Assert.assertEquals;

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
public class DealVoteManagerDatastoreTest {

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private final DealVoteManagerDatastore mockDealVoteManager = new DealVoteManagerDatastore();

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
    assertEquals(0, mockDealVoteManager.getVotes(DEAL_ID_A));
  }

  @Test
  public void vote_incrementsVotesCorrectly() {
    mockDealVoteManager.updateDealVotes(DEAL_ID_A, 1);
    assertEquals(1, mockDealVoteManager.getVotes(DEAL_ID_A));
    mockDealVoteManager.updateDealVotes(DEAL_ID_A, 1);
    assertEquals(2, mockDealVoteManager.getVotes(DEAL_ID_A));
  }

  @Test
  public void vote_decrementsVotesCorrectly() {
    mockDealVoteManager.updateDealVotes(DEAL_ID_A, 1);
    assertEquals(1, mockDealVoteManager.getVotes(DEAL_ID_A));
    mockDealVoteManager.updateDealVotes(DEAL_ID_A, -1);
    assertEquals(0, mockDealVoteManager.getVotes(DEAL_ID_A));
  }

  @Test
  public void testManyVotes() {
    for (int i = 1; i <= 100; i++) {
      mockDealVoteManager.updateDealVotes(DEAL_ID_A, 1);
    }
    assertEquals(100, mockDealVoteManager.getVotes(DEAL_ID_A));
  }

  @Test
  public void testGetDealsInOrderOfVotes() {

    mockDealVoteManager.updateDealVotes(DEAL_ID_A, 1);
    mockDealVoteManager.updateDealVotes(DEAL_ID_B, 2);
    List<Long> deals =
        mockDealVoteManager.getDealsInOrderOfVotes(Arrays.asList(DEAL_ID_A, DEAL_ID_B, DEAL_ID_C));
    List<Long> expected = Arrays.asList(DEAL_ID_B, DEAL_ID_A, DEAL_ID_C);
    assertEquals(expected, deals);
  }
}
