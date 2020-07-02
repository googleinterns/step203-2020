package com.google.step.datamanager;

import static org.junit.Assert.assertEquals;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.step.model.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class UserManagerDatastoreTest {

  private static final String EMAIL_A = "testa@example.com";
  private static final String EMAIL_B = "testb@example.com";

  private static final String USERNAME_A = "Alice";
  private static final String USERNAME_B = "Bob";

  private static final String BIO_A = "Hello world.";
  private static final String BIO_B = "Hello I'm Bob.";

  private static final String BLOBKEY = "a_blob_key";

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig().setApplyAllHighRepJobPolicy());

  private final UserManagerDatastore userManagerDatastore = new UserManagerDatastore();

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testCreateUser() {
    User user = userManagerDatastore.createUser(EMAIL_A);
    Assert.assertEquals(EMAIL_A, user.email);
    Assert.assertEquals(EMAIL_A, user.username);
    Assert.assertEquals("", user.bio);
    Assert.assertFalse(user.photoBlobKey.isPresent());
  }

  @Test
  public void testReadUserByEmail_firstTime() {
    User user = userManagerDatastore.readOrCreateUserByEmail(EMAIL_B);
    Assert.assertEquals(EMAIL_B, user.email);
    Assert.assertEquals(EMAIL_B, user.username);
    Assert.assertEquals("", user.bio);
    Assert.assertFalse(user.photoBlobKey.isPresent());
  }

  @Test
  public void testReadUserByEmailExists() {
    User user = userManagerDatastore.createUser(EMAIL_B);
    User userSecondTime = userManagerDatastore.readOrCreateUserByEmail(EMAIL_B);
    Assert.assertEquals(EMAIL_B, userSecondTime.email);
    Assert.assertEquals(EMAIL_B, userSecondTime.username);
    Assert.assertEquals("", userSecondTime.bio);
    Assert.assertEquals(user.id, userSecondTime.id);
    Assert.assertFalse(userSecondTime.photoBlobKey.isPresent());
  }

  @Test
  public void testReadUserById_success() {
    User userA = userManagerDatastore.createUser(EMAIL_A);
    User userARead = userManagerDatastore.readUser(userA.id);
    Assert.assertEquals(EMAIL_A, userARead.email);
    Assert.assertEquals(EMAIL_A, userARead.username);
    Assert.assertEquals("", userARead.bio);
    Assert.assertEquals(userA.id, userARead.id);
    Assert.assertFalse(userARead.photoBlobKey.isPresent());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testReadUserById_notExists() {
    userManagerDatastore.createUser(EMAIL_A);
    User _ = userManagerDatastore.readUser(100000); // a random id
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDeleteUser() {
    User userA = userManagerDatastore.createUser(EMAIL_A);
    userManagerDatastore.deleteUser(userA.id);
    userManagerDatastore.readUser(userA.id);
  }

  @Test
  public void testUpdateUser() {
    User userA = userManagerDatastore.createUser(EMAIL_A);
    User updatedUser = new User(userA.id, userA.email, USERNAME_A, null, BIO_A);
    userManagerDatastore.updateUser(updatedUser);
    User userARead = userManagerDatastore.readUser(userA.id);
    Assert.assertEquals(EMAIL_A, userARead.email);
    Assert.assertEquals(USERNAME_A, userARead.username);
    Assert.assertEquals(BIO_A, userARead.bio);
    Assert.assertFalse(userARead.photoBlobKey.isPresent());
  }

  @Test
  public void testUpdateUser_blobKey() {
    User userB = userManagerDatastore.createUser(EMAIL_B);
    User updatedUser = new User(userB.id, userB.email, USERNAME_B, BLOBKEY, BIO_B);
    userManagerDatastore.updateUser(updatedUser);
    User userBRead = userManagerDatastore.readOrCreateUserByEmail(EMAIL_B);
    assertEquals(BLOBKEY, userBRead.photoBlobKey.get());
    updatedUser = new User(userB.id, userB.email, USERNAME_B, BIO_B);
    userManagerDatastore.updateUser(updatedUser);
    userBRead = userManagerDatastore.readUser(userB.id);
    Assert.assertFalse(userBRead.photoBlobKey.isPresent());
  }
}
