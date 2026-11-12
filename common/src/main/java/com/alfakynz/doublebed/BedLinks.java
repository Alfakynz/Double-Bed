package com.alfakynz.doublebed;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class BedLinks {

    private BedLinks() {}

    public static boolean isPartner(BlockState self, BlockState other) {
        return self.getBlock() instanceof BedBlock
                && other.getBlock() instanceof BedBlock
                && other.getValue(BedBlock.FACING) == self.getValue(BedBlock.FACING)
                && other.getValue(BedBlock.PART) == self.getValue(BedBlock.PART);
    }

    public static boolean hasPartnerOn(BlockGetter level, BlockPos pos, Direction side) {
        BlockState self = level.getBlockState(pos);
        return isPartner(self, level.getBlockState(pos.relative(side)));
    }

    public static @Nullable Direction findPartnerSide(BlockGetter level, BlockPos pos) {
        BlockState self = level.getBlockState(pos);
        if (!(self.getBlock() instanceof BedBlock)) return null;

        Direction facing = self.getValue(BedBlock.FACING);
        Direction cw = facing.getClockWise();
        Direction ccw = facing.getCounterClockWise();

        if (hasPartnerOn(level, pos, cw)) return cw;
        if (hasPartnerOn(level, pos, ccw)) return ccw;
        return null;
    }

    public static float sleepOffset(BlockGetter level, BlockPos pos) {
        Direction side = findPartnerSide(level, pos);
        if (side == null) return 0.0F;

        Direction facing = level.getBlockState(pos).getValue(BedBlock.FACING);
        return side == facing.getClockWise() ? -90.0F : 90.0F;
    }
}