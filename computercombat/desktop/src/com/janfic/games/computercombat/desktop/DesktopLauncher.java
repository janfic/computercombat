package com.janfic.games.computercombat.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.janfic.games.computercombat.ComputerCombatGame;

public class DesktopLauncher {

    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1920 / 2;
        config.height = 1080 / 2;
        config.resizable = false;
        new LwjglApplication(new ComputerCombatGame(), config);
    }
}
