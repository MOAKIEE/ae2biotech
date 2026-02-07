package com.moakiee.ae2biotech.item;

import java.util.List;
import java.util.UUID;

import com.moakiee.ae2biotech.component.EntityData;
import com.moakiee.ae2biotech.init.AE2BiotechComponents;
import com.moakiee.ae2biotech.init.AE2BiotechItems;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

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
 * - Right-click on entities to scan (costs 1M AE and 1 Blank Chip)
 */
public class BioScannerItem extends AEBaseItem implements IAEItemPowerStorage {
    
    // 16 million AE capacity
    private static final double MAX_POWER = 16_000_000;
    private static final double SCAN_COST = 1_000_000;
    
    public BioScannerItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand hand) {
        // 0. Check Cooldown
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResult.FAIL;
        }

        // 1. Check Energy (Dual side verify or Server only?)
        // Better to check on both sides to predict failure
        if (getAECurrentPower(stack) < SCAN_COST) {
            player.displayClientMessage(Component.translatable("message.ae2biotech.low_energy"), true);
            return InteractionResult.FAIL;
        }
        
        // 2. Check for Blank Bio-Data Chip
        if (!hasBlankChip(player)) {
            player.displayClientMessage(Component.translatable("message.ae2biotech.missing_chip"), true);
            return InteractionResult.FAIL;
        }
        
        // 3. Start Scanning
        // Update stack on both sides to ensure sync and prevent item mismatch during usage
        stack.set(AE2BiotechComponents.SCAN_TARGET_ID, interactionTarget.getId());
        player.startUsingItem(hand);
        
        return InteractionResult.CONSUME;
    }
    
    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (!(livingEntity instanceof Player player)) return;
        
        // Retrieve Target ID
        Integer targetId = stack.get(AE2BiotechComponents.SCAN_TARGET_ID);
        if (targetId == null) {
            player.stopUsingItem();
            return;
        }
        
        Entity target = level.getEntity(targetId);
        
        // Check if target is valid and still in range (5 blocks - valid reach)
        if (target == null || !target.isAlive() || player.distanceToSqr(target) > 25.0) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.ae2biotech.scan_failed_moved"), true);
            }
            player.stopUsingItem();
            return;
        }
        
        // Optional: Show progress (every second)
        if (!level.isClientSide && remainingUseDuration % 20 == 0) {
            player.displayClientMessage(Component.translatable("message.ae2biotech.scan_started"), true);
        }
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (level.isClientSide || !(livingEntity instanceof Player player)) return stack;
        
        // Retrieve Target ID
        Integer targetId = stack.get(AE2BiotechComponents.SCAN_TARGET_ID);
        if (targetId == null) return stack;
        
        Entity target = level.getEntity(targetId);
        
        // Final Validation
        if (target != null && target.isAlive() && player.distanceToSqr(target) <= 25.0) {
            // Check resources again
            if (getAECurrentPower(stack) >= SCAN_COST && consumeBlankChip(player)) {
                // Deduct Energy
                extractAEPower(stack, SCAN_COST, Actionable.MODULATE);
                
                // Create Encrypted Chip
                CompoundTag entityNbt = new CompoundTag();
                target.save(entityNbt);
                
                ItemStack encryptedChip = new ItemStack(AE2BiotechItems.ENCRYPTED_BIO_DATA_CHIP.get());
                encryptedChip.set(AE2BiotechComponents.ENTITY_DATA, new EntityData(
                        EntityType.getKey(target.getType()),
                        entityNbt,
                        target.getDisplayName(),
                        UUID.randomUUID()
                ));
                
                // Give chip to player
                if (!player.getInventory().add(encryptedChip)) {
                    player.drop(encryptedChip, false);
                }
                
                player.displayClientMessage(Component.translatable("message.ae2biotech.scan_complete"), true);
                
                // Add cooldown to prevent accidental re-triggering and "Missing Chip" message
                player.getCooldowns().addCooldown(this, 10); // 0.5s cooldown
            }
        } else {
             player.displayClientMessage(Component.translatable("message.ae2biotech.scan_failed_moved"), true);
        }
        
        // Clean up
        stack.remove(AE2BiotechComponents.SCAN_TARGET_ID);
        
        return stack;
    }
    
    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        stack.remove(AE2BiotechComponents.SCAN_TARGET_ID);
        if (!level.isClientSide && livingEntity instanceof Player player) {
            player.displayClientMessage(Component.translatable("message.ae2biotech.scan_cancelled"), true);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 100; // 5 seconds
    }
    
    @Override
    public net.minecraft.world.item.UseAnim getUseAnimation(ItemStack stack) {
        return net.minecraft.world.item.UseAnim.BOW;
    }
    
    private boolean hasBlankChip(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() == AE2BiotechItems.BLANK_BIO_DATA_CHIP.get()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean consumeBlankChip(Player player) {
        // Find and remove one blank chip
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack slotStack = player.getInventory().getItem(i);
            if (slotStack.getItem() == AE2BiotechItems.BLANK_BIO_DATA_CHIP.get()) {
                slotStack.shrink(1);
                return true;
            }
        }
        return false;
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
