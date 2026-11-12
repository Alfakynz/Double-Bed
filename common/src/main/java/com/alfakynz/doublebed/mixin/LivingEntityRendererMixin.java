package com.alfakynz.doublebed.mixin;

import com.alfakynz.doublebed.BedLinks;
import com.alfakynz.doublebed.SleepSideAccess;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionfc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
            at = @At("TAIL"),
            require = 1
    )
    private void doubleBed$extractSleepSide(LivingEntity entity, LivingEntityRenderState state, float partialTicks, CallbackInfo ci) {
        float offset = 0.0F;
        if (entity.isSleeping()) {
            offset = entity.getSleepingPos()
                    .map(pos -> BedLinks.sleepOffset(entity.level(), pos))
                    .orElse(0.0F);
        }
        ((SleepSideAccess) state).doubleBed$setSleepOffset(offset);
    }

    @WrapOperation(
            method = "setupRotations",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;sleepDirectionToRotation(Lnet/minecraft/core/Direction;)F"
                    )
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionfc;)V",
                    ordinal = 2
            ),
            require = 1
    )
    private void doubleBed$setBodyRotation(PoseStack poseStack, Quaternionfc by, Operation<Void> original, @Local(argsOnly = true, name = "state") LivingEntityRenderState state) {
        original.call(poseStack, by);

        float offset = ((SleepSideAccess) state).doubleBed$getSleepOffset();
        if (offset == 0.0F) return;

        poseStack.mulPose(Axis.YP.rotationDegrees(-offset));
        poseStack.translate(-0.2F * (offset / 90.0F), 0.0F, 0.0F);
    }
}