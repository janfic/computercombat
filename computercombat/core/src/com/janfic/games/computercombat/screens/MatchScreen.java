package com.janfic.games.computercombat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.actors.*;
import com.janfic.games.computercombat.model.*;
import com.janfic.games.computercombat.model.moves.*;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.network.Message;
import com.janfic.games.computercombat.network.Type;
import com.janfic.games.computercombat.network.client.ClientMatch;
import com.janfic.games.computercombat.util.ObjectMapSerializer;
import java.util.*;

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

    Panel info, buttons;
    Label infoLabel;
    BorderedGrid infoPanel;
    BorderedGrid leftPanel, rightPanel;
    Map<String, List<SoftwareActor>> softwareActors;
    Map<String, ComputerActor> computerActors;

    List<List<Action>> animation;

    ClientMatch matchData;

    boolean checkGameOver = true;

    public MatchScreen(ComputerCombatGame game, ClientMatch match) {
        this.game = game;
        this.assetManager = game.getAssetManager();
        this.softwareActors = new HashMap<>();
        this.computerActors = new HashMap<>();
        this.animation = new LinkedList<>();
        this.matchData = match;
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
            System.out.println(matchStateData.getMessage());
            MatchState state = json.fromJson(MatchState.class, matchStateData.getMessage());
            this.matchData.setCurrentState(state);
        }

        Component[][] componentBoard = this.matchData.getCurrentState().componentBoard;
        board = new Board(skin, matchData, game, animation);
        for (int x = 0; x < componentBoard.length; x++) {
            for (int y = 0; y < componentBoard[x].length; y++) {
                board.addComponent(new ComponentActor(this.componentAtlas, componentBoard[x][y]), x, y);
            }
        }

        this.softwareActors.put(game.getCurrentProfile().getUID(), new ArrayList<>());
        this.softwareActors.put(matchData.getCurrentState().getOtherProfile(game.getCurrentProfile()).getUID(), new ArrayList<>());
        this.computerActors.put(game.getCurrentProfile().getUID(), new ComputerActor(skin, game));
        this.computerActors.put(matchData.getCurrentState().getOtherProfile(game.getCurrentProfile()).getUID(), new ComputerActor(skin, game));

        leftPanel = new BorderedGrid(skin);
        leftPanel.pad(7);
        leftPanel.top();
        leftPanel.defaults().space(2);
        leftPanel.add(computerActors.get(game.getCurrentProfile().getUID())).expandY().growX().bottom();

        rightPanel = new BorderedGrid(skin);
        rightPanel.pad(7);
        rightPanel.top();
        rightPanel.defaults().space(2);
        rightPanel.add(computerActors.get(matchData.getCurrentState().getOtherProfile(game.getCurrentProfile()).getUID())).expandY().growX().bottom();

        buttons = new Panel(skin);
        buttons.add(new Label(game.getCurrentProfile().getName() + " vs. " + matchData.getOpponentName(), skin));

        infoPanel = new BorderedGrid(skin);
        infoPanel.setSize(220, 43);
        info = new Panel(skin);
        infoLabel = new Label("", skin);
        infoLabel.setAlignment(Align.center);
        info.add(infoLabel).grow();
        infoPanel.add(info).grow();

        table.setFillParent(true);

        Table middleSection = new Table();
        middleSection.add(buttons).growX().height(20).colspan(3).row();
        middleSection.add(new Image(skin, "board_collector_left")).growX().top().padTop(7);
        Table middle = new Table();
        middle.add(board).row();
        middle.add(infoPanel).grow().row();
        middleSection.add(middle).growY();
        middleSection.add(new Image(skin, "board_collector_right")).growX().top().padTop(7).row();

        leftPanel.setZIndex(10);
        middleSection.setZIndex(0);
        rightPanel.setZIndex(10);
        table.add(leftPanel).pad(1, 0, 1, 0).grow().left();
        table.add(middleSection).top().growY();
        table.add(rightPanel).pad(1, 0, 1, 0).grow().right();

        mainStage.addActor(table);
    }

    public Board getBoard() {
        return board;
    }

    public Map<String, ComputerActor> getComputerActors() {
        return computerActors;
    }

    public Map<String, List<SoftwareActor>> getSoftwareActors() {
        return softwareActors;
    }

    public BorderedGrid getLeftPanel() {
        return leftPanel;
    }

    public BorderedGrid getRightPanel() {
        return rightPanel;
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public ClientMatch getMatchData() {
        return matchData;
    }

    public Skin getSkin() {
        return skin;
    }

    @Override
    public void render(float delta) {
        // Basic Update and Render Stage
        mainStage.act(delta);
        mainStage.draw();

        // Set Info text to be whos turn it is
        updateInfoText();

        // Game Over Check and Finish
        gameOverCheck();

        // Render Animations that are queued
        animations(delta);

        // Accept Opponent / Server sent move
        listenForServerMessage();
        
        // Check if player attempted MatchComponentsMove
        playerMatchComponentsMoveCheck();

        // Check if player attempted UseAbilityMove
        playerUseAbilityMoveCheck();

    }

    private void playerUseAbilityMoveCheck() {
        for (SoftwareActor softwareActor : softwareActors.get(game.getCurrentProfile().getUID())) {
            if (softwareActor.activatedAbility()) {
                UseAbilityMove move = new UseAbilityMove(
                        game.getCurrentProfile().getUID(),
                        softwareActor.getSoftware(),
                        softwareActor.getSelectedComponents(),
                        softwareActor.getSelectedSoftwares()
                );
                Json json = new Json();
                softwareActor.setActivatedAbility(false);
                if (GameRules.getAvailableMoves(matchData.getCurrentState()).contains(move)) {
                    game.getServerAPI().sendMessage(new Message(Type.MOVE_REQUEST, json.toJson(move)));
                }
            }
        }
        ComputerActor computerActor = computerActors.get(game.getCurrentProfile().getUID());
        if (computerActor.activatedAbility()) {
            UseAbilityMove move = new UseAbilityMove(
                    game.getCurrentProfile().getUID(),
                    computerActor.getComputer(),
                    new ArrayList<>(),
                    new ArrayList<>()
            );
            Json json = new Json();
            computerActor.setActivatedAbility(false);
            if (GameRules.getAvailableMoves(matchData.getCurrentState()).contains(move)) {
                game.getServerAPI().sendMessage(new Message(Type.MOVE_REQUEST, json.toJson(move)));
            }
        }
    }

    private void playerMatchComponentsMoveCheck() {
        if (board.attemptedMove() && matchData.getCurrentState().currentPlayerMove.getUID().equals(game.getCurrentProfile().getUID())) {
            Move move = board.getMove();
            Json json = new Json();
            game.getServerAPI().sendMessage(new Message(Type.MOVE_REQUEST, json.toJson(move)));
            board.consumeMove();
        }
    }

    private void listenForServerMessage() {
        if (game.getServerAPI().hasMessage() && isAnimating() == false) {
            Message response = game.getServerAPI().readMessage();
            if (response.type == Type.MOVE_ACCEPT) {
                Json json = new Json();
                json.setSerializer(ObjectMap.class, new ObjectMapSerializer());
                List<MoveResult> results = json.fromJson(List.class, response.getMessage());
                animate(results, this);
            } else if (response.type == Type.PING) {
            }
        }
    }

    private void animations(float delta) {
        if (animation.isEmpty() == false) {
            List<Action> a = animation.get(0);
            boolean allDone = true;
            List<Action> r = new ArrayList<>();
            for (Action action : a) {
                if (action.getActor() == null) {
                    r.add(action);
                    continue;
                }
                if (!action.act(delta)) {
                    allDone = false;
                } else {
                    r.add(action);
                }
            }
            a.removeAll(r);
            if (allDone) {
                animation.remove(0);
            }
        } else {
            board.setTouchable(Touchable.enabled);
        }
    }

    private void gameOverCheck() {
        if (matchData.getCurrentState().isGameOver && checkGameOver) {
            infoLabel.setText("GAME OVER! \n " + matchData.getCurrentState().winner.getName() + " wins!");
            board.setTouchable(Touchable.disabled);
            Window window = new Window("Match Info", skin);
            Table table = new Table(skin);
            Label label = new Label("GAME OVER!\n " + matchData.getCurrentState().winner.getName() + " wins!", skin);
            label.setAlignment(Align.center);
            table.add(label).grow();
            table.align(Align.center);
            TextButton okayButton = new TextButton("Okay", skin);
            okayButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.popScreen();
                }
            });
            table.row();
            table.add(okayButton).growX();
            window.add(table).grow();
            window.setSize(mainStage.getWidth() / 2, mainStage.getHeight() / 2);
            window.setPosition(mainStage.getWidth() / 4, mainStage.getHeight() / 4);
            this.mainStage.addActor(window);
            checkGameOver = false;
        }
    }

    private void updateInfoText() {
        if (matchData.getCurrentState().currentPlayerMove.getUID().equals(game.getCurrentProfile().getUID())) {
            board.setTouchable(Touchable.enabled);
            infoLabel.setText("Your Turn!");
        } else {
            board.setTouchable(Touchable.disabled);
            infoLabel.setText(matchData.getOpponentName() + "'s Turn");
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

    public boolean isAnimating() {
        return animation.isEmpty() == false;
    }

    public void animate(List<MoveResult> moveResults, MatchScreen screen) {

        //animate move 
        //change state
        //animate
        int i = 0;
        MoveResult first = moveResults.get(0);
        if (first.getMove() instanceof MatchComponentsMove) {
            List<Action> uD = new ArrayList<>();
            Action f = Actions.run(new Runnable() {
                @Override
                public void run() {
                    matchData.setCurrentState(first.getNewState());
                    board.updateBoard(matchData);
                    matchData.setCurrentState(first.getOldState());
                    int offset = 0;
                    for (MoveAnimation moveAnimation : first.getAnimations()) {
                        List<List<Action>> animations = moveAnimation.animate(matchData.getCurrentState().currentPlayerMove.getUID(), game.getCurrentProfile().getUID(), screen);
                        int indexOfUpdate = animation.indexOf(uD);
                        animation.addAll(indexOfUpdate + 1 + offset, animations);
                        offset += animations.size();
                    }
                }
            });
            f.setActor(board);
            uD.add(f);
            animation.add(uD);
            i = 1;
        }
        for (; i < moveResults.size(); i++) {
            MoveResult moveResult = moveResults.get(i);
            List<Action> updateData = new ArrayList<>();
            Action a = Actions.run(new Runnable() {
                @Override
                public void run() {
                    matchData.setCurrentState(moveResult.getOldState());
                    board.updateBoard(matchData);
                    int offset = 0;
                    for (MoveAnimation moveAnimation : moveResult.getAnimations()) {
                        List<List<Action>> animations = moveAnimation.animate(matchData.getCurrentState().currentPlayerMove.getUID(), game.getCurrentProfile().getUID(), screen);
                        int indexOfUpdate = animation.indexOf(updateData);
                        animation.addAll(indexOfUpdate + 1 + offset, animations);
                        offset += animations.size();
                    }
                }
            });
            a.setActor(board);
            updateData.add(a);
            animation.add(updateData);
        }
        List<Action> updateData = new ArrayList<>();
        Action a = Actions.run(new Runnable() {
            @Override
            public void run() {
                matchData.setCurrentState(moveResults.get(moveResults.size() - 1).getNewState());
                board.updateBoard(matchData);
            }
        });
        a.setActor(board);
        updateData.add(a);
        animation.add(updateData);
    }

    public SoftwareActor getSoftwareActorByMatchID(String playerUID, int matchID) {
        for (SoftwareActor softwareActor : softwareActors.get(playerUID)) {
            if (softwareActor.getSoftware().getMatchID() == matchID) {
                return softwareActor;
            }
        }
        return null;
    }

    public void buildPanels() {
        leftPanel.clear();
        rightPanel.clear();

        for (SoftwareActor softwareActor : softwareActors.get(game.getCurrentProfile().getUID())) {
            leftPanel.add(softwareActor).row();
        }
        for (SoftwareActor softwareActor : softwareActors.get(matchData.getCurrentState().getOtherProfile(game.getCurrentProfile()).getUID())) {
            rightPanel.add(softwareActor).row();
        }

        leftPanel.add(computerActors.get(game.getCurrentProfile().getUID())).expandY().growX().bottom().row();
        rightPanel.add(computerActors.get(matchData.getCurrentState().getOtherProfile(game.getCurrentProfile()).getUID())).expandY().growX().bottom().row();
    }
}
