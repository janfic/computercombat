package com.janfic.games.computercombat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.janfic.games.computercombat.Assets;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.actors.BorderedGrid;
import com.janfic.games.computercombat.actors.DeckActor;

/**
 *
 * @author Jan Fic
 */
public class DecksScreen implements Screen {

    ComputerCombatGame game;

    Skin skin;

    OrthographicCamera stageCamera, overlayCamera;
    Stage stage, overlay;

    Table table, decks, deckCards;
    Table decksTable, deckTable, collectionTable;

    public DecksScreen(ComputerCombatGame game) {
        this.game = game;
        this.skin = game.getAssetManager().get(Assets.SKIN, Skin.class);
    }

    @Override
    public void show() {
        this.stageCamera = new OrthographicCamera(1920 / 4, 1080 / 4);
        this.overlayCamera = new OrthographicCamera(1920 / 2, 1080 / 2);

        this.stage = ComputerCombatGame.makeNewStage(stageCamera);
        this.overlay = new Stage(new FitViewport(1920 / 2, 1080 / 2, overlayCamera));

        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        table.defaults().space(3);
        table.pad(2);

        Label title = new Label("Decks", skin, "title");
        title.setAlignment(Align.center);
        table.add(title).growX().colspan(3).row();

        decksTable = new Table(skin);
        decksTable.defaults().space(3);

        Label decksTitle = new Label("Decks", skin, "title");
        decksTitle.setAlignment(Align.center);

        decks = new Table();
        decks.defaults().space(5).height(60).width(70);
        DeckActor d = new DeckActor(null, skin);
        decks.add(d).row();
        decks.add(new DeckActor(null, skin)).row();
        decks.add(new DeckActor(null, skin)).row();
        decks.add(new DeckActor(null, skin)).row();
        decks.add(new DeckActor(null, skin)).row();
        ScrollPane decksScroll = new ScrollPane(decks, skin);
        decksScroll.setFadeScrollBars(false);
        TextButton createButton = new TextButton("Create", skin);

        decksTable.add(decksTitle).growX().row();
        decksTable.add(decksScroll).grow().row();
        decksTable.add(createButton).growX().row();

        deckTable = new Table(skin);
        deckTable.defaults().space(3);
        
        Label cardsTitle = new Label("Cards", skin, "title");
        cardsTitle.setAlignment(Align.center);

        deckCards = new Table();

        ScrollPane deckCardsPane = new ScrollPane(deckCards, skin);
        deckCardsPane.setFadeScrollBars(false);

        TextButton saveButton = new TextButton("Save", skin);

        deckTable.add(cardsTitle).growX().row();
        deckTable.add(deckCardsPane).grow().row();
        deckTable.add(saveButton).growX().row();

        collectionTable = new BorderedGrid(skin);

        table.add(decksTable).width(Value.percentWidth(0.20f, table)).growY();
        table.add(deckTable).width(Value.percentWidth(0.20f, table)).growY();
        table.add(collectionTable).grow().row();

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.popScreen();
            }
        });

        table.add(backButton).expandX().colspan(3).left().row();

        stage.addActor(table);
    }

    @Override
    public void render(float f) {
        stage.act(f);
        overlay.act(f);
        stage.draw();
        overlay.draw();
    }

    @Override
    public void resize(int i, int i1) {
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
