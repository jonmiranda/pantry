package net.jonmiranda.pantry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityRobolectricTest {

    @Test
    public void testDummy() {
        assertEquals(true, true);
    }
}
