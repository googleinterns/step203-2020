package com.google.step;

import static org.mockito.ArgumentMatchers.argThat;

import java.util.List;
import org.mockito.ArgumentMatcher;

public class TestUtils {
  public static <T> List<T> anyEmptyList() {
    return argThat(new EmptyListMatcher<T>());
  }
}

class EmptyListMatcher<T> implements ArgumentMatcher<List<T>> {

  @Override
  public boolean matches(List<T> argument) {
    return argument.isEmpty();
  }
}
