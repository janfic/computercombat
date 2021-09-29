package com.janfic.games.computercombat.network.client;

import com.badlogic.gdx.Gdx;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.Deck;
import com.janfic.games.computercombat.model.Profile;
import com.janfic.games.computercombat.model.Software;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Jan Fic
 */
public class SQLAPI {

    String url = "jdbc:mysql://computer-combat-db.cloqezbutiub.us-east-1.rds.amazonaws.com:3306";

    private static SQLAPI singleton;
    private Properties properties;
    private Connection connection;

    private SQLAPI() {
        this.properties = new Properties();
        try {
            this.properties.load(Gdx.files.internal("sql.properties").read());
            connection = DriverManager.getConnection(url, properties);
            Statement statement = connection.createStatement();
            statement.execute("USE computer_combat;");
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

        Map<Card, Integer> cards = new HashMap<>();

        try {
            //Get all player owned cards
            String sql = "SELECT card.* \n"
                    + "FROM card \n"
                    + "JOIN profile_owns_card ON card.id = profile_owns_card.card_id \n"
                    + "JOIN profile ON profile.uid = profile_owns_card.profile_id \n"
                    + "WHERE profile.uid = '" + uid + "' \n"
                    + "ORDER BY card.name;";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);
            while (set.next()) {

                //Get Components of Card
                sql = "SELECT components.* \n"
                        + "FROM card \n"
                        + "JOIN run_requirements ON card.id = run_requirements.card_id \n"
                        + "JOIN components ON components.id = run_requirements.component_id \n"
                        + "WHERE card.id = " + set.getInt("card.id");

                Statement getComponentStatement = connection.createStatement();
                ResultSet gcResults = getComponentStatement.executeQuery(sql);

                List<Class<? extends Component>> components = new ArrayList<>();
                while (gcResults.next()) {
                    components.add((Class<? extends Component>) Class.forName("com.janfic.games.computercombat.model.components." + gcResults.getString("components.name")));
                }

                Software s = new Software(
                        set.getInt("card.id"),
                        set.getString("card.name"),
                        "computer_pack",
                        set.getString("textureName"),
                        set.getInt("level"),
                        set.getInt("maxHealth"),
                        set.getInt("maxDefense"),
                        set.getInt("maxAttack"),
                        1,
                        components.toArray(new Class[0]),
                        set.getInt("card.runRequirements"),
                        null
                );
                cards.put(s, cards.getOrDefault(s, 0) + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cards;
    }

    public Software getCardById(int id) {
        try {
            String sql = "SELECT card.* \n"
                    + "FROM card \n"
                    + "WHERE card.id = " + id + ";";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);

            //Get Components of Card
            sql = "SELECT components.* \n"
                    + "FROM card \n"
                    + "JOIN run_requirements ON card.id = run_requirements.card_id \n"
                    + "JOIN components ON components.id = run_requirements.component_id \n"
                    + "WHERE card.id = " + set.getInt("card.id");

            Statement getComponentStatement = connection.createStatement();
            ResultSet gcResults = getComponentStatement.executeQuery(sql);

            List<Class<? extends Component>> components = new ArrayList<>();
            while (gcResults.next()) {
                components.add((Class<? extends Component>) Class.forName("com.janfic.games.computercombat.model.components." + gcResults.getString("components.name")));
            }

            return new Software(
                    set.getInt("card.id"),
                    set.getString("card.name"),
                    "pack",
                    set.getString("textureName"),
                    set.getInt("level"),
                    set.getInt("maxHealth"),
                    set.getInt("maxDefense"),
                    set.getInt("maxAttack"),
                    1,
                    components.toArray(new Class[0]),
                    set.getInt("card.runRequirements"),
                    null
            );
        } catch (Exception e) {
            return null;
        }
    }

    public List<Deck> getPlayerDecks(String uid) {
        List<Deck> decks = new ArrayList<>();

        try {
            //Query for decks owned by player
            String sql = "SELECT * FROM deck\n"
                    + "WHERE deck.profile_id = '" + uid + "';";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);

            while (set.next()) {
                int id = set.getInt("id");
                String name = set.getString("name");

                sql = "SELECT card.id\n"
                        + "FROM card \n"
                        + "JOIN deck_has_card ON deck_has_card.deck_id = " + id + " \n";

                Statement stat = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql);

                Deck deck = new Deck(name);

                while (rs.next()) {
                    Software c = getCardById(rs.getInt("id"));
                    deck.addCard(c, id);
                }
            }
        } catch (Exception e) {
        }

        return decks;
    }

    public Profile loadProfile(String uid) {
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
            }

            System.out.println(profile.getName() + " " + profile.getUID());

            return profile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveProfile(Profile p) {
        try {

            String sql = "SELECT uid\n"
                    + "FROM profile\n"
                    + "WHERE profile.uid = '" + p.getUID() + "';";

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            boolean exists = rs.next();

            if (exists) {
                System.out.println("SAVE PROFILE");

            } else {
                sql = "INSERT INTO profile (uid, username, email) \n"
                        + "VALUES ('" + p.getUID() + "', '" + p.getName() + "', '" + p.getEmail() + "');";
            }
            //profile table
            statement = connection.createStatement();
            int rowsUpdated = statement.executeUpdate(sql);

            //decks
            for (Deck deck : p.getDecks()) {

            }

            //cards
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean addCardToProfile(int cardID, Profile profile) {
        try {
            String sql = "SELECT uid\n"
                    + "FROM profile\n"
                    + "WHERE profile.uid = '" + profile.getUID() + "';";

            System.out.println(sql);

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            boolean exists = rs.next();

            if (exists) {
                sql = "INSERT INTO profile_owns_card (profile_id, card_id)\n"
                        + "VALUES ('" + profile.getUID() + "'," + cardID + ");";
                System.out.println(sql);
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
}
