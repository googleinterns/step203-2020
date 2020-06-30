package com.google.step.datamanager;

import com.google.step.model.User;
import java.util.Optional;

/** A UserManager handling user datastore operations. */
public interface UserManager {

  /**
   * Returns a User object when the user with the email logs in.
   *
   * @param email email of the user who logs in.
   * @return User object with the email.
   */
  public User userLogin(String email);

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
   * @return User object with the id.
   */
  public User readUser(long id);

  /**
   * Updates a user's info with the given id.
   *
   * @param id id of the user.
   * @param email email of the user.
   * @param username username of the user.
   * @param photoBlobKey blob key of the profile photo if it exists.
   * @param bio bio of the user.
   */
  public void updateUser(
      long id, String email, String username, Optional<String> photoBlobKey, String bio);

  /**
   * Deletes a user with the given id.
   *
   * @param id id of the user.
   */
  public void deleteUser(long id);
}
