package com.janfic.games.computercombat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.janfic.games.computercombat.Assets;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.actors.BorderedArea;
import com.janfic.games.computercombat.actors.BorderedGrid;
import com.janfic.games.computercombat.actors.Panel;
import com.janfic.games.computercombat.model.match.MatchResults;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import static java.time.temporal.ChronoUnit.SECONDS;

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

        Label winOrLossLabel = new Label(this.matchResults.winner ? "Victory" : "Defeat", skin);
        winOrLossLabel.setAlignment(Align.center);
        matchPanel.add(winOrLossLabel).colspan(3).growX().row();

        BorderedArea playerIcon = new BorderedArea(skin);
        BorderedArea opponentIcon = new BorderedArea(skin);

        matchPanel.add(playerIcon).width(64).height(64);
        matchPanel.add(new Label("vs.", skin));
        matchPanel.add(opponentIcon).width(64).height(64).row();

        Label player1Label = new Label(this.game.getCurrentProfile().getName(), skin);
        player1Label.setAlignment(Align.center);
        Label player2Label = new Label("Opponent's Turn", skin);
        player2Label.setAlignment(Align.center);
        matchPanel.add(player1Label).width(64);
        matchPanel.add().pad(10);
        matchPanel.add(player2Label).width(64).row();

        Label matchStartLabel = new Label(getTime(this.matchResults.start) + " - " + getTime(this.matchResults.end), skin);
        long seconds = this.matchResults.start.toLocalDateTime().until(this.matchResults.end.toLocalDateTime(), SECONDS);
        Label length = new Label("(" + (seconds / 60) + ":" + (seconds % 60) + ")", skin);
        matchPanel.add(matchStartLabel).colspan(3).row();
        matchPanel.add(length).colspan(3).row();

        Panel rewardsPanel = new Panel(skin);
        rewardsPanel.defaults().space(3);
        rewardsPanel.top();
        rewardsPanel.pad(5);

        Label rewardsLabel = new Label("Rewards", skin);
        rewardsLabel.setAlignment(Align.center);
        rewardsPanel.add(rewardsLabel).growX().colspan(2).padBottom(15).row();

        for (String string : matchResults.rewards.keySet()) {
            Image packetsImage = new Image(game.getAssetManager().get("texture_packs/components.atlas", TextureAtlas.class).findRegion("network"));
            Label networkAmount = new Label("" + matchResults.rewards.get(string), skin);
            rewardsPanel.add(new Label(string, skin)).expandX().center().colspan(2).row();
            rewardsPanel.add(packetsImage).width(24).height(24).right();
            rewardsPanel.add(networkAmount).padLeft(5).left().row();
        }
        Image packetsImage = new Image(game.getAssetManager().get("texture_packs/components.atlas", TextureAtlas.class).findRegion("network"));
        rewardsPanel.add(new Label("Total", skin)).expandX().center().colspan(2).row();
        rewardsPanel.add(packetsImage).width(24).height(24).right();
        rewardsPanel.add(new Label("" + matchResults.totalPacketsEarned, skin)).padLeft(5).left().row();

        mainTable.add(matchPanel).grow();
        mainTable.add(rewardsPanel).grow().row();

        TextButton okayButton = new TextButton("Okay", skin);
        okayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.popScreen();
            }
        });
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

    private String getTime(Timestamp time) {
        String r = "";
        LocalDateTime dt = time.toLocalDateTime();
        return "" + (dt.getHour() % 12) + ":" + dt.getMinute() + (dt.getHour() > 12 ? "PM" : "AM");
    }

}
