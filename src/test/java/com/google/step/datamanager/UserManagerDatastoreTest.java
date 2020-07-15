package com.google.step.datamanager;

import static com.google.step.TestConstants.BIO_A;
import static com.google.step.TestConstants.BIO_B;
import static com.google.step.TestConstants.BLOBKEY_B;
import static com.google.step.TestConstants.EMAIL_A;
import static com.google.step.TestConstants.EMAIL_B;
import static com.google.step.TestConstants.USERNAME_A;
import static com.google.step.TestConstants.USERNAME_B;
import static com.google.step.TestConstants.USER_ID_C;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.step.model.User;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class UserManagerDatastoreTest {

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
    User updatedUser = new User(userB.id, userB.email, USERNAME_B, BLOBKEY_B, BIO_B);
    userManagerDatastore.updateUser(updatedUser);
    User userBRead = userManagerDatastore.readUserByEmail(EMAIL_B);
    assertEquals(BLOBKEY_B, userBRead.photoBlobKey.get());
    updatedUser = new User(userB.id, userB.email, USERNAME_B, BIO_B);
    userManagerDatastore.updateUser(updatedUser);
    userBRead = userManagerDatastore.readUser(userB.id);
    assertFalse(userBRead.photoBlobKey.isPresent());
  }

  @Test
  public void testReadUsers() {
    User userA = userManagerDatastore.createUser(EMAIL_A);
    User userB = userManagerDatastore.createUser(EMAIL_B);
    List<Long> ids = Arrays.asList(userA.id, userB.id);
    List<User> users = userManagerDatastore.readUsers(ids);

    assertThat(users, containsInAnyOrder(userA, userB));
  }

  @Test
  public void testReadUsers_idDoesNotExist() {
    User userA = userManagerDatastore.createUser(EMAIL_A);
    User userB = userManagerDatastore.createUser(EMAIL_B);
    List<Long> ids = Arrays.asList(userA.id, userB.id, USER_ID_C);
    List<User> users = userManagerDatastore.readUsers(ids);
    assertThat(users, containsInAnyOrder(userA, userB));
  }
}
