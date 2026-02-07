package com.moakiee.ae2biotech.item;

import java.util.List;

import com.moakiee.ae2biotech.component.EntityData;
import com.moakiee.ae2biotech.init.AE2BiotechComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

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
    
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        EntityData data = stack.get(AE2BiotechComponents.ENTITY_DATA);
        
        if (data != null) {
            // Show entity type
            tooltip.add(Component.translatable("tooltip.ae2biotech.entity_type")
                    .withStyle(ChatFormatting.GRAY)
                    .append(data.displayName().copy().withStyle(ChatFormatting.GREEN)));
            
            // Show encryption status
            tooltip.add(Component.translatable("tooltip.ae2biotech.encrypted")
                    .withStyle(ChatFormatting.RED));
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
