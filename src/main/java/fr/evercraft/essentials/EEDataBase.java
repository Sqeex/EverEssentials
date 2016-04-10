/**
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
import java.util.HashMap;
import java.util.Map;

import fr.evercraft.essentials.service.warp.LocationSQL;
import fr.evercraft.everapi.exception.ServerDisableException;
import fr.evercraft.everapi.plugin.EDataBase;

public class EEDataBase extends EDataBase<EverEssentials> {
	private String table_players;
	private String table_ignores;
	private String table_homes;
	private String table_backs;
	
	private String table_warps;
	private String table_spawns;

	public EEDataBase(final EverEssentials plugin) {
		super(plugin, true);
	}

	public boolean init() throws ServerDisableException {
		this.table_players = "players";
		String players = 	"CREATE TABLE IF NOT EXISTS <table> (" +
							"`uuid` varchar(36) NOT NULL," +
							"`vanish` boolean NOT NULL DEFAULT 0," +
							"`god` boolean NOT NULL DEFAULT 0," +
							"`mute` long NOT NULL DEFAULT 0," +
							"`ban` long NOT NULL DEFAULT 0," +
							"PRIMARY KEY (`uuid`));";
		initTable(this.getTablePlayers(), players);
		
		this.table_ignores = "ignores";
		String ignores = 	"CREATE TABLE IF NOT EXISTS <table> (" +
							"`uuid` varchar(36) NOT NULL," +
							"`ignore` varchar(36) NOT NULL," +
							"PRIMARY KEY (`uuid`, `ignore`));";
		initTable(this.getTableIgnores(), ignores);
		
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
							+ "SET `vanish` = ? "
							+ "WHERE `god` = ? ;";
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
	
	public void setMute(final String identifier, final long mute) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "UPDATE `" + this.getTablePlayers() + "` "
							+ "SET `mute` = ? "
							+ "WHERE `uuid` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setLong(1, mute);
			preparedStatement.setString(2, identifier);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Updating the database : (identifier='" + identifier + "';mute='" + mute + "')");
    	} catch (SQLException e) {
        	this.plugin.getLogger().warn("Error during a change of mute : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	public void setBan(final String identifier, final long ban) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "UPDATE `" + this.getTablePlayers() + "` "
							+ "SET `ban` = ? "
							+ "WHERE `uuid` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setLong(1, ban);
			preparedStatement.setString(2, identifier);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Updating the database : (identifier='" + identifier + "';ban='" + ban + "')");
    	} catch (SQLException e) {
        	this.plugin.getLogger().warn("Error during a change of tempban : " + e.getMessage());
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
    		String query = 	  "UPDATE INTO `" + this.getTableBacks() + "` "
    						+ "SET `world` = ? , `x` = ? , `y` = ? , `z` = ? , `yaw` = ? , `pitch` = ? "
    						+ "WHERE `uuid` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(2, location.getWorldUUID());
			preparedStatement.setDouble(3, location.getX());
			preparedStatement.setDouble(4, location.getY());
			preparedStatement.setDouble(5, location.getZ());
			preparedStatement.setDouble(6, location.getYaw());
			preparedStatement.setDouble(7, location.getPitch());
			preparedStatement.setString(1, identifier);
			
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
	 * Warps
	 */
	public Map<String, LocationSQL> selectWarps() {
		Map<String, LocationSQL> warps = new HashMap<String, LocationSQL>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "SELECT *" 
							+ "FROM `" + this.getTableWarps() + "` ;";
			preparedStatement = connection.prepareStatement(query);
			ResultSet list = preparedStatement.executeQuery();
			if (list.next()) {
				warps.put(list.getString("identifier"), new LocationSQL(this.plugin,	list.getString("world"), 
																				list.getDouble("x"),
																				list.getDouble("y"),
																				list.getDouble("z"),
																				list.getDouble("yaw"),
																				list.getDouble("pitch")));
				this.plugin.getLogger().debug("Loading : (warp='" + list.getString("identifier") + "';location='" + warps.get(list.getString("identifier")) + "')");
			}
    	} catch (SQLException e) {
    		this.plugin.getLogger().warn("Warps error when loading : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
    	return warps;
	}
	
	public void addWarp(final String identifier, final LocationSQL location) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "INSERT INTO `" + this.getTableWarps() + "` "
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
			this.plugin.getLogger().debug("Adding to the database : (warp='" + identifier + "';location='" + location + "')");
    	} catch (SQLException e) {
        	this.plugin.getLogger().warn("Error during a change of warp : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	public void removeWarp(final String identifier) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "DELETE " 
		    				+ "FROM `" + this.getTableWarps() + "` "
		    				+ "WHERE `identifier` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, identifier);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Remove from database : (warp='" + identifier + "')");
    	} catch (SQLException e) {
        	this.plugin.getLogger().warn("Error during a change of warp : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	public void clearWarps() {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "DELETE " 
		    				+ "FROM `" + this.getTableWarps() + "` ;";
			preparedStatement = connection.prepareStatement(query);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Removes the database warps");
    	} catch (SQLException e) {
    		this.plugin.getLogger().warn("Error warps deletions : " + e.getMessage());
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
	 * Spawn
	 */
	public Map<String, LocationSQL> selectSpawns() {
		Map<String, LocationSQL> warps = new HashMap<String, LocationSQL>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "SELECT *" 
							+ "FROM `" + this.getTableSpawns() + "` ;";
			preparedStatement = connection.prepareStatement(query);
			ResultSet list = preparedStatement.executeQuery();
			if (list.next()) {
				warps.put(list.getString("identifier"), new LocationSQL(this.plugin,	list.getString("world"), 
																						list.getDouble("x"),
																						list.getDouble("y"),
																						list.getDouble("z"),
																						list.getDouble("yaw"),
																						list.getDouble("pitch")));
				this.plugin.getLogger().debug("Loading : (spawn='" + list.getString("identifier") + "';location='" + warps.get(list.getString("identifier")) + "')");
			}
    	} catch (SQLException e) {
    		this.plugin.getLogger().warn("Spawns error when loading : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
    	return warps;
	}
	
	public void addSpawn(final String identifier, final LocationSQL location) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "INSERT INTO `" + this.getTableSpawns() + "` "
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
			this.plugin.getLogger().debug("Adding to the database : (spawn='" + identifier + "';location='" + location + "')");
    	} catch (SQLException e) {
        	this.plugin.getLogger().warn("Error during a change of spawn : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	public void removeSpawn(final String identifier) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "DELETE " 
		    				+ "FROM `" + this.getTableSpawns() + "` "
		    				+ "WHERE `identifier` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, identifier);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Remove from database : (spawn='" + identifier + "')");
    	} catch (SQLException e) {
        	this.plugin.getLogger().warn("Error during a change of spawn : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	public void clearSpawns() {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.getConnection();
    		String query = 	  "DELETE " 
		    				+ "FROM `" + this.getTableSpawns() + "` ;";
			preparedStatement = connection.prepareStatement(query);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Removes the database spawns");
    	} catch (SQLException e) {
    		this.plugin.getLogger().warn("Error warps deletions : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
}
