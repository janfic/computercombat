package com.janfic.games.computercombat.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.model.Collection;

/**
 *
 * @author Jan Fic
 */
public class CollectionPackActor extends Actor {

    private TextureRegion packTexture;
    private TextureRegion icon;
    private Collection collection;
    private TextureRegion openTexture;

    float offset;
    boolean opened;

    public CollectionPackActor(ComputerCombatGame game, Skin skin, Collection collection) {
        TextureAtlas texturePack = game.getAssetManager().get("texture_packs/" + collection.getPath() + ".atlas", TextureAtlas.class);
        this.packTexture = texturePack.findRegion(collection.getTextureName());
        this.openTexture = texturePack.findRegion(collection.getTextureName() + "_opened");
        this.icon = texturePack.findRegion(collection.getTextureName() + "_icon");
        this.collection = collection;
        this.setOrigin(packTexture.getRegionWidth() * getScaleX() / 2f, packTexture.getRegionHeight() * getScaleY() / 2f);
        this.setSize(packTexture.getRegionWidth() * getScaleX(), packTexture.getRegionHeight() * getScaleY());
        this.opened = false;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(getColor());
        if (opened) {
            batch.draw(openTexture, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        } else {
            batch.draw(packTexture, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        }

    }

    @Override
    public void act(float delta) {
        super.act(delta); //To change body of generated methods, choose Tools | Templates.
        this.setScale(1 - Math.abs(offset));
        this.setOrigin(packTexture.getRegionWidth() * getScaleX() / 2f, packTexture.getRegionHeight() * getScaleY() / 2f);
        this.setSize(packTexture.getRegionWidth() * getScaleX(), packTexture.getRegionHeight() * getScaleY());
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

    public void open() {
        this.opened = true;
    }
}
