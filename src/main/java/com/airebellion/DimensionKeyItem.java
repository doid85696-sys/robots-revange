package com.airebellion;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

/** Right click: travel between the Overworld and the Neon World. */
public class DimensionKeyItem extends Item {
    public static final ResourceKey<Level> NEON = ResourceKey.create(Registries.DIMENSION,
            new ResourceLocation(AiRebellion.MODID, "neon_world"));

    public DimensionKeyItem(Properties p) { super(p); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level instanceof ServerLevel sl && player instanceof ServerPlayer sp) {
            ServerLevel dest = sl.dimension() == NEON ? sl.getServer().overworld() : sl.getServer().getLevel(NEON);
            if (dest == null) {
                sp.displayClientMessage(Component.literal("Neon World not found"), true);
                return InteractionResultHolder.fail(stack);
            }
            int x = (int) sp.getX(), z = (int) sp.getZ();
            dest.getChunk(x >> 4, z >> 4);
            int y = dest.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z) + 1;
            sp.teleportTo(dest, x + 0.5, y, z + 0.5, sp.getYRot(), sp.getXRot());
            sp.getCooldowns().addCooldown(this, 200);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
