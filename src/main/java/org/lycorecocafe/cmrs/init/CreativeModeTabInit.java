package org.lycorecocafe.cmrs.init;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CreativeModeTabInit {
    public static final CreativeModeTab CMRS_TAB = new CreativeModeTab("cmrs_tab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Items.REDSTONE);
        }
    };
}
