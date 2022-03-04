package com.janfic.games.computercombat.model.match;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.Deck;
import com.janfic.games.computercombat.model.GameRules;
import com.janfic.games.computercombat.model.Player;
import com.janfic.games.computercombat.model.abilities.AttackAbility;
import com.janfic.games.computercombat.model.animations.CascadeAnimation;
import com.janfic.games.computercombat.model.animations.CascadeAnimation.CascadeData;
import com.janfic.games.computercombat.model.animations.CollectAnimation;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.util.CardFilter;
import com.janfic.games.computercombat.util.ComponentFilter;
import com.janfic.games.computercombat.util.NullifyingJson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Predicate;

/**
 *
 * @author Jan Fic
 */
public class MatchState implements Serializable, Cloneable {

    public Component[][] componentBoard;
    public Map<String, List<Card>> activeEntities;
    public Map<String, Card> computers;
    public Map<String, Deck> decks;
    public List<Player> players;
    public Player currentPlayerMove;
    public boolean isGameOver;
    public Player winner;

    public MatchState() {
        this.componentBoard = null;
        this.activeEntities = null;
        this.computers = null;
        this.decks = null;
        this.players = null;
        this.currentPlayerMove = null;
        this.isGameOver = false;
        this.winner = null;
    }

    public MatchState(Player player1, Player player2, Component[][] componentBoard, Map<String, List<Card>> activeEntities, Map<String, Card> computers, Map<String, Deck> decks) {
        this.componentBoard = componentBoard;
        this.activeEntities = activeEntities;
        this.computers = computers;
        this.decks = decks;
        this.currentPlayerMove = player1;
        this.players = new ArrayList<>();
        this.players.add(player1);
        this.players.add(player2);
        this.isGameOver = false;
        this.winner = null;
    }

    public Component[][] getComponentBoard() {
        return componentBoard;
    }

    public void update() {
        // Update invalidated
        List<Component> matches = new ArrayList<>();
        for (int x = 0; x < componentBoard.length; x++) {
            for (int y = 0; y < componentBoard[x].length; y++) {
                Component c = componentBoard[x][y];
                if (c.isInvalid()) {
                    c.update();
                    if (c.isMatched()) {
                        matches.add(c);
                    }
                }
            }
        }
        for (Component component : matches) {
            component.updateMatchedNeighbors();
        }
    }

