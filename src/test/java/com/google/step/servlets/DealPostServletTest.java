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
          RESTAURANT_ID_A,
          null);

  private HttpServletRequest mockRequest;
  private DealPostServlet servlet;
  private DealManager mockDealManager;
  private UserService mockUserService;
  private UserManager mockUserManager;
  private HttpServletResponse mockResponse;
  private PrintWriter writer;

  @Before
  public void setUp() throws IOException {
    mockRequest = mock(HttpServletRequest.class);

    PowerMockito.mockStatic(ImageUploader.class);
    BDDMockito.given(ImageUploader.getUploadedImageBlobkey(eq(mockRequest), anyString()))
        .willReturn(BLOBKEY_A);

    mockDealManager = mock(DealManager.class);
    mockUserService = mock(UserService.class);
    mockUserManager = mock(UserManager.class);
    User currentUser = new User(EMAIL_A, "");

    // mock response
    mockResponse = mock(HttpServletResponse.class);
    StringWriter stringWriter = new StringWriter();
    writer = new PrintWriter(stringWriter);
    when(mockResponse.getWriter()).thenReturn(writer);

    // default request parameter for success case
    when(mockRequest.getParameter("description")).thenReturn(DESCRIPTION_A);
    when(mockRequest.getParameter("start")).thenReturn(DATE_A);
    when(mockRequest.getParameter("end")).thenReturn(DATE_B);
    when(mockRequest.getParameter("source")).thenReturn(SOURCE_A);
    when(mockRequest.getParameter("restaurant")).thenReturn(RESTAURANT_ID_A_STRING);

    // behaviour when user is logged in
    when(mockUserService.isUserLoggedIn()).thenReturn(true);
    when(mockUserService.getCurrentUser()).thenReturn(currentUser);
    when(mockUserManager.readUserByEmail(EMAIL_A)).thenReturn(USER_A);

    servlet = new DealPostServlet(mockDealManager, mockUserManager, mockUserService);
  }

  @Test
  public void testDoPost_success() throws IOException {
    when(mockDealManager.createDeal(
            eq(DESCRIPTION_A),
            anyString(),
            eq(DATE_A),
            eq(DATE_B),
            eq(SOURCE_A),
            anyLong(),
            eq(RESTAURANT_ID_A)))
        .thenReturn(DEAL);

    servlet.doPost(mockRequest, mockResponse);

    verify(mockDealManager)
        .createDeal(
            eq(DESCRIPTION_A),
            anyString(),
            eq(DATE_A),
            eq(DATE_B),
            eq(SOURCE_A),
            anyLong(),
            eq(RESTAURANT_ID_A));
    verify(mockResponse).sendRedirect(any());
  }

  @Test
  public void testDoPost_userNotLoggedIn_unauthorized() throws IOException {
    when(mockUserService.isUserLoggedIn()).thenReturn(false);
    when(mockUserService.getCurrentUser()).thenReturn(null);

    servlet.doPost(mockRequest, mockResponse);

    verify(mockResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  @Test
  public void testDoPost_descriptionNull_badRequest() throws IOException {
    when(mockRequest.getParameter("description")).thenReturn(null);

    servlet.doPost(mockRequest, mockResponse);

    verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_descriptionEmpty_badRequest() throws IOException {
    when(mockRequest.getParameter("description")).thenReturn("");

    servlet.doPost(mockRequest, mockResponse);

    verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_dateNull_badRequest() throws IOException {
    when(mockRequest.getParameter("start")).thenReturn(null);

    servlet.doPost(mockRequest, mockResponse);

    verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_dateInvalid_badRequest() throws IOException {
    when(mockRequest.getParameter("end")).thenReturn("trash");

    servlet.doPost(mockRequest, mockResponse);

    verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_dateWrongFormat_badRequest() throws IOException {
    when(mockRequest.getParameter("start")).thenReturn("2020-1-1");

    servlet.doPost(mockRequest, mockResponse);

    verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_dateWrongOrder_badRequest() throws IOException {
    when(mockRequest.getParameter("start")).thenReturn(DATE_B);
    when(mockRequest.getParameter("end")).thenReturn(DATE_A);

    servlet.doPost(mockRequest, mockResponse);

    verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_restaurantInvalid_badRequest() throws IOException {
    when(mockRequest.getParameter("restaurant")).thenReturn("aaa");

    servlet.doPost(mockRequest, mockResponse);

    verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }
}
