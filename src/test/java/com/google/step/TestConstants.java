package com.google.step;

import com.google.step.model.Comment;
import com.google.step.model.Deal;
import com.google.step.model.Restaurant;
import com.google.step.model.Tag;
import com.google.step.model.User;

public class TestConstants {

  private static final String URL_PREFIX = "/api/images/";
  public static final String BLOBKEY_A = "a_blob_key";
  public static final String BLOBKEY_B = "a_blob_key_b";
  public static final String BLOBKEY_C = "a_blob_key_c";

  public static final String BLOBKEY_URL_A = URL_PREFIX + BLOBKEY_A;
  public static final String BLOBKEY_URL_B = URL_PREFIX + BLOBKEY_B;

  public static final String DEFAULT_PHOTO_URL = "/images/default-profile-pic.svg";

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
      String.format(
          "{id: %d, username: \"%s\", picture: \"%s\"}", USER_ID_C, USERNAME_C, DEFAULT_PHOTO_URL);

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

  public static final String TAG_A_JSON =
      String.format("{\"id\": %d, \"name\": %s}", TAG_ID_A, TAG_NAME_A);

  public static final String TAG_B_JSON =
      String.format("{\"id\": %d, \"name\": %s}", TAG_ID_B, TAG_NAME_B);

  public static final String TAG_LIST_ABC = String.join(",", TAG_NAME_A, TAG_NAME_B, TAG_NAME_C);

  // Place
  public static final String PLACE_ID_A = "a";
  public static final String PLACE_ID_B = "b";
  public static final String PLACE_ID_C = "c";
  public static final String PLACE_ID_D = "d";

  // Restaurant
  public static final long RESTAURANT_ID_A = 1;
  public static final long RESTAURANT_ID_B = 2;
  public static final long RESTAURANT_ID_C = 3;
  public static final long RESTAURANT_ID_D = 4;

  public static final String RESTAURANT_NAME_A = "A";
  public static final String RESTAURANT_NAME_B = "B";
  public static final String RESTAURANT_NAME_C = "C";

  public static final String RESTAURANT_PHOTO_REFERENCE_A = "a_photo_reference";
  public static final String RESTAURANT_PHOTO_REFERENCE_URL_A =
      Restaurant.getImageUrlFromPhotoReference(RESTAURANT_PHOTO_REFERENCE_A);

  public static final Restaurant RESTAURANT_A =
      Restaurant.createRestaurantWithBlobkey(RESTAURANT_ID_A, RESTAURANT_NAME_A, BLOBKEY_A);

  public static final Restaurant RESTAURANT_B =
      Restaurant.createRestaurantWithBlobkey(RESTAURANT_ID_B, RESTAURANT_NAME_B, BLOBKEY_B);

  public static final String RESTAURANT_A_BRIEF_JSON =
      String.format(
          "{\"id\": %d," + "\"name\": \"%s\"," + "\"photoUrl\": \"%s\"}",
          RESTAURANT_ID_A, RESTAURANT_NAME_A, BLOBKEY_URL_A);

  public static final String RESTAURANT_B_BRIEF_JSON =
      String.format(
          "{\"id\": %d," + "\"name\": \"%s\"," + "\"photoUrl\": \"%s\"}",
          RESTAURANT_ID_B, RESTAURANT_NAME_B, BLOBKEY_URL_B);

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

  public static final int VOTE_A = 0;

  // Deal Brief for Home Page
  public static final String HOME_DEAL_A_JSON =
      String.format(
          "{restaurant: {id: %d, name: \"%s\", photoUrl:  \"%s\"}, description: \"%s\", votes: %d, id: %d, pic: \"%s\", poster:{id: %d, username: \"%s\", picture: \"%s\"}, tags: [{id: %d, name: \"%s\"}], timestamp: \"%s\"}",
          RESTAURANT_ID_A,
          RESTAURANT_NAME_A,
          BLOBKEY_URL_A,
          DESCRIPTION_A,
          VOTE_A,
          DEAL_ID_A,
          BLOBKEY_URL_A,
          USER_ID_A,
          USERNAME_A,
          BLOBKEY_URL_A,
          TAG_ID_A,
          TAG_NAME_A,
          TIME_A);

