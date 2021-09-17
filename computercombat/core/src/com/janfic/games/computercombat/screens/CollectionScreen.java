package com.janfic.games.computercombat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.janfic.games.computercombat.Assets;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.actors.BorderedGrid;
import com.janfic.games.computercombat.actors.CollectionCard;
import com.janfic.games.computercombat.actors.OverlayTextLabelArea;
import com.janfic.games.computercombat.actors.Panel;
import com.janfic.games.computercombat.model.Deck;
import com.janfic.games.computercombat.model.Profile;
import com.janfic.games.computercombat.model.Software;
import com.janfic.games.computercombat.network.Message;
import com.janfic.games.computercombat.network.Type;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class CollectionScreen implements Screen {

    ComputerCombatGame game;

    Stage stage;

    OrthographicCamera camera;

    Skin skin;
    Table collection;
    BorderedGrid filterBar;
    ScrollPane collectionScrollPane;

    ShapeRenderer sr;

    public CollectionScreen(ComputerCombatGame game) {
        this.game = game;
        this.skin = game.getAssetManager().get(Assets.SKIN);
        this.sr = new ShapeRenderer();
    }

    @Override
    public void show() {
        this.camera = new OrthographicCamera(1920 / 4, 1080 / 4);
        this.stage = ComputerCombatGame.makeNewStage(camera);
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.defaults().space(5);
        table.pad(5);

        Label title = new Label("Collection", skin, "title");
        title.setAlignment(Align.center);

        collection = new Table();
        collection.defaults().space(5).growY();

        collectionScrollPane = new ScrollPane(collection, skin);
        collectionScrollPane.setFadeScrollBars(false);

        filterBar = new BorderedGrid(skin);
        filterBar.pad(10);
        filterBar.defaults().space(5);
        filterBar.top();

        Panel filterTitle = new Panel(skin);
        filterTitle.add(new Label("Filter Collection", skin)).pad(2);
        Panel searchPanel = new Panel(skin);
        searchPanel.add(new Image(skin.get("magnifying_class_icon", Drawable.class))).pad(5);
        TextField searchField = new TextField("", skin);
        searchPanel.add(searchField).growX().row();

        TextButton applyButton = new TextButton("Apply", skin);

        filterBar.add(filterTitle).row();
        filterBar.add(searchPanel).row();
        filterBar.add(applyButton).expand().bottom().row();

        TextButton backButton = new TextButton("Back", skin);

        table.add(title).colspan(2).growX().row();
        table.add(filterBar).growY();
        table.add(collectionScrollPane).grow().row();

        table.add(backButton).expandX().width(150).left().row();

        stage.addActor(table);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.popScreen();
            }
        });

        Gdx.app.postRunnable(requestProfileInfoRunnable);
    }

    @Override
    public void render(float f) {
        stage.act(f);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
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

    final Runnable requestProfileInfoRunnable = new Runnable() {
        @Override
        public void run() {
            Json json = new Json();
            Profile profile = game.getCurrentProfile();
            Deck playerCollection = profile.getCollection();

            Message request = new Message(Type.CARD_INFO_REQUEST, json.toJson(playerCollection.getCards()));

            game.getServerAPI().sendMessage(request);

            while (game.getServerAPI().hasMessage() == false) {
            }

            Message response = game.getServerAPI().readMessage();
            List<Software> cardInfo = json.fromJson(List.class, response.getMessage());
            cardInfo.sort((o1, o2) -> {
                return o1.getName().compareTo(o2.getName()); //To change body of generated lambdas, choose Tools | Templates.
            });
            collection.clearChildren();
            boolean isEven = false;

            for (Software card : cardInfo) {
                CollectionCard cc = new CollectionCard(game, skin, card, profile.getCollection().getCardCount(card.getPack() + "/" + card.getName()));
                collection.add(cc);
                if (isEven) {
                    collection.row();
                }
                isEven = !isEven;
            }
        }
    };

}
