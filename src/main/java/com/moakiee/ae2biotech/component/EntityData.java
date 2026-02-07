package com.moakiee.ae2biotech.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

/**
 * Stores entity data for bio-data chips.
 * 
 * Contains:
 * - entityType: The entity type ID (e.g., "minecraft:zombie")
 * - entityNbt: Complete NBT data of the entity for exact replication
 * - displayName: The display name shown in tooltips
 */
public record EntityData(
    ResourceLocation entityType,
    CompoundTag entityNbt,
    Component displayName
) {
    
    public static final Codec<EntityData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("entity_type").forGetter(EntityData::entityType),
            CompoundTag.CODEC.fieldOf("entity_nbt").forGetter(EntityData::entityNbt),
            ComponentSerialization.CODEC.fieldOf("display_name").forGetter(EntityData::displayName)
    ).apply(instance, EntityData::new));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, EntityData> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            EntityData::entityType,
            ByteBufCodecs.COMPOUND_TAG,
            EntityData::entityNbt,
            ComponentSerialization.STREAM_CODEC,
            EntityData::displayName,
            EntityData::new
    );
}
