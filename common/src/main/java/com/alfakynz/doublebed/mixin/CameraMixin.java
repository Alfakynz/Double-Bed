package com.alfakynz.doublebed.mixin;

import com.alfakynz.doublebed.BedLinks;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Camera;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Camera.class)
public class CameraMixin {

    @Shadow private @Nullable Level level;
    @Shadow private @Nullable Entity entity;

    @Shadow
    protected void move(float forwards, float up, float right) {}

    @WrapOperation(
            method = "alignWithEntity",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/entity/LivingEntity;getBedOrientation()Lnet/minecraft/core/Direction;"
                    )
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;setRotation(FF)V"
            ),
            require = 1
    )
    private void doubleBed$sleepRotation(Camera camera, float yRot, float xRot, Operation<Void> original) {
        Direction side = this.doubleBed$findPartnerSide();

        if (side == null) {
            original.call(camera, yRot, xRot);
            return;
        }

        original.call(camera, side.toYRot(), xRot);
        this.move(0.2F, 0.0F, 0.0F);
    }

    @Unique
    private @Nullable Direction doubleBed$findPartnerSide() {
        if (this.level == null || !(this.entity instanceof LivingEntity living)) return null;

        return living.getSleepingPos()
                .map(pos -> BedLinks.findPartnerSide(this.level, pos))
                .orElse(null);
    }
}
