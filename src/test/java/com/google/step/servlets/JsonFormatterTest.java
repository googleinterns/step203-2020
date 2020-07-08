package com.google.step.servlets;

import static com.google.step.TestConstants.BIO_A;
import static com.google.step.TestConstants.BLOBKEY_URL_A;
import static com.google.step.TestConstants.EMAIL_A;
import static com.google.step.TestConstants.USERNAME_A;
import static com.google.step.TestConstants.USER_A;
import static com.google.step.TestConstants.USER_B;
import static com.google.step.TestConstants.USER_B_BRIEF_JSON;
import static com.google.step.TestConstants.USER_C;
import static com.google.step.TestConstants.USER_C_BRIEF_JSON;
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
            "{id:%d,email:\"%s\",username:\"%s\",bio:\"%s\",picture:\"%s\","
                + "dealsUploaded:[],"
                + "following:[%s],"
                + "followers:[%s],"
                + "tagsFollowed:[],"
                + "restaurantsFollowed:[]}",
            USER_ID_A,
            EMAIL_A,
            USERNAME_A,
            BIO_A,
            BLOBKEY_URL_A,
            USER_B_BRIEF_JSON,
            USER_C_BRIEF_JSON);
    try {
      JSONAssert.assertEquals(expected, userJson, JSONCompareMode.STRICT);
    } catch (JSONException e) {
      Assert.fail();
      e.printStackTrace();
    }
  }
}
