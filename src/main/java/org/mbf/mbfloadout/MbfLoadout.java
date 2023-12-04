package org.mbf.mbfloadout;

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.mbf.mbfloadout.command.LoadoutCommand;
import org.mbf.mbfloadout.database.LoadoutService;
import org.mbf.mbfloadout.database.model.Loadout;
import org.mbf.mbfloadout.database.model.PlayerLoadout;
import org.mbf.mbfloadout.handler.PlayerListener;

import java.sql.SQLException;
import java.util.HashSet;

public final class MbfLoadout extends JavaPlugin {
    private LoadoutService loadoutService;
    private boolean loadoutState = true;

    @Override
    public void onEnable() {
        BukkitCommandManager<CommandSender> manager = BukkitCommandManager.create(this);
        manager.registerSuggestion(SuggestionKey.of("loadouts"), (sender, context) -> {
            PlayerLoadout playerLoadout = null;
            try {
                playerLoadout = loadoutService.getPlayer((Player) sender);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            HashSet<Loadout> loadouts = playerLoadout.getLoadouts();
            return loadouts.stream().map(Loadout::getName).toList();
        });
        manager.registerCommand(new LoadoutCommand(this));

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        try {
            if(!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            loadoutService = new LoadoutService(getDataFolder().getAbsolutePath() + "/loadouts.db", this);

        } catch (SQLException e) {
            e.printStackTrace();
            getLogger().warning("Failed to initialize database, disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

    }


    public LoadoutService getLoadoutService() {
        return loadoutService;
    }

    public boolean getLoadoutState() {
        return loadoutState;
    }

    public void setLoadoutState(boolean loadoutState) {
        this.loadoutState = loadoutState;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
