package com.airebellion;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RobotBossRenderer extends HumanoidMobRenderer<RobotBoss, HumanoidModel<RobotBoss>> {
    private static final ResourceLocation TEX = new ResourceLocation(AiRebellion.MODID, "textures/entity/robot_boss.png");

    public RobotBossRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.9f);
    }

    @Override
    protected void scale(RobotBoss e, PoseStack ps, float pt) {
        ps.scale(1.9f, 1.9f, 1.9f);
    }

    @Override
    public ResourceLocation getTextureLocation(RobotBoss e) {
        return TEX;
    }
}
