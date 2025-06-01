package com.slightlypeckish.armm;

import java.util.function.Function;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.loot.LootPool;
// import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
// import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
// import net.minecraft.predicate.entity.EntityFlagsPredicate;
// import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ItemsARMM {
    // MUSIC DISK ITEM VARIABLES
    private static final SoundEvent BOSS_STAGE = registerSoundEvent("boss_stage");
    private static final RegistryKey<JukeboxSong> BOSS_STAGE_KEY = registerSong("boss_stage");
    private static final Item BOSS_STAGE_MUSIC_DISK = register("boss_stage_music_disk", Item::new, new Item.Settings().jukeboxPlayable(BOSS_STAGE_KEY).maxCount(1));

    // LAMB ITEM VARIABLES
    private static final Item RAW_LAMB = register("raw_lamb", Item::new, new Item.Settings().food(new FoodComponent.Builder().build()));
    private static final Item COOKED_LAMB = register("cooked_lamb", Item::new, new Item.Settings().food(new FoodComponent.Builder().build()));
    private static final Identifier SHEEP_ID = Identifier.of("minecraft", "entities/sheep");

    public static void initialize() {
        // LAMB ITEM INITIALIZATIONS
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register((itemGroup) -> itemGroup.add(ItemsARMM.RAW_LAMB));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register((itemGroup) -> itemGroup.add(ItemsARMM.COOKED_LAMB));
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(source.isBuiltin() && SHEEP_ID.equals(key.getValue())) {
                LootPool.Builder poolBuilder = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .conditionally(RandomChanceLootCondition.builder(1.0f))
//                    .conditionally(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().flags(EntityFlagsPredicate.Builder.create().isBaby(true))))
                    .with(ItemEntry.builder(ItemsARMM.RAW_LAMB))
                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)));

                tableBuilder.pool(poolBuilder);
            }
        });

        // MUSIC DISK ITEM INITIALIZATIONS
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register((itemGroup) -> itemGroup.add(ItemsARMM.BOSS_STAGE_MUSIC_DISK));
    }

    public static Item register(String itemName, Function<Item.Settings, Item> itemFactory, Item.Settings itemSettings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ARMM.MOD_ID, itemName));

        Item item = itemFactory.apply(itemSettings.registryKey(itemKey));

        Registry.register(Registries.ITEM, itemKey, item);

        return item;
    }

    public static SoundEvent registerSoundEvent(String soundName) {
        Identifier soundID = Identifier.of(ARMM.MOD_ID, soundName);
        return Registry.register(Registries.SOUND_EVENT, soundID, SoundEvent.of(soundID));
    }

    public static RegistryKey<JukeboxSong> registerSong(String songName) {
        return RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(ARMM.MOD_ID, songName));
    }
}