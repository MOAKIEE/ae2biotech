package com.moakiee.ae2biotech.init;

import com.moakiee.ae2biotech.AE2BioTechnology;
import com.moakiee.ae2biotech.item.BioScannerItem;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Item registration for AE2 Bio-Technology mod.
 */
public class AE2BiotechItems {
    
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AE2BioTechnology.MODID);
    
    // Bio-Scanner: Handheld tool for scanning entities
    public static final DeferredItem<BioScannerItem> BIO_SCANNER = ITEMS.register(
            "bio_scanner",
            () -> new BioScannerItem(new Item.Properties())
    );
}
