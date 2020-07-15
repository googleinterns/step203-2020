package com.google.step.model;

class Util {
  /**
   * Returns true if both objects are null or the {@code equals} method returns true. Otherwise,
   * returns false.
   */
  public static boolean isEqual(Object objA, Object objB) {
    return (objA == null ? objB == null : objA.equals(objB));
  }
}
