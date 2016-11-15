package cs601.travelHelper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Handles all database-related actions. Uses singleton design pattern. Modified
 * by Prof. Karpenko from the original example of Prof. Engle.
 * 
 * @see RegisterServer
 */
public class DatabaseHandler {

	/** Makes sure only one database handler is instantiated. */
	private static DatabaseHandler singleton = new DatabaseHandler();

	/** Used to determine if login_users table exists. */
	private static final String TABLES_SQL = "SHOW TABLES LIKE 'login_users';";

	/** Used to determine if hotel table exists. */
	private static final String TABLES_SQL_Hotel = "SHOW TABLES LIKE 'hotelData';";

	/** Used to determine if hotel table exists. */
	private static final String TABLES_SQL_Review = "SHOW TABLES LIKE 'reviewData';";

	/** Used to create login_users table for this example. */
	private static final String CREATE_SQL = "CREATE TABLE login_users ("
			+ "userid INTEGER AUTO_INCREMENT PRIMARY KEY, " + "username VARCHAR(32) NOT NULL UNIQUE, "
			+ "password CHAR(64) NOT NULL, " + "usersalt CHAR(32) NOT NULL);";

	/** Used to insert a new user's info into the login_users table */
	private static final String REGISTER_SQL = "INSERT INTO login_users (username, password, usersalt) "
			+ "VALUES (?, ?, ?);";

	/** Used to create HotelTable table for this example. */
	private static final String CREATE_SQL_HOTEL = "CREATE TABLE hotelData ("
			+ "hotelID INTEGER PRIMARY KEY, " + "hotelName VARCHAR(100) NOT NULL, "
			+ "address CHAR(64) NOT NULL, " + "rating DOUBLE(5,2) NOT NULL);";

	/** Used to insert a hotel's info into the login_users table */
	private static final String REGISTER_SQL_HOTEL = "INSERT INTO hotelData (hotelID, hotelName, address,rating) "
			+ "VALUES (?, ?, ?, ?);";
	/** Select hotel name **/
	private static final String FETCH_HOTEL = "SELECT hotelName, address, rating FROM hotelData";

	/** Used to create ReviewTable table for this example. */
	private static final String CREATE_SQL_REVIEW = "CREATE TABLE reviewData ("
			+ "reviewId VARCHAR(100) NOT NULL PRIMARY KEY, "+"hotelId INTEGER NOT NULL," +
			"reviewTitle VARCHAR(100) NOT NULL, "+"review VARCHAR(2000) NOT NULL,"+"username VARCHAR(100) NOT NULL, "
			+ "date VARCHAR(100) NOT NULL, " + "rating DOUBLE(5,2) NOT NULL);";

	/**Altering table for foreign key.*/
	private static final String ALTER_SQL_REVIEW ="ALTER TABLE reviewData ADD CONSTRAINT fk_hotelID FOREIGN KEY (hotelId) REFERENCES hotelData (hotelId);";

	/** Used to insert a hotel's info into the login_users table */
	private static final String REGISTER_SQL_REVIEW= "INSERT INTO reviewData (reviewId, hotelId, reviewTitle,review,username,date,rating)"
			+ "VALUES (?, ?, ?, ?, ?, ?, ?);";

	/** Used to determine if a username already exists. */
	private static final String USER_SQL = "SELECT username FROM login_users WHERE username = ?";

	// ------------------ constants below will be useful for the login operation
	// once you implement it
	/** Used to retrieve the salt associated with a specific user. */
	private static final String SALT_SQL = "SELECT usersalt FROM login_users WHERE username = ?";

	private static final String SALT_PASSWORD_SQL = "SELECT usersalt, password FROM login_users WHERE username = ?";

	/** Used to authenticate a user. */
	private static final String AUTH_SQL = "SELECT username FROM login_users " + "WHERE username = ? AND password = ?";

	/** Used to remove a user from the database. */
	private static final String DELETE_SQL = "DELETE FROM login_users WHERE username = ?";

	/** Used to configure connection to database. */
	private static DatabaseConnector db;

	/** Used to generate password hash salt for user. */
	private Random random;

	/**
	 * This class is a singleton, so the constructor is private. Other classes
	 * need to call getInstance()
	 */
	private DatabaseHandler() {
		Status status = Status.OK;
		random = new Random(System.currentTimeMillis());

		try {
			db = new DatabaseConnector("database.properties");
			status = db.testConnection() ? setupTables() : Status.CONNECTION_FAILED;
		} catch (FileNotFoundException e) {
			status = Status.MISSING_CONFIG;
		} catch (IOException e) {
			status = Status.MISSING_VALUES;
		}

		if (status != Status.OK) {
			System.out.println("Error while obtaining a connection to the database: " + status);
		}
	}

	/**
	 * Gets the single instance of the database handler.
	 *
	 * @return instance of the database handler
	 */
	public static DatabaseHandler getInstance() {
		return singleton;
	}

