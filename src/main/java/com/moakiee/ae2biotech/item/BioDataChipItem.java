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
 * Bio-Data Chip - The usable, decrypted chip containing entity data.
 * 
 * This is the core component used in machines like the ME Bio-Simulation Chamber,
 * Bio-Farm, and Gene Editor. It carries the entity type, NBT data, and gene modifications.
 */
public class BioDataChipItem extends Item {
    
    public BioDataChipItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        EntityData data = stack.get(AE2BiotechComponents.ENTITY_DATA);
        
        if (data != null) {
            tooltip.add(Component.translatable("tooltip.ae2biotech.entity_type")
                    .withStyle(ChatFormatting.GRAY)
                    .append(data.displayName().copy().withStyle(ChatFormatting.GREEN)));
            
            // Show chip UUID
            String uuidStr = data.chipId().toString();
            tooltip.add(Component.translatable("tooltip.ae2biotech.chip_id")
                    .withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.literal(uuidStr).withStyle(ChatFormatting.YELLOW)));
        } else {
            tooltip.add(Component.translatable("tooltip.ae2biotech.no_data")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
