package com.google.step.datamanager;

import static org.junit.Assert.assertEquals;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class VoteManagerDatastoreTest {

  private static final long USER_ID_A = 123;
  private static final long USER_ID_B = 456;

  private static final long DEAL_ID_A = 111;
  private static final long DEAL_ID_B = 222;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private final VoteManagerDatastore manager = new VoteManagerDatastore();

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
    assertEquals(manager.getVotes(DEAL_ID_A), 0);
  }

  @Test
  public void vote_incrementsVotesCorrectly() {
    manager.vote(USER_ID_A, DEAL_ID_A, 1);
    assertEquals(manager.getVotes(DEAL_ID_A), 1);
    manager.vote(USER_ID_B, DEAL_ID_A, 1);
    assertEquals(manager.getVotes(DEAL_ID_A), 2);
  }

  @Test
  public void vote_doesNotIncrementForDuplicateVoteFromUser() {
    manager.vote(USER_ID_A, DEAL_ID_A, 1);
    assertEquals(manager.getVotes(DEAL_ID_A), 1);
    manager.vote(USER_ID_A, DEAL_ID_A, 1);
    assertEquals(manager.getVotes(DEAL_ID_A), 1);
  }

  @Test
  public void vote_decrementsVotesCorrectly() {
    manager.vote(USER_ID_A, DEAL_ID_A, -1);
    assertEquals(manager.getVotes(DEAL_ID_A), -1);
    manager.vote(USER_ID_B, DEAL_ID_A, -1);
    assertEquals(manager.getVotes(DEAL_ID_A), -2);
  }

  @Test
  public void vote_doesNotAffectOtherDeals() {
    manager.vote(USER_ID_A, DEAL_ID_A, 1);
    manager.vote(USER_ID_A, DEAL_ID_B, -1);
    assertEquals(manager.getVotes(DEAL_ID_A), 1);
    assertEquals(manager.getVotes(DEAL_ID_B), -1);
  }

  @Test
  public void vote_userChangeDirection() {
    manager.vote(USER_ID_A, DEAL_ID_A, 1);
    assertEquals(manager.getVotes(DEAL_ID_A), 1);
    manager.vote(USER_ID_A, DEAL_ID_A, -1);
    assertEquals(manager.getVotes(DEAL_ID_A), -1);
  }

  @Test
  public void vote_userUndoesVote() {
    manager.vote(USER_ID_A, DEAL_ID_A, 1);
    assertEquals(manager.getVotes(DEAL_ID_A), 1);
    manager.vote(USER_ID_A, DEAL_ID_A, 0);
    assertEquals(manager.getVotes(DEAL_ID_A), 0);
  }

  @Test
  public void testManyVotes() {
    for (int i = 1; i <= 100; i++) {
      manager.vote(i, DEAL_ID_A, 1);
    }
    assertEquals(manager.getVotes(DEAL_ID_A), 100);
  }
}