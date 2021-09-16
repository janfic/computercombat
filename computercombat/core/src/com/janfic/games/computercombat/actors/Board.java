package com.janfic.games.computercombat.actors;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
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
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.model.animations.CascadeAnimation.CascadeData;
import com.janfic.games.computercombat.model.moves.MatchComponentsMove;
import com.janfic.games.computercombat.network.client.ClientMatch;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 *
 * @author Jan Fic
 */
public class Board extends BorderedGrid {

    ComponentActor selected1, selected2;

    TextureAtlas componentAtlas;

    ClientMatch matchData;
    ComputerCombatGame game;

    Move move;

    Cell<ComponentActor>[][] board;
    Cell<Group> newComponentSpawn;
    List<ComponentActor> components;

    Queue<List<Action>> animation;

    private final static int[][] neighbors = new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
    boolean canSelect = true;

    Runnable removeActions = new Runnable() {
        @Override
        public void run() {
            selected1 = null;
            selected2 = null;
            canSelect = true;
        }
    };

    public Board(Skin skin, ClientMatch matchData, ComputerCombatGame game) {
        super(skin);
        this.pad(7);
        this.matchData = matchData;
        this.game = game;
        this.componentAtlas = game.getAssetManager().get("texture_packs/components.atlas");
        this.defaults().space(2);
        this.selected1 = null;
        this.board = new Cell[8][8];
        this.animation = new LinkedList<>();
        this.newComponentSpawn = this.add(new Group()).height(0).pad(0).space(0).growX().colspan(8);
        this.row();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                board[x][y] = this.add();
            }
            this.row();
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta); //To change body of generated methods, choose Tools | Templates.
        this.setCullingArea(new Rectangle(0, 0, getWidth(), getHeight()));
        if (matchData.getCurrentState().currentPlayerMove.getUID().equals(game.getCurrentProfile().getUID())) {
            this.setTouchable(Touchable.enabled);
        } else {
            this.setTouchable(Touchable.disabled);
        }
        if (animation.isEmpty() == false) {
            List<Action> a = animation.peek();
            boolean allDone = true;
            List<Action> r = new ArrayList<>();
            for (Action action : a) {
                if (action.getActor() == null) {
                    r.add(action);
                    continue;
                }
                if (!action.act(delta)) {
                    allDone = false;
                } else {
                    r.add(action);
                }
            }
            a.removeAll(r);
            if (allDone) {
                animation.poll();
            }
        } else {
            canSelect = true;
        }
    }

    public void addComponent(ComponentActor actor, int x, int y) {
        board[x][y].setActor(actor);
        actor.addListener(new ClickListener() {

            float dragStartX = -10, dragStartY = -10;

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (actor != selected1 && actor != selected2 && canSelect) {
                    actor.addAction(Actions.scaleTo(1.2f, 1.2f, 0.5f));
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                if (actor != selected1 && actor != selected2 && canSelect) {
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
                if (!canSelect) {
                    return;
                }

                selected1 = null;
                selected2 = null;

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
                            other.addAction(Actions.sequence(Actions.rotateTo(0), Actions.moveTo(s1x, s1y, 0.35f), Actions.moveTo(s2x, s2y, 0.35f), Actions.scaleTo(1, 1, 0.25f), Actions.rotateTo(0, 0.25f), Actions.run(removeActions)));
                            actor.addAction(Actions.sequence(Actions.rotateTo(0), Actions.moveTo(s2x, s2y, 0.35f), Actions.moveTo(s1x, s1y, 0.35f), Actions.scaleTo(1, 1, 0.25f), Actions.rotateTo(0, 0.25f), Actions.run(removeActions)));
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

                //Select 1st Component
                if (selected1 == null) {
                    selected1 = actor;
                    selected1.addAction(Actions.forever(Actions.sequence(Actions.rotateTo(10, 0.1f), Actions.rotateTo(-10, 0.1f))));
                    return;
                }

                //Selected already selected Component
                if (actor == selected1) {
                    selected1.clearActions();
                    selected1.addAction(Actions.scaleTo(1, 1, 0.25f));
                    selected1.addAction(Actions.sequence(Actions.rotateTo(0, 0.25f), Actions.run(removeActions)));
                    return;
                }

                //Selected New Actor
                int ax = actor.getComponent().getX();
                int ay = actor.getComponent().getY();
                int sx = selected1.getComponent().getX();
                int sy = selected1.getComponent().getY();

                //Check if can switch
                if (isNeighbor(ax, ay, sx, sy) && canSelect) {
                    //switch anim
                    canSelect = false;
                    selected2 = actor;
                    selected2.addAction(Actions.forever(Actions.sequence(Actions.rotateTo(10, 0.1f), Actions.rotateTo(-10, 0.1f))));
                    float s1x = selected1.getX();
                    float s1y = selected1.getY();
                    float s2x = selected2.getX();
                    float s2y = selected2.getY();
                    selected2.clearActions();
                    selected1.clearActions();

                    Move move = new MatchComponentsMove(game.getCurrentProfile().getUID(), selected1.getComponent(), selected2.getComponent());
                    boolean isLegalMove = GameRules.getAvailableMoves(matchData.getCurrentState()).contains(move);
                    if (!isLegalMove) {
                        List<Action> as = new ArrayList<>();
                        Action a = Actions.sequence(Actions.rotateTo(0), Actions.moveTo(s1x, s1y, 0.35f), Actions.moveTo(s2x, s2y, 0.35f), Actions.scaleTo(1, 1, 0.25f), Actions.rotateTo(0, 0.25f));
                        Action a2 = Actions.sequence(Actions.rotateTo(0), Actions.moveTo(s2x, s2y, 0.35f), Actions.moveTo(s1x, s1y, 0.35f), Actions.scaleTo(1, 1, 0.25f), Actions.rotateTo(0, 0.25f), Actions.run(removeActions));
                        a.setActor(selected2);
                        a2.setActor(selected1);
                        as.add(a);
                        as.add(a2);
                        animation.add(as);
                    } else {
                        List<Action> as = new ArrayList<>();
                        Action a = Actions.sequence(Actions.rotateTo(0), Actions.moveTo(s1x, s1y, 0.35f), Actions.scaleTo(1, 1, 0.25f), Actions.rotateTo(0, 0.25f));
                        Action a2 = Actions.sequence(Actions.rotateTo(0), Actions.moveTo(s2x, s2y, 0.35f), Actions.scaleTo(1, 1, 0.25f), Actions.rotateTo(0, 0.25f));
                        a.setActor(selected2);
                        a2.setActor(selected1);
                        as.add(a);
                        as.add(a2);
                        animation.add(as);
                        Board.this.move = move;
                    }
                } else {
                    if (!canSelect) {
                        return;
                    }
                    selected1.clearActions();
                    selected1.addAction(Actions.scaleTo(1, 1, 0.25f));
                    selected1.addAction(Actions.rotateTo(0, 0.25f));
                    selected1 = actor;
                    selected1.addAction(Actions.forever(Actions.sequence(Actions.rotateTo(10, 0.1f), Actions.rotateTo(-10, 0.1f))));
                }
            }
        });
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
    }

    public boolean isAnimating() {
        return animation.isEmpty() == false;
    }

    public void animate(List<MoveResult> moveResults) {

        //animate move 
        //change state
        //animate
        for (MoveResult moveResult : moveResults) {
            List<Action> updateData = new ArrayList<>();
            Action a = Actions.run(new Runnable() {
                @Override
                public void run() {
                    Board.this.matchData.setCurrentState(moveResult.getOldState());
                    updateBoard(matchData);
                    System.out.println("END OF UPDATE DATA");
                }
            });
            a.setActor(this);
            updateData.add(a);
            final List<Action> collectAnimation = new ArrayList<>();
            List<Action> calculateCollectAnimation = new ArrayList<>();
            Action b = Actions.run(new Runnable() {
                @Override
                public void run() {
//                    Set set = moveResult.getCollectedComponents().keySet();
//                    for (Object i : new ArrayList<>(set)) {
//                        Integer mark = Integer.parseInt(i.toString());
//                        for (Component component : moveResult.getCollectedComponents().get("" + mark)) {
//                            for (Cell<ComponentActor>[] cells : board) {
//                                for (Cell<ComponentActor> cell : cells) {
//                                    if (cell.getActor().getComponent().equals(component)) {
//                                        Action a = Actions.parallel(Actions.scaleTo(1.5f, 1.5f, 0.2f), Actions.fadeOut(0.2f));
//                                        a.setActor(cell.getActor());
//                                        collectAnimation.add(a);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    System.out.println("END OF CALCULATE COLLECT " + collectAnimation);
                }
            });
            b.setActor(this);
            calculateCollectAnimation.add(b);

            final List<Action> calculateCascadeAnimation = new ArrayList<>();

            final List<Action> cascadeAnimation = new ArrayList<>();
            Action c = Actions.run(new Runnable() {
                @Override
                public void run() {
                    Map<Integer, List<CascadeData>> newComponentFallingOrder = new HashMap<>();
//                    for (CascadeData cascade : moveResult.getCascade()) {
//                        boolean found = false;
//                        for (Cell<ComponentActor>[] cells : board) {
//                            for (Cell<ComponentActor> cell : cells) {
//                                if (cell.getActor().getComponent().equals(cascade.getOriginalComponent())) {
//                                    Action moveAction = Actions.moveTo(
//                                            board[cascade.getFallenComponent().getX()][cascade.getFallenComponent().getY()].getActorX(),
//                                            board[cascade.getFallenComponent().getX()][cascade.getFallenComponent().getY()].getActorY(),
//                                            (cascade.getFallenComponent().getY() - cascade.getOriginalComponent().getY()) / 2f, Interpolation.bounceOut);
//                                    moveAction.setActor(cell.getActor());
//                                    cascadeAnimation.add(moveAction);
//                                    found = true;
//                                }
//                            }
//                        }
//                        if (!found) {
//                            List<CascadeData> columnFall = newComponentFallingOrder.getOrDefault(cascade.getOriginalComponent().getX(), new ArrayList<>());
//                            columnFall.add(cascade);
//                            newComponentFallingOrder.put(cascade.getOriginalComponent().getX(), columnFall);
//                        }
//                    }
                    Group newSpawn = newComponentSpawn.getActor();
                    for (int x = 0; x < board.length; x++) {
                        List<CascadeData> columnFall = newComponentFallingOrder.get(x);
                        if (columnFall == null) {
                            continue;
                        }
                        columnFall.sort(new Comparator<CascadeData>() {
                            @Override
                            public int compare(CascadeData o1, CascadeData o2) {
                                return o2.getFallenComponent().getY() - o1.getFallenComponent().getY();
                            }
                        });
                        for (int i = 0; i < columnFall.size(); i++) {
                            ComponentActor component = new ComponentActor(componentAtlas, columnFall.get(i).getFallenComponent());
                            newSpawn.addActor(component);
                            component.setPosition(26 * x, i * 26);
                            Action move = Actions.moveTo(
                                    26 * x,
                                    -(columnFall.get(i).getFallenComponent().getY() + 1) * 26,
                                    (columnFall.get(i).getFallenComponent().getY() - (-1 - i)) / 2f,
                                    Interpolation.bounceOut
                            );
                            move.setActor(component);
                            cascadeAnimation.add(move);
                        }
                    }
                    System.out.println("END OF CALCULATE CASCADE " + cascadeAnimation);
                }
            });
            c.setActor(this);
            calculateCascadeAnimation.add(c);
            animation.add(updateData);
            animation.add(calculateCollectAnimation);
            animation.add(collectAnimation);
            animation.add(calculateCascadeAnimation);
            animation.add(cascadeAnimation);

        }
        List<Action> updateData = new ArrayList<>();
        Action a = Actions.run(new Runnable() {
            @Override
            public void run() {
                Board.this.matchData.setCurrentState(moveResults.get(moveResults.size() - 1).getNewState());
                updateBoard(matchData);
                System.out.println("END OF FINAL UPDATE DATA");
            }
        });
        a.setActor(this);
        updateData.add(a);
        animation.add(updateData);
    }

    private static boolean isNeighbor(int x1, int y1, int x2, int y2) {
        for (int i = 0; i < neighbors.length; i++) {
            if (x1 - x2 == neighbors[i][0] && y1 - y2 == neighbors[i][1]) {
                return true;
            }
        }
        return false;
    }
}
