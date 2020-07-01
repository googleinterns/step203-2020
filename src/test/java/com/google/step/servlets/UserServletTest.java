package com.google.step.servlets;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.step.datamanager.UserManager;
import com.google.step.model.User;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

@RunWith(JUnit4.class)
public class UserServletTest {

  private static final long ID_A = 1;

  private static final String EMAIL_A = "testa@example.com";

  private static final String USERNAME_A = "Alice";

  private static final String BLOBKEY_A = "a_blob_key";

  private static final String BIO_A = "Hello world.";

  private static final User USER_A = new User(ID_A, EMAIL_A, USERNAME_A, BLOBKEY_A, BIO_A);

  private UserServlet servlet;
  private UserManager userManager;

  @Before
  public void setUp() {
    userManager = mock(UserManager.class);
    servlet = new UserServlet(userManager);
  }

  @Test
  public void testDoGet_success() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(request.getPathInfo()).thenReturn("/1");
    when(userManager.readUser(1)).thenReturn(USER_A);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    servlet.doGet(request, response);

    writer.flush();

    String expected =
        String.format(
            "{id:%d,email:\"%s\",username:\"%s\",bio:\"%s\",photoBlobKey:\"%s\","
                + "dealsUploaded:[],"
                + "following:[],"
                + "followers:[],"
                + "tagsFollowed:[],"
                + "restaurantsFollowed:[]}",
            ID_A, EMAIL_A, USERNAME_A, BIO_A, BLOBKEY_A);

    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testDoGet_notExist() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(request.getPathInfo()).thenReturn("/1000");
    when(userManager.readUser(1000)).thenReturn(null);

    servlet.doGet(request, response);
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoGet_invalidId() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(request.getPathInfo()).thenReturn("/100x00");

    servlet.doGet(request, response);
    System.out.println(response.getStatus());

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoGet_noId() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(request.getPathInfo()).thenReturn("");

    servlet.doGet(request, response);
    System.out.println(response.getStatus());

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }
}
