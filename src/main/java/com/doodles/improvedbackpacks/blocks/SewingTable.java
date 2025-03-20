package com.doodles.improvedbackpacks.blocks;

import com.doodles.improvedbackpacks.blockentities.SewingTableEntity;
import com.doodles.improvedbackpacks.menus.SewingTableMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SewingTable extends Block implements EntityBlock {
	
	// Define blockstates
 	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
 	public static final BooleanProperty SPOOL = BooleanProperty.create("spool");
 	// Define hitbox model
    private static final VoxelShape BASE   = Block.box( 1.0D,  1.0D,  1.0D, 15.0D, 12.0D, 15.0D);
  	private static final VoxelShape LEG_NW = Block.box( 0.0D,  0.0D,  0.0D,  4.0D, 11.0D,  4.0D);
  	private static final VoxelShape LEG_NE = Block.box(12.0D,  0.0D,  0.0D, 16.0D, 11.0D,  4.0D);
 	private static final VoxelShape LEG_SW = Block.box( 0.0D,  0.0D, 12.0D,  4.0D, 11.0D, 16.0D);
 	private static final VoxelShape LEG_SE = Block.box(12.0D,  0.0D, 12.0D, 16.0D, 11.0D, 16.0D);
 	private static final VoxelShape TOP    = Block.box( 0.0D, 12.0D,  0.0D, 16.0D, 16.0D, 16.0D);
 	private static final VoxelShape SHAPE  = Shapes.or(BASE, LEG_NW, LEG_NE, LEG_SW, LEG_SE, TOP);
 	// Define menu title
    private static final Component CONTAINER_TITLE = Component.translatable("container.improvedbackpacks.sewing");
	
	public SewingTable(Properties properties) {
		super(properties);
	}
	
	// Assign and Initialize Blockstates
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
        pBuilder.add(SPOOL);
    }
	@Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(SPOOL, false);
	}
	
	// Assign hitbox model
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}
	
	// Assign BlockEntity object
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SewingTableEntity(pos, state);
	}
	
	// Right Click Functionality
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.isClientSide) {
	        return InteractionResult.SUCCESS;
	    } else {
			player.openMenu(state.getMenuProvider(level, pos));
	        return InteractionResult.CONSUME;
	    }
	}
	
	// GUI
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
    	return new SimpleMenuProvider((id, inv, access) -> {
    		return new SewingTableMenu(id, inv, ContainerLevelAccess.create(level, pos));
    	}, CONTAINER_TITLE);
    }
	 
}