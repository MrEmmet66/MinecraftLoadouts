package org.mbf.mbfloadout.command;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mbf.mbfloadout.MbfLoadout;
import org.mbf.mbfloadout.database.model.Loadout;
import org.mbf.mbfloadout.database.model.PlayerLoadout;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;

@Command("loadout")
@Permission("loadout.command")
public class LoadoutCommand extends BaseCommand {
    private final MbfLoadout plugin;

    public LoadoutCommand(MbfLoadout plugin) {
        this.plugin = plugin;
    }

    @SubCommand("activate")
    @Permission("loadout.command.activate")
    public void activateLoadoutCommand(Player sender, boolean state) {
        plugin.setLoadoutState(state);
        sender.sendMessage(ChatColor.GREEN + "Loadout" + ChatColor.GRAY + " " + (state ? "enabled" : "disabled") + "!");
    }

    @SubCommand("view")
    public void viewLoadoutCommand(Player sender, @Suggestion("loadouts") String name) throws SQLException, IOException {
        PlayerLoadout playerLoadout = plugin.getLoadoutService().getPlayer(sender.getUniqueId().toString());
        Loadout loadout = playerLoadout.getLoadout(name);
        if(loadout == null) {
            sender.sendMessage(ChatColor.RED + "Loadout" + ChatColor.GRAY + " " + name + " " + ChatColor.RED + "does not exist!");
            return;
        }
        ItemStack[] itemStacks = playerLoadout.itemStackFromBase64(loadout.getItems());
        Inventory inventory = Bukkit.createInventory(null, 54, name);
        inventory.setContents(itemStacks);
        sender.openInventory(inventory);
    }

    @SubCommand("remove")
    public void clearLoadoutCommand(Player sender, @Suggestion("loadouts") String name) throws SQLException {
        PlayerLoadout playerLoadout = plugin.getLoadoutService().getPlayer(sender.getUniqueId().toString());
        Loadout loadout = playerLoadout.getLoadout(name);
        playerLoadout.getLoadouts().remove(loadout);
        plugin.getLoadoutService().updatePlayer(playerLoadout);
        sender.sendMessage(ChatColor.GREEN + "Loadout" + ChatColor.GRAY + " " + name + " " + ChatColor.GREEN + "removed!");
    }

    @SubCommand("save")
    public void saveLoadoutCommand(Player sender, @Suggestion("loadouts") String name) throws SQLException {
        PlayerLoadout playerLoadout = plugin.getLoadoutService().getPlayer(sender.getUniqueId().toString());
        ItemStack[] itemStacks = sender.getInventory().getContents();
        String items = playerLoadout.itemStackToBase64(itemStacks);
        Loadout loadout = new Loadout(name, items);
        HashSet<Loadout> loadouts = playerLoadout.getLoadouts();
        if(playerLoadout.getLoadout(name) != null) {
            loadouts.remove(playerLoadout.getLoadout(name));
        }
        loadouts.add(loadout);
        playerLoadout.setLoadouts(loadouts);
        plugin.getLoadoutService().updatePlayer(playerLoadout);
        sender.sendMessage(ChatColor.GREEN + "Loadout" + ChatColor.GRAY + " " + name + " " + ChatColor.GREEN + "saved!");
    }

    @SubCommand("set")
    public void setLoadoutCommand(Player sender, @Suggestion("loadouts") String name) throws SQLException {
        PlayerLoadout playerLoadout = plugin.getLoadoutService().getPlayer(sender.getUniqueId().toString());
        Loadout loadout = playerLoadout.getLoadout(name);
        if(loadout == null) {
            sender.sendMessage(ChatColor.RED + "Loadout" + ChatColor.GRAY + " " + name + " " + ChatColor.RED + "does not exist!");
            return;
        }
        playerLoadout.setActiveLoadoutName(name);
        plugin.getLoadoutService().updatePlayer(playerLoadout);
        sender.sendMessage(ChatColor.GREEN + "Loadout" + ChatColor.GRAY + " " + name + " " + ChatColor.GREEN + "set!");
    }

}
