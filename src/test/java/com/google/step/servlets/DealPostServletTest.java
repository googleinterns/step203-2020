package com.google.step.servlets;

import static com.google.step.TestConstants.BLOBKEY_A;
import static com.google.step.TestConstants.DATE_A;
import static com.google.step.TestConstants.DATE_B;
import static com.google.step.TestConstants.DEAL_ID_A;
import static com.google.step.TestConstants.DESCRIPTION_A;
import static com.google.step.TestConstants.EMAIL_A;
import static com.google.step.TestConstants.RESTAURANT_ID_A;
import static com.google.step.TestConstants.SOURCE_A;
import static com.google.step.TestConstants.USER_A;
import static com.google.step.TestConstants.USER_ID_A;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.step.datamanager.DealManager;
import com.google.step.datamanager.UserManager;
import com.google.step.model.Deal;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ImageUploader.class)
public class DealPostServletTest {

  private static final String RESTAURANT_ID_A_STRING = Long.toString(RESTAURANT_ID_A);

  private static final Deal DEAL =
      new Deal(
          DEAL_ID_A,
          DESCRIPTION_A,
          BLOBKEY_A,
          DATE_A,
          DATE_B,
          SOURCE_A,
          USER_ID_A,
          RESTAURANT_ID_A);

  private HttpServletRequest request;
  private DealPostServlet servlet;
  private DealManager dealManager;
  private UserService userService;
  private UserManager userManager;
  private HttpServletResponse response;
  private PrintWriter writer;

  @Before
  public void setUp() throws IOException {
    request = mock(HttpServletRequest.class);

    PowerMockito.mockStatic(ImageUploader.class);
    BDDMockito.given(ImageUploader.getUploadedImageBlobkey(eq(request), anyString()))
        .willReturn(BLOBKEY_A);

    dealManager = mock(DealManager.class);
    userService = mock(UserService.class);
    userManager = mock(UserManager.class);
    User currentUser = new User(EMAIL_A, "");

    // mock response
    response = mock(HttpServletResponse.class);
    StringWriter stringWriter = new StringWriter();
    writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    // default request parameter for success case
    when(request.getParameter("description")).thenReturn(DESCRIPTION_A);
    when(request.getParameter("start")).thenReturn(DATE_A);
    when(request.getParameter("end")).thenReturn(DATE_B);
    when(request.getParameter("source")).thenReturn(SOURCE_A);
    when(request.getParameter("restaurant")).thenReturn(RESTAURANT_ID_A_STRING);

    // behaviour when user is logged in
    when(userService.isUserLoggedIn()).thenReturn(true);
    when(userService.getCurrentUser()).thenReturn(currentUser);
    when(userManager.readUserByEmail(EMAIL_A)).thenReturn(USER_A);

    servlet = new DealPostServlet(dealManager, userManager, userService);
  }

  @Test
  public void testDoPost_success() throws IOException {
    when(dealManager.createDeal(
            eq(DESCRIPTION_A),
            anyString(),
            eq(DATE_A),
            eq(DATE_B),
            eq(SOURCE_A),
            anyLong(),
            eq(RESTAURANT_ID_A)))
        .thenReturn(DEAL);

    servlet.doPost(request, response);

    verify(dealManager)
        .createDeal(
            eq(DESCRIPTION_A),
            anyString(),
            eq(DATE_A),
            eq(DATE_B),
            eq(SOURCE_A),
            anyLong(),
            eq(RESTAURANT_ID_A));
    verify(response).sendRedirect(any());
  }

  @Test
  public void testDoPost_userNotLoggedIn_unauthorized() throws IOException {
    when(userService.isUserLoggedIn()).thenReturn(false);
    when(userService.getCurrentUser()).thenReturn(null);

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  @Test
  public void testDoPost_descriptionNull_badRequest() throws IOException {
    when(request.getParameter("description")).thenReturn(null);

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_descriptionEmpty_badRequest() throws IOException {
    when(request.getParameter("description")).thenReturn("");

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_dateNull_badRequest() throws IOException {
    when(request.getParameter("start")).thenReturn(null);

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_dateInvalid_badRequest() throws IOException {
    when(request.getParameter("end")).thenReturn("trash");

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_dateWrongFormat_badRequest() throws IOException {
    when(request.getParameter("start")).thenReturn("2020-1-1");

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_dateWrongOrder_badRequest() throws IOException {
    when(request.getParameter("start")).thenReturn(DATE_B);
    when(request.getParameter("end")).thenReturn(DATE_A);

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_restaurantInvalid_badRequest() throws IOException {
    when(request.getParameter("restaurant")).thenReturn("aaa");

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }
}
