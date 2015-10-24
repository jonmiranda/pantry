package net.jonmiranda.pantry;

import android.app.Application;

import net.jonmiranda.pantry.dagger.AppModule;
import net.jonmiranda.pantry.dagger.DebugModule;

import dagger.ObjectGraph;

public class PantryApplication extends Application {

  private ObjectGraph objectGraph;

  @Override
  public void onCreate() {
    super.onCreate();
    if (BuildConfig.DEBUG) {
      objectGraph = ObjectGraph.create(new DebugModule(this));
    } else {
      objectGraph = ObjectGraph.create(new AppModule(this));
    }
  }

  public void inject(Object object) {
    objectGraph.inject(object);
  }
}
