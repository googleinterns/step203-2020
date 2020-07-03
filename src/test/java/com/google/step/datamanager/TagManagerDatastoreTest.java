package com.google.step.datamanager;

import static org.junit.Assert.assertEquals;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.step.model.Tag;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class TagManagerDatastoreTest {

  private static final String NAME_A = "1for1";
  private static final String NAME_B = "tea";

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
    Tag tag = tagManagerDatastore.readOrCreateTagByName(NAME_A);
    assertEquals(NAME_A, tag.name);
  }

  @Test
  public void testGetTag_exist() {
    Tag tag = tagManagerDatastore.readOrCreateTagByName(NAME_A);
    Tag duplicateTag = tagManagerDatastore.readOrCreateTagByName(NAME_A);

    assertEquals(tag.id, duplicateTag.id);
    assertEquals(tag.name, duplicateTag.name);
  }

  @Test
  public void testReadTag_success() {
    Tag tagA = tagManagerDatastore.readOrCreateTagByName(NAME_A);
    tagManagerDatastore.readOrCreateTagByName(NAME_B);
    Tag tagARead = tagManagerDatastore.readTag(tagA.id);

    assertEquals(NAME_A, tagARead.name);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testReadTag_notExist() {
    tagManagerDatastore.readOrCreateTagByName(NAME_A);
    tagManagerDatastore.readTag(1000);
  }
}
