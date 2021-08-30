package com.janfic.games.computercombat.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.Software;
import com.janfic.games.computercombat.model.components.BugComponent;
import com.janfic.games.computercombat.model.components.CPUComponent;
import com.janfic.games.computercombat.model.components.NetworkComponent;
import com.janfic.games.computercombat.model.components.PowerComponent;
import com.janfic.games.computercombat.model.components.RAMComponent;
import com.janfic.games.computercombat.model.components.StorageComponent;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jan Fic
 */
public class CollectionCard extends BorderedGrid {

    public static final Map<Class<? extends Component>, String> components;

    static {
        components = new HashMap<>();
        components.put(CPUComponent.class, "CPU");
        components.put(BugComponent.class, "BUG");
        components.put(PowerComponent.class, "POWER");
        components.put(NetworkComponent.class, "NETWORK");
        components.put(RAMComponent.class, "RAM");
        components.put(StorageComponent.class, "STORAGE");
    }

    public CollectionCard(ComputerCombatGame game, Skin skin, Software software, int amount) {
        super(skin);
        this.defaults().space(3);
        this.pad(5);
        this.top();
        this.align(Align.top);
        BorderedArea area = new BorderedArea(skin);
        area.add(new Image(game.getAssetManager().get("texture_packs/" + software.getPack() + ".atlas", TextureAtlas.class).findRegion(software.getTextureName())));
        Label l = new Label(software.getName(), skin, "paneled");
        l.setWrap(true);
        l.setAlignment(Align.center);
        add(l).width(100).row();
        add(area).width(48).height(48).row();

        ProgressBar.ProgressBarStyle red = new ProgressBar.ProgressBarStyle(skin.get("default-vertical", ProgressBar.ProgressBarStyle.class));
        red.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("df3e23"));
        ProgressBar.ProgressBarStyle green = new ProgressBar.ProgressBarStyle(skin.get("default-vertical", ProgressBar.ProgressBarStyle.class));
        green.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("9cdb43"));
        ProgressBar.ProgressBarStyle grey = new ProgressBar.ProgressBarStyle(skin.get("default-vertical", ProgressBar.ProgressBarStyle.class));
        grey.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("dae0ea"));
        ProgressBar.ProgressBarStyle blue = new ProgressBar.ProgressBarStyle(skin.get("default-vertical", ProgressBar.ProgressBarStyle.class));
        blue.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("249fde"));

        ProgressBar magicBar = new ProgressBar(0, 10, 1, false, blue);
        ProgressBar healthBar = new ProgressBar(0, 10, 1, false, green);
        ProgressBar defenseBar = new ProgressBar(0, 10, 1, false, grey);
        ProgressBar attackBar = new ProgressBar(0, 10, 1, false, red);
        magicBar.setValue(10);
        healthBar.setValue(10);
        defenseBar.setValue(10);
        attackBar.setValue(10);

        this.add(attackBar).width(90).row();
        this.add(defenseBar).width(90).row();
        this.add(healthBar).width(90).row();
        this.add(magicBar).width(90).row();

        Panel leds = new Panel(skin);
        for (Class<? extends Component> runComponent : software.getRunComponents()) {
            LEDActor led = new LEDActor(skin, components.get(runComponent));
            led.setLightOn(true);
            leds.add(led).padLeft(3).padRight(3);
        }
        leds.defaults().space(7);
        Label amountLabel = new Label("" + amount, skin, "paneled");
        amountLabel.setAlignment(Align.center);
        this.add(amountLabel).expand().minWidth(20).bottom().row();
        this.add(leds).growX().bottom().expand().row();
    }
}
