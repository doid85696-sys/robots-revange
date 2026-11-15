package com.airebellion;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;

@Mod(AiRebellion.MODID)
public class AiRebellion {
    public static final String MODID = "airebellion";

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<Block> CIRCUIT_ORE = BLOCKS.register("circuit_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.IRON_ORE).strength(3.5f, 4f), UniformInt.of(2, 5)));
    public static final RegistryObject<Block> NEON_BLOCK = BLOCKS.register("neon_block",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).strength(2f)
                    .lightLevel(s -> 15).sound(SoundType.GLASS)));

    public static final RegistryObject<Item> CIRCUIT_ORE_ITEM = ITEMS.register("circuit_ore", () -> new BlockItem(CIRCUIT_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> NEON_BLOCK_ITEM = ITEMS.register("neon_block", () -> new BlockItem(NEON_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> CIRCUIT_CORE = ITEMS.register("circuit_core", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ARCANE_STAFF = ITEMS.register("arcane_staff", () -> new ArcaneStaffItem(new Item.Properties().durability(500)));
    public static final RegistryObject<Item> NANO_BLADE = ITEMS.register("nano_blade", () -> new NanoBladeItem(new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> DIMENSION_KEY = ITEMS.register("dimension_key", () -> new DimensionKeyItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<EntityType<RobotBoss>> ROBOT_BOSS = ENTITIES.register("robot_boss",
            () -> EntityType.Builder.of(RobotBoss::new, MobCategory.MONSTER).sized(1.2f, 3.6f).fireImmune()
                    .clientTrackingRange(10).build("robot_boss"));
    public static final RegistryObject<Item> ROBOT_SPAWN_EGG = ITEMS.register("robot_boss_spawn_egg",
            () -> new ForgeSpawnEggItem(ROBOT_BOSS, 0x2b2b3a, 0x00e5ff, new Item.Properties()));

    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.airebellion"))
                    .icon(() -> new ItemStack(ARCANE_STAFF.get()))
                    .displayItems((params, out) -> ITEMS.getEntries().forEach(i -> out.accept(i.get())))
                    .build());

    public AiRebellion() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
        TABS.register(bus);
        bus.addListener(this::attributes);
    }

    private void attributes(EntityAttributeCreationEvent e) {
        e.put(ROBOT_BOSS.get(), RobotBoss.createAttributes().build());
    }
}