	/**
	 * Checks to see if a String is null or empty.
	 * 
	 * @param text
	 *            - String to check
	 * @return true if non-null and non-empty
	 */
	public static boolean isBlank(String text) {
		return (text == null) || text.trim().isEmpty();
	}

	/**
	 * Checks if necessary table exists in database, and if not tries to create
	 * it.
	 *
	 * @return {@link Status.OK} if table exists or create is successful
	 */
	private Status setupTables() {
		Status status = Status.ERROR;

		try (Connection connection = db.getConnection();
			 Statement statement = connection.createStatement();) {

			if (!statement.executeQuery(TABLES_SQL).next()|| !statement.executeQuery(TABLES_SQL_Hotel).next() || !statement.executeQuery(TABLES_SQL_Review).next()) {
				// Table missing, must create
				statement.executeUpdate(CREATE_SQL_REVIEW);
				statement.executeUpdate(ALTER_SQL_REVIEW);
				statement.executeUpdate(CREATE_SQL_HOTEL);
				statement.executeUpdate(CREATE_SQL);

				// Check if create was successful
				if (!statement.executeQuery(TABLES_SQL).next() || !statement.executeQuery(TABLES_SQL_Hotel).next() || !statement.executeQuery(TABLES_SQL_Review).next()) {
					status = Status.CREATE_FAILED;
				} else {
					status = Status.OK;
				}
			} else {
				status = Status.OK;
			}
		} catch (Exception ex) {
			status = Status.CREATE_FAILED;
		}

		return status;
	}

	/**
	 * Tests if a user already exists in the database. Requires an active
	 * database connection.
	 *
	 * @param connection
	 *            - active database connection
	 * @param user
	 *            - username to check
	 * @return Status.OK if user does not exist in database
	 * @throws SQLException
	 */
	private Status duplicateUser(Connection connection, String user) {

		assert connection != null;
		assert user != null;

		Status status = Status.ERROR;

		try (PreparedStatement statement = connection.prepareStatement(USER_SQL);) {
			statement.setString(1, user);

			ResultSet results = statement.executeQuery();
			status = results.next() ? Status.DUPLICATE_USER : Status.OK;
		} catch (SQLException e) {
			status = Status.SQL_EXCEPTION;
			System.out.println("Exception occured while processing SQL statement:" + e);
		}

		return status;
	}

	/**
	 * Returns the hex encoding of a byte array.
	 *
	 * @param bytes
	 *            - byte array to encode
	 * @param length
	 *            - desired length of encoding
	 * @return hex encoded byte array
	 */
	public static String encodeHex(byte[] bytes, int length) {
		BigInteger bigint = new BigInteger(1, bytes);
		String hex = String.format("%0" + length + "X", bigint);

		assert hex.length() == length;
		return hex;
	}

