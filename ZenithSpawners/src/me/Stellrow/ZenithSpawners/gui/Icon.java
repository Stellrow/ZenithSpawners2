package me.Stellrow.ZenithSpawners.gui;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.ArrayList;

public class Icon {
    public final ItemStack itemstack;
    public final List<ZenithSpawnersGuiManager.GuiAction> guiActions = new ArrayList<>();

    public Icon(ItemStack icon) {
        this.itemstack=icon;
    }
    public Icon addClickAction(ZenithSpawnersGuiManager.GuiAction gAction) {
        this.guiActions.add(gAction);
        return this;
    }
    public List<ZenithSpawnersGuiManager.GuiAction> getActions(){
        return guiActions;
    }
}
