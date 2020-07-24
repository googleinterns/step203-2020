package com.google.step.servlets;

import static com.google.step.TestConstants.USER_A;
import static com.google.step.TestConstants.USER_B;
import static com.google.step.TestConstants.USER_ID_A;
import static com.google.step.TestConstants.USER_ID_B;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.step.datamanager.FollowManager;
import com.google.step.datamanager.MailManager;
import com.google.step.datamanager.UserManager;
import com.google.step.model.User;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class MailServletTest {
  private UserManager mockUserManager;
  private MailManager mockMailManager;
  private FollowManager mockFollowManager;
  private HttpServletRequest mockRequest;
  private HttpServletResponse mockResponse;

  private MailServlet mailServlet;

  @Before
  public void setUp() {
    mockRequest = mock(HttpServletRequest.class);
    mockResponse = mock(HttpServletResponse.class);
    mockUserManager = mock(UserManager.class);
    mockFollowManager = mock(FollowManager.class);
    mockMailManager = mock(MailManager.class);
    mailServlet = new MailServlet(mockMailManager, mockUserManager, mockFollowManager);
  }

  @Test
  public void testDoPost_success() throws IOException {
    when(mockRequest.getParameter("poster-id")).thenReturn(USER_ID_A + "");
    when(mockUserManager.readUser(USER_ID_A)).thenReturn(USER_A);

    List<Long> followerIds = Arrays.asList(USER_ID_B);
    when(mockFollowManager.getFollowerIdsOfUser(USER_ID_A)).thenReturn(new HashSet<>(followerIds));
    when(mockUserManager.readUsers(followerIds)).thenReturn(Arrays.asList(USER_B));

    mailServlet.doPost(mockRequest, mockResponse);
    ArgumentCaptor<User> posterCaptor = ArgumentCaptor.forClass(User.class);
    ArgumentCaptor<List> recipientsCaptor = ArgumentCaptor.forClass(List.class);

    verify(mockMailManager)
        .sendNewPostNotificationMail(recipientsCaptor.capture(), posterCaptor.capture());
    assertEquals(Arrays.asList(USER_B), recipientsCaptor.getValue());
    assertEquals(USER_A, posterCaptor.getValue());
  }

  @Test
  public void testDoPost_posterIdDoesNotExist() throws IOException {
    when(mockRequest.getParameter("poster-id")).thenReturn("1000");
    when(mockUserManager.readUser(1000)).thenThrow(new IllegalArgumentException());

    mailServlet.doPost(mockRequest, mockResponse);

    verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_missingPosterId() throws IOException {
    when(mockRequest.getParameter("poster-id")).thenReturn(null);

    mailServlet.doPost(mockRequest, mockResponse);

    verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }
}
