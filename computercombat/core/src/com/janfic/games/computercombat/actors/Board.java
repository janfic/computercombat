package com.janfic.games.computercombat.actors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.GameRules;
import com.janfic.games.computercombat.model.GameRules.MoveResult;
import com.janfic.games.computercombat.model.Move;
import com.janfic.games.computercombat.model.Move.MatchComponentsMove;
import com.janfic.games.computercombat.network.client.ClientMatch;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 *
 * @author Jan Fic
 */
public class Board extends BorderedGrid {

    ComponentActor selected1, selected2;

    ClientMatch matchData;
    ComputerCombatGame game;

    Move move;

    Cell<ComponentActor>[][] board;
    List<ComponentActor> components;

    Queue<List<TemporalAction>> animation;

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
        this.defaults().space(2);
        this.selected1 = null;
        this.board = new Cell[8][8];
        this.animation = new LinkedList<>();
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
        if (matchData.getCurrentState().currentPlayerMove.getUID().equals(game.getCurrentProfile().getUID())) {
            this.setTouchable(Touchable.enabled);
        } else {
            this.setTouchable(Touchable.disabled);
        }
        if (animation.isEmpty() == false) {
            List<TemporalAction> a = animation.peek();
            boolean allDone = true;
            List<TemporalAction> r = new ArrayList<>();
            for (TemporalAction temporalAction : a) {
                if (temporalAction.getActor() == null) {
                    r.add(temporalAction);
                    continue;
                }
                if (temporalAction.getTime() == 0) {
                    temporalAction.getActor().addAction(temporalAction);
                } else if (!temporalAction.isComplete()) {
                    allDone = false;
                } else {
                    r.add(temporalAction);
                }
            }
            a.removeAll(r);
            if (allDone) {
                animation.poll();
            }
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
                        selected2.addAction(Actions.sequence(Actions.rotateTo(0), Actions.moveTo(s1x, s1y, 0.35f), Actions.moveTo(s2x, s2y, 0.35f), Actions.scaleTo(1, 1, 0.25f), Actions.rotateTo(0, 0.25f)));
                        selected1.addAction(Actions.sequence(Actions.rotateTo(0), Actions.moveTo(s2x, s2y, 0.35f), Actions.moveTo(s1x, s1y, 0.35f), Actions.scaleTo(1, 1, 0.25f), Actions.rotateTo(0, 0.25f), Actions.run(removeActions)));
                    } else {
                        selected2.addAction(Actions.sequence(Actions.rotateTo(0), Actions.moveTo(s1x, s1y, 0.35f), Actions.scaleTo(1, 1, 0.25f), Actions.rotateTo(0, 0.25f)));
                        selected1.addAction(Actions.sequence(Actions.rotateTo(0), Actions.moveTo(s2x, s2y, 0.35f), Actions.scaleTo(1, 1, 0.25f), Actions.rotateTo(0, 0.25f)));
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
        for (int x = 0; x < board.length; x++) {
            
        }
    }

    public void animate(List<MoveResult> moveResults) {
        for (MoveResult moveResult : moveResults) {
            int matchesCount = moveResult.getCollectedComponents().size();
            Set set = moveResult.getCollectedComponents().keySet();
            List<TemporalAction> actions = new ArrayList<>();
            for (Object i : new ArrayList<>(set)) {
                Integer mark = Integer.parseInt(i.toString());
                for (Component component : moveResult.getCollectedComponents().get("" + mark)) {
                    for (Cell<ComponentActor>[] cells : board) {
                        for (Cell<ComponentActor> cell : cells) {
                            if (cell.getActor().getComponent().equals(component)) {
                                TemporalAction a = Actions.scaleTo(2, 2, 1);
                                a.setActor(cell.getActor());
                                actions.add(a);
                                animation.add(actions);
//                                cell.getActor().addAction(Actions.scaleTo(2, 2, 1));
                            }
                        }
                    }
                }
            }
            System.out.println(moveResult.getCollectedComponents());
        }
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
