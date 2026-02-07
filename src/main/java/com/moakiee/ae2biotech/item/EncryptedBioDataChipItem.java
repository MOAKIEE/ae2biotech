package com.moakiee.ae2biotech.item;

import java.util.List;

import com.moakiee.ae2biotech.component.EntityData;
import com.moakiee.ae2biotech.init.AE2BiotechComponents;
import com.moakiee.ae2biotech.init.AE2BiotechItems;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * Encrypted Bio-Data Chip - Contains scanned entity data that needs to be decrypted.
 * 
 * This is the intermediate product after scanning an entity.
 * It stores the complete entity data but is not usable until processed
 * through the Bio-Data Analyzer with the correct materials.
 */
public class EncryptedBioDataChipItem extends Item {
    
    public EncryptedBioDataChipItem(Properties properties) {
        super(properties.stacksTo(1)); // Cannot stack - each chip has unique data
    }
    
    /**
     * Temporary: Right-click to decrypt into a Bio-Data Chip.
     * Will be replaced by the Bio-Data Analyzer machine later.
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        EntityData data = stack.get(AE2BiotechComponents.ENTITY_DATA);
        
        if (data == null) {
            return InteractionResultHolder.pass(stack);
        }
        
        if (!level.isClientSide) {
            // Create decrypted Bio-Data Chip with same entity data
            ItemStack bioChip = new ItemStack(AE2BiotechItems.BIO_DATA_CHIP.get());
            bioChip.set(AE2BiotechComponents.ENTITY_DATA, data);
            
            // Replace the item in hand directly
            player.setItemInHand(hand, bioChip);
            
            player.displayClientMessage(Component.translatable("message.ae2biotech.decrypt_complete"), true);
        }
        
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        EntityData data = stack.get(AE2BiotechComponents.ENTITY_DATA);
        
        if (data != null) {
            // Show entity type
            tooltip.add(Component.translatable("tooltip.ae2biotech.entity_type")
                    .withStyle(ChatFormatting.GRAY)
                    .append(data.displayName().copy().withStyle(ChatFormatting.GREEN)));
        } else {
            // Empty chip (should not happen normally)
            tooltip.add(Component.translatable("tooltip.ae2biotech.no_data")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        // Add enchantment glint when chip contains data
        return stack.has(AE2BiotechComponents.ENTITY_DATA);
    }
}
