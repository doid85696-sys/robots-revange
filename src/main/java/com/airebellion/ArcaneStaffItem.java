package com.airebellion;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Right click: fireball. Sneak + right click: lightning where you look. */
public class ArcaneStaffItem extends Item {
    public ArcaneStaffItem(Properties p) { super(p); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            if (player.isShiftKeyDown()) {
                HitResult hit = player.pick(40, 0, false);
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
                if (bolt != null) {
                    bolt.moveTo(hit.getLocation());
                    level.addFreshEntity(bolt);
                }
                stack.hurtAndBreak(5, player, p -> p.broadcastBreakEvent(hand));
                player.getCooldowns().addCooldown(this, 60);
            } else {
                Vec3 look = player.getLookAngle();
                SmallFireball fb = new SmallFireball(level, player, look.x, look.y, look.z);
                fb.setPos(player.getX() + look.x, player.getEyeY() - 0.1 + look.y, player.getZ() + look.z);
                level.addFreshEntity(fb);
                level.playSound(null, player.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1f, 1.2f);
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                player.getCooldowns().addCooldown(this, 12);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
