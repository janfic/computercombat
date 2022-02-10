package com.janfic.games.computercombat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.janfic.games.computercombat.Assets;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.actors.CollectionPackActor;
import com.janfic.games.computercombat.model.Collection;

/**
 *
 * @author Jan Fic
 */
public class OpenPackScreen implements Screen {
    
    ComputerCombatGame game;
    
    Stage stage;
    Camera camera;
    Skin skin;
    
    Collection collection;
    CollectionPackActor pack;
    
    public OpenPackScreen(ComputerCombatGame game, Collection collection) {
        this.game = game;
        this.collection = collection;
    }
    
    @Override
    public void show() {
        this.camera = new OrthographicCamera(1920 / 4, 1080 / 4);
        this.stage = ComputerCombatGame.makeNewStage(camera);
        this.skin = game.getAssetManager().get(Assets.SKIN);
        
        this.pack = new CollectionPackActor(game, skin, collection);
        
        Gdx.input.setInputProcessor(stage);
        
        Table table = new Table();
        table.setFillParent(true);
        table.defaults().space(20);
        
        TextButton openButton = new TextButton("Open!", skin);
        openButton.addAction(Actions.fadeOut(0));
        openButton.setTouchable(Touchable.disabled);
        
        table.add(pack).row();
        table.add(openButton).width(100).row();
        
        Action cardAction = Actions.sequence(Actions.visible(false), Actions.delay(2), Actions.moveBy(0, -stage.getHeight() * 2), Actions.visible(true),
                Actions.moveBy(0, stage.getHeight() * 2, 2, Interpolation.fastSlow),
                Actions.delay(2));
        
        Action buttonAction = Actions.sequence(Actions.delay(5), Actions.fadeIn(1), Actions.touchable(Touchable.enabled));
        
        openButton.addAction(buttonAction);
        pack.addAction(cardAction);
        
        openButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.popScreen();
            }
        });
        
        stage.addActor(table);
        table.layout();
    }
    
    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        this.stage.getViewport().update(width, height);
        camera.update();
    }
    
    @Override
    public void pause() {
    }
    
    @Override
    public void resume() {
    }
    
    @Override
    public void hide() {
    }
    
    @Override
    public void dispose() {
    }
    
}
