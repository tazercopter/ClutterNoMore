package dev.tazer.clutternomore.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class StepBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<? extends StepBlock> CODEC = simpleCodec(StepBlock::new);

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public StepBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(HALF, Half.BOTTOM).setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends StepBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF).add(FACING).add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        FluidState replacingFluidState = context.getLevel().getFluidState(pos);

        BlockState stateForPlacement = super.getStateForPlacement(context)
                .setValue(FACING, context.getHorizontalDirection())
                .setValue(WATERLOGGED, replacingFluidState.getType() == Fluids.WATER);

        if (context.getClickedFace() == Direction.DOWN || context.getClickLocation().y - pos.getY() > 0.5F) {
            stateForPlacement = stateForPlacement.setValue(HALF, Half.TOP);
        }

        return stateForPlacement;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        double y = state.getValue(HALF) == Half.BOTTOM ? 0 : 0.5;

        return switch (state.getValue(FACING)) {
            case Direction.NORTH -> Shapes.create(0, y, 0, 1, y + 0.5, 0.5);
            case Direction.EAST -> Shapes.create(0.5, y, 0, 1, y + 0.5, 1);
            case Direction.SOUTH -> Shapes.create(0, y, 0.5, 1, y + 0.5, 1);
            case Direction.WEST -> Shapes.create(0, y, 0, 0.5, y + 0.5, 1);
            default -> Shapes.block();
        };
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.getValue(WATERLOGGED)) {
            return Fluids.WATER.getSource(false);
        }

        return state.getFluidState();
    }
}
