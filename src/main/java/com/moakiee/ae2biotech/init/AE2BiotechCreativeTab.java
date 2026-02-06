package com.moakiee.ae2biotech.init;

import com.moakiee.ae2biotech.AE2BioTechnology;

import appeng.api.config.Actionable;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Creative mode tab registration for AE2 Bio-Technology mod.
 */
public class AE2BiotechCreativeTab {
    
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = 
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AE2BioTechnology.MODID);
    
    /**
     * Main creative tab for AE2 Bio-Technology mod items.
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AE2_BIOTECH_TAB = 
            CREATIVE_TABS.register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.ae2biotech.main"))
                    .icon(() -> new ItemStack(AE2BiotechItems.BIO_SCANNER.get()))
                    .displayItems((parameters, output) -> {
                        // Bio-Scanner (empty)
                        output.accept(AE2BiotechItems.BIO_SCANNER.get());
                        
                        // Bio-Scanner (fully charged)
                        var chargedScanner = new ItemStack(AE2BiotechItems.BIO_SCANNER.get());
                        AE2BiotechItems.BIO_SCANNER.get().injectAEPower(chargedScanner, 
                                AE2BiotechItems.BIO_SCANNER.get().getAEMaxPower(chargedScanner), 
                                Actionable.MODULATE);
                        output.accept(chargedScanner);
                    })
                    .build());
}

