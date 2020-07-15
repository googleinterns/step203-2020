package com.google.step;

import com.google.step.model.Deal;
import com.google.step.model.Restaurant;
import com.google.step.model.Tag;
import com.google.step.model.User;

public class TestConstants {
  // User
  public static final long USER_ID_A = 1;
  public static final long USER_ID_B = 2;
  public static final long USER_ID_C = 3;

  public static final String EMAIL_A = "testa@example.com";
  public static final String EMAIL_B = "testb@example.com";
  public static final String EMAIL_C = "testc@example.com";

  public static final String USERNAME_A = "Alice";
  public static final String USERNAME_A_NEW = "AliceW";
  public static final String USERNAME_B = "Bob";
  public static final String USERNAME_C = "Charlie";

  public static final String BLOBKEY_A = "a_blob_key";
  public static final String BLOBKEY_B = "a_blob_key_b";

  private static final String URL_PREFIX = "/api/images/";
  public static final String BLOBKEY_URL_A = URL_PREFIX + BLOBKEY_A;
  public static final String BLOBKEY_URL_B = URL_PREFIX + BLOBKEY_B;
  public static final String IMAGE_URL_A = "/api/images/" + BLOBKEY_A;
  public static final String IMAGE_URL_B = "/api/images/" + BLOBKEY_B;

  public static final String BIO_A = "Hello world.";
  public static final String BIO_A_NEW = "Hi, I'm Alice";
  public static final String BIO_B = "Hello I'm Bob.";
  public static final String BIO_C = "";

  public static final User USER_A = new User(USER_ID_A, EMAIL_A, USERNAME_A, BLOBKEY_A, BIO_A);
  public static final User USER_B = new User(USER_ID_B, EMAIL_B, USERNAME_B, BLOBKEY_B, BIO_B);
  public static final User USER_C = new User(USER_ID_C, EMAIL_C, USERNAME_C, BIO_C);

  public static final String USER_A_BRIEF_JSON =
      String.format(
          "{id: %d, username: \"%s\", picture: \"%s\"}", USER_ID_A, USERNAME_A, BLOBKEY_URL_A);
  public static final String USER_B_BRIEF_JSON =
      String.format(
          "{id: %d, username: \"%s\", picture: \"%s\"}", USER_ID_B, USERNAME_B, BLOBKEY_URL_B);
  public static final String USER_C_BRIEF_JSON =
      String.format("{id: %d, username: \"%s\"}", USER_ID_C, USERNAME_C);

  // Tag
  public static final String TAG_NAME_A = "1for1";
  public static final String TAG_NAME_B = "tea";
  public static final String TAG_NAME_C = "fish";
  public static final String TAG_NAME_D = "20%off";

  public static final long TAG_ID_A = 1;
  public static final long TAG_ID_B = 2;
  public static final long TAG_ID_C = 3;
  public static final long TAG_ID_D = 4;

  public static final Tag TAG_A = new Tag(TAG_ID_A, TAG_NAME_A);
  public static final Tag TAG_B = new Tag(TAG_ID_B, TAG_NAME_B);
  public static final Tag TAG_C = new Tag(TAG_ID_C, TAG_NAME_C);
  public static final Tag TAG_D = new Tag(TAG_ID_D, TAG_NAME_D);

  // Deal
  public static final long DEAL_ID_A = 1;
  public static final long DEAL_ID_B = 2;
  public static final long DEAL_ID_C = 3;
  public static final long DEAL_ID_D = 4;

  public static final String DESCRIPTION_A = "starbucks mocha 1-for-1";
  public static final String DESCRIPTION_B = "KFC chick buy 5 free 10";

  public static final String DATE_A = "2020-01-01";
  public static final String DATE_B = "2020-01-02";
  public static final String DATE_C = "2020-01-03";
  public static final String DATE_D = "2020-01-04";

  public static final String SOURCE_A = "www.example.com";
  public static final String SOURCE_B = "www.somethingelse.com";

  // Restaurant
  public static final long RESTAURANT_ID_A = 1;
  public static final long RESTAURANT_ID_B = 2;
  public static final long RESTAURANT_ID_C = 3;
  public static final long RESTAURANT_ID_D = 4;

  public static final String RESTAURANT_NAME_A = "A";
  public static final String RESTAURANT_NAME_B = "B";

  public static final Restaurant RESTAURANT_A =
      new Restaurant(RESTAURANT_ID_A, RESTAURANT_NAME_A, BLOBKEY_A);

  // Comment
  public static final long COMMENT_ID_A = 1;
  public static final long COMMENT_ID_B = 2;
  public static final long COMMENT_ID_C = 3;
  public static final long COMMENT_ID_D = 4;

  public static final String CONTENT_A = "Hello world";
  public static final String CONTENT_B = "Hello world2";

  public static final String TIME_A = "2020-07-10T10:15:30";
  public static final String TIME_B = "2020-07-10T12:15:30";

  // Deal class
  public static final Deal DEAL_A =
      new Deal(
          DEAL_ID_A,
          DESCRIPTION_A,
          BLOBKEY_A,
          DATE_A,
          DATE_B,
          SOURCE_A,
          USER_ID_A,
          RESTAURANT_ID_A);

  public static final Deal DEAL_B =
      new Deal(
          DEAL_ID_B,
          DESCRIPTION_B,
          BLOBKEY_B,
          DATE_C,
          DATE_D,
          SOURCE_B,
          USER_ID_B,
          RESTAURANT_ID_B);
}
