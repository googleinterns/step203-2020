package com.google.step.datamanager;

import static com.google.step.TestConstants.BLOBKEY_A;
import static com.google.step.TestConstants.BLOBKEY_B;
import static com.google.step.TestConstants.DATE_A;
import static com.google.step.TestConstants.DATE_B;
import static com.google.step.TestConstants.DATE_C;
import static com.google.step.TestConstants.DATE_D;
import static com.google.step.TestConstants.DEAL_ID_A;
import static com.google.step.TestConstants.DEAL_ID_B;
import static com.google.step.TestConstants.DEAL_ID_C;
import static com.google.step.TestConstants.DESCRIPTION_A;
import static com.google.step.TestConstants.DESCRIPTION_B;
import static com.google.step.TestConstants.RESTAURANT_ID_A;
import static com.google.step.TestConstants.RESTAURANT_ID_B;
import static com.google.step.TestConstants.SOURCE_A;
import static com.google.step.TestConstants.SOURCE_B;
import static com.google.step.TestConstants.USER_ID_A;
import static com.google.step.TestConstants.USER_ID_B;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.google.appengine.tools.development.testing.LocalSearchServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.step.model.Deal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class DealSearchManagerIndexTest {

  private final Deal DEAL_A =
      new Deal(
          DEAL_ID_A,
          DESCRIPTION_A,
          BLOBKEY_A,
          DATE_A,
          DATE_B,
          SOURCE_A,
          USER_ID_A,
          RESTAURANT_ID_A);

  private final Deal DEAL_B =
      new Deal(
          DEAL_ID_B,
          DESCRIPTION_B,
          BLOBKEY_B,
          DATE_C,
          DATE_D,
          SOURCE_B,
          USER_ID_B,
          RESTAURANT_ID_B);

  private final List<Long> EMPTY_TAGS = new ArrayList<>();

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalSearchServiceTestConfig());

  private final DealSearchManagerIndex dealSearchManager = new DealSearchManagerIndex();

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testSearch() {
    dealSearchManager.putDeal(DEAL_A, EMPTY_TAGS);
    dealSearchManager.putDeal(DEAL_B, EMPTY_TAGS);

    List<Long> searchResults = dealSearchManager.search(DESCRIPTION_A, EMPTY_TAGS);

    assertTrue(searchResults.size() == 1);
    assertTrue(searchResults.get(0) == DEAL_A.id);
  }

  @Test
  public void testSearch_word() {
    Deal deal1 =
        new Deal(
            DEAL_ID_A,
            "word1 word2",
            BLOBKEY_A,
            DATE_A,
            DATE_B,
            SOURCE_A,
            USER_ID_A,
            RESTAURANT_ID_A);
    Deal deal2 =
        new Deal(
            DEAL_ID_B,
            "word1 word3",
            BLOBKEY_B,
            DATE_C,
            DATE_D,
            SOURCE_B,
            USER_ID_B,
            RESTAURANT_ID_B);

    dealSearchManager.putDeal(deal1, EMPTY_TAGS);
    dealSearchManager.putDeal(deal2, EMPTY_TAGS);

    List<Long> searchResults = dealSearchManager.search("word1", EMPTY_TAGS);

    assertTrue(searchResults.size() == 2);
    assertThat(searchResults, hasItems(deal1.id, deal2.id));
  }

  @Test
  public void testSearch_tags() {
    dealSearchManager.putDeal(DEAL_A, Arrays.asList(1L));
    dealSearchManager.putDeal(DEAL_B, Arrays.asList(1L, 2L));

    List<Long> searchResults = dealSearchManager.search("", Arrays.asList(2L));

    assertTrue(searchResults.size() == 1);
    assertTrue(searchResults.get(0) == DEAL_B.id);
  }

  @Test
  public void testSearch_wordAndTag() {
    Deal deal1 =
        new Deal(
            DEAL_ID_A, "keyword", BLOBKEY_A, DATE_A, DATE_B, SOURCE_A, USER_ID_A, RESTAURANT_ID_A);
    Deal deal2 =
        new Deal(
            DEAL_ID_B, "keyword", BLOBKEY_B, DATE_C, DATE_D, SOURCE_B, USER_ID_B, RESTAURANT_ID_B);
    Deal deal3 =
        new Deal(
            DEAL_ID_C,
            "trashword",
            BLOBKEY_B,
            DATE_C,
            DATE_D,
            SOURCE_B,
            USER_ID_B,
            RESTAURANT_ID_B);

    dealSearchManager.putDeal(deal1, Arrays.asList(1L));
    dealSearchManager.putDeal(deal2, Arrays.asList(2L));
    dealSearchManager.putDeal(deal3, Arrays.asList(1L));

    List<Long> searchResults = dealSearchManager.search("keyword", Arrays.asList(1L));

    assertTrue(searchResults.size() == 1);
    assertTrue(searchResults.get(0) == deal1.id);
  }

  @Test
  public void testSearch_caseInsensitive() {
    Deal deal =
        new Deal(
            DEAL_ID_A,
            "SoMeThInG",
            BLOBKEY_A,
            DATE_A,
            DATE_B,
            SOURCE_A,
            USER_ID_A,
            RESTAURANT_ID_A);

    dealSearchManager.putDeal(deal, EMPTY_TAGS);

    List<Long> searchResults = dealSearchManager.search("something", EMPTY_TAGS);

    assertTrue(searchResults.size() == 1);
    assertTrue(searchResults.get(0) == deal.id);
  }
}
