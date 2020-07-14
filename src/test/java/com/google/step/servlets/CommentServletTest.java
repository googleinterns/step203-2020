package com.google.step.servlets;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.step.datamanager.CommentManager;
import com.google.step.model.Comment;
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
public class CommentServletTest {

  private static final long DEALID = 2;

  private static final long ID_A = 1;
  private static final long USERID_A = 3;
  private static final String CONTENT_A = "Hello world";
  private static final String UPDATE_CONTENT_A = "Update";
  private static final Comment UPDATE_COMMENT_A =
      new Comment(ID_A, DEALID, USERID_A, UPDATE_CONTENT_A, "1");

  private CommentManager mockCommentManager;

  private CommentServlet commentServlet;

  @Before
  public void setUp() {
    mockCommentManager = mock(CommentManager.class);
    commentServlet = new CommentServlet(mockCommentManager);
  }

  @Test
  public void testDoPut_success() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/1");
    when(request.getParameter("content")).thenReturn(UPDATE_CONTENT_A);
    when(mockCommentManager.updateComment(1, UPDATE_CONTENT_A)).thenReturn(UPDATE_COMMENT_A);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    commentServlet.doPut(request, response);

    String expected =
        String.format(
            "{id:%d,dealId:%d,userId:%d,content:\"%s\"}", ID_A, DEALID, USERID_A, UPDATE_CONTENT_A);

    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testDoPut_invalidID() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/abcd");

    commentServlet.doPut(request, response);
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPut_noID() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/");

    commentServlet.doPut(request, response);
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPut_notExist() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/100");

    when(request.getParameter("content")).thenReturn(CONTENT_A);
    when(mockCommentManager.updateComment(100, CONTENT_A)).thenReturn(null);

    commentServlet.doPut(request, response);

    verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
  }

  @Test
  public void testDoDelete_success() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/1");

    commentServlet.doDelete(request, response);

    verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(mockCommentManager).deleteComment(anyLong());
  }

  @Test
  public void testDoDelete_invalidID() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/abcd");

    commentServlet.doDelete(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoDelete_noID() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/");

    commentServlet.doDelete(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }
}
