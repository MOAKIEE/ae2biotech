package com.moakiee.ae2biotech.init;

import com.moakiee.ae2biotech.AE2BioTechnology;
import com.moakiee.ae2biotech.item.BioScannerItem;
import com.moakiee.ae2biotech.item.BlankBioDataChipItem;
import com.moakiee.ae2biotech.item.EncryptedBioDataChipItem;

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
    
    // Blank Bio-Data Chip: Basic consumable for receiving scanned data
    public static final DeferredItem<BlankBioDataChipItem> BLANK_BIO_DATA_CHIP = ITEMS.register(
            "blank_bio_data_chip",
            () -> new BlankBioDataChipItem(new Item.Properties())
    );

    // Encrypted Bio-Data Chip: Holds entity data
    public static final DeferredItem<EncryptedBioDataChipItem> ENCRYPTED_BIO_DATA_CHIP = ITEMS.register(
            "encrypted_bio_data_chip",
            () -> new EncryptedBioDataChipItem(new Item.Properties())
    );
}

