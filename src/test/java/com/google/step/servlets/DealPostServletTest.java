package com.google.step.servlets;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.step.datamanager.DealManager;
import com.google.step.model.Deal;
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
public class DealPostServletTest {
  // public final long id;
  // public final String description;
  // public final String photoBlobkey;
  // public final String start;
  // public final String end;
  // public final String source;
  // public final long posterId;
  // public final long restaurantId

  private static final long ID_A = 1;

  private static final String DESCRIPTION = "starbucks mocha 1-for-1";
  private static final String DESCRIPTION_EMPTY = "";

  private static final String BLOBKEY = "a_blob_key";

  private static final String DATE_A = "2020-01-01";
  private static final String DATE_B = "2020-01-02";
  private static final String DATE_INVALID = "trash";
  private static final String DATE_WRONG_FORMAT = "2020-1-1";

  private static final String SOURCE = "www.example.com";

  private static final long POSTER_ID = 10;

  private static final long RESTAURANT_ID_NUM = 11;
  private static final String RESTAURANT_ID = "11";
  private static final String RESTAURANT_ID_INVALID = "aaa";

  private static final Deal DEAL =
      new Deal(ID_A, DESCRIPTION, BLOBKEY, DATE_A, DATE_B, SOURCE, POSTER_ID, RESTAURANT_ID_NUM);

  private DealPostServlet servlet;
  private DealManager dealManager;

  @Before
  public void setUp() {
    dealManager = mock(DealManager.class);
    servlet = new DealPostServlet(dealManager);
  }

  @Test
  public void testDoPost_sucess() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("description")).thenReturn(DESCRIPTION);
    when(request.getParameter("photoBlobkey")).thenReturn(BLOBKEY);
    when(request.getParameter("start")).thenReturn(DATE_A);
    when(request.getParameter("end")).thenReturn(DATE_B);
    when(request.getParameter("source")).thenReturn(SOURCE);
    when(request.getParameter("restaurant")).thenReturn(RESTAURANT_ID);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    when(dealManager.createDeal(
            eq(DESCRIPTION),
            anyString(),
            eq(DATE_A),
            eq(DATE_B),
            eq(SOURCE),
            anyLong(),
            eq(RESTAURANT_ID_NUM)))
        .thenReturn(DEAL);

    servlet.doPost(request, response);

    verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_descriptionNull_badRequest() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("description")).thenReturn(null);
    when(request.getParameter("photoBlobkey")).thenReturn(BLOBKEY);
    when(request.getParameter("start")).thenReturn(DATE_A);
    when(request.getParameter("end")).thenReturn(DATE_B);
    when(request.getParameter("source")).thenReturn(SOURCE);
    when(request.getParameter("restaurant")).thenReturn(RESTAURANT_ID);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_descriptionEmpty_badRequest() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("description")).thenReturn(DESCRIPTION_EMPTY);
    when(request.getParameter("photoBlobkey")).thenReturn(BLOBKEY);
    when(request.getParameter("start")).thenReturn(DATE_A);
    when(request.getParameter("end")).thenReturn(DATE_B);
    when(request.getParameter("source")).thenReturn(SOURCE);
    when(request.getParameter("restaurant")).thenReturn(RESTAURANT_ID);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_dateNull_badRequest() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("description")).thenReturn(DESCRIPTION);
    when(request.getParameter("photoBlobkey")).thenReturn(BLOBKEY);
    when(request.getParameter("start")).thenReturn(null);
    when(request.getParameter("end")).thenReturn(DATE_B);
    when(request.getParameter("source")).thenReturn(SOURCE);
    when(request.getParameter("restaurant")).thenReturn(RESTAURANT_ID);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_dateInvalid_badRequest() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("description")).thenReturn(DESCRIPTION);
    when(request.getParameter("photoBlobkey")).thenReturn(BLOBKEY);
    when(request.getParameter("start")).thenReturn(DATE_A);
    when(request.getParameter("end")).thenReturn(DATE_INVALID);
    when(request.getParameter("source")).thenReturn(SOURCE);
    when(request.getParameter("restaurant")).thenReturn(RESTAURANT_ID);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_dateWrongFormat_badRequest() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("description")).thenReturn(DESCRIPTION);
    when(request.getParameter("photoBlobkey")).thenReturn(BLOBKEY);
    when(request.getParameter("start")).thenReturn(DATE_WRONG_FORMAT);
    when(request.getParameter("end")).thenReturn(DATE_B);
    when(request.getParameter("source")).thenReturn(SOURCE);
    when(request.getParameter("restaurant")).thenReturn(RESTAURANT_ID);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_dateWrongOrder_badRequest() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("description")).thenReturn(DESCRIPTION);
    when(request.getParameter("photoBlobkey")).thenReturn(BLOBKEY);
    when(request.getParameter("start")).thenReturn(DATE_B);
    when(request.getParameter("end")).thenReturn(DATE_A);
    when(request.getParameter("source")).thenReturn(SOURCE);
    when(request.getParameter("restaurant")).thenReturn(RESTAURANT_ID);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_restaurantInvalid_badRequest() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("description")).thenReturn(DESCRIPTION);
    when(request.getParameter("photoBlobkey")).thenReturn(BLOBKEY);
    when(request.getParameter("start")).thenReturn(DATE_B);
    when(request.getParameter("end")).thenReturn(DATE_A);
    when(request.getParameter("source")).thenReturn(SOURCE);
    when(request.getParameter("restaurant")).thenReturn(RESTAURANT_ID_INVALID);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }
}