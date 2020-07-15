package com.google.step.servlets;

import static com.google.step.TestConstants.BLOBKEY_A;
import static com.google.step.TestConstants.DATE_A;
import static com.google.step.TestConstants.DATE_B;
import static com.google.step.TestConstants.DEAL_ID_A;
import static com.google.step.TestConstants.DESCRIPTION_A;
import static com.google.step.TestConstants.RESTAURANT_ID_A;
import static com.google.step.TestConstants.SOURCE_A;
import static com.google.step.TestConstants.USER_ID_A;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
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

  private HttpServletRequest request;
  private DealPostServlet servlet;
  private DealManager dealManager;

  @Before
  public void setUp() {
    request = mock(HttpServletRequest.class);

    PowerMockito.mockStatic(ImageUploader.class);
    BDDMockito.given(ImageUploader.getUploadedImageBlobkey(eq(request), anyString()))
        .willReturn(BLOBKEY_A);

    dealManager = mock(DealManager.class);
    servlet = new DealPostServlet(dealManager);

    // default request parameter for success case
    when(request.getParameter("description")).thenReturn(DESCRIPTION_A);
    when(request.getParameter("start")).thenReturn(DATE_A);
    when(request.getParameter("end")).thenReturn(DATE_B);
    when(request.getParameter("source")).thenReturn(SOURCE_A);
    when(request.getParameter("restaurant")).thenReturn(RESTAURANT_ID_A_STRING);
  }

  @Test
  public void testDoPost_success() throws IOException {
    HttpServletResponse response = mock(HttpServletResponse.class);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

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
    verify(response).setStatus(HttpServletResponse.SC_OK);
  }

  @Test
  public void testDoPost_descriptionNull_badRequest() throws IOException {
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("description")).thenReturn(null);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_descriptionEmpty_badRequest() throws IOException {
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("description")).thenReturn("");

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

    when(request.getParameter("start")).thenReturn(null);

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

    when(request.getParameter("end")).thenReturn("trash");

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

    when(request.getParameter("start")).thenReturn("2020-1-1");

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

    when(request.getParameter("start")).thenReturn(DATE_B);
    when(request.getParameter("end")).thenReturn(DATE_A);

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

    when(request.getParameter("restaurant")).thenReturn("aaa");

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }
}
