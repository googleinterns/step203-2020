package com.google.step.datamanager;

import static com.google.step.TestConstants.DEAL_ID_A;
import static com.google.step.TestConstants.DEAL_ID_B;
import static com.google.step.TestConstants.DEAL_ID_C;
import static com.google.step.TestConstants.DEAL_ID_D;
import static com.google.step.TestConstants.TAG_ID_A;
import static com.google.step.TestConstants.TAG_ID_B;
import static com.google.step.TestConstants.TAG_ID_C;
import static com.google.step.TestConstants.TAG_ID_D;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

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
public class DealTagManagerDatastoreTest {

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig().setApplyAllHighRepJobPolicy());

  private final DealTagManagerDatastore dealTagManagerDatastore = new DealTagManagerDatastore();

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testUpdateTagsOfDeal() {
    List<Long> tagIds = Arrays.asList(TAG_ID_A, TAG_ID_B);
    dealTagManagerDatastore.updateTagsOfDeal(DEAL_ID_A, tagIds);
    Long[] actual = dealTagManagerDatastore.getTagIdsOfDeal(DEAL_ID_A).toArray(new Long[0]);
    Long[] expected = tagIds.toArray(new Long[0]);
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testUpdateTagsOfDeal_newTagIds() {
    List<Long> tagIds = Arrays.asList(TAG_ID_A, TAG_ID_B);
    dealTagManagerDatastore.updateTagsOfDeal(DEAL_ID_A, tagIds);
    List<Long> newTagIds = Arrays.asList(TAG_ID_C, TAG_ID_D);
    dealTagManagerDatastore.updateTagsOfDeal(DEAL_ID_A, newTagIds);
    Long[] actual = dealTagManagerDatastore.getTagIdsOfDeal(DEAL_ID_A).toArray(new Long[0]);
    Long[] expected = newTagIds.toArray(new Long[0]);
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testUpdateTagsOfDeal_removeTagIds() {
    List<Long> tagIds = Arrays.asList(TAG_ID_A, TAG_ID_B);
    dealTagManagerDatastore.updateTagsOfDeal(DEAL_ID_A, tagIds);
    List<Long> newTagIds = Arrays.asList(TAG_ID_A);
    dealTagManagerDatastore.updateTagsOfDeal(DEAL_ID_A, newTagIds);
    Long[] actual = dealTagManagerDatastore.getTagIdsOfDeal(DEAL_ID_A).toArray(new Long[0]);
    Long[] expected = newTagIds.toArray(new Long[0]);
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testUpdateTagsOfDeal_addTagIds() {
    List<Long> tagIds = Arrays.asList(TAG_ID_A, TAG_ID_B);
    dealTagManagerDatastore.updateTagsOfDeal(DEAL_ID_A, tagIds);
    List<Long> newTagIds = Arrays.asList(TAG_ID_A, TAG_ID_B, TAG_ID_D);
    dealTagManagerDatastore.updateTagsOfDeal(DEAL_ID_A, newTagIds);
    Long[] actual = dealTagManagerDatastore.getTagIdsOfDeal(DEAL_ID_A).toArray(new Long[0]);
    Long[] expected = newTagIds.toArray(new Long[0]);
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testUpdateTagsOfDeal_moreDealIds() {
    List<Long> tagIdsOfA = Arrays.asList(TAG_ID_A, TAG_ID_B);
    dealTagManagerDatastore.updateTagsOfDeal(DEAL_ID_A, tagIdsOfA);
    List<Long> tagIdsOfB = Arrays.asList(TAG_ID_A, TAG_ID_B, TAG_ID_D);
    dealTagManagerDatastore.updateTagsOfDeal(DEAL_ID_B, tagIdsOfB);
    List<Long> tagIdsOfC = Arrays.asList(TAG_ID_A, TAG_ID_C, TAG_ID_D);
    dealTagManagerDatastore.updateTagsOfDeal(DEAL_ID_C, tagIdsOfC);

    Long[] actual = dealTagManagerDatastore.getTagIdsOfDeal(DEAL_ID_A).toArray(new Long[0]);
    Long[] expected = tagIdsOfA.toArray(new Long[0]);
    assertArrayEquals(expected, actual);

    actual = dealTagManagerDatastore.getTagIdsOfDeal(DEAL_ID_C).toArray(new Long[0]);
    expected = tagIdsOfC.toArray(new Long[0]);
    assertArrayEquals(expected, actual);

    assertTrue(dealTagManagerDatastore.getTagIdsOfDeal(DEAL_ID_D).isEmpty());
  }

  @Test
  public void getDealsWithTag() {
    List<Long> tagIdsOfA = Arrays.asList(TAG_ID_A, TAG_ID_B);
    dealTagManagerDatastore.updateTagsOfDeal(DEAL_ID_A, tagIdsOfA);
    List<Long> tagIdsOfB = Arrays.asList(TAG_ID_A, TAG_ID_B, TAG_ID_D);
    dealTagManagerDatastore.updateTagsOfDeal(DEAL_ID_B, tagIdsOfB);
    List<Long> tagIdsOfC = Arrays.asList(TAG_ID_A, TAG_ID_C, TAG_ID_D);
    dealTagManagerDatastore.updateTagsOfDeal(DEAL_ID_C, tagIdsOfC);
    List<Long> dealIds = dealTagManagerDatastore.getDealIdsWithTag(TAG_ID_A);

    Long[] actual = dealIds.toArray(new Long[0]);
    Long[] expected = new Long[] {DEAL_ID_A, DEAL_ID_B, DEAL_ID_C};

    assertArrayEquals(expected, actual);
  }

  @Test
  public void getDealsWithTag_tagDoesNotExist() {
    List<Long> tagIdsOfA = Arrays.asList(TAG_ID_A, TAG_ID_B);
    dealTagManagerDatastore.updateTagsOfDeal(DEAL_ID_A, tagIdsOfA);
    List<Long> tagIdsOfB = Arrays.asList(TAG_ID_A, TAG_ID_B);
    dealTagManagerDatastore.updateTagsOfDeal(DEAL_ID_B, tagIdsOfB);
    List<Long> tagIdsOfC = Arrays.asList(TAG_ID_A, TAG_ID_C);
    dealTagManagerDatastore.updateTagsOfDeal(DEAL_ID_C, tagIdsOfC);
    List<Long> dealIds = dealTagManagerDatastore.getDealIdsWithTag(TAG_ID_D);

    assertTrue(dealIds.isEmpty());
  }
}
