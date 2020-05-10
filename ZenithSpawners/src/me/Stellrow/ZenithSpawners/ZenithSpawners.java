package me.Stellrow.ZenithSpawners;

import me.Stellrow.ZenithSpawners.commands.ZenithSpawnersCommands;
import me.Stellrow.ZenithSpawners.entitystacking.EntityStacker;
import me.Stellrow.ZenithSpawners.events.ZenithSpawnersEvent;
import me.Stellrow.ZenithSpawners.gui.ZenithSpawnersGuiHandle;
import me.Stellrow.ZenithSpawners.gui.ZenithSpawnersGuiListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class ZenithSpawners extends JavaPlugin {
    public boolean keepStacking;
    public boolean killAll;
    public boolean alwaysDropAmount;
    //Entity stacker
    private EntityStacker entityStacker;
    //Tier Upgrade Prices
    public HashMap<EntityType,EntityUpgrade> entityTierUpgrade = new HashMap<EntityType,EntityUpgrade>();
    //Tier drop list
    public HashMap<EntityType,EntityDrop> entityTierDrop = new HashMap<EntityType, EntityDrop>();
    //Economy
    public static Economy economy = null;
    private boolean hasEco = true;
    //GUI Handle
    public ZenithSpawnersGuiHandle gh;
    //Namespacedkey to identify spawners/mobs
    public NamespacedKey customSpawnerKey = new NamespacedKey(this,"ZenithSpawner");
    public void onEnable(){
        loadConfig();
        getServer().getPluginManager().registerEvents(new ZenithSpawnersEvent(this),this);
        getServer().getPluginManager().registerEvents(new ZenithSpawnersGuiListener(),this);
        getCommand("zenithspawners").setExecutor(new ZenithSpawnersCommands(this));
        getCommand("zs").setExecutor(new ZenithSpawnersCommands(this));
        findVault();
        checkEconomy();
        gh = new ZenithSpawnersGuiHandle(this,hasEco);
        buildTierUpgrade();
        buildTierDrop();
        entityStacker=new EntityStacker(this);
    }
    public void onDisable(){

    }
    private void loadConfig(){
        getConfig().options().copyDefaults(true);
        saveConfig();
        reloadValues();
    }
    public void reloadValues(){
        killAll = getConfig().getBoolean("General.killAllMobsAtOnce");
        keepStacking = getConfig().getBoolean("General.keepStackingMobs");
        alwaysDropAmount = getConfig().getBoolean("OneByOneKills.alwaysDropAmount");
    }



    //Try and find vault
    private void findVault() {
        if(getServer().getPluginManager().getPlugin("Vault")!=null&&getServer().getPluginManager().isPluginEnabled("Vault")) {
            setupEconomy();
            hasEco=true;
            getServer().getConsoleSender().sendMessage("[ZenithSpawners]"+ChatColor.GREEN+" Found Vault,hooking into economy");
            return;
        }
        getServer().getConsoleSender().sendMessage("[ZenithSpawners]"+ChatColor.RED+" Vault wasnt found! Level up using money feature is disabled!");
        return;
    }
    //Setup vault
    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
    //Check if a economy provider exists
    private void checkEconomy() {
        if(economy==null) {
            hasEco = false;
            getServer().getConsoleSender().sendMessage("[ZenithSpawners]"+ ChatColor.RED+" No economy provider was found,money based features are disabled");
            return;
        }
        getServer().getConsoleSender().sendMessage("[ZenithSpawners]"+ChatColor.GREEN+" Found economy provider,money based features are enabled!");
        return;
    }
    //Build tier upgrade prices
    private void buildTierUpgrade(){
        for(String s : getConfig().getConfigurationSection("SpawnerUpgradePrice").getKeys(false)){
            EntityUpgrade eu = new EntityUpgrade();
            for(String price : getConfig().getConfigurationSection("SpawnerUpgradePrice."+s).getKeys(false)){
            eu.addPrice((Integer.parseInt(price.split("Tier")[1])),getConfig().getInt("SpawnerUpgradePrice."+s+"."+price));
            }
            entityTierUpgrade.put(EntityType.valueOf(s),eu);

        }
    }
    //Utility TierUpgradeList
    public int getNextTierUpgradeCost(EntityType type,Integer currentTier){
        return entityTierUpgrade.get(type).returnNextTierPrice(currentTier);
    }
    //Build tier drop list
    private void buildTierDrop(){
        for(String s : getConfig().getConfigurationSection("SpawnerTierDrop").getKeys(false)){
            EntityDrop ed = new EntityDrop();
            for(String drop : getConfig().getConfigurationSection("SpawnerTierDrop."+s).getKeys(false)){
                ed.addDrop(Integer.parseInt(drop.split("Tier")[1]), Material.valueOf(getConfig().getString("SpawnerTierDrop."+s+"."+drop)));
            }
            entityTierDrop.put(EntityType.valueOf(s),ed);
        }
    }
    public Material getTierDrop(EntityType type,Integer currentTier){
        return entityTierDrop.get(type).returnDrop(currentTier);
    }
    //Utility config
    public String returnStringFromConfig(String path){
        return ChatColor.translateAlternateColorCodes('&',getConfig().getString(path));
    }

}
