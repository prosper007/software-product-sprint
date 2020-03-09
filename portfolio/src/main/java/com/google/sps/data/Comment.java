package com.google.sps.data;

public final class Comment {

  private final String comment;
  private final String commenter;
  private final String email;

  public Comment(String comment, String commenter, String email) {
    this.comment = comment;
    this.commenter = commenter;
    this.email = email;
  }
}