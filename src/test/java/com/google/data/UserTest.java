package com.google.data;

import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class UserTest {
  @Test
  public void testInitialization() {
    User user = User.builder().setEmail("test@example.com").setId("123").build();
    Assert.assertEquals("123", user.getId());
  }

  @Test
  public void testJson() {
    User user =
        User.builder().setEmail("test@example.com").setId("123").setUsername("test").build();
    Gson gson = new Gson();
    String json = gson.toJson(user);
    System.out.println(json);
  }
}
