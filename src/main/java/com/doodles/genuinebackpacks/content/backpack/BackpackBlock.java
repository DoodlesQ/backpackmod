package com.doodles.genuinebackpacks.content.backpack;

import java.util.Map;
import static java.util.Map.entry;    

import com.doodles.genuinebackpacks.GenuineBackpacks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BackpackBlock extends Block implements EntityBlock {

 	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
 	public static final BooleanProperty MOUNTED = BooleanProperty.create("mounted");
 	public static final BooleanProperty ENDER = BooleanProperty.create("ender");
 	public static final IntegerProperty EGG = IntegerProperty.create("easter_egg", 0, BackpackItem.SPECIAL);
    private static final VoxelShape SHAPE = Block.box( 4.0D,  0.0D,  4.0D, 12.0D, 11.0D, 12.0D);
    private static final Map<Direction,VoxelShape> MOUNTSHAPE = Map.ofEntries(
		entry(Direction.SOUTH, Block.box( 4.0D,  2.0D,  9.0D, 12.0D, 13.0D, 16.0D)),
		entry(Direction.NORTH, Block.box( 4.0D,  2.0D,  0.0D, 12.0D, 13.0D,  7.0D)),
		entry(Direction.EAST,  Block.box( 9.0D,  2.0D,  4.0D, 16.0D, 13.0D, 12.0D)),
		entry(Direction.WEST,  Block.box( 0.0D,  2.0D,  4.0D,  7.0D, 13.0D, 12.0D))
	);
 	
	public BackpackBlock(Properties properties) {
		super(properties);
	}
	
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
        pBuilder.add(MOUNTED);
        pBuilder.add(ENDER);
        pBuilder.add(EGG);
    }
	@Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
		if (context.getClickedFace() == Direction.DOWN) return null;
		Direction facing = context.getHorizontalDirection();
		Direction clicked = context.getClickedFace().getOpposite();
		BlockPos pos = context.getClickedPos();
		Level level = context.getLevel();
		ItemStack held = context.getItemInHand();
		BlockState state = this.defaultBlockState()
			.setValue(ENDER, held.is(GenuineBackpacks.ENDER_BACKPACK.get()))
			.setValue(EGG, BackpackItem.getSpecial(held));
		boolean doMount = clicked != Direction.DOWN;
		if (!doMount) {
			state = state.setValue(FACING, facing).setValue(MOUNTED, false);
		} else {
			state = state.setValue(MOUNTED, canSurvive(state.setValue(MOUNTED, true).setValue(FACING, clicked), level, pos)).setValue(FACING, clicked);
		}
		return state;
	}
	// Assign hitbox model
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(MOUNTED) ? MOUNTSHAPE.get(state.getValue(FACING)) : SHAPE;
	}
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
	      Direction direction = state.getValue(MOUNTED) ? state.getValue(FACING) : Direction.DOWN;
	      return Block.canSupportCenter(level, pos.relative(direction), direction.getOpposite());
	}

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
    	if (!level.isClientSide) {
    		if (level.getBlockEntity(pos) instanceof BackpackTileEntity blockEntity) {
    			if (!state.getValue(ENDER))
    				BackpackItem.open(level, player, blockEntity.backpack != null ? blockEntity.backpack : new ItemStack(GenuineBackpacks.BACKPACK.get()), pos);
    			else
    				EnderBackpackItem.open(level, player, blockEntity.backpack != null ? blockEntity.backpack : new ItemStack(GenuineBackpacks.ENDER_BACKPACK.get()), pos);
    		}
    	}
        return InteractionResult.SUCCESS;
    }
    
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BackpackTileEntity(pos, state);
	}
	
	public static Item packType(BlockState state) {
		return packType(state.getValue(ENDER));
	}
	
	public static Item packType(boolean ender) {
		Item backpack = GenuineBackpacks.BACKPACK.get();
		if (ender) backpack = GenuineBackpacks.ENDER_BACKPACK.get();
		return backpack;
	}
	public static ItemStack getDefaultPack(BlockState state) {
		return getDefaultPack(state.getValue(ENDER), state.getValue(EGG));
	}
	public static ItemStack getDefaultPack(boolean ender, int egg) {
		ItemStack backpack = new ItemStack(packType(ender));
		BackpackItem.setSpecial(backpack, egg);
		return backpack;
	}

    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
    	super.setPlacedBy(level, pos, state, placer, stack);
    	if (level.getBlockEntity(pos) instanceof BackpackTileEntity blockEntity && stack.is(packType(state))) {
    		blockEntity.setBackpack(stack.copy());
    	}
    }
    
    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willharvest, FluidState fluid) {
    	if (!level.isClientSide) {
    		ItemStack drop = getDefaultPack(state);
    		if (level.getBlockEntity(pos) instanceof BackpackTileEntity blockEntity && blockEntity.backpack != null) {
    			drop = blockEntity.backpack.copy();
    		}
    		popResource(level, pos, drop);
    	}
		return super.onDestroyedByPlayer(state, level, pos, player, willharvest, fluid);
    }
    
    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {    	
    	if (level.getExistingBlockEntity(pos) instanceof BackpackTileEntity blockEntity) {
    		if (blockEntity.backpack != null) {
    			return blockEntity.backpack.copy();
    		}
    	}
    	
		return new ItemStack(packType(state));
    }
    
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
    	if (state.getValue(ENDER)) {
	        for (int i = 0; i < 3; ++i) {
	           int j = random.nextInt(2) * 2 - 1;
	           int k = random.nextInt(2) * 2 - 1;
	           double d0 = (double)pos.getX() + 0.5D + 0.125D * (double)j;
	           double d1 = (double)((float)pos.getY() + 0.5D * random.nextFloat());
	           double d2 = (double)pos.getZ() + 0.5D + 0.125D * (double)k;
	           double d3 = (double)(random.nextFloat() * (float)j);
	           double d4 = ((double)random.nextFloat() - 0.5D) * 0.125D;
	           double d5 = (double)(random.nextFloat() * (float)k);
	           level.addParticle(ParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
	        }
    	}
     }
}
