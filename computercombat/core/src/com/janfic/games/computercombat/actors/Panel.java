package com.janfic.games.computercombat.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 *
 * @author Jan Fic
 */
public class Panel extends Table {

    protected NinePatch panel;

    public Panel(Skin skin) {
        this.panel = skin.getPatch("panel");
        this.setSkin(skin);
        this.pad(4);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        panel.draw(batch, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        super.draw(batch, parentAlpha); //To change body of generated methods, choose Tools | Templates.
    }
}
