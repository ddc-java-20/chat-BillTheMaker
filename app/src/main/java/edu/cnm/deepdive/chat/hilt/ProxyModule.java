package edu.cnm.deepdive.chat.hilt;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import edu.cnm.deepdive.chat.InstantDeserializer;
import edu.cnm.deepdive.chat.R;
import edu.cnm.deepdive.chat.service.ChatServiceLongPollingProxy;
import edu.cnm.deepdive.chat.service.ChatServiceProxy;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import javax.inject.Singleton;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


@Module
@InstallIn(SingletonComponent.class)
public class ProxyModule {

  @Inject
  ProxyModule() {}

  @Provides
  @Singleton
  Gson provideGson(JsonDeserializer<Instant> deserializer) {
    return new GsonBuilder()
        .registerTypeAdapter(Instant.class, deserializer)
        .excludeFieldsWithoutExposeAnnotation()
        .create();
  }

  @Provides
  @Singleton
  Interceptor provideInterceptor(Gson gson, @ApplicationContext Context context) {
    return new HttpLoggingInterceptor()
        .setLevel(Level.valueOf(context.getString(R.string.log_level).toUpperCase()));
  }

  @Provides
  @Singleton
  ChatServiceProxy provideProxy(
          @ApplicationContext Context context, Gson gson, Interceptor interceptor) {
    OkHttpClient client = new OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build();
    return new Retrofit.Builder()
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .baseUrl(context.getString(R.string.base_url))
        .build()
        .create(ChatServiceProxy.class);
  }


  @Provides
  @Singleton
  ChatServiceLongPollingProxy provideLongPollingProxy(
      @ApplicationContext Context context, Gson gson, Interceptor interceptor) {
    OkHttpClient client = new OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .connectTimeout(Duration.ZERO)
        .writeTimeout(Duration.ZERO)
        .readTimeout(Duration.ZERO)
        .callTimeout(Duration.ofSeconds(30))
        .build();
    return new Retrofit.Builder()
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .baseUrl(context.getString(R.string.base_url))
        .build()
        .create(ChatServiceLongPollingProxy.class);
  }


}
