package com.janfic.games.computercombat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.actors.Board;
import com.janfic.games.computercombat.actors.BorderedGrid;
import com.janfic.games.computercombat.actors.ComponentActor;
import com.janfic.games.computercombat.actors.ComputerActor;
import com.janfic.games.computercombat.actors.OverlayTextLabelArea;
import com.janfic.games.computercombat.actors.Panel;
import com.janfic.games.computercombat.actors.SoftwareActor;
import com.janfic.games.computercombat.model.Software;
import com.janfic.games.computercombat.model.components.*;
import com.janfic.games.computercombat.network.client.ClientMatch;
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
    
    ClientMatch match;

    public MatchScreen(ComputerCombatGame game) {
        this.game = game;
        this.assetManager = game.getAssetManager();
        this.softwareActors = new ArrayList<>();
        this.computerActors = new ArrayList<>();
        this.overlayActors = new HashMap<>();
    }

    @Override
    public void show() {
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

        Software fireWall = new Software("Fire Wall", "computer_pack", "firewall", 1, 3, 5, 1, 1, new Class[]{CPUComponent.class}, 3, null);
        Software virus = new Software("Virus", "computer_pack", "virus", 1, 3, 5, 1, 1, new Class[]{CPUComponent.class}, 3, null);
        Software disk_defrag = new Software("Disk Defragmenter", "computer_pack", "disk_defragmenter", 1, 3, 5, 1, 1, new Class[]{StorageComponent.class, RAMComponent.class}, 3, null);
        Software worm = new Software("Worm", "computer_pack", "worm", 1, 3, 5, 1, 1, new Class[]{PowerComponent.class, CPUComponent.class, NetworkComponent.class}, 3, null);
        Software bubble = new Software("Bubble Sort", "computer_pack", "bubble_sort", 1, 3, 5, 1, 1, new Class[]{CPUComponent.class, RAMComponent.class}, 3, null);
        Software rng = new Software("Random Number Generator", "computer_pack", "rng", 1, 3, 5, 1, 1, new Class[]{CPUComponent.class}, 3, null);
        Software web_search = new Software("Web Search", "computer_pack", "web_search", 1, 3, 5, 1, 1, new Class[]{NetworkComponent.class, RAMComponent.class}, 3, null);
        Software directory = new Software("Directory", "computer_pack", "directory", 1, 3, 5, 1, 1, new Class[]{StorageComponent.class}, 3, null);

        softwareActors.add(new SoftwareActor(skin, false, fireWall, game));
        softwareActors.add(new SoftwareActor(skin, false, virus, game));
        softwareActors.add(new SoftwareActor(skin, false, disk_defrag, game));
        softwareActors.add(new SoftwareActor(skin, false, worm, game));
        softwareActors.add(new SoftwareActor(skin, true, bubble, game));
        softwareActors.add(new SoftwareActor(skin, true, rng, game));
        softwareActors.add(new SoftwareActor(skin, true, web_search, game));
        softwareActors.add(new SoftwareActor(skin, true, directory, game));
        computerActors.add(new ComputerActor(skin));
        computerActors.add(new ComputerActor(skin));

        BorderedGrid leftPanel = new BorderedGrid(skin);
        leftPanel.pad(7);
        leftPanel.top();
        leftPanel.defaults().space(2);
        leftPanel.add(softwareActors.get(0)).row();
        leftPanel.add(softwareActors.get(1)).row();
        leftPanel.add(softwareActors.get(2)).row();
        leftPanel.add(softwareActors.get(3)).row();
        leftPanel.add(computerActors.get(0)).expandY().growX().bottom();

        BorderedGrid rightPanel = new BorderedGrid(skin);
        rightPanel.pad(7);
        rightPanel.top();
        rightPanel.defaults().space(2);
        rightPanel.add(softwareActors.get(4)).row();
        rightPanel.add(softwareActors.get(5)).row();
        rightPanel.add(softwareActors.get(6)).row();
        rightPanel.add(softwareActors.get(7)).row();
        rightPanel.add(computerActors.get(1)).expandY().growX().bottom();

        Panel buttons = new Panel(skin);

        BorderedGrid infoPanel = new BorderedGrid(skin);
        infoPanel.setSize(220, 43);
        Panel info = new Panel(skin);
        infoPanel.add(info).grow();

        table.setFillParent(true);

        Table middleSection = new Table();
        middleSection.pad(0, 5, 0, 5);
        middleSection.add(buttons).grow().row();
        middleSection.add(board).row();
        middleSection.add(infoPanel).grow().row();
        middleSection.defaults().pad(5).top().expand();

        table.add(leftPanel).pad(1).growY().left();
        table.add(middleSection).grow();
        table.add(rightPanel).pad(1).growY().right();

        mainStage.addActor(table);

        for (SoftwareActor softwareActor : softwareActors) {
            for (OverlayTextLabelArea<Software> overlayTextLabelArea : softwareActor.getOverlayTextLabelAreas()) {
                statsStage.addActor(overlayTextLabelArea.getOverlayLabel());
            }
        }
    }

    @Override
    public void render(float f) {
        mainStage.act(f);
        mainStage.draw();
        statsStage.act(f);
        statsStage.draw();
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
