package com.moakiee.ae2biotech.item;

import net.minecraft.world.item.Item;

/**
 * Blank Bio-Data Chip - Basic consumable item.
 * 
 * Used to receive scanned entity data when placed in player inventory.
 * The scanner will write data to this chip, converting it into an Encrypted Bio-Data Chip.
 */
public class BlankBioDataChipItem extends Item {
    
    public BlankBioDataChipItem(Properties properties) {
        super(properties.stacksTo(64));
    }
}
