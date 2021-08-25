package com.janfic.games.computercombat.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;

/**
 *
 * @author Jan Fic
 */
public class SoftwareActor extends Panel {

    ProgressBar magicBar, healthBar, defenseBar, attackBar;
    BorderedArea imageArea;

    public SoftwareActor(Skin skin, boolean flipped) {
        super(skin);

        this.defaults().height(48).space(1);

        ProgressBarStyle red = new ProgressBarStyle(skin.get("default-vertical", ProgressBarStyle.class));
        red.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("df3e23"));
        ProgressBarStyle green = new ProgressBarStyle(skin.get("default-vertical", ProgressBarStyle.class));
        green.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("9cdb43"));
        ProgressBarStyle grey = new ProgressBarStyle(skin.get("default-vertical", ProgressBarStyle.class));
        grey.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("dae0ea"));
        ProgressBarStyle blue = new ProgressBarStyle(skin.get("default-vertical", ProgressBarStyle.class));
        blue.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("249fde"));

        magicBar = new ProgressBar(0, 10, 1, true, blue);
        healthBar = new ProgressBar(0, 10, 1, true, green);
        defenseBar = new ProgressBar(0, 10, 1, true, grey);
        attackBar = new ProgressBar(0, 10, 1, true, red);
        imageArea = new BorderedArea(skin);

        magicBar.setValue(5);
        healthBar.setValue(5);
        defenseBar.setValue(5);
        attackBar.setValue(5);

        Stack magicStack = new Stack();
        magicStack.add(magicBar);
        Table magicOverlay = new Table();
        magicOverlay.add(new BorderedArea(skin, "magicTextBox")).expand().fillX().height(9);
        magicStack.add(magicOverlay);

        Stack healthStack = new Stack();
        healthStack.add(healthBar);
        Table healthOverlay = new Table();
        healthOverlay.add(new BorderedArea(skin, "attackTextBox")).expand().fillX().height(9);
        healthStack.add(healthOverlay);

        Stack defenseStack = new Stack();
        defenseStack.add(defenseBar);
        Table defenseOverlay = new Table();
        defenseOverlay.add(new BorderedArea(skin, "defenseTextBox")).expand().fillX().height(9);
        defenseStack.add(defenseOverlay);

        Stack attackStack = new Stack();
        attackStack.add(attackBar);
        Table attackOverlay = new Table();
        attackOverlay.add(new BorderedArea(skin, "attackTextBox")).expand().fillX().height(9);
        attackStack.add(attackOverlay);

        VerticalGroup leds = new VerticalGroup();
        leds.addActor(new LEDActor(skin, "NETWORK"));
        leds.addActor(new LEDActor(skin, "CPU"));
        leds.addActor(new LEDActor(skin, "STORAGE"));
        //leds.addActor(new LEDActor(skin, "POWER"));
        leds.addActor(new LEDActor(skin, "RAM"));
        leds.addActor(new LEDActor(skin, "BUG"));

        leds.space(3).center();

        if (flipped) {
            this.add(leds).padRight(2);
            this.add(magicStack).width(9).prefSize(8, 8);
            this.add(attackStack).width(9);
            this.add(imageArea).width(48);
            this.add(defenseStack).width(9);
            this.add(healthStack).width(9);
        } else {
            this.add(healthStack).width(9);
            this.add(defenseStack).width(9);
            this.add(imageArea).width(48);
            this.add(attackStack).width(9);
            this.add(magicStack).width(9).prefSize(8, 8);
            this.add(leds).padLeft(2);
        }
    }

}
