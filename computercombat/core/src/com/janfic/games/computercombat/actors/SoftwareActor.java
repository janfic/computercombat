package com.janfic.games.computercombat.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.Software;
import com.janfic.games.computercombat.model.components.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jan Fic
 */
public class SoftwareActor extends Panel {

    ProgressBar magicBar, healthBar, defenseBar, attackBar;
    BorderedArea imageArea;

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

    Software software;
    List<OverlayTextLabelArea<Software>> areas;

    public SoftwareActor(Skin skin, boolean flipped, Software software, ComputerCombatGame game) {
        super(skin);

        this.software = software;
        this.areas = new ArrayList<>();
//        Json json = new Json();
//        FileHandle file = Gdx.files.local(software.getName() + ".json");
//        file.writeString(json.toJson(this.software), false);

        this.defaults().height(48).space(1);

        ProgressBarStyle red = new ProgressBarStyle(skin.get("default-vertical", ProgressBarStyle.class));
        red.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("df3e23"));
        ProgressBarStyle green = new ProgressBarStyle(skin.get("default-vertical", ProgressBarStyle.class));
        green.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("9cdb43"));
        ProgressBarStyle grey = new ProgressBarStyle(skin.get("default-vertical", ProgressBarStyle.class));
        grey.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("dae0ea"));
        ProgressBarStyle blue = new ProgressBarStyle(skin.get("default-vertical", ProgressBarStyle.class));
        blue.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("249fde"));

        magicBar = new ProgressBar(0, 10, 1, true, blue);
        healthBar = new ProgressBar(0, 10, 1, true, green);
        defenseBar = new ProgressBar(0, 10, 1, true, grey);
        attackBar = new ProgressBar(0, 10, 1, true, red);
        imageArea = new BorderedArea(skin);
        imageArea.add(new Image(game.getAssetManager().get("texture_packs/" + software.getPack() + ".atlas", TextureAtlas.class).findRegion(software.getTextureName())));

        magicBar.setValue(5);
        healthBar.setValue(5);
        defenseBar.setValue(5);
        attackBar.setValue(5);

        Stack magicStack = new Stack();
        magicStack.add(magicBar);
        Table magicOverlay = new Table();
        OverlayTextLabelArea<Software> magicLabelArea = new OverlayTextLabelArea<Software>(skin, software) {
            @Override
            public String updateLabel(Software dataObject) {
                return "" + dataObject.getMagic();
            }
        };
        this.areas.add(magicLabelArea);
        magicOverlay.add(magicLabelArea).expand().fillX().height(9);
        magicStack.add(magicOverlay);

        Stack healthStack = new Stack();
        healthStack.add(healthBar);
        Table healthOverlay = new Table();
        OverlayTextLabelArea<Software> healthLabelArea = new OverlayTextLabelArea<Software>(skin, software) {
            @Override
            public String updateLabel(Software dataObject) {
                return "" + dataObject.getHealth();
            }
        };
        this.areas.add(healthLabelArea);
        healthOverlay.add(healthLabelArea).expand().fillX().height(9);
        healthStack.add(healthOverlay);

        Stack defenseStack = new Stack();
        defenseStack.add(defenseBar);
        Table defenseOverlay = new Table();
        OverlayTextLabelArea<Software> defenseLabelArea = new OverlayTextLabelArea<Software>(skin, software) {
            @Override
            public String updateLabel(Software dataObject) {
                return "" + dataObject.getArmor();
            }
        };
        this.areas.add(defenseLabelArea);
        defenseOverlay.add(defenseLabelArea).expand().fillX().height(9);
        defenseStack.add(defenseOverlay);

        Stack attackStack = new Stack();
        attackStack.add(attackBar);
        Table attackOverlay = new Table();
        OverlayTextLabelArea<Software> attackLabelArea = new OverlayTextLabelArea<Software>(skin, software) {
            @Override
            public String updateLabel(Software dataObject) {
                return "" + dataObject.getAttack();
            }
        };
        this.areas.add(attackLabelArea);
        attackOverlay.add(attackLabelArea).expand().fillX().height(9);
        attackStack.add(attackOverlay);

        VerticalGroup leds = new VerticalGroup();
        for (Class<? extends Component> runComponent : software.getRunComponents()) {
            leds.addActor(new LEDActor(skin, components.get(runComponent)));
        }

        leds.space(3).center();

        if (flipped) {
            this.add(leds).padRight(2);
            this.add(magicStack).width(9).prefSize(8, 8);
            this.add(attackStack).width(9);
            this.add(imageArea).width(48);
            this.add(defenseStack).width(9);
            this.add(healthStack).width(9);
        } else {
            this.add(healthStack).width(9);
            this.add(defenseStack).width(9);
            this.add(imageArea).width(48);
            this.add(attackStack).width(9);
            this.add(magicStack).width(9).prefSize(8, 8);
            this.add(leds).padLeft(2);
        }
    }

    public List<OverlayTextLabelArea<Software>> getOverlayTextLabelAreas() {
        return areas;
    }
}
