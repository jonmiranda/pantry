package net.jonmiranda.pantry;

import android.app.Application;
import android.support.annotation.VisibleForTesting;

import net.jonmiranda.pantry.dagger.AppModule;

import dagger.ObjectGraph;

public class PantryApplication extends Application {

  @VisibleForTesting
  ObjectGraph objectGraph;

  @Override
  public void onCreate() {
    super.onCreate();
    objectGraph = ObjectGraph.create(new AppModule(this));
  }

  public void inject(Object object) {
    objectGraph.inject(object);
  }
}
