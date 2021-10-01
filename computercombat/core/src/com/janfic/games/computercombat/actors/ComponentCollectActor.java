package com.janfic.games.computercombat.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.janfic.games.computercombat.model.Component;

/**
 *
 * @author Jan Fic
 */
public class ComponentCollectActor extends Actor {

    TextureRegion collectedRegion;
    Component component;

    public static TextureAtlas componentAtlas;

    public ComponentCollectActor(TextureAtlas componentAtlas, Component component) {
        this.setSize(24, 24);
        this.setOrigin(12, 12);
        ComponentCollectActor.componentAtlas = componentAtlas;
        this.component = component;
        this.collectedRegion = componentAtlas.findRegion(component.getTextureName() + "_collect");
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.setColor(getColor());
        batch.draw(collectedRegion, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        batch.setColor(Color.WHITE);
    }

}
