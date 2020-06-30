package com.google.step.datamanager;

import static org.junit.Assert.assertEquals;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.step.model.User;
import java.util.Optional;
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
  public void testUserLogin_firstTime() {
    User user = userManagerDatastore.userLogin(EMAIL_B);
    Assert.assertEquals(EMAIL_B, user.email);
    Assert.assertEquals(EMAIL_B, user.username);
    Assert.assertEquals("", user.bio);
    Assert.assertFalse(user.photoBlobKey.isPresent());
  }

  @Test
  public void testUserLoginBefore() {
    User user = userManagerDatastore.userLogin(EMAIL_B);
    User userSecondTime = userManagerDatastore.userLogin(EMAIL_B);
    Assert.assertEquals(EMAIL_B, userSecondTime.email);
    Assert.assertEquals(EMAIL_B, userSecondTime.username);
    Assert.assertEquals("", userSecondTime.bio);
    Assert.assertEquals(user.id, userSecondTime.id);
    Assert.assertFalse(userSecondTime.photoBlobKey.isPresent());
  }

  @Test
  public void testReadUser_success() {
    User userA = userManagerDatastore.userLogin(EMAIL_A);
    User userARead = userManagerDatastore.readUser(userA.id);
    Assert.assertEquals(EMAIL_A, userARead.email);
    Assert.assertEquals(EMAIL_A, userARead.username);
    Assert.assertEquals("", userARead.bio);
    Assert.assertEquals(userA.id, userARead.id);
    Assert.assertFalse(userARead.photoBlobKey.isPresent());
  }

  @Test
  public void testReadUser_notExists() {
    userManagerDatastore.userLogin(EMAIL_A);
    User userRead = userManagerDatastore.readUser(100000); // a random id
    Assert.assertNull(userRead);
  }

  @Test
  public void testDeleteUser() {
    User userA = userManagerDatastore.userLogin(EMAIL_A);
    userManagerDatastore.deleteUser(userA.id);
    Assert.assertNull(userManagerDatastore.readUser(userA.id));
  }

  @Test
  public void testUpdateUser() {
    User userA = userManagerDatastore.userLogin(EMAIL_A);
    userManagerDatastore.updateUser(userA.id, userA.email, USERNAME_A, Optional.empty(), BIO_A);
    User userARead = userManagerDatastore.readUser(userA.id);
    Assert.assertEquals(EMAIL_A, userARead.email);
    Assert.assertEquals(USERNAME_A, userARead.username);
    Assert.assertEquals(BIO_A, userARead.bio);
    Assert.assertFalse(userARead.photoBlobKey.isPresent());
  }

  @Test
  public void testUpdateUser_blobKey() {
    User userB = userManagerDatastore.userLogin(EMAIL_B);
    userManagerDatastore.updateUser(userB.id, userB.email, USERNAME_B, Optional.of(BLOBKEY), BIO_B);
    User userBRead = userManagerDatastore.userLogin(EMAIL_B);
    assertEquals(BLOBKEY, userBRead.photoBlobKey.get());
    userManagerDatastore.updateUser(userB.id, userB.email, USERNAME_B, Optional.empty(), BIO_B);
    userBRead = userManagerDatastore.readUser(userB.id);
    Assert.assertFalse(userBRead.photoBlobKey.isPresent());
  }
}
