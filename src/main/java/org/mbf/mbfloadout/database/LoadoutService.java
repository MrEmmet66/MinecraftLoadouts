package org.mbf.mbfloadout.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.bukkit.entity.Player;
import org.mbf.mbfloadout.MbfLoadout;
import org.mbf.mbfloadout.database.model.Loadout;
import org.mbf.mbfloadout.database.model.PlayerLoadout;

import java.sql.SQLException;
import java.util.HashSet;

public class LoadoutService {
    private final Dao<PlayerLoadout, String> playerLoadoutDao;
    private final MbfLoadout plugin;

    public LoadoutService(String path, MbfLoadout plugin) throws SQLException {
        ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + path);
        TableUtils.createTableIfNotExists(connectionSource, PlayerLoadout.class);
        playerLoadoutDao = DaoManager.createDao(connectionSource, PlayerLoadout.class);
        this.plugin = plugin;
    }

    public PlayerLoadout createPlayer(String uuid) throws SQLException {
        PlayerLoadout playerLoadout = new PlayerLoadout();
        playerLoadout.setUuid(uuid);
        playerLoadout.setLoadouts(new HashSet<>());
        playerLoadoutDao.create(playerLoadout);
        return playerLoadout;
    }

    public boolean isPlayerExists(String uuid) throws SQLException {
        return playerLoadoutDao.queryForId(uuid) != null;
    }

    public PlayerLoadout getPlayer(String uuid) throws SQLException {
        return playerLoadoutDao.queryForId(uuid);
    }
    public PlayerLoadout getPlayer(Player player) throws SQLException {
        return playerLoadoutDao.queryForId(player.getUniqueId().toString());
    }

    public void updatePlayer(PlayerLoadout playerLoadout) throws SQLException {
        playerLoadoutDao.update(playerLoadout);
    }
}
