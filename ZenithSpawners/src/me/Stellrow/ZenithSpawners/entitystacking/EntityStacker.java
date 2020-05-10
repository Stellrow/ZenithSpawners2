package me.Stellrow.ZenithSpawners.entitystacking;

import me.Stellrow.ZenithSpawners.ZenithSpawners;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

public class EntityStacker {

    private final ZenithSpawners pl;
    public int maxStackSize;

    public EntityStacker(ZenithSpawners pl) {
        this.pl = pl;
        maxStackSize = pl.getConfig().getInt("General.maxStackSize");
        startRunnable();
    }
    private void startRunnable(){
        //Check nearby same entities
        new BukkitRunnable(){

            @Override
            public void run() {
                if(!pl.keepStacking){
                    return;
                }
                for(World wr : Bukkit.getWorlds()){
                    for(Entity e : wr.getEntities()){
                        if(e.getCustomName()!=null){
                            String name = e.getCustomName();
                            if(name.contains("Tier")) {
                                if (name.split(" ").length == 4) {
                                    String[] trs = name.split(" ");
                                    String number = trs[0];
                                    Integer nr = Integer.parseInt(ChatColor.stripColor(number.split("X")[0]));
                                    if (nr < maxStackSize) {


                                        String type = trs[1];
                                        String tier = trs[2];
                                        Integer tierNr = Integer.parseInt(ChatColor.stripColor(trs[3]));
                                        for (Entity nrby : e.getNearbyEntities(5, 5, 5)) {
                                            if (nrby.getCustomName() != null) {
                                                String nrbyName = nrby.getName();
                                                String[] nrbyTrs = nrbyName.split(" ");
                                                String nbnumber = nrbyTrs[0];
                                                Integer nrbnr = Integer.parseInt(ChatColor.stripColor(nbnumber.split("X")[0]));
                                                String nrbtype = nrbyTrs[1];
                                                Integer nrbTier = Integer.parseInt(nrbyTrs[3]);
                                                if(nrbnr<maxStackSize) {
                                                    if (type.equalsIgnoreCase(nrbtype)) {
                                                        if (tierNr == nrbTier) {
                                                            morphEntity(e, nrby, type, nr, nrbnr, tierNr);
                                                            return;
                                                        }
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }
                            }


                        }
                    }
                }

            }
        }.runTaskTimer(pl,0,5*20);
    }
    private void morphEntity(Entity toMorph,Entity result,String type,Integer toMorphNumber,Integer secondMorphNumber,Integer tier){
    if(toMorphNumber+secondMorphNumber>500){
        Integer remaining = toMorphNumber+secondMorphNumber-500;
        toMorph.setCustomName(ChatColor.GREEN+""+500+"X "+ChatColor.GRAY+type+ChatColor.GOLD+" Tier "+tier);
        result.setCustomName(ChatColor.GREEN+""+remaining+"X "+ChatColor.GRAY+type+ChatColor.GOLD+" Tier "+tier);
        return;
    }
    toMorph.remove();
    Integer remaining = toMorphNumber+secondMorphNumber;
    result.setCustomName(ChatColor.GREEN+""+remaining+"X "+ChatColor.GRAY+type+ChatColor.GOLD+" Tier "+tier);
    return;
    }
}
