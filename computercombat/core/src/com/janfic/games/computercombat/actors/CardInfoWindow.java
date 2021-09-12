package com.janfic.games.computercombat.actors;

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

    public CardInfoWindow(ComputerCombatGame game, Software software, Skin skin) {
        super("Software Info", skin);
        softwareInfo = new Table();
        softwareInfo.defaults().space(5);
        Label title = new Label("Software", skin, "title");
        title.setAlignment(Align.center);
        softwareInfo.add(title).colspan(2).growX().row();
        CollectionCard c = new CollectionCard(game, skin, software, 1);
        c.removeListener(c.getNewWindowOnClick());
        softwareInfo.add(c);
        Table stats = new Table(skin);
        Label name = new Label("Name: " + software.getName(), skin);
        Label pack = new Label("Pack: " + software.getPack(), skin);
        Label level = new Label("Level: " + 1, skin);
        Label attack = new Label("Attack: " + software.getMaxAttack(), skin);
        Label defense = new Label("Defense: " + software.getMaxArmor(), skin);
        Label health = new Label("Health: " + software.getMaxHealth(), skin);
        Label charge = new Label("Charge: " + software.getRunRequirements(), skin);
        stats.add(name).expandX().left().row();
        stats.add(pack).expandX().left().row();
        stats.add(level).expandX().left().row();
        stats.add(attack).expandX().left().row();
        stats.add(defense).expandX().left().row();
        stats.add(health).expandX().left().row();
        stats.add(charge).expandX().left().row();
        softwareInfo.add(stats).grow().row();
        Button leftButton = new Button(skin, "left_arrow");
        Button rightButton = new Button(skin, "right_arrow");
        this.add(leftButton).expandY();
        this.midSection = this.add(softwareInfo).pad(4).grow();
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
}