	/**
	 * Calculates the hash of a password and salt using SHA-256.
	 *
	 * @param password
	 *            - password to hash
	 * @param salt
	 *            - salt associated with user
	 * @return hashed password
	 */
	public static String getHash(String password, String salt) {
		String salted = salt + password;
		String hashed = salted;

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salted.getBytes());
			hashed = encodeHex(md.digest(), 64);
		} catch (Exception ex) {
			System.out.println("Unable to properly hash password." + ex);
		}

		return hashed;
	}

	/**
	 * Registers a new user, placing the username, password hash, and salt into
	 * the database if the username does not already exist.
	 *
	 * @param newuser
	 *            - username of new user
	 * @param newpass
	 *            - password of new user
	 * @return {@link Status.OK} if registration successful
	 */
	public Status registerUser(String newuser, String newpass) {
		Status status = Status.ERROR;
		System.out.println("Registering " + newuser + ".");

		// make sure we have non-null and non-emtpy values for login
		if (isBlank(newuser) || isBlank(newpass)) {
			status = Status.INVALID_LOGIN;
			System.out.println("Invalid regiser info");
			return status;
		}

		// try to connect to database and test for duplicate user
		try (Connection connection = db.getConnection();) {
			status = duplicateUser(connection, newuser);

			// if okay so far, try to insert new user
			if (status == Status.OK) {
				// generate salt
				byte[] saltBytes = new byte[16];
				random.nextBytes(saltBytes);

				String usersalt = encodeHex(saltBytes, 32); // hash salt
				String passhash = getHash(newpass, usersalt); // combine
																// password and
																// salt and hash
																// again

				// add user info to the database table
				try (PreparedStatement statement = connection.prepareStatement(REGISTER_SQL);) {
					statement.setString(1, newuser);
					statement.setString(2, passhash);
					statement.setString(3, usersalt);
					statement.executeUpdate();

					status = Status.OK;
				}
			}
		} catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			System.out.println("Error while connecting to the database: " + ex);
		}

		return status;
	}


	/**
	 adding hotel
	 */
	public Status addHotelDB(String hotelID, String hotelName, String address, double rating) {
		Status status = Status.OK;


		try (Connection connection = db.getConnection();) {

			// if okay so far, try to insert new user
			if (status == Status.OK) {
				// add hotel info to the database table
				try (PreparedStatement statement = connection.prepareStatement(REGISTER_SQL_HOTEL);) {
					statement.setString(1, hotelID);
					statement.setString(2, hotelName);
					statement.setString(3, address);
					statement.setString(4, String.valueOf(rating));

					statement.executeUpdate();

					status = Status.OK;
				}
			}
		} catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			System.out.println("Error while connecting to the database: " + ex);
		}

		return status;
	}
	/**
	 adding hotel
	 */
	public Status addReviewDB(String reviewId, String hotelId,String reviewTitle, String review,
							  String username,String date,double rating){

		Status status = Status.OK;


		try (Connection connection = db.getConnection();) {

			// if okay so far, try to insert new user
			if (status == Status.OK) {
				// add hotel info to the database table
				try (PreparedStatement statement = connection.prepareStatement(REGISTER_SQL_REVIEW);) {
					statement.setString(1, reviewId);
					statement.setString(2, hotelId);
					statement.setString(3, reviewTitle);
					statement.setString(4, review);
					statement.setString(5, username);
					statement.setString(6, date);
					statement.setString(7, String.valueOf(rating));

					statement.executeUpdate();

					status = Status.OK;
				}
			}
		} catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			System.out.println("Error while connecting to the database: " + ex);
		}

		return status;
	}




	/**
	 * Registers a new user, placing the username, password hash, and salt into
	 * the database if the username does not already exist.
	 *
	 * @param user
	 *            - username of new user
	 * @param password
	 *            - password of new user
	 * @return {@link Status.OK} if registration successful
	 */
	public Status authenticateUser(String user, String password) {
		Status status = Status.ERROR;
		System.out.println("Authenticating " + user + ".");

		// make sure we have non-null and non-emtpy values for login
		if (isBlank(user) || isBlank(password)) {
			status = Status.INVALID_LOGIN;
			System.out.println("Invalid login for user: " + user);
			return status;
		}

		// try to connect to database and test for duplicate user
		try (Connection connection = db.getConnection()) {
			Map<String, String> loginDetails = getLoginDetails(connection, user);
			String usersalt = loginDetails.get("usersalt");
			String encryptedPassword = loginDetails.get("password");
			String passhash = getHash(password, usersalt);
			if (!encryptedPassword.equals(passhash)) {
				status = Status.INVALID_LOGIN;

				System.out.println("Invalid login for user: " + user);
			}
			else {
				status = Status.OK;
			}
		} catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			System.out.println("Error while connecting to the database: " + ex);
		}

		return status;
	}

	/**
	 * Gets the salt for a specific user.
	 *
	 * @param connection
	 *            - active database connection
	 * @param user
	 *            - which user to retrieve salt for
	 * @return salt for the specified user or null if user does not exist
	 * @throws SQLException
	 *             if any issues with database connection
	 */
	private String getSalt(Connection connection, String user) throws SQLException {
		assert connection != null;
		assert user != null;

		String salt = null;

		try (PreparedStatement statement = connection.prepareStatement(SALT_SQL);) {
			statement.setString(1, user);

			ResultSet results = statement.executeQuery();

			if (results.next()) {
				salt = results.getString("usersalt");
			}
		}

		return salt;
	}

	/**
	 * Gets the salt for a specific user.
	 *
	 * @param connection
	 *            - active database connection
	 * @param user
	 *            - which user to retrieve salt for
	 * @return salt for the specified user or null if user does not exist
	 * @throws SQLException
	 *             if any issues with database connection
	 */
	private Map<String, String> getLoginDetails(Connection connection, String user) throws SQLException {
		assert connection != null;
		assert user != null;

		Map<String, String> loginDetails = new HashMap<>();

		try (PreparedStatement statement = connection.prepareStatement(SALT_PASSWORD_SQL);) {
			statement.setString(1, user);

			ResultSet results = statement.executeQuery();

			if (results.next()) {
				loginDetails.put("usersalt", results.getString("usersalt"));
				loginDetails.put("password", results.getString("password"));
			}
		}

		return loginDetails;
	}

	/**
	 * Gets the salt for a specific user.
	 *
	 * @param connection
	 *            - active database connection
	 * @param user
	 *            - which user to retrieve salt for
	 * @return salt for the specified user or null if user does not exist
	 * @throws SQLException
	 *             if any issues with database connection
	 */
//	public Map<String, String> getHotelDetails(Connection connection) throws SQLException {
//		assert connection != null;
//
//
//		Map<String, String> hoteldetails = new HashMap<>();
//
//		try (PreparedStatement statement = connection.prepareStatement(FETCH_HOTEL);) {
//
//			ResultSet results = statement.executeQuery();
//
//			if (results.next()) {
//				hoteldetails.put("HotelName",results.getString("hotelName"));
//				hoteldetails.put("Address",results.getString("address"));
//				hoteldetails.put("Rating",results.getString("rating"));
//			}
//		}
//
//		return hoteldetails;
//	}
}
