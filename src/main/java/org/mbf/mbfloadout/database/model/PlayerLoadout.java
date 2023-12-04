package org.mbf.mbfloadout.database.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;

@DatabaseTable(tableName = "player_loadout")
public class PlayerLoadout {

    @DatabaseField(id = true)
    private String uuid;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashSet<Loadout> loadouts;

    @DatabaseField
    private String activeLoadoutName;

    public String getActiveLoadoutName() {
        return activeLoadoutName;
    }

    public void setActiveLoadoutName(String activeLoadoutName) {
        this.activeLoadoutName = activeLoadoutName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public HashSet<Loadout> getLoadouts() {
        return loadouts;
    }

    public void setLoadouts(HashSet<Loadout> loadouts) {
        this.loadouts = loadouts;
    }

    public Loadout getLoadout(String name) {
        return loadouts.stream().filter(loadout -> loadout.getName().equals(name)).findFirst().orElse(null);

    }

    public void applyLoadout(Player player) throws IOException {
        Loadout loadout = getLoadout(activeLoadoutName);
        if(loadout == null) {
            return;
        }
        player.getInventory().setContents(itemStackFromBase64(loadout.getItems()));
    }

    public String itemStackToBase64(ItemStack[] itemStacks) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(itemStacks.length);
            for (ItemStack itemStack : itemStacks) {
                dataOutput.writeObject(itemStack);
            }
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ItemStack[] itemStackFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] itemStacks = new ItemStack[dataInput.readInt()];
            for (int i = 0; i < itemStacks.length; i++) {
                itemStacks[i] = (ItemStack) dataInput.readObject();
            }
            dataInput.close();
            return itemStacks;
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }
}
