package com.google.step;

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
  public static final String BLOBKEY_C = "a_blob_key_c";

  public static final String BIO_A = "Hello world.";
  public static final String BIO_A_NEW = "Hi, I'm Alice";
  public static final String BIO_B = "Hello I'm Bob.";
  public static final String BIO_C = "";

  public static final String USER_A_BRIEF_JSON =
      String.format(
          "{id: %d, username: \"%s\", photoBlobKey: \"%s\"}", USER_ID_A, USERNAME_A, BLOBKEY_A);
  public static final String USER_B_BRIEF_JSON =
      String.format(
          "{id: %d, username: \"%s\", photoBlobKey: \"%s\"}", USER_ID_B, USERNAME_B, BLOBKEY_B);
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

  // Deal
  public static final long DEAL_ID_A = 1;
  public static final long DEAL_ID_B = 2;
  public static final long DEAL_ID_C = 3;
  public static final long DEAL_ID_D = 4;

  public static final String DESCRIPTION_A = "starbucks mocha 1-for-1";
  public static final String DESCRIPTION_B = "KFC chick buy 5 free 10";
  public static final String DESCRIPTION_C = "bubble tea 50% off";

  public static final String DATE_A = "2020-01-01";
  public static final String DATE_B = "2020-01-02";
  public static final String DATE_C = "2020-01-03";
  public static final String DATE_D = "2020-01-04";

  public static final String SOURCE_A = "www.example.com";
  public static final String SOURCE_B = "www.somethingelse.com";
  public static final String SOURCE_C = "www.helloworld.com";

  public static final String TIME_A = "2020-07-10T10:15:30";
  public static final String TIME_B = "2020-07-10T12:15:30";
  public static final String TIME_C = "2020-07-10T14:15:30";

  public static final long VOTE_A = 0;

  // Restaurant
  public static final long RESTAURANT_ID_A = 1;
  public static final long RESTAURANT_ID_B = 2;
  public static final long RESTAURANT_ID_C = 3;
  public static final long RESTAURANT_ID_D = 4;

  // Comment
  public static final long COMMENT_ID_A = 1;
  public static final long COMMENT_ID_B = 2;
  public static final long COMMENT_ID_C = 3;
  public static final long COMMENT_ID_D = 4;

  // Deal Brief JSON
  public static final String DEAL_A_BRIEF_JSON =
      String.format(
          "{restaurant: %d, description: \"%s\", votes: %d, id: %d, pic: \"%s\", poster: %d}",
          RESTAURANT_ID_A, DESCRIPTION_A, VOTE_A, DEAL_ID_A, BLOBKEY_A, USER_ID_A);
  public static final String DEAL_B_BRIEF_JSON =
      String.format(
          "{restaurant: %d, description: \"%s\", votes: %d, id: %d, pic: \"%s\", poster: %d}",
          RESTAURANT_ID_B, DESCRIPTION_B, VOTE_A, DEAL_ID_B, BLOBKEY_B, USER_ID_B);
  public static final String DEAL_C_BRIEF_JSON =
      String.format(
          "{restaurant: %d, description: \"%s\", votes: %d, id: %d, pic: \"%s\", poster: %d}",
          RESTAURANT_ID_C, DESCRIPTION_C, VOTE_A, DEAL_ID_C, BLOBKEY_C, USER_ID_C);
}
