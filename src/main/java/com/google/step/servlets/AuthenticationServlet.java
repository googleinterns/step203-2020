package com.google.step.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.step.datamanager.UserManager;
import com.google.step.datamanager.UserManagerDatastore;
import com.google.step.model.User;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/authentication")
public class AuthenticationServlet extends HttpServlet {
  private UserManager userManager = new UserManagerDatastore();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail();
      String urlToRedirectToAfterUserLogsOut = "/";
      String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);

      // TODO: move the following part to JsonFormatter
      User user = userManager.userLogin(userEmail);
      Gson gson = new Gson();
      JsonElement jsonElement = gson.toJsonTree(user);
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      jsonObject.addProperty("isLoggedIn", true);
      jsonObject.addProperty("logoutUrl", logoutUrl);
      response.getWriter().println(jsonObject);
    } else {
      String urlToRedirectToAfterUserLogsIn = "/";
      String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);

      JsonObject json = new JsonObject();
      json.addProperty("isLoggedIn", false);
      json.addProperty("loginUrl", loginUrl);
      response.getWriter().println(json);
    }
  }
}
