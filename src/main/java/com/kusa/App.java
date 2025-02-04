package com.kusa;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

//https://javadoc.io/doc/com.badlogicgames.gdx/gdx-backend-lwjgl3/latest/com/badlogic/gdx/backends/lwjgl3/Lwjgl3Application.html
//https://javadoc.io/doc/com.badlogicgames.gdx/gdx-backend-lwjgl3/latest/com/badlogic/gdx/backends/lwjgl3/Lwjgl3ApplicationConfiguration.html

/**
 * App launcher for desktop platforms. 
 *
 * This class is responsible for setting up a LWJGL3 application and 
 * creating an instance of game.
 *
 */
public class App
{
    /**
     * Entry point to entire app.
     *
     * @param args array of command line arguments. (IGNORED FOR NOW)
     */
    public static void main(String[] args)
    {
        //if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createGame();
    }

    /**
     * Instantiates a game with default configuration.
     */
    private static Lwjgl3Application createGame()
    {
        return new Lwjgl3Application(new MyGame(), getDefaultConfig());
    }

    /**
     * Returns an Lwjgl3ApplicationConfiguration with default values.
     *
     * the default values are specified by the programer **here**.
     *
     * See more about config here: 
     * 
     */
    private static Lwjgl3ApplicationConfiguration getDefaultConfig()
    {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();

        //Set application title.
        configuration.setTitle("Some Title");

        /*
         * .useVsync(true)
         * Vsync limits the frames per second to what your hardware can display, and helps eliminate
         * screen tearing. This setting doesn't always work on Linux, so the line after is a safeguard.
         *
         * .setForgroundFPS(...)
         * Limits FPS to the refresh rate of the currently active monitor, plus 1 to try to match fractional
         * refresh rates. The Vsync setting above should limit the actual FPS to match the monitor.
         *
         * If you remove the set foreground line and set Vsync to false, you can get unlimited FPS, which can be
         * useful for testing performance, but can also be very stressful to some hardware.
         *
         * You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.
         */
        
        configuration.useVsync(true);
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);


        //Set windowed mode and dimensions.
		configuration.setWindowedMode(640, 480);

        return configuration;
    }
}
