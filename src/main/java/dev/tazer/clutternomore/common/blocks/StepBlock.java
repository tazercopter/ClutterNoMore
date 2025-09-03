package dev.tazer.clutternomore.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StepBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<? extends StepBlock> CODEC = simpleCodec(StepBlock::new);

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<SlabType> SLAB_TYPE = BlockStateProperties.SLAB_TYPE;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public StepBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(SLAB_TYPE, SlabType.BOTTOM).setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends StepBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SLAB_TYPE).add(FACING).add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        BlockState replacingBlockState = context.getLevel().getBlockState(pos);
        FluidState replacingFluidState = context.getLevel().getFluidState(pos);

        if (replacingBlockState.is(this)) {
            if (replacingBlockState.getValue(SLAB_TYPE) == SlabType.BOTTOM) {
                replacingBlockState = replacingBlockState.setValue(FACING, replacingBlockState.getValue(FACING).getOpposite());
            }
            return replacingBlockState.setValue(SLAB_TYPE, SlabType.DOUBLE);
        }

        BlockState stateForPlacement = super.getStateForPlacement(context)
                .setValue(FACING, context.getHorizontalDirection())
                .setValue(WATERLOGGED, replacingFluidState.getType() == Fluids.WATER);

        if (context.getClickedFace() == Direction.DOWN || context.getClickLocation().y - pos.getY() > 0.5F) {
            stateForPlacement = stateForPlacement.setValue(SLAB_TYPE, SlabType.TOP);
        }

        return stateForPlacement;
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        ItemStack itemStack = context.getItemInHand();
        if (state.getValue(SLAB_TYPE) == SlabType.DOUBLE || !(itemStack.is(asItem())) ) {
            return false;
        }

        if (!context.replacingClickedOnBlock()) {
            return true;
        }

        return context.getClickedFace() == state.getValue(FACING).getOpposite();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        if (state.getValue(SLAB_TYPE) == SlabType.DOUBLE) {
            return Shapes.or(createShape(direction, 0.5), createShape(direction.getOpposite(), 0));
        }

        double y = state.getValue(SLAB_TYPE) == SlabType.TOP ? 0.5 : 0;
        return createShape(direction, y);
    }

    public static VoxelShape createShape(Direction direction, double y) {
        return switch (direction) {
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
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.getValue(WATERLOGGED)) {
            return Fluids.WATER.getSource(false);
        }

        return state.getFluidState();
    }
}
