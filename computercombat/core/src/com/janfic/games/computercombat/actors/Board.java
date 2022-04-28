package com.janfic.games.computercombat.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.GameRules;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MatchComponentsMove;
import com.janfic.games.computercombat.network.client.ClientMatch;
import com.janfic.games.computercombat.util.ComponentFilter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class Board extends BorderedGrid {

    int componentsToSelect;
    List<ComponentActor> selected;

    TextureAtlas componentAtlas;

    ClientMatch matchData;
    ComputerCombatGame game;

    Move move;

    Cell<ComponentActor>[][] board;
    Cell<Group> newComponentSpawn;
    List<ComponentActor> components;

    private final static int[][] neighbors = new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
    boolean canSelect = true;

    List<List<Action>> matchAnimations;

    Runnable removeActions = new Runnable() {
        @Override
        public void run() {
            canSelect = true;
        }
    };

    public Board(Skin skin, ClientMatch matchData, ComputerCombatGame game, List<List<Action>> matchAnimations) {
        super(skin);
        this.pad(7);
        setSkin(skin);
        this.matchData = matchData;
        this.game = game;
        this.componentsToSelect = -1;
        this.componentAtlas = game.getAssetManager().get("texture_packs/components.atlas");
        this.selected = new ArrayList<>();
        this.board = new Cell[8][8];
        this.components = new ArrayList<>();
        this.newComponentSpawn = this.add(new Group()).height(0).pad(0).space(0).growX().colspan(8);
        this.row();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                board[x][y] = this.add();
            }
            this.row();
        }
        this.matchAnimations = matchAnimations;
    }

    @Override
    public void act(float delta) {
        super.act(delta); //To change body of generated methods, choose Tools | Templates.
        this.setCullingArea(new Rectangle(24, 24, getWidth() - 24, getHeight() - 24));
        this.newComponentSpawn.getActor().setCullingArea(new Rectangle(0, -getHeight(), getWidth(), getHeight() - 1));
        canSelect = matchAnimations.isEmpty() && matchData.getCurrentState().currentPlayerMove.equals(game.getCurrentProfile().getUID());
        if (!matchData.getCurrentState().currentPlayerMove.equals(game.getCurrentProfile().getUID())) {
            selected.clear();
        }
    }

    public void addComponent(ComponentActor actor, int x, int y) {
        board[x][y].setActor(actor);
        components.add(actor);
        actor.addListener(new ClickListener() {

            float dragStartX = -10, dragStartY = -10;

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (!selected.contains(actor) && canSelect) {
                    actor.addAction(Actions.scaleTo(1.2f, 1.2f, 0.5f));
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                if (!selected.contains(actor) && canSelect) {
                    actor.clearActions();
                    actor.addAction(Actions.scaleTo(1, 1, 0.25f));
                }
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                dragStartX = x;
                dragStartY = y;
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (!canSelect || componentsToSelect > -1) {
                    return;
                }

                selected.clear();

                int ax = actor.getComponent().getX();
                int ay = actor.getComponent().getY();

                if (dragStartX > 0 && dragStartY > 0) {
                    float deltaX = dragStartX - x;
                    float deltaY = dragStartY - y;
                    if (Math.abs(deltaX) > 10 || Math.abs(deltaY) > 10) {

                        int bx = 0;
                        int by = 0;

                        if (deltaX < -10) {
                            bx++;
                        } else if (deltaX > 10) {
                            bx--;
                        } else if (deltaY < -10) {
                            by--;
                        } else if (deltaY > 10) {
                            by++;
                        }

                        if (ax + bx >= 0 && ax + bx < 8 && ay + by >= 0 && ay + by < 8) {
                            ComponentActor other = board[ax + bx][ay + by].getActor();

                            float s1x = actor.getX();
                            float s1y = actor.getY();
                            float s2x = other.getX();
                            float s2y = other.getY();

                            canSelect = false;
                            dragStartX = -10;
                            dragStartY = -10;

                            Move move = new MatchComponentsMove(game.getCurrentProfile().getUID(), actor.getComponent(), other.getComponent());
                            boolean isLegalMove = GameRules.getAvailableMoves(matchData.getCurrentState()).contains(move);
                            if (!isLegalMove) {
                                List<Action> as = new ArrayList<>();
                                Action a = Actions.sequence(Actions.rotateTo(0), Actions.moveTo(s1x, s1y, 0.35f), Actions.moveTo(s2x, s2y, 0.35f), Actions.scaleTo(1, 1, 0.25f), Actions.rotateTo(0, 0.25f));
                                Action a2 = Actions.sequence(Actions.rotateTo(0), Actions.moveTo(s2x, s2y, 0.35f), Actions.moveTo(s1x, s1y, 0.35f), Actions.scaleTo(1, 1, 0.25f), Actions.rotateTo(0, 0.25f), Actions.run(removeActions));
                                a.setActor(other);
                                a2.setActor(actor);
                                as.add(a);
                                as.add(a2);
                                matchAnimations.add(as);
                            } else {
                                List<Action> as = new ArrayList<>();
                                Action a = Actions.sequence(Actions.rotateTo(0), Actions.moveTo(s1x, s1y, 0.35f), Actions.scaleTo(1, 1, 0.25f), Actions.rotateTo(0, 0.25f));
                                Action a2 = Actions.sequence(Actions.rotateTo(0), Actions.moveTo(s2x, s2y, 0.35f), Actions.scaleTo(1, 1, 0.25f), Actions.rotateTo(0, 0.25f));
                                a.setActor(other);
                                a2.setActor(actor);
                                as.add(a);
                                as.add(a2);
                                matchAnimations.add(as);
                                Board.this.move = move;
                            }
                        }
                    }
                }
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                dragStartX = -10;
                dragStartY = -10;

                if (!canSelect) {
                    return;
                }

                if (selected.contains(actor)) {
                    selected.get(0).clearActions();
                    selected.get(0).addAction(Actions.scaleTo(1, 1, 0.25f));
                    selected.get(0).addAction(Actions.sequence(Actions.rotateTo(0, 0.25f), Actions.run(removeActions)));
                    selected.remove(actor);
                    return;
                } else {
                    selected.add(actor);
                    selected.get(0).addAction(Actions.forever(Actions.sequence(Actions.rotateTo(10, 0.1f), Actions.rotateTo(-10, 0.1f))));
                }

                if (componentsToSelect != -1 || selected.size() <= 1) {
                    return;
                }

                //Selected New Actor
                int ax = actor.getComponent().getX();
                int ay = actor.getComponent().getY();
                int sx = selected.get(0).getComponent().getX();
                int sy = selected.get(0).getComponent().getY();

                //Check if can switch
                if (isNeighbor(ax, ay, sx, sy) && canSelect) {
                    //switch anim
                    canSelect = false;
                    selected.add(actor);
                    selected.get(1).addAction(Actions.forever(Actions.sequence(Actions.rotateTo(10, 0.1f), Actions.rotateTo(-10, 0.1f))));
                    float s1x = selected.get(0).getX();
                    float s1y = selected.get(0).getY();
                    float s2x = selected.get(1).getX();
                    float s2y = selected.get(1).getY();
                    selected.get(0).clearActions();
                    selected.get(1).clearActions();

                    Move move = new MatchComponentsMove(game.getCurrentProfile().getUID(), selected.get(0).getComponent(), selected.get(1).getComponent());
                    boolean isLegalMove = GameRules.getAvailableMoves(matchData.getCurrentState()).contains(move);
                    if (!isLegalMove) {
                        List<Action> as = new ArrayList<>();
                        Action a = Actions.sequence(Actions.rotateTo(0), Actions.moveTo(s1x, s1y, 0.35f), Actions.moveTo(s2x, s2y, 0.35f), Actions.scaleTo(1, 1, 0.25f), Actions.rotateTo(0, 0.25f));
                        Action a2 = Actions.sequence(Actions.rotateTo(0), Actions.moveTo(s2x, s2y, 0.35f), Actions.moveTo(s1x, s1y, 0.35f), Actions.scaleTo(1, 1, 0.25f), Actions.rotateTo(0, 0.25f), Actions.run(removeActions));
                        a.setActor(selected.get(1));
                        a2.setActor(selected.get(0));
                        as.add(a);
                        as.add(a2);
                        matchAnimations.add(as);
                    } else {
                        List<Action> as = new ArrayList<>();
                        Action a = Actions.sequence(Actions.rotateTo(0), Actions.moveTo(s1x, s1y, 0.35f), Actions.scaleTo(1, 1, 0.25f), Actions.rotateTo(0, 0.25f));
                        Action a2 = Actions.sequence(Actions.rotateTo(0), Actions.moveTo(s2x, s2y, 0.35f), Actions.scaleTo(1, 1, 0.25f), Actions.rotateTo(0, 0.25f));
                        a.setActor(selected.get(1));
                        a2.setActor(selected.get(0));
                        as.add(a);
                        as.add(a2);
                        matchAnimations.add(as);
                        Board.this.move = move;
                    }
                } else {
                    if (!canSelect) {
                        return;
                    }
                    for (ComponentActor componentActor : selected) {
                        componentActor.clearActions();
                        componentActor.addAction(Actions.scaleTo(1, 1, 0.25f));
                        componentActor.addAction(Actions.rotateTo(0, 0.25f));
                        componentActor.clearActions();
                    }
                    selected.clear();
                    selected.add(actor);
                    selected.get(0).addAction(Actions.forever(Actions.sequence(Actions.rotateTo(10, 0.1f), Actions.rotateTo(-10, 0.1f))));
                }
            }
        });
    }

    public void startComponentSelection(ComponentFilter filter) {
        this.selected.clear();
        this.componentsToSelect = 1;
        for (Cell<ComponentActor>[] cells : board) {
            for (Cell<ComponentActor> cell : cells) {
                boolean selectable = filter.filter(cell.getActor().getComponent(), matchData.getCurrentState(), null);
                if (!selectable) {
                    cell.getActor().setColor(Color.GRAY);
                    cell.getActor().setTouchable(Touchable.disabled);
                }
            }
        }
        // Highlight selectable components
        // Set others to untouchable / disabled / grey
    }

    public void startAbilitySelection(int amount) {
        this.selected.clear();
        this.componentsToSelect = amount;
    }

    public boolean isSelecting() {
        return this.componentsToSelect >= 0;
    }

    public boolean didCompleteSelection() {
        if (this.componentsToSelect < 0) {
            return true;
        } else {
            return this.componentsToSelect == this.selected.size();
        }
    }

    public void endComponentSelection() {
        this.selected.clear();
        this.componentsToSelect = -1;
        for (Cell<ComponentActor>[] cells : board) {
            for (Cell<ComponentActor> cell : cells) {
                cell.getActor().setColor(Color.WHITE);
                cell.getActor().setTouchable(Touchable.enabled);
            }
        }
    }

    public boolean attemptedMove() {
        return move != null;
    }

    public Move getMove() {
        return move;
    }

    public void consumeMove() {
        this.move = null;
    }

    public void updateBoard(ClientMatch data) {
        this.matchData = data;
        this.clear();
        components.clear();
        this.newComponentSpawn = this.add(new Group()).height(0).pad(0).space(0).growX().colspan(8);
        this.row();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                board[x][y] = this.add();
            }
            this.row();
        }
        Component[][] componentBoard = this.matchData.getCurrentState().componentBoard;
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                this.addComponent(new ComponentActor(componentAtlas, componentBoard[x][y]), x, y);
            }
        }
        this.layout();
    }

    public List<ComponentActor> getComponents() {
        return components;
    }

    public Cell<ComponentActor>[][] getBoard() {
        return board;
    }

    public Cell<Group> getNewComponentSpawn() {
        return newComponentSpawn;
    }

    private static boolean isNeighbor(int x1, int y1, int x2, int y2) {
        for (int i = 0; i < neighbors.length; i++) {
            if (x1 - x2 == neighbors[i][0] && y1 - y2 == neighbors[i][1]) {
                return true;
            }
        }
        return false;
    }

    public List<ComponentActor> getSelected() {
        return selected;
    }

    public List<Component> getSelectedComponents() {
        List<Component> componentsSelected = new ArrayList<>();
        for (ComponentActor componentActor : selected) {
            componentsSelected.add(componentActor.getComponent());
        }
        return componentsSelected;
    }
}