    private boolean isInvalidBoard() {
        for (int x = 0; x < componentBoard.length; x++) {
            for (int y = 0; y < componentBoard[x].length; y++) {
                Component c = componentBoard[x][y];
                if (c.isInvalid()) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<MoveResult> results(Move move) {
        List<MoveResult> results = new ArrayList<>();

        boolean extraTurn = false;
        while (isInvalidBoard() || getMatches().isEmpty() == false) {
            // Save Old State
            // Update
            update();

            // Collect Components
            Map<Integer, List<Component>> collected = collectComponents();
            Map<Component, Card> progress = new HashMap<>();
            CollectAnimation collectAnimation = new CollectAnimation(collected, progress);
            progress(collectAnimation);

            // Attack
            boolean attack = false;
            for (Integer integer : collected.keySet()) {
                for (Component component : collected.get(integer)) {
                    if (component.getColor() == 5) {
                        attack = true;
                        break;
                    }
                }
            }

            if (attack && activeEntities.get(currentPlayerMove.getUID()).isEmpty() == false) {
                results.addAll(attack(move));
            }

            // Remove, Cascade, then Invalidate
            // Remove
            for (Integer matchNumber : collected.keySet()) {
                for (Component component : collected.get(matchNumber)) {
                    this.componentBoard[component.getX()][component.getY()].changeColor(0);
                }
            }

            // Cascade
            // This can be optimized by storing collected components
            CascadeAnimation cascadeAnimation = new CascadeAnimation(cascade());
            List<MoveAnimation> moveAnimation = new ArrayList<>();
            moveAnimation.add(collectAnimation);
            moveAnimation.add(cascadeAnimation);

            for (Integer integer : collected.keySet()) {
                if (collected.get(integer).size() >= 4) {
                    extraTurn = true;
                }
            }
            MoveResult result = new MoveResult(move, MatchState.record(this), moveAnimation);
            results.add(result);
        }

        if (extraTurn == false && results.size() > 0) {
            MoveResult last = results.get(results.size() - 1);
            last.getState().currentPlayerMove = last.getState().getOtherProfile(last.getState().currentPlayerMove);
            this.currentPlayerMove = last.getState().currentPlayerMove;
        }

        return results;
    }

    public List<MoveResult> attack(Move move) {
        ObjectMap<Card, List<Card>> attacks = new ObjectMap<>();
        Card attacker = activeEntities.get(currentPlayerMove.getUID()).get(0);
        List<Card> attacked = new ArrayList<>();
        if (activeEntities.get(this.getOtherProfile(currentPlayerMove).getUID()).isEmpty()) {
            attacked.add(computers.get(this.getOtherProfile(currentPlayerMove).getUID()));
        } else {
            attacked.add(activeEntities.get(this.getOtherProfile(currentPlayerMove).getUID()).get(0));
        }
        attacks.put(attacker, attacked);
        AttackAbility attackAbility = new AttackAbility(new ArrayList<>(), attacks);

        List<MoveResult> res = attackAbility.doAbility(this, move);
        return res;
    }

    public Map<Integer, List<Component>> collectComponents() {
        int matchNumber = 0;
        Map<Integer, List<Component>> collected = new HashMap<>();
        for (int x = 0; x < componentBoard.length; x++) {
            for (int y = 0; y < componentBoard[x].length; y++) {
                if (componentBoard[x][y].isMatched()) {
                    List<Component> match = new ArrayList<>();
                    Stack<Component> bfs = new Stack<>();
                    bfs.add(componentBoard[x][y]);
                    while (bfs.isEmpty() == false) {
                        Component current = bfs.pop();
                        match.add(new Component(current));
                        for (Component neighbor : current.getNeighbors(current.getMatchNeighbors())) {
                            if (neighbor.isMatched()) {
                                bfs.add(neighbor);
                            }
                        }
                        current.getMatchNeighbors().clear();
                    }
                    collected.put(matchNumber, match);
                    matchNumber++;
                }
            }
        }
        return collected;
    }

    public void progress(CollectAnimation collectAnimation) {
        Map<Component, Card> progress = collectAnimation.progress;
        for (Component c : collectAnimation.getAllComponents()) {
            boolean collectedByCard = false;
            for (Card card : activeEntities.get(currentPlayerMove.getUID())) {
                if (card.getRunProgress() < card.getRunRequirements()) {
                    for (Integer requirement : card.getRunComponents()) {
                        if (c.getColor() == requirement) {
                            card.recieveComponents(requirement, 1);
                            collectedByCard = true;
                            progress.put(c, card);
                            break;
                        }
                    }
                }
                if (collectedByCard == true) {
                    break;
                }
            }
            if (collectedByCard == false) {
                computers.get(currentPlayerMove.getUID()).recieveProgress(1);
            }
        }
    }

    public List<CascadeData> cascade() {
        List<CascadeAnimation.CascadeData> cascade = new ArrayList<>();
        for (int x = 0; x < componentBoard.length; x++) {
            for (int y = componentBoard[x].length - 1; y >= 0; y--) {
                if (componentBoard[x][y].getColor() == 0) {
                    componentBoard[x][y].invalidate();
                    componentBoard[x][y].invalidateNeighbors();
                    boolean cascaded = false;
                    for (int i = y - 1; i >= 0; i--) {
                        if (componentBoard[x][i].getColor() != 0) {
                            Component fallenComponent = new Component(componentBoard[x][i].getColor(), x, y);
                            Component originalComponent = new Component(componentBoard[x][i]);
                            cascade.add(new CascadeAnimation.CascadeData(fallenComponent, originalComponent));
                            componentBoard[x][y].changeColor(componentBoard[x][i].getColor());
                            componentBoard[x][i].changeColor(0);
                            cascaded = true;
                            break;
                        }
                    }
                    if (cascaded == false) {
                        componentBoard[x][y].changeColor(GameRules.getNewColor());
                        cascade.add(new CascadeAnimation.CascadeData(new Component(componentBoard[x][y]), x, (-y) - 1));
                    }
                }
            }
        }
        return cascade;
    }

    public List<Component> getMatches() {
        List<Component> componentsInMatch = new ArrayList<>();
        for (int x = 0; x < componentBoard.length; x++) {
            for (int y = 0; y < componentBoard[x].length; y++) {
                Component c = componentBoard[x][y];
                if (c.isMatched()) {
                    componentsInMatch.add(c);
                }
            }
        }
        return componentsInMatch;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Player player1 = (Player) players.get(0).clone();
        Player player2 = (Player) players.get(1).clone();

        Component[][] componentBoard = new Component[8][8];
        for (int x = 0; x < componentBoard.length; x++) {
            for (int y = 0; y < componentBoard[x].length; y++) {
                componentBoard[x][y] = new Component(this.componentBoard[x][y]);
            }
        }

        Map<String, List<Card>> activeEntities = new HashMap<>();
        List<Card> player1Cards = new ArrayList<>();
        List<Card> player2Cards = new ArrayList<>();
        activeEntities.put(player1.getUID(), player1Cards);
        activeEntities.put(player2.getUID(), player2Cards);
        for (String string : this.activeEntities.keySet()) {
            for (Card card : this.activeEntities.get(string)) {
                activeEntities.get(string).add((Card) (card.clone()));
            }
        }

        Map<String, Deck> decks = new HashMap<>();
        for (String uid : this.decks.keySet()) {
            decks.put(uid, (Deck) this.decks.get(uid).clone());
        }

        Map<String, Card> computers = new HashMap<>();
        for (String uid : this.computers.keySet()) {
            computers.put(uid, (Card) this.computers.get(uid).clone());
        }

        MatchState state = new MatchState(player1, player2, componentBoard, activeEntities, computers, decks);
        state.isGameOver = this.isGameOver;
        if (this.winner != null) {
            state.winner = (Player) this.winner.clone();
        }
        state.currentPlayerMove = (Player) this.currentPlayerMove.clone();

        MatchState.buildNeighbors(componentBoard);
        state.update();
        return state;
    }

    public static MatchState record(MatchState state) {
        try {
            return (MatchState) state.clone();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public MatchState(MatchState state) {
        Json json = new NullifyingJson();
        MatchState s = json.fromJson(MatchState.class, json.toJson(state));
        this.componentBoard = s.componentBoard;
        this.activeEntities = s.activeEntities;
        this.computers = s.computers;
        this.decks = s.decks;
        this.currentPlayerMove = s.currentPlayerMove;
        this.players = s.players;
        this.winner = s.winner;
        this.isGameOver = s.isGameOver;
        MatchState.buildNeighbors(this.componentBoard);
        this.update();
    }

    public MatchState(MatchState state, String playerUID) {
        this(state);
    }

    public String toStringBoard() {
        String s = "";
        for (int y = 0; y < componentBoard[0].length; y++) {
            for (int x = 0; x < componentBoard.length; x++) {
                if (componentBoard[x][y] != null) {
                    s += componentBoard[x][y].getTextureName() + " , ";
                } else {
                    s += "null , ";
                }
            }
            s += "\n";
        }
        return s;
    }

    public Player getOtherProfile(Player profile) {
        for (Player player : players) {
            if (!player.getUID().equals(profile.getUID())) {
                return player;
            }
        }
        return null;
    }

    public Player getOtherProfile(String uid) {
        for (Player player : players) {
            if (!player.getUID().equals(uid)) {
                return player;
            }
        }
        return null;
    }

    public List<Component> getComponentsAsList() {
        List<Component> components = new ArrayList<>();
        for (Component[] cr : componentBoard) {
            for (Component c : cr) {
                components.add(c);
            }
        }
        return components;
    }

    @Override
    public void write(Json json) {
        json.writeValue("players", players, List.class);
        json.writeValue("currentPlayerMove", currentPlayerMove, Player.class);
        json.writeValue("activeEntities", activeEntities, Map.class);
        json.writeValue("computers", computers, Map.class);
        json.writeValue("decks", decks, Map.class);
        json.writeValue("winner", winner, Player.class);
        json.writeValue("isGameOver", isGameOver, boolean.class);
        String board = "";
        for (Component[] components : componentBoard) {
            for (Component component : components) {
                board += "" + component.getColor();
            }
        }
        assert (board.length() == 64);
        json.writeValue("componentBoard", board);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.players = json.readValue("players", List.class, jsonData);
        this.currentPlayerMove = json.readValue("currentPlayerMove", Player.class, jsonData);
        this.isGameOver = json.readValue("isGameOver", boolean.class, jsonData);
        this.winner = json.readValue("winner", Player.class, jsonData);
        this.activeEntities = json.readValue("activeEntities", HashMap.class, List.class, jsonData);
        this.computers = json.readValue("computers", HashMap.class, Card.class, jsonData);
        this.decks = json.readValue("decks", HashMap.class, Deck.class, jsonData);
        String boardString = json.readValue("componentBoard", String.class, jsonData);
        componentBoard = new Component[8][8];
        assert (boardString.length() == 64);
        for (int i = 0; i < boardString.length(); i++) {
            int x = i / 8;
            int y = i % 8;
            try {
                componentBoard[x][y] = new Component(Integer.parseInt("" + boardString.substring(i, i + 1)), x, y);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int countComponents(ComponentFilter filter, Move move) {
        int count = 0;
        for (Component[] components : componentBoard) {
            for (Component component : components) {
                if (filter.filter(component, this, move)) {
                    count++;
                }
            }
        }
        return count;
    }

    public List<Component> getComponentsByFilter(ComponentFilter filter, Move move) {
        List<Component> list = getComponentsAsList();
        list.removeIf(new Predicate<Component>() {
            @Override
            public boolean test(Component t) {
                return !filter.filter(t, MatchState.this, move);
            }
        });
        return list;
    }

    public List<Card> getAllCards() {
        List<Card> cards = new ArrayList<>();
        for (String key : activeEntities.keySet()) {
            cards.addAll(activeEntities.get(key));
        }
        return cards;
    }

    public List<Card> getCardsByFilter(CardFilter filter, Move move) {
        List<Card> cards = getAllCards();
        cards.removeIf(new Predicate<Card>() {
            @Override
            public boolean test(Card t) {
                return !filter.filter(t, MatchState.this, move);
            }
        });
        return cards;
    }

    public static void buildNeighbors(Component[][] componentBoard) {
        for (int x = 0; x < componentBoard.length; x++) {
            for (int y = 0; y < componentBoard[x].length; y++) {
                componentBoard[x][y].invalidate();
                for (int i = 0; i < Component.coordsToNeighbors.length; i += 3) {
                    int neighborX = x + Component.coordsToNeighbors[i];
                    int neighborY = y + Component.coordsToNeighbors[i + 1];
                    if (neighborX < componentBoard.length && neighborY < componentBoard[x].length && neighborX >= 0 && neighborY >= 0) {
                        Component c = componentBoard[neighborX][neighborY];
                        componentBoard[x][y].setNeighbor(Component.coordsToNeighbors[i + 2], c);
                    }
                }
            }
        }
    }

}
