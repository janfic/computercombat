package com.janfic.games.computercombat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Json;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.actors.*;
import com.janfic.games.computercombat.model.*;
import com.janfic.games.computercombat.model.moves.*;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.network.Message;
import com.janfic.games.computercombat.network.Type;
import com.janfic.games.computercombat.network.client.ClientMatch;
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

    BorderedGrid leftPanel, rightPanel;
    Map<String, List<SoftwareActor>> softwareActors;
    Map<String, ComputerActor> computerActors;

    List<List<Action>> animation;

    ClientMatch matchData;

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

        Panel buttons = new Panel(skin);

        BorderedGrid infoPanel = new BorderedGrid(skin);
        infoPanel.setSize(220, 43);
        Panel info = new Panel(skin);
        info.add(new Label(game.getCurrentProfile().getName() + " vs. " + matchData.getOpponentName(), skin));
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
        mainStage.act(delta);
        mainStage.draw();
        if (matchData.getCurrentState().currentPlayerMove.getUID().equals(game.getCurrentProfile().getUID())) {
            board.setTouchable(Touchable.enabled);
        } else {
            board.setTouchable(Touchable.disabled);
        }

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

        if (board.attemptedMove() && matchData.getCurrentState().currentPlayerMove.getUID().equals(game.getCurrentProfile().getUID())) {
            Move move = board.getMove();
            Json json = new Json();
            game.getServerAPI().sendMessage(new Message(Type.MOVE_REQUEST, json.toJson(move)));
            board.consumeMove();
        }
        if (game.getServerAPI().hasMessage() && isAnimating() == false) {
            Message response = game.getServerAPI().readMessage();
            if (response.type == Type.MOVE_ACCEPT) {
                Json json = new Json();
                List<MoveResult> results = json.fromJson(List.class, response.getMessage());
//                matchData.setCurrentState(results.get(results.size() - 1).getNewState());
//                leftPanel.clear();
//                rightPanel.clear();
//                for (String uid : softwareActors.keySet()) {
//                    List<Card> software = matchData.getCurrentState().activeEntities.get(uid);
//                    softwareActors.get(uid).clear();
//                    for (Card card : software) {
//                        SoftwareActor softwareActor = new SoftwareActor(skin, !uid.equals(game.getCurrentProfile().getUID()), (Software) card, game);
//                        softwareActors.get(uid).add(softwareActor);
//                        if (uid.equals(game.getCurrentProfile().getUID())) {
//                            leftPanel.add(softwareActor).row();
//                        } else {
//                            rightPanel.add(softwareActor).row();
//                        }
//                    }
//                }
//                for (String uid : computerActors.keySet()) {
//                    ComputerActor computerActor = computerActors.get(uid);
//                    computerActor.setComputer(matchData.getCurrentState().computers.get(uid));
//                    if (uid.equals(game.getCurrentProfile().getUID())) {
//                        leftPanel.add(computerActors.get(uid)).expandY().growX().bottom();
//                    } else {
//                        rightPanel.add(computerActors.get(uid)).expandY().growX().bottom();
//                    }
//                }
                animate(results, this);
            } else if (response.type == Type.PING) {
                System.out.println("PINGED");
            }
        }
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
        MoveResult first = moveResults.get(0);
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
        for (int i = 1; i < moveResults.size(); i++) {
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

}
