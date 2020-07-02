package com.google.step.datamanager;

import com.google.step.model.User;

/** A UserManager handling user datastore operations. */
public interface UserManager {

  /**
   * Returns a User object when the user with the given email. Creates a User object if it does not
   * exist.
   *
   * @param email email of the user.
   * @return User object with the email.
   */
  public User readOrCreateUserByEmail(String email);

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
