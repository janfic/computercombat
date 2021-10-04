package com.janfic.games.computercombat.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

/**
 *
 * @author Jan Fic
 */
public class BorderedGrid extends Table {
    
    NinePatch border;
    TiledDrawable grid;
    boolean showGrid;
    
    public BorderedGrid(Skin skin) {
        border = skin.getPatch("border");
        grid = skin.getTiledDrawable("grid_tiled");
        showGrid = true;
        this.pad(6);
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(getColor());
        border.draw(batch, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        //grid.draw(batch, getX() + 1, getY() + 1, getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        if (showGrid) {
            grid.draw(batch, getX() + 6, getY() + 6, getWidth() - 12, getHeight() - 12);
        }
        //batch.draw(grid.getRegion(), getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        super.draw(batch, parentAlpha);
    }
    
    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }
    
}
