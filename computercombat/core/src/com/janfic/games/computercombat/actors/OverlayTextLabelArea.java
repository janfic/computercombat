package com.janfic.games.computercombat.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

public class OverlayTextLabelArea<T> extends BorderedArea {

    T dataObject;

    Label dataLabel;

    public OverlayTextLabelArea(Skin skin, T dataObject) {
        super(skin);
        this.dataObject = dataObject;
        dataLabel = new Label("", skin);
        dataLabel.setFontScale(0.5f);
        dataLabel.setAlignment(Align.center);
        this.add(dataLabel);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        this.dataLabel.setText(updateLabel(dataObject));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
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
}
