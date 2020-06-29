package com.google.step.model;

public class Tag {
  public final long id;
  public final String tag;
  public final long deal;

  public Tag(long id, String tag, long deal) {
    this.id = id;
    this.tag = tag;
    this.deal = deal;
  }
}
