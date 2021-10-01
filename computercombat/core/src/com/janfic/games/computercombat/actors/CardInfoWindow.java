package com.janfic.games.computercombat.actors;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.model.Software;

/**
 *
 * @author Jan Fic
 */
public class CardInfoWindow extends Window {

    Table softwareInfo, abilityInfo;
    Cell<Table> midSection;
    TextButton useAbilityButton;
    ComputerCombatGame game;
    Software software;
    Skin skin;

    public CardInfoWindow(ComputerCombatGame game, Software software, Skin skin, boolean useAbilityEnabled) {
        super("Software Info", skin);

        this.game = game;
        this.software = software;
        this.skin = skin;

        useAbilityButton = new TextButton("Use Ability", skin);
        useAbilityButton.setVisible(useAbilityEnabled);

        createSoftwareInfo(skin, game, software);
        createAbilityInfo(skin, game, software);

        Button leftButton = new Button(skin, "left_arrow");
        Button rightButton = new Button(skin, "right_arrow");
        leftButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (midSection.getActor() == softwareInfo) {
                    midSection.setActor(abilityInfo);
                } else {
                    midSection.setActor(softwareInfo);
                }
            }
        });
        rightButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (midSection.getActor() == softwareInfo) {
                    midSection.setActor(abilityInfo);
                    midSection.grow();
                } else {
                    midSection.setActor(softwareInfo);
                    midSection.grow();
                }
            }
        });
        this.add(leftButton).expandY();
        this.midSection = this.add(softwareInfo).pad(2).grow();
        this.add(rightButton).expandY().row();
        TextButton okayButton = new TextButton("Close", skin);
        okayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CardInfoWindow.this.remove();
            }
        });
        this.add(okayButton).growX().colspan(3).row();
    }

    private void createSoftwareInfo(Skin skin, ComputerCombatGame game, Software software) {
        softwareInfo = new Table();
        softwareInfo.defaults().space(5);
        Label title = new Label("Software", skin, "title");
        title.setAlignment(Align.center);
        softwareInfo.add(title).colspan(2).growX().row();
        CollectionCard c = new CollectionCard(game, skin, software, 1);
        c.removeListener(c.getNewWindowOnClick());
        softwareInfo.add(c);
        Table stats = new Table(skin);
        stats.defaults().space(1);
        //Label pack = new Label("Pack: " + software.getPack(), skin, "filled");
        Label level = new Label("Level: " + 1, skin, "filled");
        Label attack = new Label("Attack: " + software.getMaxAttack(), skin, "filled");
        Label defense = new Label("Defense: " + software.getMaxArmor(), skin, "filled");
        Label health = new Label("Health: " + software.getMaxHealth(), skin, "filled");
        Label charge = new Label("Charge: " + software.getRunRequirements(), skin, "filled");
        Label ability = new Label("Ability: " + software.getAbility().getName(), skin, "filled");
        //stats.add(pack).growX().left().row();
        stats.add(level).growX().left().row();
        stats.add(attack).growX().left().row();
        stats.add(defense).growX().left().row();
        stats.add(health).growX().left().row();
        stats.add(charge).growX().left().row();
        stats.add(ability).growX().left().row();
        softwareInfo.add(stats).grow().row();
    }

    private void createAbilityInfo(Skin skin, ComputerCombatGame game, Software software) {
        abilityInfo = new Table();
        abilityInfo.defaults().space(5);
        Label title = new Label("Ability", skin, "title");
        title.setAlignment(Align.center);
        abilityInfo.add(title).colspan(2).growX().row();
        BorderedArea imageBorder = new BorderedArea(skin);
        Image i = new Image(game.getAssetManager()
                .get("texture_packs/" + software.getPack() + ".atlas", TextureAtlas.class)
                .findRegion(software.getAbility().getTextureName())
        );
        imageBorder.add(i).width(46).height(46);
        Label name = new Label(software.getAbility().getName(), skin, "filled");
        name.setAlignment(Align.center);
        Label description = new Label(software.getAbility().getDescription(), skin, "filled");
        description.setAlignment(Align.center);
        abilityInfo.add(name).center().growX().row();
        abilityInfo.add(imageBorder).center().width(48).height(48).expandX().row();
        abilityInfo.add(description).center().grow().row();
        if (useAbilityButton.isVisible()) {
            abilityInfo.add(useAbilityButton).center().growX().row();
        }
    }

    public void setUseAbilityButtonVisibility(boolean isVisible) {
        useAbilityButton.setVisible(isVisible);
        createAbilityInfo(skin, game, software);
    }

    public TextButton getUseAbilityButton() {
        return useAbilityButton;
    }

}
