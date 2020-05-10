package me.Stellrow.ZenithSpawners.gui;

import me.Stellrow.ZenithSpawners.ZenithSpawners;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import javax.annotation.Nullable;

public class ZenithSpawnersGuiHandle {
    private final ZenithSpawners pl;
    private boolean hasEco = false;
    ZenithSpawnersGuiManager gm;
    public ZenithSpawnersGuiHandle(ZenithSpawners pl,boolean hasEco) {
        this.hasEco=hasEco;
        gm = new ZenithSpawnersGuiManager(27,"Spawner");
        this.pl = pl;
    }

    public void loadAndCreateInventory(String[] spawnerInformation, Player whoClicked){
        Icon type = new Icon(buildItem(Material.valueOf(spawnerInformation[1]+"_SPAWN_EGG"),1,ChatColor.AQUA+"Type: "+spawnerInformation[1],null));
        Icon stackQuantity = new Icon(buildItem(Material.SPAWNER,1, ChatColor.GRAY+"Spawners Stacked: "+spawnerInformation[2],null));
        Icon tier = new Icon(buildItem(Material.EMERALD,1,ChatColor.GOLD+"Tier: "+spawnerInformation[3],null));
        Icon upgradeTier = new Icon(buildItem(Material.EXPERIENCE_BOTTLE,1,ChatColor.GREEN+"Next tier cost: "+pl.getNextTierUpgradeCost(EntityType.valueOf(spawnerInformation[1]),Integer.parseInt(spawnerInformation[3])),null));
        gm.setIcon(4,type);
        gm.setIcon(12,stackQuantity);
        gm.setIcon(14,tier);
        gm.setIcon(23,upgradeTier);
        Inventory inv = gm.getInventory();

        //Add filling
        for(int x =0;x<inv.getSize();x++) {
            if(inv.getItem(x)==null) {
                inv.setItem(x, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
            }
        }
        whoClicked.openInventory(inv);
    }

    private ItemStack buildItem(Material mat, Integer amount, @Nullable String name, @Nullable List<String> lore){
    ItemStack toReturn = new ItemStack(mat,amount);
    ItemMeta im = toReturn.getItemMeta();
    if(name!=null){
        im.setDisplayName(name);
    }
    if(lore!=null){
        im.setLore(lore);
    }
    toReturn.setItemMeta(im);
    return toReturn;

    }
}
