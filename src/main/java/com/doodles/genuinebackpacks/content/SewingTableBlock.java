package com.doodles.genuinebackpacks.content;

import javax.annotation.Nullable;

import com.doodles.genuinebackpacks.GenuineBackpacks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;

public class SewingTableBlock extends Block implements EntityBlock {
	
	// Define blockstates
 	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
 	public static final BooleanProperty SPOOL = BooleanProperty.create("spool");
 	public static final BooleanProperty SHEARS = BooleanProperty.create("shears");
 	// Define hitbox model
    private static final VoxelShape BASE   = Block.box( 1.0D,  1.0D,  1.0D, 15.0D, 12.0D, 15.0D);
  	private static final VoxelShape LEG_NW = Block.box( 0.0D,  0.0D,  0.0D,  4.0D, 11.0D,  4.0D);
  	private static final VoxelShape LEG_NE = Block.box(12.0D,  0.0D,  0.0D, 16.0D, 11.0D,  4.0D);
 	private static final VoxelShape LEG_SW = Block.box( 0.0D,  0.0D, 12.0D,  4.0D, 11.0D, 16.0D);
 	private static final VoxelShape LEG_SE = Block.box(12.0D,  0.0D, 12.0D, 16.0D, 11.0D, 16.0D);
 	private static final VoxelShape TOP    = Block.box( 0.0D, 12.0D,  0.0D, 16.0D, 16.0D, 16.0D);
 	private static final VoxelShape SHAPE  = Shapes.or(BASE, LEG_NW, LEG_NE, LEG_SW, LEG_SE, TOP);
 	// Define menu title
    private static final Component CONTAINER_TITLE = Component.translatable("container."+GenuineBackpacks.MODID+".sewing");
	
	public SewingTableBlock(Properties properties) {
		super(properties);
	}
	
	// Assign and Initialize Blockstates
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
        pBuilder.add(SPOOL);
        pBuilder.add(SHEARS);
    }
	@Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(SPOOL, false).setValue(SHEARS, false);
	}
	
	// Assign hitbox model
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SewingTableEntity(pos, state);
	}

	// Connect EntityTicker
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
    	return type == GenuineBackpacks.SEWING_TABLE_ENTITY.get() ? SewingTableEntity::tick : null;
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof SewingTableEntity) {
                MenuProvider containerProvider = new MenuProvider() {
                    @Override
                    public Component getDisplayName() { return CONTAINER_TITLE; }

                    @Override
                    public AbstractContainerMenu createMenu(int windowId, Inventory __, Player playerEntity) {
                        return new SewingTableMenu(windowId, playerEntity, pos, ContainerLevelAccess.create(level, pos));
                    }
                };
                NetworkHooks.openScreen((ServerPlayer) player, containerProvider, blockEntity.getBlockPos());
            } else {
                throw new IllegalStateException("Incorrect SewingTableEntity upon Table Use");
            }
        }
        return InteractionResult.SUCCESS;
    }
    
    
    @Override
    public void playerWillDestroy (Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof SewingTableEntity) {
			ItemStackHandler items = ((SewingTableEntity) blockEntity).getItems();
			for (int slot = 0; slot < items.getSlots() - 1; slot++) {
				Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), items.getStackInSlot(slot));
			}
		} else {
			throw new IllegalStateException("Incorrect SewingTableEntity upon Table Break");
		}
		super.playerWillDestroy(level, pos, state, player);
    }
    
    @Override
    public void playerDestroy (Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack __) {
    	super.playerDestroy(level, player, pos, state, blockEntity, __);
    }
}