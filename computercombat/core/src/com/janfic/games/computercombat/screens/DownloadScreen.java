package com.janfic.games.computercombat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.janfic.games.computercombat.Assets;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.actors.CollectionPackActor;
import com.janfic.games.computercombat.actors.Panel;
import com.janfic.games.computercombat.model.Collection;
import com.janfic.games.computercombat.network.client.SQLAPI;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class DownloadScreen implements Screen {
    
    ComputerCombatGame game;
    Skin skin;
    
    Stage stage;
    Camera cam;
    
    List<Collection> collections;
    List<CollectionPackActor> packs;
    int startPacks = 0, endPacks = 2, selected = 1;
    
    Table packsTable;
    Label packName, packDescription, packPrice;
    
    public DownloadScreen(ComputerCombatGame game) {
        this.skin = game.getAssetManager().get(Assets.SKIN);
        this.game = game;
        this.packs = new ArrayList<>();
    }
    
    @Override
    public void show() {
        this.cam = new OrthographicCamera(1920 / 4, 1080 / 4);
        this.stage = ComputerCombatGame.makeNewStage(cam);
        this.collections = SQLAPI.getSingleton().getCollections();
        packs.clear();
        
        for (Collection collection : collections) {
            packs.add(new CollectionPackActor(game, skin, collection));
        }
        Gdx.input.setInputProcessor(stage);
        
        Table table = new Table();
        table.setFillParent(true);
        table.defaults().space(5);
        table.pad(4);
        
        packsTable = new Table(skin);
        packsTable.defaults().space(5);
        ScrollPane scrollPane = new ScrollPane(packsTable, skin);
        makePacks();
        
        Panel packInfoTable = new Panel(skin);
        packInfoTable.defaults().space(3).pad(2);
        packInfoTable.top();
        
        Label packInfoLabel = new Label("About", skin, "title");
        packInfoLabel.setAlignment(Align.center);
        packInfoTable.add(packInfoLabel).growX().row();
        
        packName = new Label("Press a Pack to learn more.", skin);
        packDescription = new Label("Press a Pack to learn more.", skin);
        Label packPriceLabel = new Label("Packets to Download:", skin);
        packPrice = new Label("???", skin);
        packName.setWrap(true);
        packPrice.setWrap(true);
        packDescription.setWrap(true);
        packPriceLabel.setWrap(true);
        packName.setAlignment(Align.center);
        packPrice.setAlignment(Align.center);
        packDescription.setAlignment(Align.center);
        packPriceLabel.setAlignment(Align.center);
        
        Image packetsImage = new Image(game.getAssetManager().get("texture_packs/components.atlas", TextureAtlas.class).findRegion("network"));
        Table packetTable = new Table(skin);
        packetTable.defaults().space(10);
        packetTable.add(packetsImage).size(24, 24);
        packetTable.add(packPrice);
        
        packInfoTable.add(packName).growX().row();
        packInfoTable.add(packDescription).grow().row();
        packInfoTable.add(packPriceLabel).growX().expandY().bottom().row();
        packInfoTable.add(packetTable).growX().row();
        TextButton viewCollectionButton = new TextButton("View Collection", skin);
        final TextButton downloadButton = new TextButton("Download", skin);
        packInfoTable.add(viewCollectionButton).growX().row();
        packInfoTable.add(downloadButton).growX();
        
        Table titleTable = new Table(skin);
        titleTable.setBackground("border");
        
        Label title = new Label("Card Packs", skin);
        title.setAlignment(Align.center);
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.popScreen();
            }
        });
        
        Image playerPacketImage = new Image(game.getAssetManager().get("texture_packs/components.atlas", TextureAtlas.class).findRegion("network"));
        Table playerPacketTable = new Table(skin);
        playerPacketTable.defaults().padRight(10);
        playerPacketTable.add(new Label("Your Packets: ", skin));
        playerPacketTable.add(playerPacketImage).size(24, 24);
        playerPacketTable.add(new Label("" + game.getCurrentProfile().getPackets(), skin));
        titleTable.add(backButton);
        titleTable.add(title).growX();
        titleTable.add(playerPacketTable).row();
        
        table.add(scrollPane).grow();
        table.add(packInfoTable).minWidth(200).growY().row();
        table.add(titleTable).growX().colspan(2).row();
        
        stage.addActor(table);
        
        downloadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.getCurrentProfile().getPackets() >= collections.get(selected).getPrice()) {
                    downloadButton.addAction(Actions.sequence(
                            Actions.touchable(Touchable.disabled),
                            Actions.color(Color.GREEN),
                            Actions.color(Color.WHITE, 1),
                            Actions.run(() -> {
                                game.getCurrentProfile().setPackets(game.getCurrentProfile().getPackets() - collections.get(selected).getPrice());
                                game.pushScreen(new OpenPackScreen(game, collections.get(selected)));
                            }),
                            Actions.touchable(Touchable.enabled)
                    ));
                    
                } else {
                    downloadButton.addAction(Actions.sequence(
                            Actions.touchable(Touchable.disabled),
                            Actions.color(Color.RED),
                            Actions.color(Color.WHITE, 1),
                            Actions.touchable(Touchable.enabled)
                    ));
                }
            }
        });
        viewCollectionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selected > 0) {
                    game.pushScreen(new CollectionScreen(game, collections.get(selected)));
                }
            }
        });
    }
    
    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        this.stage.getViewport().update(width, height);
        cam.update();
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
    
    public void makePacks() {
        int mid = (endPacks + startPacks) / 2;
        for (int i = 0; i < packs.size(); i++) {
            packsTable.add(packs.get(i));
            int j = i;
            packs.get(i).addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    makeAbout(j);
                }
            });
        }
    }
    
    public void makeAbout(int packIndex) {
        selected = packIndex;
        packName.setText(collections.get(packIndex).getName() + " Pack");
        packDescription.setText(collections.get(packIndex).getDescription());
        packPrice.setText("" + collections.get(packIndex).getPrice());
    }
    
}
