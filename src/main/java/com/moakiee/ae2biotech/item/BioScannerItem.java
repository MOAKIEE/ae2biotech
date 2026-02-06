package com.moakiee.ae2biotech.item;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.ids.AEComponents;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.core.localization.Tooltips;
import appeng.items.AEBaseItem;

/**
 * Bio-Scanner - A handheld tool for scanning living entities.
 * 
 * Features:
 * - Uses AE2 energy system (compatible with FE via AE2's energy bridge)
 * - Can be charged in AE2 Charger
 * - Right-click on entities to scan (to be implemented)
 */
public class BioScannerItem extends AEBaseItem implements IAEItemPowerStorage {
    
    // 16 million AE capacity
    private static final double MAX_POWER = 16_000_000;
    
    public BioScannerItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        var storedEnergy = getAECurrentPower(stack);
        var energyCapacity = getAEMaxPower(stack);
        lines.add(Tooltips.energyStorageComponent(storedEnergy, energyCapacity));
    }
    
    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }
    
    @Override
    public int getBarWidth(ItemStack stack) {
        double filled = getAECurrentPower(stack) / getAEMaxPower(stack);
        return (int) Math.round(filled * 13);
    }
    
    @Override
    public int getBarColor(ItemStack stack) {
        // Green color for energy bar
        return 0x00FF00;
    }
    
    // ==================== IAEItemPowerStorage Implementation ====================
    
    @Override
    public double injectAEPower(ItemStack stack, double amount, Actionable mode) {
        double maxStorage = getAEMaxPower(stack);
        double currentStorage = getAECurrentPower(stack);
        double required = maxStorage - currentStorage;
        double overflow = Math.max(0, amount - required);
        
        if (mode == Actionable.MODULATE) {
            double toAdd = Math.min(amount, required);
            setAECurrentPower(stack, currentStorage + toAdd);
        }
        
        return overflow;
    }
    
    @Override
    public double extractAEPower(ItemStack stack, double amount, Actionable mode) {
        double currentStorage = getAECurrentPower(stack);
        double fulfillable = Math.min(amount, currentStorage);
        
        if (mode == Actionable.MODULATE) {
            setAECurrentPower(stack, currentStorage - fulfillable);
        }
        
        return fulfillable;
    }
    
    @Override
    public double getAEMaxPower(ItemStack stack) {
        return MAX_POWER;
    }
    
    @Override
    public double getAECurrentPower(ItemStack stack) {
        return stack.getOrDefault(AEComponents.STORED_ENERGY, 0.0);
    }
    
    private void setAECurrentPower(ItemStack stack, double power) {
        if (power < 0.0001) {
            stack.remove(AEComponents.STORED_ENERGY);
        } else {
            stack.set(AEComponents.STORED_ENERGY, power);
        }
    }
    
    @Override
    public AccessRestriction getPowerFlow(ItemStack stack) {
        return AccessRestriction.WRITE;
    }
    
    @Override
    public double getChargeRate(ItemStack stack) {
        // No charge rate limit - charge as fast as possible
        return Double.MAX_VALUE;
    }
}
