package com.janfic.games.computercombat.actors;

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

    float offset;

    public CollectionPackActor(ComputerCombatGame game, Skin skin, Collection collection) {
        TextureAtlas texturePack = game.getAssetManager().get("texture_packs/" + collection.getPath() + ".atlas", TextureAtlas.class);
        this.packTexture = texturePack.findRegion(collection.getTextureName());
        this.icon = texturePack.findRegion(collection.getTextureName() + "_icon");
        this.collection = collection;
        this.setOrigin(packTexture.getRegionWidth() * getScaleX() / 2f, packTexture.getRegionHeight() * getScaleY() / 2f);
        this.setSize(packTexture.getRegionWidth() * getScaleX(), packTexture.getRegionHeight() * getScaleY());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(getColor());
        batch.draw(packTexture, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
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
}
