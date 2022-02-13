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
import com.janfic.games.computercombat.actors.CollectionCard;
import com.janfic.games.computercombat.actors.CollectionPackActor;
import com.janfic.games.computercombat.model.Collection;
import com.janfic.games.computercombat.model.Software;
import com.janfic.games.computercombat.network.client.SQLAPI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    List<Software> collectionCards;
    List<CollectionCard> cardActors;

    public OpenPackScreen(ComputerCombatGame game, Collection collection) {
        this.game = game;
        this.collection = collection;
    }

    @Override
    public void show() {
        this.camera = new OrthographicCamera(1920 / 4, 1080 / 4);
        this.stage = ComputerCombatGame.makeNewStage(camera);
        this.skin = game.getAssetManager().get(Assets.SKIN);

        List<Integer> collectionIDs = new ArrayList<>();
        collectionIDs.add(collection.getID());
        this.collectionCards = SQLAPI.getSingleton().getCardsInCollection(collectionIDs, null);

        this.pack = new CollectionPackActor(game, skin, collection);
        pack.setScale(1.25f);

        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.defaults().space(20);

        TextButton openButton = new TextButton("Open!", skin);
        openButton.setTouchable(Touchable.disabled);

        table.add(pack).row();
        table.add(openButton).width(100).row();

        Action cardAction = Actions.sequence(Actions.visible(false), Actions.delay(1), Actions.moveBy(0, -stage.getHeight() * 2), Actions.visible(true),
                Actions.moveBy(0, stage.getHeight() * 2, 2, Interpolation.fastSlow),
                Actions.delay(2));

        Action buttonAction = Actions.sequence(Actions.fadeOut(0), Actions.delay(3), Actions.fadeIn(1), Actions.touchable(Touchable.enabled));

        openButton.addAction(buttonAction);
        pack.addAction(cardAction);

        cardActors = new ArrayList<>();
        openButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                pack.open();
                openButton.setVisible(false);
                Action open = Actions.sequence(
                        Actions.moveBy(0, -200, 1),
                        Actions.run(() -> {
                            confetti();
                            for (CollectionCard openCard : cardActors) {
                                openCard.setPosition(pack.getX(), pack.getY());
                                openCard.setSize(pack.getWidth() - 4, pack.getHeight());
                                openCard.addAction(Actions.parallel(
                                        Actions.moveBy(0, 200, 1, Interpolation.circleOut),
                                        Actions.moveBy((cardActors.indexOf(openCard) - 1) * 150, 0, 1, Interpolation.circleIn)
                                ));
                            }
                        })
                );
                pack.addAction(open);
            }
        });

        List<Software> openCards = new ArrayList<>();
        openCards.add(roll());
        openCards.add(roll());
        openCards.add(roll());

        for (Software openCard : openCards) {
            CollectionCard c = new CollectionCard(game, skin, openCard, 1);
            cardActors.add(c);
            stage.addActor(c);
        }

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

    public void confetti() {

    }

    public Software roll() {
        int r = (int) (Math.random() * 5 * 5 * 5) + 1;
        int selectedRarity = 1;
        for (int i = 1; i <= 5; i++) {
            selectedRarity = i;
            if (i * i * i >= r) {
                break;
            }
        }
        List<Software> pool = new ArrayList<>();
        for (Software card : collectionCards) {
            if (card.getRarity() == selectedRarity) {
                pool.add(card);
            }
        }

        Collections.shuffle(pool);
        return pool.get(0);
    }
}
