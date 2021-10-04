package com.janfic.games.computercombat.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 *
 * @author Jan Fic
 */
public class BorderedArea extends Table {
    
    NinePatch screen;
    
    public BorderedArea(Skin skin) {
        this.screen = skin.getPatch("screen");
    }
    
    public BorderedArea(Skin skin, String name) {
        this(skin);
        this.setName(name);
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(this.getColor());
        screen.draw(batch, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        super.draw(batch, parentAlpha); //To 
    }
    
}
