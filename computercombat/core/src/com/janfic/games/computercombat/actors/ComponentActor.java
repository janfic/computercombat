package com.janfic.games.computercombat.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.janfic.games.computercombat.model.Component;

/**
 *
 * @author Jan Fic
 */
public class ComponentActor extends Actor {

    public static ComponentActor touched;
    TextureRegion region;
    ComponentCollectActor collectedActor;
    Component component;
    boolean isSelected;

    public static TextureAtlas componentAtlas;

    public ComponentActor(TextureAtlas componentAtlas) {
        this.region = componentAtlas.getRegions().get((int) (Math.random() * 6));
        this.setSize(24, 24);
        this.setOrigin(12, 12);
        this.setZIndex(1);
        ComponentActor.componentAtlas = componentAtlas;
    }

    public ComponentActor(TextureAtlas componentAtlas, Component component) {
        this(componentAtlas);
        this.component = component;
        this.region = componentAtlas.findRegion(component.getTextureName());
        this.collectedActor = new ComponentCollectActor(componentAtlas, component);
        this.collectedActor.setVisible(false);
    }

    public ComponentActor(Component component) {
        this(ComponentActor.componentAtlas, component);
    }

    @Override
    public void act(float delta) {
        super.act(delta); //To change body of generated methods, choose Tools | Templates.
        collectedActor.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(getColor());
        batch.draw(region, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        if (collectedActor.isVisible()) {
            batch.setColor(collectedActor.getColor());
            batch.draw(collectedActor.collectedRegion, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        }
        super.draw(batch, parentAlpha); //To change body of generated methods, choose Tools | Templates.
        batch.setColor(Color.WHITE);
    }

    public Component getComponent() {
        return component;
    }

    public ComponentCollectActor getCollectedRegion() {
        return collectedActor;
    }

    public void setRegion(TextureRegion region) {
        this.region = region;
    }

    public void setRegion(Component component) {
        setRegion(componentAtlas.findRegion(component.getTextureName()));
    }
}
