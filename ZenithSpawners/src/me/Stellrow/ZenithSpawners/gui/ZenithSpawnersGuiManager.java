package me.Stellrow.ZenithSpawners.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ZenithSpawnersGuiManager implements InventoryHolder {
    private final Map<Integer, Icon> icons = new HashMap<>();
    private int size;
    private final String title;
    public ZenithSpawnersGuiManager(int size,String title){
    this.size=size;
    this.title=title;

    }
    public void setIcon(int position,@Nullable Icon icon) {
        if(icon==null) {
            if(this.icons.containsKey(position)) {
                this.icons.remove(position);
                return;
            }
            return;
        }
        this.icons.put(position, icon);
    }

    public interface GuiAction {
        void Execute(Player player);
    }
    public Icon getIcon(int position) {
        return this.icons.get(position);
    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, this.size,this.title);
        for(Map.Entry<Integer,Icon> entry : this.icons.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().itemstack);
        }
        return inventory;
    }

}
