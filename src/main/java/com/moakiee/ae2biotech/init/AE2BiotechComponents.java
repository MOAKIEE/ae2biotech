package com.moakiee.ae2biotech.init;

import java.util.function.Consumer;

import com.moakiee.ae2biotech.AE2BioTechnology;
import com.moakiee.ae2biotech.component.EntityData;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Data component registration for AE2 Bio-Technology mod.
 */
public class AE2BiotechComponents {
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = 
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, AE2BioTechnology.MODID);
    
    /**
     * Stores entity data in bio-data chips.
     * Contains entity type, NBT data, and display name.
     */
    public static final DataComponentType<EntityData> ENTITY_DATA = register("entity_data",
            builder -> builder.persistent(EntityData.CODEC)
                    .networkSynchronized(EntityData.STREAM_CODEC));
    
    /**
     * Stores the entity ID being scanned by the Bio-Scanner (temporary data).
     */
    public static final DataComponentType<Integer> SCAN_TARGET_ID = register("scan_target_id",
            builder -> builder.persistent(com.mojang.serialization.Codec.INT)
                    .networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.VAR_INT));
    
    private static <T> DataComponentType<T> register(String name, Consumer<DataComponentType.Builder<T>> customizer) {
        var builder = DataComponentType.<T>builder();
        customizer.accept(builder);
        var componentType = builder.build();
        DATA_COMPONENTS.register(name, () -> componentType);
        return componentType;
    }
}
