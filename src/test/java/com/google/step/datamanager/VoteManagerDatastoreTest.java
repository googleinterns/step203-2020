package com.google.step.datamanager;

import static com.google.step.TestConstants.DEAL_ID_A;
import static com.google.step.TestConstants.DEAL_ID_B;
import static com.google.step.TestConstants.USER_ID_A;
import static com.google.step.TestConstants.USER_ID_B;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class VoteManagerDatastoreTest {

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
    assertEquals(0, manager.getVotes(DEAL_ID_A));
  }

  @Test
  public void vote_incrementsVotesCorrectly() {
    manager.vote(USER_ID_A, DEAL_ID_A, 1);
    assertEquals(1, manager.getVotes(DEAL_ID_A));
    manager.vote(USER_ID_B, DEAL_ID_A, 1);
    assertEquals(2, manager.getVotes(DEAL_ID_A));
  }

  @Test
  public void vote_doesNotIncrementForDuplicateVoteFromUser() {
    manager.vote(USER_ID_A, DEAL_ID_A, 1);
    assertEquals(1, manager.getVotes(DEAL_ID_A));
    manager.vote(USER_ID_A, DEAL_ID_A, 1);
    assertEquals(1, manager.getVotes(DEAL_ID_A));
  }

  @Test
  public void vote_decrementsVotesCorrectly() {
    manager.vote(USER_ID_A, DEAL_ID_A, -1);
    assertEquals(-1, manager.getVotes(DEAL_ID_A));
    manager.vote(USER_ID_B, DEAL_ID_A, -1);
    assertEquals(-2, manager.getVotes(DEAL_ID_A));
  }

  @Test
  public void vote_doesNotAffectOtherDeals() {
    manager.vote(USER_ID_A, DEAL_ID_A, 1);
    manager.vote(USER_ID_A, DEAL_ID_B, -1);
    assertEquals(1, manager.getVotes(DEAL_ID_A));
    assertEquals(-1, manager.getVotes(DEAL_ID_B));
  }

  @Test
  public void vote_userChangeDirection() {
    manager.vote(USER_ID_A, DEAL_ID_A, 1);
    assertEquals(1, manager.getVotes(DEAL_ID_A));
    manager.vote(USER_ID_A, DEAL_ID_A, -1);
    assertEquals(-1, manager.getVotes(DEAL_ID_A));
  }

  @Test
  public void vote_userUndoesVote() {
    manager.vote(USER_ID_A, DEAL_ID_A, 1);
    assertEquals(1, manager.getVotes(DEAL_ID_A));
    manager.vote(USER_ID_A, DEAL_ID_A, 0);
    assertEquals(0, manager.getVotes(DEAL_ID_A));
  }

  @Test
  public void testManyVotes() {
    for (int i = 1; i <= 100; i++) {
      manager.vote(i, DEAL_ID_A, 1);
    }
    assertEquals(100, manager.getVotes(DEAL_ID_A));
  }

  @Test
  public void testCache_notExpired() {
    VoteCache mockVoteCache = mock(VoteCache.class);
    when(mockVoteCache.readVotes(DEAL_ID_A)).thenReturn(new VoteWithExpiry(0, false));

    VoteManagerDatastore voteManagerDatastore = new VoteManagerDatastore(mockVoteCache);
    voteManagerDatastore.getVotes(DEAL_ID_A);

    System.out.println(mockVoteCache);
    verify(mockVoteCache, never()).saveVotes(eq(DEAL_ID_A), anyInt());
  }

  @Test
  public void testCache_expired() {
    VoteCache mockVoteCache = mock(VoteCache.class);
    when(mockVoteCache.readVotes(DEAL_ID_A)).thenReturn(new VoteWithExpiry(0, true));

    VoteManagerDatastore voteManagerDatastore = new VoteManagerDatastore(mockVoteCache);
    voteManagerDatastore.getVotes(DEAL_ID_A);

    verify(mockVoteCache).saveVotes(eq(DEAL_ID_A), anyInt());
  }

  @Test
  public void testCache_deleteCache() {
    VoteCache mockVoteCache = mock(VoteCache.class);
    when(mockVoteCache.readVotes(DEAL_ID_A)).thenReturn(new VoteWithExpiry(0, true));

    VoteManagerDatastore voteManagerDatastore = new VoteManagerDatastore(mockVoteCache);
    voteManagerDatastore.vote(USER_ID_A, DEAL_ID_A, 1);

    verify(mockVoteCache).deleteCache(DEAL_ID_A);
  }
}
