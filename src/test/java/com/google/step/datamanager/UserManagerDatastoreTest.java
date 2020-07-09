package com.google.step.datamanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.step.model.User;
import org.junit.After;
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
    assertEquals(EMAIL_A, user.email);
    assertEquals(EMAIL_A, user.username);
    assertEquals("", user.bio);
    assertFalse(user.photoBlobKey.isPresent());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testReadUserByEmail_userDoesNotExist() {
    userManagerDatastore.readUserByEmail(EMAIL_B);
  }

  @Test
  public void testReadUserByEmail_existingUser() {
    User user = userManagerDatastore.createUser(EMAIL_B);
    User userSecondTime = userManagerDatastore.readUserByEmail(EMAIL_B);
    assertEquals(user, userSecondTime);
  }

  @Test
  public void testReadUserById_success() {
    User userA = userManagerDatastore.createUser(EMAIL_A);
    User userARead = userManagerDatastore.readUser(userA.id);
    assertEquals(EMAIL_A, userARead.email);
    assertEquals(EMAIL_A, userARead.username);
    assertEquals("", userARead.bio);
    assertEquals(userA.id, userARead.id);
    assertFalse(userARead.photoBlobKey.isPresent());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testReadUserById_notExists() {
    userManagerDatastore.createUser(EMAIL_A);
    userManagerDatastore.readUser(100000); // a random id
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
    assertEquals(EMAIL_A, userARead.email);
    assertEquals(USERNAME_A, userARead.username);
    assertEquals(BIO_A, userARead.bio);
    assertFalse(userARead.photoBlobKey.isPresent());
  }

  @Test
  public void testUpdateUser_blobKey() {
    User userB = userManagerDatastore.createUser(EMAIL_B);
    User updatedUser = new User(userB.id, userB.email, USERNAME_B, BLOBKEY, BIO_B);
    userManagerDatastore.updateUser(updatedUser);
    User userBRead = userManagerDatastore.readUserByEmail(EMAIL_B);
    assertEquals(BLOBKEY, userBRead.photoBlobKey.get());
    updatedUser = new User(userB.id, userB.email, USERNAME_B, BIO_B);
    userManagerDatastore.updateUser(updatedUser);
    userBRead = userManagerDatastore.readUser(userB.id);
    assertFalse(userBRead.photoBlobKey.isPresent());
  }
}
