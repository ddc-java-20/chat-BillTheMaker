package edu.cnm.deepdive.chat.service;

import edu.cnm.deepdive.chat.model.dao.ChannelRepository;
import edu.cnm.deepdive.chat.model.dao.MessageRepository;
import edu.cnm.deepdive.chat.model.entity.Channel;
import edu.cnm.deepdive.chat.model.entity.Message;
import edu.cnm.deepdive.chat.model.entity.User;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService implements AbstractMessageService {

  private static final Duration MAX_SINCE_DURATION = Duration.ofSeconds(Long.MAX_VALUE);

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;

  @Autowired
  public MessageService(MessageRepository messageRepository, ChannelRepository channelRepository) {
    this.messageRepository = messageRepository;
    this.channelRepository = channelRepository;
  }

  @Override
  public List<Message> add(Message message, UUID channelKey, User author, Instant since) {
    return channelRepository
        .findByExternalKey(channelKey)
        .map((channel) -> {
          return addAndRefresh(message, author, since, channel);
        })
        .orElseThrow();
  }

  @Override
  public List<Message> getSince(UUID channelKey, Instant since) {

    return channelRepository
        .findByExternalKey(channelKey)
        .map((Channel channel) -> getSinceAtMost(since, channel))
        .orElseThrow();
  }

  private List<Message> getSinceAtMost(Instant since, Channel channel) {
    Instant effectiveSince = getEffectiveSince(since);
    return messageRepository
        .getAllByChannelAndPostedAfterOrderByPostedAsc(channel, effectiveSince);
  }

  private List<Message> addAndRefresh(Message message, User author, Instant since, Channel channel) {
    message.setChannel(channel);
    message.setSender(author);
    messageRepository.save(message);
    getEffectiveSince(since);
    return messageRepository
        .getAllByChannelAndPostedAfterOrderByPostedAsc(channel, since);
  }

  private static Instant getEffectiveSince(Instant since) {
    Instant earliestSince = Instant.now().minus(MAX_SINCE_DURATION);
    return (since.isBefore(earliestSince)) ? earliestSince : since;
  }
}