/*
 * This file is part of EverEssentials.
 *
 * EverEssentials is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EverEssentials is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EverEssentials.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.evercraft.essentials;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import fr.evercraft.essentials.service.subject.EMail;
import fr.evercraft.essentials.service.subject.EUserSubject;
import fr.evercraft.everapi.exception.PluginDisableException;
import fr.evercraft.everapi.exception.ServerDisableException;
import fr.evercraft.everapi.plugin.EDataBase;
import fr.evercraft.everapi.server.location.LocationSQL;
import fr.evercraft.everapi.services.essentials.Mail;

public class EEDataBase extends EDataBase<EverEssentials> {
	private String table_players;
	private String table_ignores;
	private String table_mails;
	private String table_homes;
	private String table_backs;
	
	private String table_warps;
	private String table_spawns;

	public EEDataBase(final EverEssentials plugin) throws PluginDisableException {
		super(plugin, true);
	}

	public boolean init() throws ServerDisableException {
		this.table_players = "players";
		String players = 	"CREATE TABLE IF NOT EXISTS <table> (" +
							"`uuid` varchar(36) NOT NULL," +
							"`vanish` BOOL NOT NULL DEFAULT '0'," +
							"`god` BOOL NOT NULL DEFAULT '0'," +
							"`toggle` BOOL NOT NULL DEFAULT '1'," +
							"`freeze` BOOL NOT NULL DEFAULT '0'," +
							"`total_time_played` INT NOT NULL," +
							"PRIMARY KEY (`uuid`));";
		initTable(this.getTablePlayers(), players);
		
		this.table_ignores = "ignores";
		String ignores = 	"CREATE TABLE IF NOT EXISTS <table> (" +
							"`uuid` varchar(36) NOT NULL," +
							"`ignore` varchar(36) NOT NULL," +
							"PRIMARY KEY (`uuid`, `ignore`));";
		initTable(this.getTableIgnores(), ignores);
		
		this.table_mails = "mails";
		String mails = 	"CREATE TABLE IF NOT EXISTS <table> (" +
							"`id` MEDIUMINT NOT NULL AUTO_INCREMENT," +
							"`datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP," +
							"`read` BOOLEAN DEFAULT FALSE," +
							"`player` varchar(36) NOT NULL," +
							"`to` varchar(36) NOT NULL," +
							"`message` varchar(255) NOT NULL," +
							"PRIMARY KEY (`id`));";
		initTable(this.getTableMails(), mails);
		
		this.table_homes = "homes";
		String homes = 		"CREATE TABLE IF NOT EXISTS <table> (" +
							"`uuid` varchar(36) NOT NULL," +
							"`name` varchar(25) NOT NULL," +
							"`world` varchar(36) NOT NULL," +
							"`x` double NOT NULL," +
							"`y` double NOT NULL," +
							"`z` double NOT NULL," +
							"`yaw` double," +
							"`pitch` double," +
							"PRIMARY KEY (`uuid`, `name`));";
		initTable(this.getTableHomes(), homes);
		
		this.table_backs = "backs";
		String backs = 		"CREATE TABLE IF NOT EXISTS <table> (" +
							"`uuid` varchar(36) NOT NULL," +
							"`world` varchar(36) NOT NULL," +
							"`x` double NOT NULL," +
							"`y` double NOT NULL," +
							"`z` double NOT NULL," +
							"`yaw` double," +
							"`pitch` double," +
							"PRIMARY KEY (`uuid`));";
		initTable(this.getTableBacks(), backs);
		
		this.table_warps = "warps";
		String warps = 		"CREATE TABLE IF NOT EXISTS <table> (" +
							"`identifier` varchar(25) NOT NULL," +
							"`world` varchar(36) NOT NULL," +
							"`x` double NOT NULL," +
							"`y` double NOT NULL," +
							"`z` double NOT NULL," +
							"`yaw` double," +
							"`pitch` double," +
							"PRIMARY KEY (`identifier`));";
		initTable(this.getTableWarps(), warps);
		
		this.table_spawns = "spawns";
		String spawns = 	"CREATE TABLE IF NOT EXISTS <table> (" +
							"`identifier` varchar(25) NOT NULL," +
							"`world` varchar(36) NOT NULL," +
							"`x` double NOT NULL," +
							"`y` double NOT NULL," +
							"`z` double NOT NULL," +
							"`yaw` double," +
							"`pitch` double," +
							"PRIMARY KEY (`identifier`));";
		initTable(this.getTableSpawns(), spawns);
		
		return true;
	}
	
	public String getTablePlayers() {
		return this.getPrefix() + this.table_players;
	}
	
	public String getTableIgnores() {
		return this.getPrefix() + this.table_ignores;
	}
	
	public String getTableMails() {
		return this.getPrefix() + this.table_mails;
	}
	
	public String getTableHomes() {
		return this.getPrefix() + this.table_homes;
	}
	
	public String getTableBacks() {
		return this.getPrefix() + this.table_backs;
	}
	
	public String getTableWarps() {
		return this.getPrefix() + this.table_warps;
	}
	
	public String getTableSpawns() {
		return this.getPrefix() + this.table_spawns;
	}

	/*
	 * Autres fonctions
	 */
	
	public void setVanish(final String identifier, final boolean vanish) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "UPDATE `" + this.getTablePlayers() + "` "
							+ "SET `vanish` = ? "
							+ "WHERE `uuid` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setBoolean(1, vanish);
			preparedStatement.setString(2, identifier);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Updating the database : (identifier='" + identifier + "';vanish='" + vanish + "')");
    	} catch (SQLException e) {
        	this.plugin.getLogger().warn("Error during a change of vanish : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	public void setGod(final String identifier, final boolean god) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "UPDATE `" + this.getTablePlayers() + "` "
							+ "SET `god` = ? "
							+ "WHERE `uuid` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setBoolean(1, god);
			preparedStatement.setString(2, identifier);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Updating the database : (identifier='" + identifier + "';god='" + god + "')");
    	} catch (SQLException e) {
        	this.plugin.getLogger().warn("Error during a change of god : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	public void setToggle(final String identifier, final boolean toggle) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "UPDATE `" + this.getTablePlayers() + "` "
							+ "SET `toggle` = ? "
							+ "WHERE `uuid` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setBoolean(1, toggle);
			preparedStatement.setString(2, identifier);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Updating the database : (identifier='" + identifier + "';toggle='" + toggle + "')");
    	} catch (SQLException e) {
        	this.plugin.getLogger().warn("Error during a change of toggle : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	public void setFreeze(final String identifier, final boolean freeze) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "UPDATE `" + this.getTablePlayers() + "` "
							+ "SET `freeze` = ? "
							+ "WHERE `uuid` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setBoolean(1, freeze);
			preparedStatement.setString(2, identifier);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Updating the database : (identifier='" + identifier + "';freeze='" + freeze + "')");
    	} catch (SQLException e) {
        	this.plugin.getLogger().warn("Error during a change of freeze : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	public void setTotalTimePlayed(final String identifier, final long time) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "UPDATE `" + this.getTablePlayers() + "` "
							+ "SET `total_time_played` = ? "
							+ "WHERE `uuid` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setLong(1, time);
			preparedStatement.setString(2, identifier);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Updating the database : (identifier='" + identifier + "';total_time_played='" + time + "')");
    	} catch (SQLException e) {
        	this.plugin.getLogger().warn("Error during a change of setTotalTimePlayed : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	public void addIgnore(final String identifier, final String ignore) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "INSERT INTO `" + this.getTableIgnores() + "` "
    						+ "VALUES (?, ?);";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, identifier);;
			preparedStatement.setString(2, ignore);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Adding to the database : (identifier='" + identifier + "';ignore='" + ignore + "')");
    	} catch (SQLException e) {
        	this.plugin.getLogger().warn("Error during a change of ignore : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	public void removeIgnore(final String identifier, final String ignore) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "DELETE " 
		    				+ "FROM `" + this.getTableIgnores() + "` "
		    				+ "WHERE `uuid` = ? AND `ignore` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, identifier);
			preparedStatement.setString(2, ignore);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Remove from database : (identifier='" + identifier + "';ignore='" + ignore + "')");
    	} catch (SQLException e) {
        	this.plugin.getLogger().warn("Error during a change of ignore : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	public void clearIgnores(final String identifier) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "DELETE " 
		    				+ "FROM `" + this.getTableIgnores() + "` "
		    				+ "WHERE `uuid` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, identifier);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Removes the database ignores : (identifier='" + identifier + "';)");
    	} catch (SQLException e) {
    		this.plugin.getLogger().warn("Error ignores deletions : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	/*
	 * Home
	 */
	
	public void addHome(final String identifier, final String name, final LocationSQL location) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "INSERT INTO `" + this.getTableHomes() + "` "
    						+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, identifier);
			preparedStatement.setString(2, name);
			preparedStatement.setString(3, location.getWorldUUID());
			preparedStatement.setDouble(4, location.getX());
			preparedStatement.setDouble(5, location.getY());
			preparedStatement.setDouble(6, location.getZ());
			preparedStatement.setDouble(7, location.getYaw());
			preparedStatement.setDouble(8, location.getPitch());
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Adding to the database : (identifier='" + identifier + "';home='" + name + "';location='" + location + "')");
    	} catch (SQLException e) {
        	this.plugin.getLogger().warn("Error during a change of home : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	public void moveHome(final String identifier, final String name, final LocationSQL location) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "UPDATE `" + this.getTableHomes() + "` "
							+ "SET `world` = ? ,"
								+ "`x` = ?, "
								+ "`y` = ?, "
								+ "`z` = ?, "
								+ "`yaw` = ?, "
								+ "`pitch` = ? "
							+ "WHERE `uuid` = ? "
							+ "AND `name` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, location.getWorldUUID());
			preparedStatement.setDouble(2, location.getX());
			preparedStatement.setDouble(3, location.getY());
			preparedStatement.setDouble(4, location.getZ());
			preparedStatement.setDouble(5, location.getYaw());
			preparedStatement.setDouble(6, location.getPitch());
			preparedStatement.setString(7, identifier);
			preparedStatement.setString(8, name);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Updating to the database : (identifier='" + identifier + "';home='" + name + "';location='" + location + "')");
    	} catch (SQLException e) {
        	this.plugin.getLogger().warn("Error during a change of home : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	public void removeHome(final String identifier, final String name) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "DELETE " 
		    				+ "FROM `" + this.getTableHomes() + "` "
		    				+ "WHERE `uuid` = ? AND `name` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, identifier);
			preparedStatement.setString(2, name);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Remove from database : (identifier='" + identifier + "';name='" + name + "')");
    	} catch (SQLException e) {
        	this.plugin.getLogger().warn("Error during a change of home : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	public void clearHomes(final String identifier) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "DELETE " 
		    				+ "FROM `" + this.getTableHomes() + "` "
		    				+ "WHERE `uuid` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, identifier);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Removes the database homes : (identifier='" + identifier + "';)");
    	} catch (SQLException e) {
    		this.plugin.getLogger().warn("Error homes deletions : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	/*
	 * Back
	 */
	
	public void addBack(final String identifier, final LocationSQL location) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "INSERT INTO `" + this.getTableBacks() + "` "
    						+ "VALUES (?, ?, ?, ?, ?, ?, ?);";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, identifier);
			preparedStatement.setString(2, location.getWorldUUID());
			preparedStatement.setDouble(3, location.getX());
			preparedStatement.setDouble(4, location.getY());
			preparedStatement.setDouble(5, location.getZ());
			preparedStatement.setDouble(6, location.getYaw());
			preparedStatement.setDouble(7, location.getPitch());
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Adding to the database : (identifier='" + identifier + "';location='" + location + "')");
    	} catch (SQLException e) {
        	this.plugin.getLogger().warn("Error during a change of back : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	public void setBack(final String identifier, final LocationSQL location) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "UPDATE `" + this.getTableBacks() + "` "
    						+ "SET `world` = ? , `x` = ? , `y` = ? , `z` = ? , `yaw` = ? , `pitch` = ? "
    						+ "WHERE `uuid` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, location.getWorldUUID());
			preparedStatement.setDouble(2, location.getX());
			preparedStatement.setDouble(3, location.getY());
			preparedStatement.setDouble(4, location.getZ());
			preparedStatement.setDouble(5, location.getYaw());
			preparedStatement.setDouble(6, location.getPitch());
			preparedStatement.setString(7, identifier);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Updating the database : (identifier='" + identifier + "';location='" + location + "')");
    	} catch (SQLException e) {
        	this.plugin.getLogger().warn("Error during a change of back : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	public void clearBack(final String identifier) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "DELETE " 
		    				+ "FROM `" + this.getTableBacks() + "` "
		    				+ "WHERE `uuid` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, identifier);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Remove from database : (identifier='" + identifier + "')");
    	} catch (SQLException e) {
        	this.plugin.getLogger().warn("Error during a change of back : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	/*
	 * Mails
	 */

	public void sendMail(EUserSubject subject, String to, String message) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "INSERT INTO `" + this.getTableMails() + "` (`datetime`, `player`, `to`, `message`) "
    						+ "VALUES (?, ?, ?, ?);";
    		
    		Timestamp datetime = new Timestamp(System.currentTimeMillis());
    		
			preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setTimestamp(1, datetime);
			preparedStatement.setString(2, subject.getIdentifier());
			preparedStatement.setString(3, to);
			preparedStatement.setString(4, message);
			preparedStatement.executeUpdate();
			
			ResultSet tableKeys = preparedStatement.getGeneratedKeys();
			if(tableKeys.next()) {
				subject.addMail(new EMail(this.plugin, tableKeys.getInt(1), datetime.getTime(), to, false, message));
				this.plugin.getLogger().debug("Adding to the database : ("
								+ "id='" + tableKeys.getInt(1) + "';"
								+ "time='" + datetime.getTime() + "';"
								+ "player='" + subject.getIdentifier() + "';"
								+ "to='" + to + "';"
								+ "message='" + message + "';)");
			} else {
				this.plugin.getLogger().debug("Error while adding an email in the database : ("
						+ "time='" + datetime.getTime() + "';"
						+ "player='" + subject.getIdentifier() + "';"
						+ "to='" + to + "';"
						+ "message='" + message + "';)");
			}
    	} catch (SQLException e) {
    		this.plugin.getLogger().debug("Error while adding an email in the database : ("
						+ "player='" + subject.getIdentifier() + "';"
						+ "to='" + to + "';"
						+ "message='" + message + "';) : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	public void updateMail(Mail mail) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "UPDATE `" + this.getTableMails() + "` "
    						+ "SET `read` = ? "
    						+ "WHERE `id` = ? ;";
    		
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setBoolean(1, mail.isRead());
			preparedStatement.setInt(2, mail.getID());
			preparedStatement.execute();
			
			this.plugin.getLogger().debug("Update an email in the database : ("
					+ "id='" + mail.getID() + "';"
					+ "read='" + mail.isRead() + "';)");
    	} catch (SQLException e) {
    		this.plugin.getLogger().debug("Error while updating an email in the database ("
						+ "id='" + mail.getID() + "';"
						+ "read='" + mail.isRead() + "') : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	public void removeMails(String identifier, int id) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "DELETE " 
		    				+ "FROM `" + this.getTableMails() + "` "
		    				+ "WHERE `player` = ?  AND `id` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, identifier);
			preparedStatement.setInt(2, id);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Deleting an email from the database : (id='" + id + "';player='" + identifier + "')");
    	} catch (SQLException e) {
        	this.plugin.getLogger().warn("Error while deleting an email (id='" + id + "';player='" + identifier + "') : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}

	public void sendAllMail(String identifier, String message) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "SELECT *" 
							+ "FROM `" + this.plugin.getDataBases().getTablePlayers() + "` ;";
			preparedStatement = connection.prepareStatement(query);
			
			Timestamp datetime = new Timestamp(System.currentTimeMillis());
			ResultSet list = preparedStatement.executeQuery();
			while (list.next()) {
				sendMail(connection, datetime, identifier, list.getString("uuid"), message);
			}
			
			for(EUserSubject subject : this.plugin.getManagerServices().getEssentials().getAll()) {
				subject.loadMails(connection);
			}
    	} catch (SQLException e) {
    		this.plugin.getLogger().warn("Error while adding an email in the database : " + e.getMessage());
    	} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	public void sendMail(Connection connection, Timestamp datetime, String to, String player, String message) {
		PreparedStatement preparedStatement = null;
    	try {
    		String query = 	  "INSERT INTO `" + this.getTableMails() + "` (`datetime`, `player`, `to`, `message`) "
    						+ "VALUES (?, ?, ?, ?);";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setTimestamp(1, datetime);
			preparedStatement.setString(2, player);
			preparedStatement.setString(3, to);
			preparedStatement.setString(4, message);
			preparedStatement.execute();
    	} catch (SQLException e) {
    		this.plugin.getLogger().warn(": " + e.getMessage());
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
			} catch (SQLException e) {}
	    }
	}
}
