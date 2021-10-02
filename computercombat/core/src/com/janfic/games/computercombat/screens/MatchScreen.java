package com.janfic.games.computercombat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Json;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.actors.Board;
import com.janfic.games.computercombat.actors.BorderedGrid;
import com.janfic.games.computercombat.actors.ComponentActor;
import com.janfic.games.computercombat.actors.ComputerActor;
import com.janfic.games.computercombat.actors.Panel;
import com.janfic.games.computercombat.actors.SoftwareActor;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.MatchState;
import com.janfic.games.computercombat.model.Software;
import com.janfic.games.computercombat.model.components.CPUComponent;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.network.Message;
import com.janfic.games.computercombat.network.Type;
import com.janfic.games.computercombat.network.client.ClientMatch;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class MatchScreen implements Screen {

    OrthographicCamera mainCamera;
    Stage mainStage;

    AssetManager assetManager;
    Skin skin;
    TextureAtlas componentAtlas;

    ComputerCombatGame game;

    Board board;

    List<SoftwareActor> softwareActors;
    List<ComputerActor> computerActors;

    ClientMatch match;

    public MatchScreen(ComputerCombatGame game, ClientMatch match) {
        this.game = game;
        this.assetManager = game.getAssetManager();
        this.softwareActors = new ArrayList<>();
        this.computerActors = new ArrayList<>();
        this.match = match;
    }

    @Override
    public void show() {
        this.skin = assetManager.get("skins/computer_combat_skin.json");
        this.componentAtlas = assetManager.get("texture_packs/components.atlas");

        this.mainCamera = new OrthographicCamera(1920 / 4, 1080 / 4);

        this.mainStage = ComputerCombatGame.makeNewStage(mainCamera);

        Gdx.input.setInputProcessor(mainStage);

        Table table = new Table();
        //table.defaults().grow().space(5);
        //table.debugAll();

        while (game.getServerAPI().hasMessage() == false) {
        }
        Message matchStateData = game.getServerAPI().readMessage();
        Json json = new Json();

        if (matchStateData.type == Type.MATCH_STATE_DATA) {
            MatchState state = json.fromJson(MatchState.class, matchStateData.getMessage());
            this.match.setCurrentState(state);
        }

        Component[][] componentBoard = this.match.getCurrentState().componentBoard;
        board = new Board(skin, match, game);
        for (int x = 0; x < componentBoard.length; x++) {
            for (int y = 0; y < componentBoard[x].length; y++) {
                board.addComponent(new ComponentActor(this.componentAtlas, componentBoard[x][y]), x, y);
            }
        }

        softwareActors.add(new SoftwareActor(skin, true, new Software(1, "", "computer_pack", "virus", 1, 3, 3, 3, 3, new Class[]{CPUComponent.class}, 10, null), game));
        softwareActors.add(new SoftwareActor(skin, true, new Software(1, "", "computer_pack", "firewall", 1, 3, 3, 3, 3, new Class[]{CPUComponent.class}, 10, null), game));
        softwareActors.add(new SoftwareActor(skin, false, new Software(1, "", "computer_pack", "directory", 1, 3, 3, 3, 3, new Class[]{CPUComponent.class}, 10, null), game));
        softwareActors.add(new SoftwareActor(skin, false, new Software(1, "", "computer_pack", "worm", 1, 3, 3, 3, 3, new Class[]{CPUComponent.class}, 10, null), game));
        computerActors.add(new ComputerActor(skin));
        computerActors.add(new ComputerActor(skin));

        BorderedGrid leftPanel = new BorderedGrid(skin);
        leftPanel.pad(7);
        leftPanel.top();
        leftPanel.defaults().space(2);
        leftPanel.add(softwareActors.get(2)).row();
        leftPanel.add(softwareActors.get(3)).row();
        leftPanel.add(computerActors.get(0)).expandY().growX().bottom();

        BorderedGrid rightPanel = new BorderedGrid(skin);
        rightPanel.pad(7);
        rightPanel.top();
        rightPanel.defaults().space(2);
        rightPanel.add(softwareActors.get(0)).row();
        rightPanel.add(softwareActors.get(1)).row();
        rightPanel.add(computerActors.get(1)).expandY().growX().bottom();

        Panel buttons = new Panel(skin);

        BorderedGrid infoPanel = new BorderedGrid(skin);
        infoPanel.setSize(220, 43);
        Panel info = new Panel(skin);
        info.add(new Label(game.getCurrentProfile().getName() + " vs. " + match.getOpponentName(), skin));
        infoPanel.add(info).grow();

        table.setFillParent(true);

        Table middleSection = new Table();
        middleSection.add(buttons).colspan(3).grow().row();
        middleSection.add(new Image(skin, "board_collector_left")).growX();
        middleSection.add(board);
        middleSection.add(new Image(skin, "board_collector_right")).growX().row();
        middleSection.add(infoPanel).colspan(3).grow().row();

        table.add(leftPanel).pad(1, 0, 1, 0).growY().left();
        table.add(middleSection).grow();
        table.add(rightPanel).pad(1, 0, 1, 0).growY().right();

        mainStage.addActor(table);
    }

    @Override
    public void render(float f) {
        mainStage.act(f);
        mainStage.draw();
        if (board.attemptedMove() && match.getCurrentState().currentPlayerMove.getUID().equals(game.getCurrentProfile().getUID())) {
            Move move = board.getMove();
            Json json = new Json();
            game.getServerAPI().sendMessage(new Message(Type.MOVE_REQUEST, json.toJson(move)));
            board.consumeMove();
        }
        if (game.getServerAPI().hasMessage() && board.isAnimating() == false) {
            Message response = game.getServerAPI().readMessage();
            if (response.type == Type.MOVE_ACCEPT) {
                Json json = new Json();
                List<MoveResult> results = json.fromJson(List.class, response.getMessage());
                board.animate(results);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        mainStage.getViewport().update(width, height);
        mainCamera.update();
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
