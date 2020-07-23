package com.google.step.datamanager;

import com.google.step.model.User;
import java.util.List;

/** A UserManager handling user datastore operations. */
public interface UserManager {

  /**
   * Returns a User object with the given email if it exists.
   *
   * @param email email of the user.
   * @return User object with the email.
   * @throws IllegalArgumentException if the user object does not exist.
   */
  public User readUserByEmail(String email) throws IllegalArgumentException;

  /**
   * Creates a User object with the given email.
   *
   * @param email email of the user.
   * @return User object with the email.
   */
  public User createUser(String email);

  /**
   * Returns a User object with the given id.
   *
   * @param id id of the user.
   * @return user object with the id.
   * @throws IllegalArgumentException if user id does not exist.
   */
  public User readUser(long id) throws IllegalArgumentException;

  /**
   * Returns a list of users identified by the list of ids.
   *
   * @param ids a list of user ids.
   * @return a list of users.
   */
  public List<User> readUsers(List<Long> ids);

  /**
   * Updates a user's info with a partially populated user object. Updates fields except email that
   * are not null and keeps other field unchanged.
   *
   * @param user a user object with updated value.
   * @throws IllegalArgumentException if user id does not exist.
   */
  public void updateUser(User user) throws IllegalArgumentException;

  /**
   * Deletes a user with the given id.
   *
   * @param id id of the user.
   */
  public void deleteUser(long id);
}
