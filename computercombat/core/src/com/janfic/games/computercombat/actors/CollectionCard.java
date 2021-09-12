package com.janfic.games.computercombat.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    Software software;
    int amount;
    EventListener newWindowOnClick;

    public CollectionCard(ComputerCombatGame game, Skin skin, Software software, int amount) {
        super(skin);
        this.software = software;
        this.amount = amount;
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

        ProgressBar runBar = new ProgressBar(0, 10, 1, false, blue);
        ProgressBar healthBar = new ProgressBar(0, 10, 1, false, green);
        ProgressBar defenseBar = new ProgressBar(0, 10, 1, false, grey);
        ProgressBar attackBar = new ProgressBar(0, 10, 1, false, red);
        runBar.setValue(10);
        healthBar.setValue(10);
        defenseBar.setValue(10);
        attackBar.setValue(10);

        Stack defenseStack = new Stack();
        defenseStack.add(defenseBar);
        Table defenseOverlay = new Table();
        OverlayTextLabelArea<Software> defenseOverlayTextLabelArea = new OverlayTextLabelArea<Software>(skin, software) {
            @Override
            public String updateLabel(Software dataObject) {
                return "" + dataObject.getMaxArmor();
            }
        };
        defenseOverlay.add(defenseOverlayTextLabelArea).width(12).height(9);
        defenseStack.add(defenseOverlay);
        Stack runRequirementsStack = new Stack();
        runRequirementsStack.add(runBar);
        Table runRequirementsOverlay = new Table();
        OverlayTextLabelArea<Software> runOverlayTextLabelArea = new OverlayTextLabelArea<Software>(skin, software) {
            @Override
            public String updateLabel(Software dataObject) {
                return "" + dataObject.getRunRequirements();
            }
        };
        runRequirementsOverlay.add(runOverlayTextLabelArea).width(12).height(9);
        runRequirementsStack.add(runRequirementsOverlay);
        Stack healthStack = new Stack();
        healthStack.add(healthBar);
        Table healthOverlay = new Table();
        OverlayTextLabelArea<Software> healthOverlayTextLabelArea = new OverlayTextLabelArea<Software>(skin, software) {
            @Override
            public String updateLabel(Software dataObject) {
                return "" + dataObject.getMaxHealth();
            }
        };
        healthOverlay.add(healthOverlayTextLabelArea).width(12).height(9);
        healthStack.add(healthOverlay);
        Stack attackStack = new Stack();
        attackStack.add(attackBar);
        Table attackOverlay = new Table();
        OverlayTextLabelArea<Software> attackOverlayTextLabelArea = new OverlayTextLabelArea<Software>(skin, software) {
            @Override
            public String updateLabel(Software dataObject) {
                return "" + dataObject.getMaxAttack();
            }
        };
        attackOverlay.add(attackOverlayTextLabelArea).width(12).height(9);
        attackStack.add(attackOverlay);

        this.add(attackStack).width(90).height(9).row();
        this.add(defenseStack).width(90).height(9).row();
        this.add(healthStack).width(90).height(9).row();
        this.add(runRequirementsStack).width(90).height(9).row();

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
        newWindowOnClick = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Window w = new CardInfoWindow(game, software, skin);
                w.setSize(2 * getStage().getWidth() / 3f, getStage().getHeight());
                w.setPosition(getStage().getWidth() / 6f, getStage().getHeight());
                CollectionCard.this.getStage().addActor(w);
            }
        };
        this.addListener(newWindowOnClick);
    }

    @Override

    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public EventListener getNewWindowOnClick() {
        return newWindowOnClick;
    }
}
