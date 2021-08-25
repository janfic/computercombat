package com.janfic.games.computercombat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.actors.Board;
import com.janfic.games.computercombat.actors.BorderedArea;
import com.janfic.games.computercombat.actors.BorderedGrid;
import com.janfic.games.computercombat.actors.ComponentActor;
import com.janfic.games.computercombat.actors.ComputerActor;
import com.janfic.games.computercombat.actors.Panel;
import com.janfic.games.computercombat.actors.SoftwareActor;
import com.janfic.games.computercombat.model.components.BugComponent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jan Fic
 */
public class MatchScreen implements Screen {

    OrthographicCamera mainCamera, statsCamera;
    Stage mainStage, statsStage;

    AssetManager assetManager;
    Skin skin;
    TextureAtlas componentAtlas;

    ComputerCombatGame game;

    List<SoftwareActor> softwareActors;
    List<ComputerActor> computerActors;
    Map<Actor, Label> overlayActors;

    public MatchScreen(ComputerCombatGame game) {
        this.game = game;
        this.assetManager = game.getAssetManager();
        this.softwareActors = new ArrayList<>();
        this.computerActors = new ArrayList<>();
        this.overlayActors = new HashMap<>();
    }

    @Override
    public void show() {
        System.out.println("HERER");
        this.skin = assetManager.get("skins/computer_combat_skin.json");
        this.componentAtlas = assetManager.get("texture_packs/components.atlas");

        this.mainCamera = new OrthographicCamera(1920 / 4, 1080 / 4);
        this.statsCamera = new OrthographicCamera(1920 / 2, 1080 / 2);

        this.mainStage = ComputerCombatGame.makeNewStage(mainCamera);
        this.statsStage = new Stage(new FitViewport(1920 / 2, 1080 / 2, statsCamera));

        Gdx.input.setInputProcessor(mainStage);

        Table table = new Table();
        //table.defaults().grow().space(5);
        //table.debugAll();

        Board board = new Board(skin);
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                board.addComponent(new ComponentActor(this.componentAtlas, new BugComponent(x, y)), x, y);
            }
        }

        softwareActors.add(new SoftwareActor(skin, false));
        softwareActors.add(new SoftwareActor(skin, false));
        softwareActors.add(new SoftwareActor(skin, true));
        softwareActors.add(new SoftwareActor(skin, true));
        softwareActors.add(new SoftwareActor(skin, true));
        softwareActors.add(new SoftwareActor(skin, true));
        computerActors.add(new ComputerActor(skin));
        computerActors.add(new ComputerActor(skin));

        BorderedGrid leftPanel = new BorderedGrid(skin);
        leftPanel.pad(7);
        leftPanel.top();
        leftPanel.defaults().space(2);
        leftPanel.add(softwareActors.get(0)).row();
        leftPanel.add(softwareActors.get(1)).row();
        leftPanel.add(computerActors.get(0)).expandY().growX().bottom();

        BorderedGrid rightPanel = new BorderedGrid(skin);
        rightPanel.pad(7);
        rightPanel.top();
        rightPanel.defaults().space(2);
        rightPanel.add(softwareActors.get(2)).row();
        rightPanel.add(softwareActors.get(3)).row();
        rightPanel.add(softwareActors.get(4)).row();
        rightPanel.add(softwareActors.get(5)).row();
        rightPanel.add(computerActors.get(1)).expandY().growX().bottom();

        Panel buttons = new Panel(skin);

        BorderedGrid infoPanel = new BorderedGrid(skin);
        infoPanel.setSize(220, 43);
        Panel info = new Panel(skin);
        infoPanel.add(info).grow();

        table.setFillParent(true);

        VerticalGroup middleSection = new VerticalGroup();
        middleSection.addActor(buttons);
        middleSection.addActor(board);
        middleSection.addActor(infoPanel);
        middleSection.space(3).top().expand();

        table.add(leftPanel).pad(1).growY().left();
        table.add(middleSection).grow();
        table.add(rightPanel).pad(1).growY().right();

        mainStage.addActor(table);

        for (SoftwareActor softwareActor : softwareActors) {
            for (Actor actor : softwareActor.getChildren()) {
                if (actor instanceof Stack) {
                    BorderedArea textBox = (BorderedArea) ((Table) ((Stack) actor).getChild(1)).getChild(0);
                    Label label = new Label("", skin);
                    overlayActors.put(textBox, label);
                    statsStage.addActor(label);
                }
            }
        }
    }

    @Override
    public void render(float f) {
        mainStage.act(f);
        mainStage.draw();
        statsStage.act(f);
        statsStage.draw();
        for (SoftwareActor softwareActor : softwareActors) {
            for (Actor actor : softwareActor.getChildren()) {
                if (actor instanceof Stack) {
                    BorderedArea textBox = (BorderedArea) ((Table) ((Stack) actor).getChild(1)).getChild(0);
                    Vector2 v = textBox.localToScreenCoordinates(new Vector2(0, 0));
                    Label l = overlayActors.get(textBox);
                    l.setWidth(8);
                    l.setHeight(8);
                    l.setText("" + 9);
                    boolean two = l.getText().length() == 2;
                    Vector2 stageCoords = new Vector2(v.x + textBox.getWidth() / 2 + 1 + (two ? -3 : 0), Gdx.graphics.getHeight() - v.y + textBox.getHeight() / 2 + 1);
                    l.setPosition(stageCoords.x, stageCoords.y);

                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        mainStage.getViewport().update(width, height);
        mainCamera.update();
        statsStage.getViewport().update(width, height);
        statsCamera.update();
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
