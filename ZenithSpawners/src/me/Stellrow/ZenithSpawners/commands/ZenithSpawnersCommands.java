package me.Stellrow.ZenithSpawners.commands;

import me.Stellrow.ZenithSpawners.ZenithSpawners;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ZenithSpawnersCommands implements CommandExecutor {
    private final ZenithSpawners pl;

    public ZenithSpawnersCommands(ZenithSpawners pl) {
        this.pl = pl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lab, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if(args.length==1){
                if(args[0].equalsIgnoreCase("debug")){
                    Block b = p.getTargetBlock(null,10);
                    if(b.getType()==Material.SPAWNER){
                        CreatureSpawner cs = (CreatureSpawner) b.getState();
                        if(cs.getPersistentDataContainer().has(pl.customSpawnerKey, PersistentDataType.STRING)){
                            String raw = cs.getPersistentDataContainer().get(pl.customSpawnerKey,PersistentDataType.STRING);
                            Integer stacks = Integer.parseInt(raw.split(" ")[2])+1;
                            cs.getPersistentDataContainer().set(pl.customSpawnerKey,PersistentDataType.STRING,"ZenithSpawner "+cs.getSpawnedType().toString()+ " "+stacks+ " 1");
                            cs.update();
                            p.sendMessage("Updated!");
                        }
                    }
                    return true;
                }
            }
            if (args.length >= 4) {
                if (args[0].equalsIgnoreCase("give")) {
                    if (p.hasPermission("zenithspawners.give")) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target == null) {
                            p.sendMessage(ChatColor.RED + "Player not online or not found!");
                            return true;
                        }
                        EntityType type = EntityType.valueOf(args[2].toUpperCase());
                        int amount = 1;
                        int tier =1;
                            try {
                                tier=Integer.parseInt(args[4]);
                                amount = Integer.parseInt(args[3]);
                            } catch (NumberFormatException e) {
                                p.sendMessage(ChatColor.RED + "Not a number!");
                                return true;
                            }

                        p.sendMessage(ChatColor.GREEN+"Successfully gave a spawner to that player!");
                        giveSpawner(target,type,amount,tier);

                    }
                }

            }


        }
        return true;
    }
    private void giveSpawner(Player p, EntityType type,Integer amount,Integer tier){
        ItemStack spawner = new ItemStack(Material.SPAWNER,amount);
        ItemMeta im = spawner.getItemMeta();
        im.setDisplayName(ChatColor.GOLD+type.toString()+ChatColor.GRAY+" Spawner Tier "+tier);
        im.getPersistentDataContainer().set(pl.customSpawnerKey,PersistentDataType.STRING,"ZenithSpawner "+type.toString()+ " 1" + " "+tier+" 0");

        spawner.setItemMeta(im);
        p.getInventory().addItem(spawner);


    }
}
