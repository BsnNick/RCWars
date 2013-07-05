package me.SgtMjrME;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.SgtMjrME.Object.DatabaseObject;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class mysqlLink {
	String address;
	String port;
	String user;
	String pass;
	String database_name;
	Connection con;

	mysqlLink(String address, String port, String u, String p,
			String database_name) {
		this.address = address;
		this.port = port;
		user = u;
		pass = p;
		this.database_name = database_name;
		try {
			reestablishConnection();
			if (!con.isValid(10)) {
				con = null;
				return;
			}
			PreparedStatement s = con
					.prepareStatement("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '"
							+ database_name + "' AND table_name = 'data';");
			ResultSet result = s.executeQuery();
			result.next();
			int hasTable = result.getInt(1);
			if (hasTable != 1) {
				createTable();
				System.out.println("[RCWars] New table created");
			}
		} catch (SQLException e) {
			con = null;
			System.out.println("[RCWars] MySQL ERROR SQLEX");
			e.printStackTrace();
		}
	}

	public void createTable() {
		try {
			if ((con == null) || (!con.isValid(0))) {
				System.out.println("[RCWars] Connection is invalid");
				return;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			return;
		}
		try {
			PreparedStatement createTable = con
					.prepareStatement("CREATE  TABLE " + 
							"data (`name` VARCHAR(16) NOT NULL ,"+
							"`kills` INT NOT NULL DEFAULT 0 ,"+
							"`deaths` INT NOT NULL DEFAULT 0 ,"+
							"`wp` INT NOT NULL DEFAULT 0 ,"+
							"PRIMARY KEY (`name`) );");
			if (!createTable.execute())
				System.out.println("problem creating table");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void dropTable() {
		try {
			if ((con == null) || (!con.isValid(0))) {
				System.out.println("[RCWars] Connection is invalid");
				return;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			try {
				PreparedStatement dropTable = con
						.prepareStatement("drop table `" + database_name
								+ "`.`data`;");
				dropTable.execute();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void close() {
		try {
			if ((con != null) && (con.isValid(0)))
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updatePlayer(Player p, String type) {
		updatePlayer(p, type, 0);
	}

	public void updatePlayer(final Player p, final String type, final int amt) {
		if ((con == null) || (p == null))
			return;
		Bukkit.getScheduler().runTaskAsynchronously(RCWars.returnPlugin(), new Runnable(){
			@Override
			public void run() {
				try {
					if (!con.isValid(0))
						reestablishConnection();
				} catch (SQLException e1) {
					e1.printStackTrace();
					return;
				}
				try {
					PreparedStatement s = con.prepareStatement("select * from "
							+ database_name + ".data where name = \"" + p.getName()
							+ "\";");
					ResultSet result = s.executeQuery();
					if (result.next()) {
						if (type.equalsIgnoreCase("wp"))
							addwp(p, amt);
						else if (type.equalsIgnoreCase("kill"))
							addKill(p);
						else if (type.equalsIgnoreCase("death"))
							addDeath(p);
					} else {
						System.out.println("[RCWars] Creating new table entry for "
								+ p.getName());
						PreparedStatement addPlayer = con
								.prepareStatement("insert into " + database_name
										+ ".data (name, kills, deaths, wp) values('"
										+ p.getName() + "','0','0','0');");
						addPlayer.executeUpdate();
						updatePlayer(p, type);
					}
				} catch (SQLException e) {
					System.out.println("[RCWars] MySQL ERROR: KILL");
					e.printStackTrace();
				}
			}
		});
		
	}

	private void reestablishConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String connection = "jdbc:mysql://" + address + ":" + port + "/"
					+ database_name;
			con = DriverManager.getConnection(connection, user, pass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void addKill(Player p) {
		try {
			PreparedStatement add = con.prepareStatement("update "
					+ database_name
					+ ".data set kills = (kills + 1) where name = '"
					+ p.getName() + "';");
			add.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void addDeath(Player p) {
		try {
			PreparedStatement add = con.prepareStatement("update "
					+ database_name
					+ ".data set deaths = (deaths + 1) where name = '"
					+ p.getName() + "';");
			add.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void addwp(Player p, int amt) {
		try {
			PreparedStatement add = con.prepareStatement("update "
					+ database_name + ".data set wp = wp + " + amt
					+ " where name = '" + p.getName() + "';");
			add.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int[] getStats(String p) {
		int[] out = new int[3];
		try {
			if (!con.isValid(0))
				reestablishConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
			return null;
		}
		try {
			PreparedStatement stats = con.prepareStatement("select * from "
					+ database_name + ".data where name = '" + p + "';");
			ResultSet result = stats.executeQuery();
			if (result.next()) {
				out[0] = result.getInt(2);
				out[1] = result.getInt(3);
				out[2] = result.getInt(4);
				return out;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public DatabaseObject[] getMaxStats(){//Run this async!
		//returns databseobject array length 3. 
		//First is max kills, second is max deaths, third is max wp
		DatabaseObject[] out = new DatabaseObject[3];
		//F*** it, select all and do checks in java for speed
		ResultSet result;
		try {
			PreparedStatement p = con.prepareStatement("SELECT * FROM " + database_name + "`data`");
			result = p.executeQuery();
			while (result.next()){
				String name = result.getString("name");
				int kills = result.getInt("kills");
				int deaths = result.getInt("deaths");
				int wp = result.getInt("wp");
				if (out[0].kills < kills){
					out[0].s = name;
					out[0].kills = kills;
					out[0].deaths = deaths;
					out[0].wp = wp;
				}
				if (out[1].deaths < deaths){
					out[1].s = name;
					out[1].kills = kills;
					out[1].deaths = deaths;
					out[1].wp = wp;
				}
				if (out[2].wp < wp){
					out[2].s = name;
					out[2].kills = kills;
					out[2].deaths = deaths;
					out[2].wp = wp;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return out;
	}
}