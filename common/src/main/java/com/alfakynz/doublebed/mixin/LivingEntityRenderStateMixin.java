package com.alfakynz.doublebed.mixin;

import com.alfakynz.doublebed.SleepSideAccess;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
public class LivingEntityRenderStateMixin implements SleepSideAccess {

    @Unique
    private float doubleBed$sleepOffset = 0.0F;

    @Override
    public float doubleBed$getSleepOffset() {
        return this.doubleBed$sleepOffset;
    }

    @Override
    public void doubleBed$setSleepOffset(float offset) {
        this.doubleBed$sleepOffset = offset;
    }
}