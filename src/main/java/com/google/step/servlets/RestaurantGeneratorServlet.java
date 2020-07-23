package com.google.step.servlets;

import com.google.step.datamanager.RestaurantGenerator;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles generating restaurants. */
@WebServlet("/api/gen-restaurants")
public class RestaurantGeneratorServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    RestaurantGenerator.populateRestaurantsDatabase();
  }
}