  public static final Deal DEAL_A =
      new Deal(
          DEAL_ID_A,
          DESCRIPTION_A,
          BLOBKEY_A,
          DATE_A,
          DATE_B,
          SOURCE_A,
          USER_ID_A,
          RESTAURANT_ID_A,
          TIME_A);

  public static final Deal DEAL_B =
      new Deal(
          DEAL_ID_B,
          DESCRIPTION_B,
          BLOBKEY_B,
          DATE_C,
          DATE_D,
          SOURCE_B,
          USER_ID_B,
          RESTAURANT_ID_B,
          TIME_B);

  public static final Deal DEAL_C =
      new Deal(
          DEAL_ID_C,
          DESCRIPTION_C,
          BLOBKEY_C,
          DATE_A,
          DATE_B,
          SOURCE_C,
          USER_ID_C,
          RESTAURANT_ID_C,
          TIME_C);

  public static final String DEAL_A_BRIEF_JSON =
      String.format(
          "{\"id\": %d,\"description\": \"%s\",\"image\": \"%s\"}",
          DEAL_ID_A, DESCRIPTION_A, BLOBKEY_URL_A, RESTAURANT_ID_A, 0, USER_ID_A);

  public static final String DEAL_B_BRIEF_JSON =
      String.format(
          "{\"id\": %d,\"description\": \"%s\",\"image\": \"%s\"}",
          DEAL_ID_B, DESCRIPTION_B, BLOBKEY_URL_B, RESTAURANT_ID_B, 0, USER_ID_B);

  // Comment
  public static final long COMMENT_ID_A = 1;
  public static final long COMMENT_ID_B = 2;
  public static final long COMMENT_ID_C = 3;
  public static final long COMMENT_ID_D = 4;

  public static final String CONTENT_A = "Hello world";
  public static final String CONTENT_B = "Hello world2";

  public static final String SENTIMENT_A = "0.5";
  public static final String SENTIMENT_B = "1.0";
  public static final String TOKEN_A = "ghcnyoawecf";
  public static final String TOKEN_B = "bhveigsurdk";

  public static final Comment COMMENT_A =
      new Comment(COMMENT_ID_A, DEAL_ID_A, USER_ID_A, CONTENT_A, TIME_A, SENTIMENT_A);

  public static final Comment COMMENT_B =
      new Comment(COMMENT_ID_B, DEAL_ID_A, USER_ID_B, CONTENT_B, TIME_B, SENTIMENT_B);

  public static final Comment UPDATED_COMMENT_A =
      new Comment(COMMENT_ID_A, DEAL_ID_A, USER_ID_A, CONTENT_B, TIME_A, SENTIMENT_B);

  public static final String COMMENT_A_JSON =
      String.format(
          "{"
              + "\"id\": %d,"
              + "\"dealId\": %d,"
              + "\"content\": \"%s\","
              + "\"timestamp\": \"%s\","
              + "\"user\": {"
              + "\"id\": %d,"
              + "\"picture\": \"%s\","
              + "\"username\": \"%s\""
              + "},"
              + "\"sentiment\": \"%s\""
              + "}",
          COMMENT_ID_A,
          DEAL_ID_A,
          CONTENT_A,
          TIME_A,
          USER_ID_A,
          BLOBKEY_URL_A,
          USERNAME_A,
          SENTIMENT_A);

  public static final String COMMENT_B_JSON =
      String.format(
          "{"
              + "\"id\": %d,"
              + "\"dealId\": %d,"
              + "\"content\": \"%s\","
              + "\"timestamp\": \"%s\","
              + "\"user\": {"
              + "\"id\": %d,"
              + "\"picture\": \"%s\","
              + "\"username\": \"%s\""
              + "},"
              + "\"sentiment\": \"%s\""
              + "}",
          COMMENT_ID_B,
          DEAL_ID_A,
          CONTENT_B,
          TIME_B,
          USER_ID_B,
          BLOBKEY_URL_B,
          USERNAME_B,
          SENTIMENT_B);
}
