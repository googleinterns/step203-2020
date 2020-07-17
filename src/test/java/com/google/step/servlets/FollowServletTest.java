package com.google.step.servlets;

import static com.google.step.TestConstants.RESTAURANT_ID_A;
import static com.google.step.TestConstants.USER_ID_A;
import static com.google.step.TestConstants.USER_ID_B;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.appengine.repackaged.com.google.api.client.http.HttpStatusCodes;
import com.google.step.datamanager.FollowManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class FollowServletTest {

  private static final long ID = 123;

  private FollowServlet servlet;
  private FollowManager followManager;

  @Before
  public void setUp() {
    followManager = mock(FollowManager.class);
    servlet = new FollowServlet(followManager);
  }

  @Test
  public void testDoPost_restaurant_success() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/restaurants/" + ID);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    servlet.doPost(request, response);

    verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(followManager).followRestaurant(anyLong(), eq(ID));
  }

  @Test
  public void testDoPost_tag_success() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/tags/" + ID);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    servlet.doPost(request, response);

    verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(followManager).followTag(anyLong(), eq(ID));
  }

  @Test
  public void testDoPost_user_success() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/users/" + ID);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    servlet.doPost(request, response);

    verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(followManager).followUser(anyLong(), eq(ID));
  }

  @Test
  public void testDoPost_invalidName_badRequest() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/trash/123");

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_invalidId_badRequest() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/tags/123/trash");

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_missingId_badRequest() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/");

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_emptyPath_badRequest() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("");

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoGet_isFollowingRestaurant() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("restaurantId")).thenReturn(RESTAURANT_ID_A + "");
    when(request.getParameter("followerId")).thenReturn(USER_ID_A + "");
    when(followManager.isFollowingRestaurant(USER_ID_A, RESTAURANT_ID_A)).thenReturn(true);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    servlet.doGet(request, response);

    assertTrue(stringWriter.toString().contains("true"));
  }

  @Test
  public void testDoGet_isFollowingRestaurant_invalidId() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("restaurantId")).thenReturn(RESTAURANT_ID_A + "xxx");
    when(request.getParameter("followerId")).thenReturn(USER_ID_A + "");
    when(followManager.isFollowingRestaurant(USER_ID_A, RESTAURANT_ID_A)).thenReturn(true);

    servlet.doGet(request, response);

    verify(response).setStatus(HttpStatusCodes.STATUS_CODE_BAD_REQUEST);
  }

  @Test
  public void testDoGet_isFollowingUser() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("userId")).thenReturn(USER_ID_A + "");
    when(request.getParameter("followerId")).thenReturn(USER_ID_B + "");
    when(followManager.isFollowingUser(USER_ID_B, USER_ID_A)).thenReturn(false);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    servlet.doGet(request, response);

    assertTrue(stringWriter.toString().contains("false"));
  }

  @Test
  public void testDoGet_isFollowing_invalidParamters() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("followerId")).thenReturn(USER_ID_B + "");

    servlet.doGet(request, response);

    verify(response).setStatus(HttpStatusCodes.STATUS_CODE_BAD_REQUEST);
  }
}
