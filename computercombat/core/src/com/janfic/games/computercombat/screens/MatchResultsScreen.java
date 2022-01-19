package com.janfic.games.computercombat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.janfic.games.computercombat.Assets;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.actors.BorderedArea;
import com.janfic.games.computercombat.actors.BorderedGrid;
import com.janfic.games.computercombat.actors.Panel;
import com.janfic.games.computercombat.model.match.MatchResults;

/**
 *
 * @author Jan Fic
 */
public class MatchResultsScreen implements Screen {

    MatchResults matchResults;
    ComputerCombatGame game;

    Stage stage;
    OrthographicCamera camera;
    Table table;

    Skin skin;

    public MatchResultsScreen(ComputerCombatGame game, MatchResults matchResults) {
        this.game = game;
        this.matchResults = matchResults;
        this.skin = game.getAssetManager().get(Assets.SKIN, Skin.class);
    }

    @Override
    public void show() {
        this.camera = new OrthographicCamera(1920 / 4, 1080 / 4);
        this.stage = ComputerCombatGame.makeNewStage(camera);
        Gdx.input.setInputProcessor(stage);

        this.table = new Table(this.skin);
        table.setFillParent(true);
        table.defaults().space(3);
        table.pad(3);
        table.top();
        stage.addActor(table);

        Table titleTable = new Table(skin);
        titleTable.setBackground("border");
        titleTable.add(new Label("Match Results", skin));
        table.add(titleTable).growX().row();

        BorderedGrid mainTable = new BorderedGrid(skin);
        mainTable.defaults().space(3);
        mainTable.pad(8);
        mainTable.top();
        table.add(mainTable).grow();

        Panel matchPanel = new Panel(skin);
        matchPanel.defaults().space(3);
        matchPanel.top();
        matchPanel.pad(5);

        Label winOrLossLabel = new Label("Victory", skin);
        winOrLossLabel.setAlignment(Align.center);
        matchPanel.add(winOrLossLabel).colspan(3).growX().row();

        BorderedArea playerIcon = new BorderedArea(skin);
        BorderedArea opponentIcon = new BorderedArea(skin);

        matchPanel.add(playerIcon).width(64).height(64);
        matchPanel.add(new Label("vs.", skin));
        matchPanel.add(opponentIcon).width(64).height(64).row();

        Label player1Label = new Label("Player 1", skin);
        player1Label.setAlignment(Align.center);
        Label player2Label = new Label("Player 2", skin);
        player2Label.setAlignment(Align.center);
        matchPanel.add(player1Label).width(64);
        matchPanel.add().pad(10);
        matchPanel.add(player2Label).width(64).row();

        Label matchStartLabel = new Label("Match Start: 12:00PM", skin);
        Label matchEndLabel = new Label("Match End: 12:30PM", skin);

        matchPanel.add(matchStartLabel).colspan(3).row();
        matchPanel.add(matchEndLabel).colspan(3).row();

        Panel rewardsPanel = new Panel(skin);
        rewardsPanel.defaults().space(3);
        rewardsPanel.top();
        rewardsPanel.pad(5);

        Label rewardsLabel = new Label("Rewards", skin);
        rewardsLabel.setAlignment(Align.center);
        rewardsPanel.add(rewardsLabel).growX().row();

        mainTable.add(matchPanel).grow();
        mainTable.add(rewardsPanel).grow().row();

        TextButton okayButton = new TextButton("Okay", skin);
        mainTable.add(okayButton).colspan(2).width(100).expandX().pad(5).spaceBottom(5);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
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
