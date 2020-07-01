package com.google.step.servlets;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
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

import java.util.List;
import java.util.ArrayList;

@RunWith(JUnit4.class)
public class CommentServletTest {

    private static final long dealId = 2;

    private static final long ID_A = 1;
    private static final long userId_A = 3;
    private static final String content_A = "Hello world";
    private static final Comment comment_A = new Comment(ID_A, dealId, userId_A, content_A);

    private static final long ID_B = 2;
    private static final long userId_B = 4;
    private static final String content_B = "Hello world2";
    private static final Comment comment_B = new Comment(ID_B, dealId, userId_B, content_B);

    private static final List<Comment> comments = new ArrayList<>();

    private CommentManager commentManager;

    private CommentServlet commentServlet;

    @Before
    public void setUp() {
      commentManager = mock(CommentManager.class);
      commentServlet = new CommentServlet();
      comments.add(comment_A);
      comments.add(comment_B);
   }
 
    @Test
    public void testDoGet_success() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getPathInfo()).thenReturn("/2");
        when(commentManager.getComments(2)).thenReturn(comments);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        commentServlet.doGet(request, response);

        String commentA = String.format("{id:%d,deal:%d,user:%d,content:\"%s\"}",
                          ID_A, dealId, userId_A, content_A);
        String commentB = String.format("{id:%d,deal:%d,user:%d,content:\"%s\"}",
                          ID_B, dealId, userId_B, content_B);
        String expected = "[" + commentA + "," + commentB + "]";

        JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
    }
}
