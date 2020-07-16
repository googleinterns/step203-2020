package com.google.step.datamanager;

import static com.google.step.TestConstants.TAG_ID_C;
import static com.google.step.TestConstants.TAG_NAME_A;
import static com.google.step.TestConstants.TAG_NAME_B;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.step.model.Tag;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class TagManagerDatastoreTest {

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig().setApplyAllHighRepJobPolicy());

  private final TagManagerDatastore tagManagerDatastore = new TagManagerDatastore();

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testGetTag_newTag() {
    Tag tag = tagManagerDatastore.readOrCreateTagByName(TAG_NAME_A);
    assertEquals(TAG_NAME_A, tag.name);
  }

  @Test
  public void testGetTag_existingTag() {
    Tag tag = tagManagerDatastore.readOrCreateTagByName(TAG_NAME_A);
    Tag duplicateTag = tagManagerDatastore.readOrCreateTagByName(TAG_NAME_A);

    assertEquals(tag.id, duplicateTag.id);
    assertEquals(tag.name, duplicateTag.name);
  }

  @Test
  public void testReadTag_success() {
    Tag tagA = tagManagerDatastore.readOrCreateTagByName(TAG_NAME_A);
    tagManagerDatastore.readOrCreateTagByName(TAG_NAME_B);
    Tag tagARead = tagManagerDatastore.readTag(tagA.id);

    assertEquals(TAG_NAME_A, tagARead.name);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testReadTag_notExist() {
    tagManagerDatastore.readOrCreateTagByName(TAG_NAME_A);
    tagManagerDatastore.readTag(1000);
  }

  public void testReadTags() {
    Tag tagA = tagManagerDatastore.readOrCreateTagByName(TAG_NAME_A);
    Tag tagB = tagManagerDatastore.readOrCreateTagByName(TAG_NAME_B);
    List<Long> ids = Arrays.asList(tagA.id, tagB.id);
    List<Tag> tags = tagManagerDatastore.readTags(ids);

    assertThat(tags, containsInAnyOrder(tagA, tagB));
  }

  public void testReadTags_idDoesNotExist() {
    Tag tagA = tagManagerDatastore.readOrCreateTagByName(TAG_NAME_A);
    Tag tagB = tagManagerDatastore.readOrCreateTagByName(TAG_NAME_B);
    List<Long> ids = Arrays.asList(tagA.id, tagB.id, TAG_ID_C);
    List<Tag> tags = tagManagerDatastore.readTags(ids);

    assertThat(tags, containsInAnyOrder(tagA, tagB));
  }
}
