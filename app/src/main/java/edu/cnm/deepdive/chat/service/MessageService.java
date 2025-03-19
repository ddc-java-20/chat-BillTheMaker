package edu.cnm.deepdive.chat.service;

import edu.cnm.deepdive.chat.model.dto.Channel;
import edu.cnm.deepdive.chat.model.dto.Message;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import retrofit2.http.Path;

public class MessageService {

  private final ChatServiceProxy proxy;
  private final ChatServiceLongPollingProxy longPollingProxy;
  private final Scheduler scheduler;

  @Inject
  public MessageService(ChatServiceProxy proxy, ChatServiceLongPollingProxy longPollingProxy) {
    this.proxy = proxy;
    this.longPollingProxy = longPollingProxy;
    scheduler = Schedulers.io();
  }

  Single<List<Message>> getMessages(@Path("key") UUID channelKey, Instant since) {
    return longPollingProxy
        .getSince(channelKey, since.toEpochMilli())
        .subscribeOn(scheduler);
  }

  Single<List<Message>> sendMessage(UUID channelKey, Message message, Instant since) {
    // TODO: 3/19/2025 refresh bearer token and pass downstream.
    return proxy
        .postMessage(message, channelKey, since.toEpochMilli())
        .subscribeOn(scheduler);
  }

  Single<List<Channel>> getchannels(boolean active) {
    // TODO: 3/19/2025 refresh bearer token and pass downstream
    return proxy
        .getChannels(active)
        .subscribeOn(scheduler);
  }

}
