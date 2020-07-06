package com.google.step.datamanager;

import com.google.step.model.Tag;

/** TagManager handles tag operations. */
public interface TagManager {
  /**
   * Returns a tag object with the name. Creates a new tag if it does not exist.
   *
   * @param name tag name.
   * @return tag with the given name.
   */
  public Tag readOrCreateTagByName(String name);

  /**
   * Returns a tag object with the id.
   *
   * @param id id of the tag.
   * @return a tag object with the id.
   * @throws IllegalArgumentException if the id does not exist.
   */
  public Tag readTag(long id) throws IllegalArgumentException;
}
