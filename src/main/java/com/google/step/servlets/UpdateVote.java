package com.google.step.servlets;

import com.google.step.datamanager.DealVoteCountManager;
import com.google.step.datamanager.VoteManager;

public class UpdateVote {

  public static void updateVote(
      long userId,
      long dealId,
      String dir,
      VoteManager voteManager,
      DealVoteCountManager dealVoteCountManager) {
    int prevDir = voteManager.getDirection(userId, dealId);
    int dirInt = Integer.parseInt(dir);
    voteManager.vote(userId, dealId, dirInt);
    if (dirInt != prevDir) {
      dealVoteCountManager.updateDealVotes(dealId, dirInt - prevDir);
    }
  }
}
