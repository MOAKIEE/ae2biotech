package com.moakiee.ae2biotech;

import org.slf4j.Logger;

import com.moakiee.ae2biotech.init.AE2BiotechCreativeTab;
import com.moakiee.ae2biotech.init.AE2BiotechItems;
import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

/**
 * AE2 Bio-Technology - Main Mod Class
 * 
 * Digitizes creature genetics into programmable chips, enabling simulated drop farming
 * and the replication of genetically enhanced entities.
 */
@Mod(AE2BioTechnology.MODID)
public class AE2BioTechnology {
    
    public static final String MODID = "ae2biotech";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AE2BioTechnology(IEventBus modEventBus, ModContainer modContainer) {
        // Register items
        AE2BiotechItems.ITEMS.register(modEventBus);
        
        // Register creative mode tab
        AE2BiotechCreativeTab.CREATIVE_TABS.register(modEventBus);
        
        // Register data components
        com.moakiee.ae2biotech.init.AE2BiotechComponents.DATA_COMPONENTS.register(modEventBus);
        
        LOGGER.info("AE2 Bio-Technology initializing...");
    }
}

