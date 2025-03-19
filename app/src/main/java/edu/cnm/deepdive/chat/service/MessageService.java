package edu.cnm.deepdive.chat.service;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import edu.cnm.deepdive.chat.model.dto.Channel;
import edu.cnm.deepdive.chat.model.dto.Message;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MessageService {

  private static final String BEARER_TOKEN_FORMAT = "Bearer %s";

  private final ChatServiceProxy proxy;
  private final ChatServiceLongPollingProxy longPollingProxy;
  private final GoogleSignInService signInService;
  private final Scheduler scheduler;

  @Inject
  public MessageService(ChatServiceProxy proxy, ChatServiceLongPollingProxy longPollingProxy,
      GoogleSignInService signInService) {
    this.proxy = proxy;
    this.longPollingProxy = longPollingProxy;
    this.signInService = signInService;
    scheduler = Schedulers.io();
  }

  public Single<List<Message>> getMessages(UUID channelKey, Instant since) {
    return signInService
        .refreshBearerToken()
        .observeOn(scheduler)
        .map((token) -> String.format(BEARER_TOKEN_FORMAT, token))
        .flatMap((bearerToken) -> longPollingProxy
        .getSince(channelKey, since, bearerToken))
        .subscribeOn(scheduler);
  }

  public Single<List<Message>> sendMessage(UUID channelKey, Message message, Instant since) {
    // TODO: 3/19/2025 refresh bearer token and pass downstream.
    return signInService
        .refreshBearerToken()
        .observeOn(scheduler)
        .map((token) -> String.format(BEARER_TOKEN_FORMAT, token))
        .flatMap((bearerToken) -> proxy
          .postMessage(message, channelKey, since, bearerToken));
  }

  public Single<List<Channel>> getchannels(boolean active) {
    // TODO: 3/19/2025 refresh bearer token and pass downstream
    return signInService
        .refreshBearerToken()
        .observeOn(scheduler)
        .map((token) -> String.format(BEARER_TOKEN_FORMAT, token))
        .flatMap((bearerToken) -> proxy.getChannels(active, bearerToken));
  }

}
