package edu.cnm.deepdive.chat.model.dto;

import com.google.gson.annotations.Expose;
import java.net.URL;
import java.time.Instant;
import java.util.UUID;

public class User {

  @Expose(serialize = false)
  private UUID key;

  @Expose
  private String displayName;

  @Expose
  private URL avater;

  @Expose(serialize = false)
  private Instant joined;

// /* SETTERS AND GETTERS

  public UUID getKey() {
    return key;
  }

  public void setKey(UUID key) {
    this.key = key;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public URL getAvater() {
    return avater;
  }

  public void setAvater(URL avater) {
    this.avater = avater;
  }

  public Instant getJoined() {
    return joined;
  }

  public void setJoined(Instant joined) {
    this.joined = joined;
  }
}
