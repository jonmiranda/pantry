package net.jonmiranda.pantry.dagger;

import net.jonmiranda.pantry.MainActivity;
import net.jonmiranda.pantry.PantryApplication;
import net.jonmiranda.pantry.storage.DebugStorage;
import net.jonmiranda.pantry.storage.Storage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
  injects = {
    MainActivity.class,
  },
  library = true
)
public class DebugModule {
    final PantryApplication application;

    public DebugModule(PantryApplication application) {
        this.application = application;
    }

    @Provides @Singleton
    Storage provideStorage() {
        return new DebugStorage();
    }
}
