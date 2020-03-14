// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Text;
import com.google.sps.data.Comment;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("timestamp", SortDirection.ASCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<>();
    for(Entity entity : results.asIterable()){
      Text commentAsText = (Text) entity.getProperty("comment");
      String commentAsString = commentAsText.getValue();

      Text commenterAsText = (Text) entity.getProperty("commenter");
      String commenterAsString = commenterAsText.getValue();

      String email = (String) entity.getProperty("email");

      Comment comment = new Comment(commentAsString, commenterAsString, email);
      comments.add(comment);
    }
    
    String json = convertToJson(comments);

    response.setContentType("application/json; charset=utf-8");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if(!userService.isUserLoggedIn()){
      response.sendRedirect("/");
      return;
    }

    String email = userService.getCurrentUser().getEmail();

    String commentValue = request.getParameter("comment");
    
    if(isStringValid(commentValue)){
      Text comment = new Text(commentValue);
      
      String commenterValue = request.getParameter("commenter");
      Text commenter;
      if(isStringValid(commenterValue)){
        commenter = new Text(commenterValue);
      } else {
        commenter = new Text(email);
      }
      
      

      long timestamp = System.currentTimeMillis();

      String id = userService.getCurrentUser().getUserId();
      
      Entity commentEntity = new Entity("Comment");
      commentEntity.setProperty("comment", comment);
      commentEntity.setProperty("commenter", commenter);
      commentEntity.setProperty("email", email);
      commentEntity.setProperty("timestamp", timestamp);

      Entity userEntity = new Entity("UserInfo", id);
      userEntity.setProperty("id", id);
      userEntity.setProperty("username", commenter);
      userEntity.setProperty("email", email);

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(commentEntity);
      datastore.put(userEntity);
    }
  
    response.sendRedirect("/index.html");
  }

  private String convertToJson(List<Comment> comments){
    Gson gson = new Gson();
    String json = gson.toJson(comments);
    return json;
  }

  private Boolean isStringValid(String value){
    return value != null && !value.trim().isEmpty();
  }
}
