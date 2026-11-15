package com.airebellion;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RobotBoss extends Monster {
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.airebellion.robot_boss"),
            BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.NOTCHED_10);
    private boolean enraged = false;

    public RobotBoss(EntityType<? extends RobotBoss> type, Level level) {
        super(type, level);
        this.xpReward = 200;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 300)
                .add(Attributes.ATTACK_DAMAGE, 12)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ARMOR, 10)
                .add(Attributes.FOLLOW_RANGE, 48)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 16f));
        goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        bossEvent.setProgress(getHealth() / getMaxHealth());

        if (!enraged && getHealth() < getMaxHealth() / 2f) {
            enraged = true;
            bossEvent.setColor(BossEvent.BossBarColor.RED);
            addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, Integer.MAX_VALUE, 1, false, false));
            playSound(SoundEvents.ENDER_DRAGON_GROWL, 3f, 0.6f);
        }

        LivingEntity t = getTarget();
        if (t != null && tickCount % (enraged ? 25 : 50) == 0 && distanceToSqr(t) > 16 && hasLineOfSight(t)) {
            double dx = t.getX() - getX();
            double dy = t.getEyeY() - (getY() + 2.8);
            double dz = t.getZ() - getZ();
            SmallFireball fb = new SmallFireball(level(), this, dx, dy, dz);
            fb.setPos(getX(), getY() + 2.8, getZ());
            level().addFreshEntity(fb);
            playSound(SoundEvents.BLAZE_SHOOT, 2f, 0.7f);
        }

        if (enraged && tickCount % 120 == 0) {
            for (Player p : level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(6))) {
                p.hurt(damageSources().mobAttack(this), 8f);
                p.push(0, 0.8, 0);
            }
            if (level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 1, getZ(), 12, 3, 0.5, 3, 0);
            }
            playSound(SoundEvents.GENERIC_EXPLODE, 2f, 0.8f);
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer p) {
        super.startSeenByPlayer(p);
        bossEvent.addPlayer(p);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer p) {
        super.stopSeenByPlayer(p);
        bossEvent.removePlayer(p);
    }

    @Override
    public boolean removeWhenFarAway(double d) {
        return false;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource src, int looting, boolean recent) {
        super.dropCustomDeathLoot(src, looting, recent);
        spawnAtLocation(new ItemStack(AiRebellion.CIRCUIT_CORE.get(), 8 + random.nextInt(8)));
        spawnAtLocation(new ItemStack(AiRebellion.NANO_BLADE.get()));
    }

    @Override protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.IRON_GOLEM_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.IRON_GOLEM_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.IRON_GOLEM_STEP, 1f, 0.8f); }
}
