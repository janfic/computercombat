package com.janfic.games.computercombat.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import java.util.function.BooleanSupplier;

public class OverlayTextLabelArea<T> extends BorderedArea {

    Label overlayLabel;
    T dataObject;
    boolean renderedLastFrame = false;

    public OverlayTextLabelArea(Skin skin, T dataObject) {
        super(skin);
        this.overlayLabel = new Label("", skin);
        this.dataObject = dataObject;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        overlayLabel.setText(updateLabel(dataObject));
        this.renderedLastFrame = false;

        Vector2 v = this.localToScreenCoordinates(new Vector2(0, 0));
        Label l = this.getOverlayLabel();
        l.setWidth(8);
        l.setHeight(8);
        boolean two = l.getText().length() == 2;
        Vector2 screenCoords = new Vector2(v.x + this.getWidth() / 2 + 1 + (two ? -3 : 0), v.y - this.getHeight() / 2);
        Vector2 stageCoords = overlayLabel.getStage().screenToStageCoordinates(screenCoords);
        l.setPosition(stageCoords.x, stageCoords.y);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        this.renderedLastFrame = true;
    }

    public String updateLabel(T dataObject) {
        return dataObject.toString();
    }

    public void setDataObject(T dataObject) {
        this.dataObject = dataObject;
    }

    public T getDataObject() {
        return dataObject;
    }

    public Label getOverlayLabel() {
        return overlayLabel;
    }
}
