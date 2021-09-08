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
public class ComponentActor extends Actor {
    
    public static ComponentActor touched;
    TextureRegion region;
    Component component;
    
    public ComponentActor(TextureAtlas componentAtlas) {
        this.region = componentAtlas.getRegions().get((int) (Math.random() * 6));
        this.setSize(24, 24);
        this.setOrigin(12, 12);
    }
    
    public ComponentActor(TextureAtlas componentAtlas, Component component) {
        this(componentAtlas);
        this.component = component;
        this.region = componentAtlas.findRegion(component.getTextureName());
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(getColor());
        batch.draw(region, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        super.draw(batch, parentAlpha); //To change body of generated methods, choose Tools | Templates.
        batch.setColor(Color.WHITE);
    }
    
    public Component getComponent() {
        return component;
    }
}
