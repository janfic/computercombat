package com.janfic.games.computercombat.network.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Collection;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.Deck;
import com.janfic.games.computercombat.model.match.MatchData;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.Profile;
import com.janfic.games.computercombat.model.Software;
import com.janfic.games.computercombat.model.moves.MoveResult;
import groovy.lang.GroovyShell;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

/**
 *
 * @author Jan Fic
 */
public class SQLAPI {

    String url = "jdbc:mysql://computer-combat-db.cloqezbutiub.us-east-1.rds.amazonaws.com:3306";

    private static SQLAPI singleton;
    private Properties properties;
    private Connection connection;
    private GroovyShell shell;

    private SQLAPI() {
        this.properties = new Properties();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.properties.load(Gdx.files.internal("sql.properties").read());
            connection = DriverManager.getConnection(url, properties);
            Statement statement = connection.createStatement();
            statement.execute("USE computer_combat;");
            CompilerConfiguration config = new CompilerConfiguration();
            config.addCompilationCustomizers(new ImportCustomizer().addStarImports(
                    "java.util",
                    "com.janfic.games.computercombat.model",
                    "com.janfic.games.computercombat.network.client",
                    "com.janfic.games.computercombat.model.abilities",
                    "com.janfic.games.computercombat.model.components",
                    "com.janfic.games.computercombat.model.moves",
                    "com.janfic.games.computercombat.model.match",
                    "com.janfic.games.computercombat.util"
            ));
            shell = new GroovyShell(config);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static SQLAPI getSingleton() {
        if (singleton == null) {
            singleton = new SQLAPI();
        }
        return singleton;
    }

    public Map<Card, Integer> getPlayerOwnedCards(String uid) {
        System.out.println("[SERVER][MYSQL]: Querying for Player collection");
        Map<Card, Integer> cards = new HashMap<>();

        try {
            //Get all player owned cards
            String sql = "SELECT * FROM card \n"
                    + "JOIN profile_owns_card ON card.id = profile_owns_card.card_id \n"
                    + "JOIN profile ON profile.uid = profile_owns_card.profile_id \n"
                    + "JOIN ability ON card.ability_id = ability.id\n"
                    + "JOIN run_requirements ON card.id = run_requirements.card_id\n"
                    + "JOIN components ON components.id = run_requirements.component_id\n"
                    + "JOIN collection ON card.collection_id = collection.id\n"
                    + "WHERE profile.uid = '" + uid + "' ORDER BY card.id;";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);

            boolean next = set.next();
            while (next) {
                int currentID = set.getInt("card.id");

                System.out.println("QUERY: " + currentID);

                Ability a = (Ability) shell.evaluate(set.getString("ability.code"));
                a.setInformation(
                        set.getString("ability.description"),
                        set.getString("ability.textureName"),
                        set.getString("ability.name"),
                        set.getString("ability.code"),
                        set.getInt("ability.id")
                );

                Collection c = new Collection(
                        set.getInt("collection.id"),
                        set.getString("collection.name"),
                        set.getString("collection.description"),
                        set.getString("collection.textureName"),
                        set.getString("collection.path"),
                        set.getInt("collection.price"));

                List<Class<? extends Component>> components = new ArrayList<>();

                int id = set.getInt("card.id");
                String name = set.getString("card.name");
                String textureName = set.getString("card.textureName");
                int level = set.getInt("card.level");
                int maxHealth = set.getInt("card.maxHealth");
                int maxDefense = set.getInt("card.maxDefense");
                int maxAttack = set.getInt("card.maxAttack");
                int runRequirements = set.getInt("card.runRequirements");

                do {
                    components.add((Class<? extends Component>) Class.forName("com.janfic.games.computercombat.model.components." + set.getString("components.name")));
                    next = set.next();
                } while (next && currentID == set.getInt("card.id"));

                Software s = new Software(
                        id,
                        uid,
                        name,
                        c,
                        textureName,
                        level,
                        maxHealth,
                        maxDefense,
                        maxAttack,
                        1,
                        components.toArray(new Class[0]),
                        runRequirements,
                        a
                );
                cards.put(s, cards.getOrDefault(s, 0) + 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cards;
    }

    public Software getCardById(int id, String optionalUID) {
        System.out.println("[SERVER][MYSQL]: Querying for Card Data");
        try {
            String sql = "SELECT * FROM card \n"
                    + "JOIN ability ON card.ability_id = ability.id\n"
                    + "JOIN run_requirements ON card.id = run_requirements.card_id\n"
                    + "JOIN components ON components.id = run_requirements.component_id\n"
                    + "JOIN collection ON card.collection_id = collection.id\n"
                    + "WHERE card.id = '" + id + "';";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);

            set.next();

            Ability a = (Ability) shell.evaluate(set.getString("ability.code"));
            a.setInformation(
                    set.getString("ability.description"),
                    set.getString("ability.textureName"),
                    set.getString("ability.name"),
                    set.getString("ability.code"),
                    set.getInt("ability.id")
            );

            Collection c = new Collection(
                    set.getInt("collection.id"),
                    set.getString("collection.name"),
                    set.getString("collection.description"),
                    set.getString("collection.textureName"),
                    set.getString("collection.path"),
                    set.getInt("collection.price"));

            List<Class<? extends Component>> components = new ArrayList<>();

            String name = set.getString("card.name");
            String textureName = set.getString("card.textureName");
            int level = set.getInt("card.level");
            int maxHealth = set.getInt("card.maxHealth");
            int maxDefense = set.getInt("card.maxDefense");
            int maxAttack = set.getInt("card.maxAttack");
            int runRequirements = set.getInt("card.runRequirements");

            do {
                components.add((Class<? extends Component>) Class.forName("com.janfic.games.computercombat.model.components." + set.getString("components.name")));
            } while (set.next());

            Software s = new Software(
                    id,
                    optionalUID,
                    name,
                    c,
                    textureName,
                    level,
                    maxHealth,
                    maxDefense,
                    maxAttack,
                    1,
                    components.toArray(new Class[0]),
                    runRequirements,
                    a
            );
            return s;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Software> getCardsInfo(List<Integer> cardIDs, String optionalUID) {
        List<Software> cards = new ArrayList<>();
        try {
            String sql = "SELECT * FROM card \n"
                    + "JOIN ability ON card.ability_id = ability.id\n"
                    + "JOIN run_requirements ON card.id = run_requirements.card_id\n"
                    + "JOIN components ON components.id = run_requirements.component_id\n"
                    + "JOIN collection ON card.collection_id = collection.id;";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);

            boolean areRowsLeft = set.next();

            while (areRowsLeft) {
                if (cardIDs.contains(set.getInt("card.id"))) {
                    Ability a = (Ability) shell.evaluate(set.getString("ability.code"));
                    a.setInformation(
                            set.getString("ability.description"),
                            set.getString("ability.textureName"),
                            set.getString("ability.name"),
                            set.getString("ability.code"),
                            set.getInt("ability.id")
                    );

                    Collection c = new Collection(
                            set.getInt("collection.id"),
                            set.getString("collection.name"),
                            set.getString("collection.description"),
                            set.getString("collection.textureName"),
                            set.getString("collection.path"),
                            set.getInt("collection.price"));

                    List<Class<? extends Component>> components = new ArrayList<>();

                    String name = set.getString("card.name");
                    String textureName = set.getString("card.textureName");
                    int level = set.getInt("card.level");
                    int id = set.getInt("card.id");
                    int maxHealth = set.getInt("card.maxHealth");
                    int maxDefense = set.getInt("card.maxDefense");
                    int maxAttack = set.getInt("card.maxAttack");
                    int runRequirements = set.getInt("card.runRequirements");

                    boolean sameCard = false;
                    do {
                        components.add((Class<? extends Component>) Class.forName("com.janfic.games.computercombat.model.components." + set.getString("components.name")));
                        areRowsLeft = set.next();
                        sameCard = areRowsLeft ? set.getInt("card.id") == id : false;
                    } while (sameCard);

                    Software s = new Software(
                            id,
                            optionalUID,
                            name,
                            c,
                            textureName,
                            level,
                            maxHealth,
                            maxDefense,
                            maxAttack,
                            1,
                            components.toArray(new Class[0]),
                            runRequirements,
                            a
                    );
                    cards.add(s);
                } else {
                    areRowsLeft = set.next();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return cards;
        }
        return cards;
    }

    public List<Software> getCardsInDeck(int deckID, String uid) {
        List<Software> cards = new ArrayList<>();

        try {
            String sql = "SELECT * FROM deck_has_card \n"
                    + "JOIN card ON deck_has_card.card_id = card.id\n"
                    + "JOIN ability ON card.ability_id = ability.id\n"
                    + "JOIN run_requirements ON card.id = run_requirements.card_id\n"
                    + "JOIN components ON components.id = run_requirements.component_id\n"
                    + "JOIN collection ON card.collection_id = collection.id\n"
                    + "WHERE deck_has_card.deck_id = '" + deckID + "';";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);

            boolean areRowsLeft = set.next();
            while (areRowsLeft) {
                int currentCardID = set.getInt("card.id");
                Ability a = (Ability) shell.evaluate(set.getString("ability.code"));
                a.setInformation(
                        set.getString("ability.description"),
                        set.getString("ability.textureName"),
                        set.getString("ability.name"),
                        set.getString("ability.code"),
                        set.getInt("ability.id")
                );

                Collection c = new Collection(
                        set.getInt("collection.id"),
                        set.getString("collection.name"),
                        set.getString("collection.description"),
                        set.getString("collection.textureName"),
                        set.getString("collection.path"),
                        set.getInt("collection.price"));

                List<Class<? extends Component>> components = new ArrayList<>();

                int id = set.getInt("card.id");
                String name = set.getString("card.name");
                String textureName = set.getString("card.textureName");
                int level = set.getInt("card.level");
                int maxHealth = set.getInt("card.maxHealth");
                int maxDefense = set.getInt("card.maxDefense");
                int maxAttack = set.getInt("card.maxAttack");
                int runRequirements = set.getInt("card.runRequirements");
                int amount = set.getInt("deck_has_card.amount");
                boolean sameCard = false;

                do {
                    components.add((Class<? extends Component>) Class.forName("com.janfic.games.computercombat.model.components." + set.getString("components.name")));
                    areRowsLeft = set.next();
                    sameCard = areRowsLeft ? currentCardID == set.getInt("card.id") : false;
                } while (sameCard && areRowsLeft);

                Software s = new Software(
                        id,
                        uid,
                        name,
                        c,
                        textureName,
                        level,
                        maxHealth,
                        maxDefense,
                        maxAttack,
                        1,
                        components.toArray(new Class[0]),
                        runRequirements,
                        a
                );
                cards.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cards;
    }

    public Ability getAbilityByID(int id) {
        System.out.println("[SERVER][MYSQL]: Querying for Ability Data");
        try {
            String sql = "SELECT * \n"
                    + "FROM ability \n"
                    + "JOIN card ON card.ability_id = ability.id \n"
                    + "WHERE ability.id = " + id + ";";

            Statement getAbilityStatement = connection.createStatement();
            ResultSet gAResults = getAbilityStatement.executeQuery(sql);

            gAResults.next();

            Ability a = (Ability) shell.evaluate(gAResults.getString("code"));
            a.setInformation(
                    gAResults.getString("description"),
                    gAResults.getString("textureName"),
                    gAResults.getString("name"),
                    gAResults.getString("code"),
                    gAResults.getInt("id")
            );
            return a;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Deck> getPlayerDecks(String uid) {
        System.out.println("[SERVER][MYSQL]: Querying for Player Decks Data");
        List<Deck> decks = new ArrayList<>();

        try {
            String sql = "SELECT * FROM deck\n"
                    + "WHERE deck.profile_id = '" + uid + "';";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);

            while (set.next()) {
                int deckID = set.getInt("deck.id");
                String deckName = set.getString("name");
                Deck deck = new Deck(deckName, deckID);
                decks.add(deck);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //Query for decks owned by player
            String sql = "SELECT * FROM deck\n"
                    + "JOIN deck_has_card ON deck_has_card.deck_id = deck.id\n"
                    + "JOIN card ON deck_has_card.card_id = card.id\n"
                    + "JOIN ability ON card.ability_id = ability.id\n"
                    + "JOIN run_requirements ON card.id = run_requirements.card_id\n"
                    + "JOIN components ON components.id = run_requirements.component_id\n"
                    + "JOIN collection ON card.collection_id = collection.id\n"
                    + "WHERE deck.profile_id = '" + uid + "' ORDER BY card.id AND deck.id;";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);

            boolean areRowsLeft = set.next();

            while (areRowsLeft) { // Change to next deck

                int deckID = set.getInt("deck.id");
                int currentDeckID = set.getInt("deck.id");
                String deckName = set.getString("name");
                Deck deck = new Deck(deckName, deckID);
                if (decks.contains(deck)) {
                    deck = decks.get(decks.indexOf(deck));
                }

                boolean sameDeck = true;
                while (sameDeck && areRowsLeft) {
                    int currentCardID = set.getInt("card.id");
                    Ability a = (Ability) shell.evaluate(set.getString("ability.code"));
                    a.setInformation(
                            set.getString("ability.description"),
                            set.getString("ability.textureName"),
                            set.getString("ability.name"),
                            set.getString("ability.code"),
                            set.getInt("ability.id")
                    );

                    Collection c = new Collection(
                            set.getInt("collection.id"),
                            set.getString("collection.name"),
                            set.getString("collection.description"),
                            set.getString("collection.textureName"),
                            set.getString("collection.path"),
                            set.getInt("collection.price"));

                    List<Class<? extends Component>> components = new ArrayList<>();

                    int id = set.getInt("card.id");
                    String name = set.getString("card.name");
                    String textureName = set.getString("card.textureName");
                    int level = set.getInt("card.level");
                    int maxHealth = set.getInt("card.maxHealth");
                    int maxDefense = set.getInt("card.maxDefense");
                    int maxAttack = set.getInt("card.maxAttack");
                    int runRequirements = set.getInt("card.runRequirements");
                    int amount = set.getInt("deck_has_card.amount");
                    boolean sameCard = false;

                    do {
                        components.add((Class<? extends Component>) Class.forName("com.janfic.games.computercombat.model.components." + set.getString("components.name")));
                        areRowsLeft = set.next();
                        sameDeck = areRowsLeft ? currentDeckID == set.getInt("deck.id") : false;
                        sameCard = areRowsLeft ? currentCardID == set.getInt("card.id") && sameDeck : false;
                    } while (sameCard && areRowsLeft);

                    Software s = new Software(
                            id,
                            uid,
                            name,
                            c,
                            textureName,
                            level,
                            maxHealth,
                            maxDefense,
                            maxAttack,
                            1,
                            components.toArray(new Class[0]),
                            runRequirements,
                            a
                    );
                    deck.addCard(s, amount);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(decks);
        return decks;
    }

    public void savePlayerDeck(Deck deck, String uid) {
        System.out.println("[SERVER][MYSQL]: Updating Player Deck Data");
        try {
            String sql = "SELECT * FROM deck\n"
                    + "WHERE deck.id = " + deck.getID() + ";";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);

            int rows = 0;

            boolean exists = set.next();
            if (exists) {

                sql = "UPDATE deck "
                        + "SET name = '" + deck.getName() + "', profile_id = '" + uid + "' \n"
                        + "WHERE id = " + deck.getID() + ";";

                rows = statement.executeUpdate(sql);
            } else {
                sql = "INSERT INTO deck (id, name, profile_id) \n"
                        + "VALUES (" + deck.getID() + " , '" + deck.getName() + "', '" + uid + "');";

                rows = statement.executeUpdate(sql);

            }

            sql = "DELETE FROM deck_has_card \n"
                    + "WHERE deck_has_card.deck_id = " + deck.getID() + ";";

            rows = statement.executeUpdate(sql);

            System.out.println(deck.getStack());

            for (String card : deck.getCards().keySet()) {
                sql = "INSERT INTO deck_has_card (deck_id, card_id, amount) \n"
                        + "VALUES (" + deck.getID() + "," + card + "," + deck.getCardCount(Integer.parseInt(card)) + ");";

                rows = statement.executeUpdate(sql);
            }

        } catch (SQLIntegrityConstraintViolationException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deletePlayerDeck(Deck deck, String uid) {
        System.out.println("[SERVER][MYSQL]: Deleting Player Deck");
        try {
            String sql = "SELECT * FROM deck\n"
                    + "WHERE deck.id = " + deck.getID() + ";";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);

            int rows;

            boolean exists = set.next();
            if (exists) {

                sql = "DELETE FROM deck_has_card \n"
                        + "WHERE deck_has_card.deck_id = " + deck.getID() + ";";

                rows = statement.executeUpdate(sql);

                sql = "DELETE FROM deck "
                        + "WHERE id = " + deck.getID() + ";";

                rows = statement.executeUpdate(sql);
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Profile loadProfile(String uid) {
        System.out.println("[SERVER][MYSQL]: Loading Player Profile");
        try {
            String sql = "SELECT *\n"
                    + "FROM profile\n"
                    + "WHERE profile.uid = '" + uid + "';";

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            Profile profile = new Profile(uid);

            while (rs.next()) {
                profile.setName(rs.getString("username"));
                profile.setEmail(rs.getString("email"));
                profile.setPackets(rs.getInt("packets"));
            }
            return profile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveProfile(Profile p) {
        System.out.println("[SERVER][MYSQL]: Saving Player Profile");
        try {

            String sql = "SELECT uid\n"
                    + "FROM profile\n"
                    + "WHERE profile.uid = '" + p.getUID() + "';";

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            boolean exists = rs.next();

            if (exists) {
                sql = "UPDATE profile SET packets = " + p.getPackets() + " WHERE profile.uid = '" + p.getUID() + "';";
            } else {
                sql = "INSERT INTO profile (uid, username, email, packets) \n"
                        + "VALUES ('" + p.getUID() + "', '" + p.getName() + "', '" + p.getEmail() + "', " + p.getPackets() + ");";
            }
            //profile table
            statement = connection.createStatement();
            int rowsUpdated = statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean addCardToProfile(int cardID, Profile profile) {
        try {
            String sql = "SELECT uid\n"
                    + "FROM profile\n"
                    + "WHERE profile.uid = '" + profile.getUID() + "';";

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            boolean exists = rs.next();

            if (exists) {
                sql = "INSERT INTO profile_owns_card (profile_id, card_id)\n"
                        + "VALUES ('" + profile.getUID() + "'," + cardID + ");";
                int rowsUpdated = statement.executeUpdate(sql);
                return rowsUpdated >= 1;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int recordMatchData(MatchData data) {
        System.out.println("[SERVER][MYSQL]: Recording Match Data");
        Json json = new Json(JsonWriter.OutputType.json);
        int r = 0;
        try {
            r = 0;
            // Insert New Match Record
            String sql = "INSERT INTO computer_combat.`match` (player1_uid, player2_uid, deck1_id, deck2_id, winner, starttime, endtime, packets_player1, packets_player2)\n"
                    + "VALUES ('"
                    + data.getPlayer1().getUID() + "' , '"
                    + data.getPlayer2().getUID() + "' , "
                    + data.getPlayer1Deck().getID() + " , "
                    + data.getPlayer2Deck().getID() + " , "
                    + (data.getWinner() ? 1 : 0) + " , "
                    + "str_to_date('" + data.getStartTime().toString() + "', '%Y-%m-%d %H:%i:%s.%f'), "
                    + "str_to_date('" + data.getEndTime().toString() + "', '%Y-%m-%d %H:%i:%s.%f'),"
                    + data.getRewards().get(data.getPlayer1().getUID()) + ","
                    + data.getRewards().get(data.getPlayer2().getUID()) + ""
                    + ");";

            Statement statement = connection.createStatement();
            int updates = statement.executeUpdate(sql);

            // Get Generated Match ID
            sql = "SELECT LAST_INSERT_ID();";
            ResultSet results = statement.executeQuery(sql);
            results.next();
            int match_id = results.getInt(1);

            for (int i = 0; i < data.getMoves().size(); i++) {
                // Insert Move Results
                List<MoveResult> moveResults = data.getMoveResults().get(i);
                sql = "INSERT INTO move_results (data) VALUES ('" + json.toJson(moveResults) + "');";
                updates = statement.executeUpdate(sql);
                r += updates;

                // Get Move Result ID
                sql = "SELECT LAST_INSERT_ID();";
                results = statement.executeQuery(sql);
                results.next();
                int move_results_id = results.getInt(1);

                // Insert Move
                Move move = data.getMoves().get(i);
                sql = "INSERT INTO move (data, match_id, move_results_id, move_number) VALUES ('"
                        + json.toJson(move) + "'," + match_id + "," + move_results_id + "," + (i + 1) + ");";
                updates = statement.executeUpdate(sql);
                r += updates;
            }

            // Insert Match States
            for (int i = 0; i < data.getMatchStates().size(); i++) {
                MatchState state = data.getMatchStates().get(i);
                sql = "INSERT INTO match_state (match_id, match_state_number, data) VALUES ("
                        + match_id + ","
                        + i + ",'"
                        + json.toJson(state)
                        + "');";
                updates = statement.executeUpdate(sql);
                r += updates;
            }

            return updates;

        } catch (Exception e) {
            e.printStackTrace();
            return r;
        }
    }

    public void dispose() {
        closeConnection();
        singleton = null;
    }

    public void closeConnection() {
        try {
            this.connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Collection> getCollections() {
        List<Collection> collections = new ArrayList<>();

        try {
            String sql = "SELECT * FROM collection;";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);

            while (set.next()) {
                Collection c = new Collection(
                        set.getInt("id"),
                        set.getString("name"),
                        set.getString("description"),
                        set.getString("textureName"),
                        set.getString("path"),
                        set.getInt("price")
                );

                collections.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return collections;
    }

    public List<Software> getCardsInCollection(List<Integer> collectionIDs, String optionalUID) {
        List<Software> cards = new ArrayList<>();

        try {
            String sql = "SELECT * FROM card \n"
                    + "JOIN ability ON card.ability_id = ability.id\n"
                    + "JOIN run_requirements ON card.id = run_requirements.card_id\n"
                    + "JOIN components ON components.id = run_requirements.component_id\n"
                    + "JOIN collection ON card.collection_id = collection.id;";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);

            boolean areRowsLeft = set.next();

            while (areRowsLeft) {
                if (collectionIDs.contains(set.getInt("collection.id"))) {
                    Ability a = (Ability) shell.evaluate(set.getString("ability.code"));
                    a.setInformation(
                            set.getString("ability.description"),
                            set.getString("ability.textureName"),
                            set.getString("ability.name"),
                            set.getString("ability.code"),
                            set.getInt("ability.id")
                    );

                    Collection c = new Collection(
                            set.getInt("collection.id"),
                            set.getString("collection.name"),
                            set.getString("collection.description"),
                            set.getString("collection.textureName"),
                            set.getString("collection.path"),
                            set.getInt("collection.price"));

                    List<Class<? extends Component>> components = new ArrayList<>();

                    String name = set.getString("card.name");
                    String textureName = set.getString("card.textureName");
                    int level = set.getInt("card.level");
                    int id = set.getInt("card.id");
                    int maxHealth = set.getInt("card.maxHealth");
                    int maxDefense = set.getInt("card.maxDefense");
                    int maxAttack = set.getInt("card.maxAttack");
                    int runRequirements = set.getInt("card.runRequirements");

                    boolean sameCard = false;
                    do {
                        components.add((Class<? extends Component>) Class.forName("com.janfic.games.computercombat.model.components." + set.getString("components.name")));
                        areRowsLeft = set.next();
                        sameCard = areRowsLeft ? set.getInt("card.id") == id : false;
                    } while (sameCard);

                    Software s = new Software(
                            id,
                            optionalUID,
                            name,
                            c,
                            textureName,
                            level,
                            maxHealth,
                            maxDefense,
                            maxAttack,
                            1,
                            components.toArray(new Class[0]),
                            runRequirements,
                            a
                    );
                    cards.add(s);
                } else {
                    areRowsLeft = set.next();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cards;
    }

    public void pingDatabase() {
        try {
            String sql = "SELECT 1;";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);

            set.last();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
