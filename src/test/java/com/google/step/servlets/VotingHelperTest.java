package com.google.step.servlets;

import static com.google.step.TestConstants.DEAL_ID_A;
import static com.google.step.TestConstants.USER_ID_A;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.step.datamanager.DealVoteCountManager;
import com.google.step.datamanager.VoteManager;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class VotingHelperTest {

  private static final String DIR_ONE = "1";
  private static final String DIR_NEG_ONE = "-1";
  private static final String DIR_UNDO = "0";
  private VoteManager voteManager;
  private DealVoteCountManager dealVoteCountManager;

  @Before
  public void setUp() throws IOException {
    voteManager = mock(VoteManager.class);
    dealVoteCountManager = mock(DealVoteCountManager.class);
  }

  @Test
  public void testVotingHelperSameDirection() throws IOException {

    when(voteManager.getDirection(USER_ID_A, DEAL_ID_A)).thenReturn(1);

    VotingHelper.updateVote(USER_ID_A, DEAL_ID_A, DIR_ONE, voteManager, dealVoteCountManager);

    verify(voteManager).vote(eq(USER_ID_A), eq(DEAL_ID_A), eq(1));
    verify(dealVoteCountManager, never()).updateDealVotes(eq(DEAL_ID_A), anyInt());
  }

  @Test
  public void testVotingHelperOppDirection() throws IOException {

    when(voteManager.getDirection(USER_ID_A, DEAL_ID_A)).thenReturn(-1);

    VotingHelper.updateVote(USER_ID_A, DEAL_ID_A, DIR_ONE, voteManager, dealVoteCountManager);

    verify(voteManager).vote(eq(USER_ID_A), eq(DEAL_ID_A), eq(1));
    verify(dealVoteCountManager).updateDealVotes(eq(DEAL_ID_A), eq(2));
  }

  @Test
  public void testVotingHelperUndo() throws IOException {

    when(voteManager.getDirection(USER_ID_A, DEAL_ID_A)).thenReturn(1);

    VotingHelper.updateVote(USER_ID_A, DEAL_ID_A, DIR_UNDO, voteManager, dealVoteCountManager);

    verify(voteManager).vote(eq(USER_ID_A), eq(DEAL_ID_A), eq(0));
    verify(dealVoteCountManager).updateDealVotes(eq(DEAL_ID_A), eq(-1));
  }

  @Test
  public void testVotingHelperDiffOppDir() throws IOException {

    when(voteManager.getDirection(USER_ID_A, DEAL_ID_A)).thenReturn(1);

    VotingHelper.updateVote(USER_ID_A, DEAL_ID_A, DIR_NEG_ONE, voteManager, dealVoteCountManager);

    verify(voteManager).vote(eq(USER_ID_A), eq(DEAL_ID_A), eq(-1));
    verify(dealVoteCountManager).updateDealVotes(eq(DEAL_ID_A), eq(-2));
  }
}
