package com.google.step.servlets;

import static com.google.step.TestConstants.DATE_A;
import static com.google.step.TestConstants.DATE_B;
import static com.google.step.TestConstants.DEAL_A;
import static com.google.step.TestConstants.DEAL_ID_A;
import static com.google.step.TestConstants.DESCRIPTION_A;
import static com.google.step.TestConstants.IMAGE_URL_A;
import static com.google.step.TestConstants.RESTAURANT_A;
import static com.google.step.TestConstants.SOURCE_A;
import static com.google.step.TestConstants.TAG_A;
import static com.google.step.TestConstants.TAG_B;
import static com.google.step.TestConstants.USERNAME_A;
import static com.google.step.TestConstants.USER_A;
import static com.google.step.TestConstants.USER_ID_A;

import com.google.step.model.Deal;
import com.google.step.model.Restaurant;
import com.google.step.model.Tag;
import com.google.step.model.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

@RunWith(JUnit4.class)
public class JsonFormatterTest {
  private static final long ID_A = 1;
  private static final long ID_B = 2;
  private static final long ID_C = 3;

  private static final String EMAIL_A = "testa@example.com";
  private static final String EMAIL_B = "testb@example.com";
  private static final String EMAIL_C = "testc@example.com";

  private static final String USERNAME_A = "Alice";
  private static final String USERNAME_B = "Bob";
  private static final String USERNAME_C = "Charlie";

  private static final String BLOBKEY_A = "a_blob_key";
  private static final String BLOBKEY_B = "a_blob_key_b";

  private static final String BIO_A = "Hello world.";
  private static final String BIO_B = "Hello, I'm Bob!";
  private static final String BIO_C = "";

  private static final User USER_A = new User(ID_A, EMAIL_A, USERNAME_A, BLOBKEY_A, BIO_A);
  private static final User USER_B = new User(ID_B, EMAIL_B, USERNAME_B, BLOBKEY_B, BIO_B);
  private static final User USER_C = new User(ID_C, EMAIL_C, USERNAME_C, BIO_C);

  private static final String USER_B_BRIEF_JSON =
      String.format(
          "{id: %d, username: \"%s\", photoBlobKey: \"%s\"}", ID_B, USERNAME_B, BLOBKEY_B);
  private static final String USER_C_BRIEF_JSON =
      String.format("{id: %d, username: \"%s\"}", ID_C, USERNAME_C);

  @Test
  public void testGetUserJson() {
    // TODO: Add deal, tag, restaurant
    List<Deal> deals = new ArrayList<>();
    List<User> following = Arrays.asList(USER_B);
    List<User> followers = Arrays.asList(USER_C);
    List<Tag> tags = new ArrayList<>();
    List<Restaurant> restaurants = new ArrayList<>();
    String userJson =
        JsonFormatter.getUserJson(USER_A, deals, following, followers, tags, restaurants);
    String expected =
        String.format(
            "{id:%d,email:\"%s\",username:\"%s\",bio:\"%s\",photoBlobKey:\"%s\","
                + "dealsUploaded:[],"
                + "following:[%s],"
                + "followers:[%s],"
                + "tagsFollowed:[],"
                + "restaurantsFollowed:[]}",
            ID_A, EMAIL_A, USERNAME_A, BIO_A, BLOBKEY_A, USER_B_BRIEF_JSON, USER_C_BRIEF_JSON);
    try {
      JSONAssert.assertEquals(expected, userJson, JSONCompareMode.STRICT);
    } catch (JSONException e) {
      Assert.fail();
      e.printStackTrace();
    }
  }

  @Test
  public void testGetDealJson() {
    List<Tag> tags = Arrays.asList(TAG_A, TAG_B);
    String dealJson = JsonFormatter.getDealJson(DEAL_A, RESTAURANT_A, USER_A, tags, 123);
    String expected =
        String.format(
            "{"
                + "\"id\": %d,"
                + "\"description\": \"%s\","
                + "\"image\": \"%s\","
                + "\"start\": \"%s\","
                + "\"end\": \"%s\","
                + "\"source\": \"%s\","
                + "\"poster\": {"
                + "\"id\": %d,"
                + "\"username\": \"%s\""
                + "},"
                // + "\"restaurant\": \"Restaurant Name\","
                // + "\"tags\": [],"
                + "\"votes\": 123"
                + "}",
            DEAL_ID_A, DESCRIPTION_A, IMAGE_URL_A, DATE_A, DATE_B, SOURCE_A, USER_ID_A, USERNAME_A);

    try {
      // TODO change LENIENT to STRICT after the todo fields in the method are implemented
      JSONAssert.assertEquals(expected, dealJson, JSONCompareMode.LENIENT);
    } catch (JSONException e) {
      Assert.fail();
      e.printStackTrace();
    }
  }
}
