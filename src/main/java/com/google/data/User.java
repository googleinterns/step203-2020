package com.google.data;

import com.google.auto.value.AutoValue;
import com.google.gson.annotations.SerializedName;
import java.util.Optional;

@AutoValue
public abstract class User {

  @SerializedName("id")
  public abstract String getId();

  @SerializedName("email")
  public abstract String getEmail();

  @SerializedName("username")
  public abstract Optional<String> getUsername();

  static Builder builder() {
    return new AutoValue_User.Builder();
  }

  @AutoValue.Builder
  abstract static class Builder {
    abstract Builder setId(String id);

    abstract Builder setEmail(String email);

    abstract Builder setUsername(String username);

    abstract User build();
  }
}
