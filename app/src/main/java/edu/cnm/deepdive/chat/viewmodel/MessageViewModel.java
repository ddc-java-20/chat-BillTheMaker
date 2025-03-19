package edu.cnm.deepdive.chat.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.chat.model.dto.Channel;
import edu.cnm.deepdive.chat.model.dto.Message;
import edu.cnm.deepdive.chat.service.MessageService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;

@HiltViewModel
public class MessageViewModel extends ViewModel implements DefaultLifecycleObserver {

  private final MessageService messageService;
  private final MutableLiveData<List<Message>> messages;
  private final MutableLiveData<List<Channel>> channels;
  private final MutableLiveData<Channel> selectedChannel;
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;

  @Inject
  public MessageViewModel(MessageService messageService) {
    this.messageService = messageService;
    messages = new MutableLiveData<>();
    channels = new MutableLiveData<>();
    selectedChannel = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();
  }

  public LiveData<List<Message>> getMessages() {
    return messages;
  }

  public LiveData<List<Channel>> getChannels() {
    return channels;
  }

  public LiveData<Channel> getSelectedChannel() {
    return selectedChannel;
  }

  public void setSelectedChannel(@NonNull Channel channel) {
    if (!channel.equals(selectedChannel.getValue())) {
      messages.setValue(new LinkedList<>());
      // TODO: 3/19/2025  Start a new query for messages in the selected channel
      selectedChannel.setValue(channel);
    }
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }
}
