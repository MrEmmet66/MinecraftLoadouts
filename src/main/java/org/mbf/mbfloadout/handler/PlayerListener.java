package org.mbf.mbfloadout.handler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.mbf.mbfloadout.MbfLoadout;

import java.io.IOException;
import java.sql.SQLException;

public class PlayerListener implements Listener {
    private final MbfLoadout plugin;

    public PlayerListener(MbfLoadout plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws SQLException {
        String uuid = event.getPlayer().getUniqueId().toString();
        if(plugin.getLoadoutService().isPlayerExists(uuid)) {
            return;
        }
        plugin.getLoadoutService().createPlayer(uuid);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if(!plugin.getLoadoutState()) {
            return;
        }
        try {
            plugin.getLoadoutService().getPlayer(event.getPlayer()).applyLoadout(event.getPlayer());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
