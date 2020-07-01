package com.google.step.datamanager;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.step.model.Tag;
import org.junit.After;
import org.junit.Assert;
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
    Tag tag = tagManagerDatastore.getTag(NAME_A);
    Assert.assertEquals(NAME_A, tag.name);
  }

  @Test
  public void testGetTag_exist() {
    Tag tag = tagManagerDatastore.getTag(NAME_A);
    Tag duplicatedTag = tagManagerDatastore.getTag(NAME_A);

    Assert.assertEquals(tag.id, duplicatedTag.id);
    Assert.assertEquals(tag.name, duplicatedTag.name);
  }

  @Test
  public void testReadTag_success() {
    Tag tag = tagManagerDatastore.getTag(NAME_A);
    Tag tabB = tagManagerDatastore.getTag(NAME_B);
    Tag tagRead = tagManagerDatastore.readTag(tag.id);

    Assert.assertEquals(NAME_A, tagRead.name);
  }

  @Test
  public void testReadTag_notExist() {
    Tag tag = tagManagerDatastore.getTag(NAME_A);
    Tag tagRead = tagManagerDatastore.readTag(1000);

    Assert.assertNull(tagRead);
  }
}
