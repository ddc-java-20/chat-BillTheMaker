package edu.cnm.deepdive.chat.hilt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Inject;
import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public class ProxyModule {

  @Inject
  ProxyModule() {}

  @Provides
  @Singleton
  Gson provideGson() {
    return new GsonBuilder()
        // TODO: 3/12/2025 register type adapters as necessary
        .excludeFieldsWithoutExposeAnnotation()
        .create();
  }
}
