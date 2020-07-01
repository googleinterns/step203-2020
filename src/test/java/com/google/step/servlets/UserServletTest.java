package com.google.step.servlets;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.step.datamanager.UserManager;
import com.google.step.model.User;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserServletTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig()
              .setDefaultHighRepJobPolicyUnappliedJobPercentage(100));

  private static final long ID_A = 1;

  private static final String EMAIL_A = "testa@example.com";

  private static final String USERNAME_A = "Alice";

  private static final String BLOBKEY_A = "a_blob_key";

  private static final String BIO_A = "Hello world.";

  private static final User USER_A = new User(ID_A, EMAIL_A, USERNAME_A, BLOBKEY_A, BIO_A);

  @Mock(name = "userManager")
  private UserManager userManager;

  @InjectMocks private UserServlet servlet;

  @Before
  public void setUp() {
    helper.setUp();
    MockitoAnnotations.initMocks(this);
    servlet = new UserServlet();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testDoGet() throws Exception {
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
    System.out.println(stringWriter.toString());

    // JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }
}
