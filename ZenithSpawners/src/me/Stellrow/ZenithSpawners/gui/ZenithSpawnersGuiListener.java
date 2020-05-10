package me.Stellrow.ZenithSpawners.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ZenithSpawnersGuiListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(e.getView().getTopInventory().getHolder() instanceof ZenithSpawnersGuiManager) {
            e.setCancelled(true);
            if(e.getWhoClicked() instanceof Player) {
                Player p = (Player) e.getWhoClicked();
                ItemStack item = e.getCurrentItem();
                if(item==null||item.getType()== Material.AIR) {
                    return;
                }
                ZenithSpawnersGuiManager gm = (ZenithSpawnersGuiManager) e.getView().getTopInventory().getHolder();
                Icon icon = gm.getIcon(e.getRawSlot());
                if(icon==null)return;
                for(ZenithSpawnersGuiManager.GuiAction guiAction : icon.getActions()) {
                    guiAction.Execute(p);
                }
            }
        }
    }
}
