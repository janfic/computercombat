package com.janfic.games.computercombat.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.model.Card;

/**
 *
 * @author Jan Fic
 */
public class CardInfoWindow extends Window {

    Table softwareInfo, abilityInfo, moreInfo;
    Cell<Table> midSection;
    TextButton useAbilityButton;
    ComputerCombatGame game;
    Card software;
    Skin skin;
    Actor preventClickActor;

    public CardInfoWindow(ComputerCombatGame game, Card software, Skin skin, boolean useAbilityEnabled) {
        super("Card Info", skin);

        this.game = game;
        this.software = software;
        this.skin = skin;

        this.preventClickActor = new Actor();

        useAbilityButton = new TextButton("Use Ability", skin);
        useAbilityButton.setColor(Color.SKY);
        useAbilityButton.setVisible(useAbilityEnabled);

        createSoftwareInfo(skin, game, software);
        createAbilityInfo(skin, game, software);
        createMoreInfo(skin, game, software);

        if (useAbilityEnabled) {
            this.midSection = this.add(abilityInfo).pad(2).grow();
        } else {
            this.midSection = this.add(softwareInfo).pad(2).grow();
        }
        this.row();
        TextButton okayButton = new TextButton("Close", skin);
        okayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getStage().getActors().removeValue(preventClickActor, true);
                CardInfoWindow.this.addAction(Actions.removeActor());
            }
        });
        this.add(okayButton).growX().colspan(3).row();

    }

    private void createSoftwareInfo(Skin skin, ComputerCombatGame game, Card software) {
        softwareInfo = new Table();
        softwareInfo.defaults().space(3);
        Table cardTable = new Table(skin);
        CollectionCard c = new CollectionCard(game, skin, software, 1);
        c.removeListener(c.getNewWindowOnClick());
        TextButton aboutButton = new TextButton("More", skin);
        aboutButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                midSection.setActor(moreInfo);
            }
        });
        cardTable.add(c).row();
        cardTable.add(aboutButton).growX();
        softwareInfo.add(cardTable).growY();
        Table stats = new Table(skin);
        stats.defaults().space(1);
        Label level = new Label("Name: " + software.getName(), skin, "title");
        Label attack = new Label("Attack: " + software.getMaxAttack(), skin, "title");
        Label defense = new Label("Defense: " + software.getMaxArmor(), skin, "title");
        Label health = new Label("Health: " + software.getMaxHealth(), skin, "title");
        Label charge = new Label("Charge: " + software.getRunRequirements(), skin, "title");
        TextButton ability = new TextButton("Ability: " + software.getAbility().getName(), skin);
        ability.align(Align.left);
        Label pack = new Label("Collection: " + software.getCollection().getName(), skin, "title");
        stats.add(level).growX().left().row();
        stats.add(pack).growX().left().row();
        stats.add(attack).growX().left().row();
        stats.add(defense).growX().left().row();
        stats.add(health).growX().left().row();
        stats.add(charge).growX().left().row();
        stats.add(ability).growX().left().row();
        softwareInfo.add(stats).grow().row();
        ability.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                midSection.setActor(abilityInfo);
            }
        });
    }

    private void createAbilityInfo(Skin skin, ComputerCombatGame game, Card software) {
        abilityInfo = new Table();
        abilityInfo.defaults().space(5);
        Label title = new Label("Ability", skin, "title");
        title.setAlignment(Align.center);
        abilityInfo.add(title).colspan(2).growX().row();
        BorderedArea imageBorder = new BorderedArea(skin);
        Image i = new Image(game.getAssetManager()
                .get("texture_packs/" + software.getCollection().getTextureName() + ".atlas", TextureAtlas.class)
                .findRegion(software.getAbility().getTextureName())
        );
        imageBorder.add(i).width(46).height(46);
        Label name = new Label(software.getAbility().getName(), skin, "title");
        name.setAlignment(Align.center);
        Label description = new Label(software.getAbility().getDescription(), skin, "filled");
        description.setWrap(true);
        description.setAlignment(Align.center);
        abilityInfo.add(name).center().growX().row();
        abilityInfo.add(imageBorder).center().width(48).height(48).expandX().row();
        abilityInfo.add(description).center().grow().row();
        if (useAbilityButton.isVisible()) {
            useAbilityButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    getStage().getActors().removeValue(preventClickActor, true);
                    CardInfoWindow.this.addAction(Actions.removeActor());
                }
            });
            abilityInfo.add(useAbilityButton).center().growX().row();
        }
        TextButton cardInfoButton = new TextButton("Card Info", skin);
        cardInfoButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                midSection.setActor(softwareInfo);
            }
        });
        abilityInfo.add(cardInfoButton).growX().row();
    }

    public void createMoreInfo(Skin skin, ComputerCombatGame game, Card software) {
        moreInfo = new Table();
        moreInfo.defaults().space(5);
        Label title = new Label("More", skin, "title");
        title.setAlignment(Align.center);
        moreInfo.add(title).colspan(2).growX().row();
        BorderedArea imageBorder = new BorderedArea(skin);
        Image i = new Image(game.getAssetManager()
                .get("texture_packs/" + software.getCollection().getTextureName() + ".atlas", TextureAtlas.class)
                .findRegion(software.getTextureName())
        );
        imageBorder.add(i).width(46).height(46);
        Label name = new Label(software.getName(), skin, "title");
        name.setAlignment(Align.center);
        Label description = new Label(software.getDescription(), skin, "filled");
        description.setWrap(true);
        description.setAlignment(Align.center);
        moreInfo.add(name).center().growX().row();
        moreInfo.add(imageBorder).center().width(48).height(48).expandX().row();
        moreInfo.add(description).center().grow().row();
        TextButton cardInfoButton = new TextButton("Back", skin);
        cardInfoButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                midSection.setActor(softwareInfo);
            }
        });
        moreInfo.add(cardInfoButton).growX().row();
    }

    public void setUseAbilityButtonVisibility(boolean isVisible) {
        useAbilityButton.setVisible(isVisible);
        createAbilityInfo(skin, game, software);
    }

    public TextButton getUseAbilityButton() {
        return useAbilityButton;
    }

    @Override
    protected void setStage(Stage stage) {
        if (stage != null) {
            this.setSize(3 * stage.getWidth() / 4f, stage.getHeight());
            this.setPosition(1 * stage.getWidth() / 8f, stage.getHeight());

            preventClickActor.setBounds(0, 0, stage.getWidth(), stage.getHeight());
            int index = stage.getActors().indexOf(this, true);
            stage.getActors().insert(index, preventClickActor);
        }
        super.setStage(stage);
    }
}
