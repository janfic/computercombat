package com.janfic.games.computercombat.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 *
 * @author Jan Fic
 */
public class LEDActor extends Actor {

    Drawable ledBorder, ledLight, ledGlow;
    boolean lightOn;
    Color ledColor;
    String componentColor;

    public LEDActor(Skin skin, String componentColor) {
        this.ledBorder = skin.getDrawable("led_border");
        this.ledLight = skin.getDrawable("led_light");
        this.ledGlow = skin.getDrawable("led_glow");
        this.lightOn = false;
        this.ledColor = skin.getColor(componentColor);
        this.componentColor = componentColor;
        setSize(5, 5);
    }

    public LEDActor(Skin skin) {
        this(skin, "RGBA_255_255_255_255");
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color c = batch.getColor();
        batch.setColor(getColor());
        ledBorder.draw(batch, getX(), getY(), getWidth(), getHeight());
        if (lightOn == false) {
            batch.setColor(ledColor.cpy().mul(0.75f, 0.75f, 0.75f, 1));
        } else {
            batch.setColor(ledColor);
        }
        ledLight.draw(batch, getX(), getY(), getWidth(), getHeight());
        batch.setColor(ledColor.cpy().mul(1, 1, 1, 0.5f));
        if (lightOn) {
            ledGlow.draw(batch, getX() - 2, getY() - 2, getWidth() + 4, getHeight() + 4);
        }
        batch.setColor(c);
    }

    public void setLightOn(boolean lightOn) {
        this.lightOn = lightOn;
    }

    @Override
    public void setColor(Color color) {
        this.ledColor = color;
    }

    public String getComponentColor() {
        return componentColor;
    }
}
