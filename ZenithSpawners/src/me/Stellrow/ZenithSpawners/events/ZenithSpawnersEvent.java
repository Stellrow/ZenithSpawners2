package me.Stellrow.ZenithSpawners.events;

import me.Stellrow.ZenithSpawners.ZenithSpawners;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Random;

public class ZenithSpawnersEvent implements Listener {
    private final ZenithSpawners pl;

    public ZenithSpawnersEvent(ZenithSpawners pl) {
        this.pl = pl;
    }

    @EventHandler
    public void onRightClickSpawner(PlayerInteractEvent e){
        if(e.getAction()== Action.RIGHT_CLICK_BLOCK) {
            if (e.getClickedBlock().getType() == Material.SPAWNER) {
                e.setCancelled(true);
                //Check if existing spawner
                checkOrCreateSpawner(e.getClickedBlock(),e.getPlayer());
            }
        }
    }
    //Name type stack tier
    @EventHandler
    public void onSpawn(SpawnerSpawnEvent e){
        CreatureSpawner cs = e.getSpawner();
        if(cs.getPersistentDataContainer().has(pl.customSpawnerKey,PersistentDataType.STRING)){
            String raw = cs.getPersistentDataContainer().get(pl.customSpawnerKey,PersistentDataType.STRING);
            String[] zenithSpawner = raw.split(" ");
            cs.setSpawnCount(1);
            cs.update();
            //500x Pig Tier 2
            e.getEntity().setCustomName(ChatColor.GREEN+zenithSpawner[2]+"X "+ChatColor.GRAY+zenithSpawner[1]+ChatColor.GOLD+" Tier "+zenithSpawner[3]);

        }
    }
    @EventHandler
    public void onZenithCreatureDeath(EntityDeathEvent e){
        if(e.getEntity().getCustomName()!=null){
            String name = e.getEntity().getCustomName();

            String[] raw = name.split(" ");
            //Anti exploit
            if(!EntityType.valueOf(ChatColor.stripColor(raw[1])).equals(e.getEntityType())){
                return;
            }
            //
            if(raw[2].contains("Tier")){
                //TODO One by one killing
                if(!pl.killAll){
                    //Entity and information about entity
                    killOne(e.getEntity(),raw,e.getEntity().getKiller().getInventory().getItemInMainHand());
                    return;
                }

                e.getDrops().clear();
                try {

                    Integer quantity = Integer.parseInt(ChatColor.stripColor(ChatColor.stripColor(raw[0].split("X")[0])));
                    //Looting modifier
                    int multiply = returnLootingLevel(e.getEntity().getKiller().getInventory().getItemInMainHand());
                    switch (multiply){
                        case 0:
                            break;
                        case 1:
                            quantity+=((int)(quantity*(15.0f/100.0f)));
                            break;
                        case 2:
                            quantity+=((int)(quantity*(25.0f/100.0f)));;
                            break;
                        case 3:
                            quantity+=((int)(quantity*(35.0f/100.0f)));;
                            break;
                        case 4:
                            quantity+=((int)(quantity*(45.0f/100.0f)));;
                            break;
                    }
                    ItemStack toDrop = new ItemStack(pl.getTierDrop(e.getEntityType(),Integer.parseInt(raw[3])));
                    while(quantity>=64){
                        e.getDrops().add(new ItemStack(toDrop.getType(),64));
                        quantity-=64;
                    }
                    toDrop.setAmount(quantity);
                    e.getDrops().add(toDrop);
                } catch (NumberFormatException numberFormatException) {
                    return;
                }
                catch(IllegalArgumentException ex){
                    return;
                }
            }
        }
    }
    public void detectNearbySpawners(BlockPlaceEvent e){
        if(e.getItemInHand().getType()==Material.SPAWNER){
            if(hasSameSpawnerNearby(e.getItemInHand(),e.getBlockPlaced().getLocation())){
                setToAir(e.getBlockPlaced());
            }
        }
    }
    //Place Spawner
    @EventHandler
    public void onZenithSpawnerPlaceing(BlockPlaceEvent e){
        EquipmentSlot hand = e.getHand();
        if(e.getBlockPlaced().getType()==Material.SPAWNER) {
            if (hand == EquipmentSlot.HAND) {
                ItemStack spawner = e.getPlayer().getInventory().getItemInMainHand();
                if (spawner.hasItemMeta()) {
                    if (spawner.getItemMeta().hasDisplayName()) {
                        String name = spawner.getItemMeta().getDisplayName();
                        if (name.contains("Spawner")) {
                            CreatureSpawner cs = (CreatureSpawner) e.getBlockPlaced().getState();
                            cs.setSpawnedType(EntityType.valueOf(ChatColor.stripColor(name.split(" ")[0].toUpperCase())));
                            cs.update();
                            e.getPlayer().sendMessage(pl.returnStringFromConfig("Messages.placeSpawner").replaceAll("%type",ChatColor.stripColor(name.split(" ")[0].toUpperCase())));
                            if(spawner.getItemMeta().getPersistentDataContainer().has(pl.customSpawnerKey,PersistentDataType.STRING)){
                                String raw = spawner.getItemMeta().getPersistentDataContainer().get(pl.customSpawnerKey,PersistentDataType.STRING);
                                setPersistent(e.getBlockPlaced(),raw);

                            }else {
                                setPersistent(e.getBlockPlaced(),"ZenithSpawner " + cs.getSpawnedType().toString() + " 1" + " 1"+" 0");
                            }
                        }
                    }
                }
                return;
            }
            if (hand == EquipmentSlot.OFF_HAND) {
                ItemStack spawner = e.getPlayer().getInventory().getItemInOffHand();
                if (spawner.hasItemMeta()) {
                    if (spawner.getItemMeta().hasDisplayName()) {
                        String name = spawner.getItemMeta().getDisplayName();
                        if (name.contains("Spawner")) {
                            CreatureSpawner cs = (CreatureSpawner) e.getBlockPlaced().getState();
                            cs.setSpawnedType(EntityType.valueOf(ChatColor.stripColor(name.split(" ")[0].toUpperCase())));
                            cs.update();
                            e.getPlayer().sendMessage(pl.returnStringFromConfig("Messages.placeSpawner").replaceAll("%type",ChatColor.stripColor(name.split(" ")[0].toUpperCase())));
                            if(spawner.getItemMeta().getPersistentDataContainer().has(pl.customSpawnerKey,PersistentDataType.STRING)){
                                String raw = spawner.getItemMeta().getPersistentDataContainer().get(pl.customSpawnerKey,PersistentDataType.STRING);
                                setPersistent(e.getBlockPlaced(),raw);

                            }else {
                                setPersistent(e.getBlockPlaced(),"ZenithSpawner " + cs.getSpawnedType().toString() + " 1" + " 1"+" 0");
                            }
                        }
                    }
                }
                return;
            }
        }
    }
    //Remove spawner with silktouch
    @EventHandler
    public void onBreakEvent(BlockBreakEvent e){
        if(e.getBlock().getType()==Material.SPAWNER){
        if(pl.getConfig().getBoolean("General.canMineWithSilkTouch")){
            if(e.getPlayer().getInventory().getItemInMainHand()!=null&&e.getPlayer().getInventory().getItemInMainHand().getType().toString().endsWith("_PICKAXE")){
                ItemStack tool = e.getPlayer().getInventory().getItemInMainHand();
                if(tool.hasItemMeta()){
                    if(tool.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)){
                        CreatureSpawner cs = (CreatureSpawner) e.getBlock().getState();
                        e.setDropItems(false);
                        e.setExpToDrop(0);
                            ItemStack spawner = new ItemStack(Material.SPAWNER);
                            ItemMeta im = spawner.getItemMeta();
                            im.setDisplayName(ChatColor.GOLD+cs.getSpawnedType().toString()+ChatColor.GRAY+" Spawner");
                            if(cs.getPersistentDataContainer().has(pl.customSpawnerKey,PersistentDataType.STRING)){
                            String raw = cs.getPersistentDataContainer().get(pl.customSpawnerKey,PersistentDataType.STRING);
                            String[] zenithSpawner = raw.split(" ");
                            im.getPersistentDataContainer().set(pl.customSpawnerKey,PersistentDataType.STRING,"ZenithSpawner "+cs.getSpawnedType().toString()+ " 1" + " "+zenithSpawner[3]+" 0");
                                im.setDisplayName(ChatColor.GOLD+cs.getSpawnedType().toString()+ChatColor.GRAY+" Spawner Tier "+ zenithSpawner[3]);
                                spawner.setAmount(Integer.parseInt(zenithSpawner[2]));
                            }
                            spawner.setItemMeta(im);
                            e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(),spawner);
                            return;
                        }
                    }
                }
            }
        }

        }



    private void checkOrCreateSpawner(Block b, Player player){
        if(b.getType()==Material.SPAWNER){
            CreatureSpawner cs = (CreatureSpawner) b.getState();
            if(cs.getPersistentDataContainer().has(pl.customSpawnerKey, PersistentDataType.STRING)){
                String[] information = cs.getPersistentDataContainer().get(pl.customSpawnerKey,PersistentDataType.STRING).split(" ");
                pl.gh.loadAndCreateInventory(information,player);
            return;
            }
             // 0ZenithSpawner 1TYPE 2STACK 3TIER 4hologram(0/1)
            cs.getPersistentDataContainer().set(pl.customSpawnerKey,PersistentDataType.STRING,"ZenithSpawner "+cs.getSpawnedType().toString()+ " 1"+ " 1"+" 0");
            cs.update();
            return;

        }
    }
    private void setPersistent(Block b,String toSet){
        new BukkitRunnable(){

            @Override
            public void run() {
                if(!(b.getState() instanceof CreatureSpawner)){
                    return;
                }
            CreatureSpawner cs = (CreatureSpawner) b.getState();
            cs.getPersistentDataContainer().set(pl.customSpawnerKey,PersistentDataType.STRING,toSet);
            cs.update();
            }
        }.runTaskLater(pl,5);
    }

    private boolean hasSameSpawnerNearby(ItemStack spawner, Location blockLocation){
        Location min = new Location(blockLocation.getWorld(),blockLocation.getX()-2,blockLocation.getY()-2,blockLocation.getZ()-2);
        Location max = new Location(blockLocation.getWorld(),blockLocation.getX()+2,blockLocation.getY()+2,blockLocation.getZ()+2);
        //loop in a radius of 2 around the center
        for (int x = (int) min.getX(); x <= (int) max.getX(); x++) {
            for (int z = (int) min.getZ(); z <= (int) max.getZ(); z++) {
                for(int y = (int)min.getY();y<=(int)max.getY();y++) {
                    Location toCheck = new Location(blockLocation.getWorld(), x, y, z);
                    if (!toCheck.equals(blockLocation)) {

                    if (toCheck.getBlock().getType() == Material.SPAWNER) {
                        CreatureSpawner cs = (CreatureSpawner) toCheck.getBlock().getState();
                        if (cs.getPersistentDataContainer().has(pl.customSpawnerKey, PersistentDataType.STRING)) {

                            String[] raw = cs.getPersistentDataContainer().get(pl.customSpawnerKey, PersistentDataType.STRING).split(" ");
                            if (Integer.parseInt(spawner.getItemMeta().getPersistentDataContainer().get(pl.customSpawnerKey, PersistentDataType.STRING).split(" ")[3]) == Integer.parseInt(raw[3])) {


                                Integer stack = Integer.parseInt(raw[2]) + 1;
                                String toSet = raw[0] + " " + raw[1]+ " "+stack+" "+raw[3]+" 0";
                                cs.getPersistentDataContainer().set(pl.customSpawnerKey, PersistentDataType.STRING, toSet);
                                cs.update();
                                return true;
                            }
                        }
                    }

                }
                }
            }
            }
    return false;
    }
    //Tile entity cords error fix
    private void setToAir(Block b){
        new BukkitRunnable(){

            @Override
            public void run() {
                b.setType(Material.AIR);
            }
        }.runTaskLater(pl,1);
    }
    private int returnLootingLevel(ItemStack itemUsed){
        if(itemUsed==null){
            return 0;
        }
        if(!itemUsed.hasItemMeta()){
            return 0;
        }
        if(itemUsed.containsEnchantment(Enchantment.LOOT_BONUS_MOBS)){
            return itemUsed.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
        }

        return 0;
    }

    private void killOne(Entity e,String[] raw,ItemStack toolUsed){
    Integer currentNumber = Integer.parseInt(ChatColor.stripColor(raw[0].split("X")[0]));
    ItemStack toDrop = new ItemStack(pl.getTierDrop(e.getType(),Integer.parseInt(raw[3])));
    if(currentNumber>1){
        currentNumber--;
        e.setCustomName(ChatColor.GREEN+""+currentNumber+"X "+ ChatColor.GRAY+raw[1] +ChatColor.GOLD+ " Tier "+raw[3]);
        return;
    }
    dropNaturally(e,toDrop,toolUsed);
    return;
    }
    private void dropNaturally(Entity e,ItemStack toDrop,ItemStack toolUsed){
        ItemStack modified = toDrop;
        int lootingLevel = returnLootingLevel(toolUsed);
        if(!pl.alwaysDropAmount){
            modified.setAmount(returnRandom());
        }else{
            modified.setAmount(returnLootingAmount(lootingLevel));
        }
    e.getWorld().dropItemNaturally(e.getLocation(),modified);
    }

    private int returnRandom(){
        Random rnd = new Random();
        return rnd.nextInt(3)+1;
    }
    private int returnLootingAmount(int lootingLevel){
        int toRet = 1;
        switch (lootingLevel){
            case 0:break;
            case 1:
                return 1;
            case 2:
                return pl.getConfig().getInt("OneByOneKills.looting2");
            case 3:
                return pl.getConfig().getInt("OneByOneKills.looting3");
            case 4:
                return pl.getConfig().getInt("OneByOneKills.looting4");
        }
        return toRet;
    }
}
