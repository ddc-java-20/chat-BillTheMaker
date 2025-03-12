package edu.cnm.deepdive.chat.controller;

import edu.cnm.deepdive.chat.model.entity.Message;
import edu.cnm.deepdive.chat.service.AbstractMessageService;
import edu.cnm.deepdive.chat.service.AbstractUserService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/channels.{channelKey}/messages")
public class MessageController {

  private static final String DEFAULT_SINCE_VALUE = "" + Long.MIN_VALUE;
  private final AbstractMessageService messageService;
  private final AbstractUserService userService;

  @Autowired
  public MessageController(AbstractMessageService messageService, AbstractUserService userService) {
    this.messageService = messageService;
    this.userService = userService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<Message> post(
      @RequestBody Message message,
      @PathVariable UUID channelKey,
      @RequestParam(required = false, defaultValue = DEFAULT_SINCE_VALUE) long since
  ) {
    return messageService.add(message, channelKey, userService.getCurrent(), Instant.ofEpochMilli(since));
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public DeferredResult<List<Message>> get(
      @PathVariable UUID channelKey,
      @RequestParam(required = false, defaultValue = DEFAULT_SINCE_VALUE) long since) {
    return messageService.pollSince(channelKey, Instant.ofEpochMilli(since));
  }
}
