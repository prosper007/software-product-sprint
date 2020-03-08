package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.AuthInfo;

@WebServlet("/login-status")
public class LoginStatusServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    
    boolean isUserLoggedIn = userService.isUserLoggedIn();
    String loginUrl = userService.createLoginURL("/");
    String logoutUrl = userService.createLogoutURL("/");
    AuthInfo authInfo = new AuthInfo(isUserLoggedIn, loginUrl, logoutUrl);

    Gson gson = new Gson();
    String userStatus = gson.toJson(authInfo);

    response.setContentType("application/json");
    response.getWriter().println(userStatus);
  }
}