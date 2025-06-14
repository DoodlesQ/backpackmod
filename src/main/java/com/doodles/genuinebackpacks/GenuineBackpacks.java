package com.doodles.genuinebackpacks;

import java.util.LinkedHashMap;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import com.doodles.genuinebackpacks.content.backpack.AbstractBackpackItem;
import com.doodles.genuinebackpacks.content.backpack.BackpackBlock;
import com.doodles.genuinebackpacks.content.backpack.BackpackItem;
import com.doodles.genuinebackpacks.content.backpack.BackpackLayer;
import com.doodles.genuinebackpacks.content.backpack.BackpackTileEntity;
import com.doodles.genuinebackpacks.content.backpack.EnderBackpackItem;
import com.doodles.genuinebackpacks.content.backpack.gui.BackpackMenu;
import com.doodles.genuinebackpacks.content.backpack.gui.BackpackScreen;
import com.doodles.genuinebackpacks.content.sewingtable.SewingTableBlock;
import com.doodles.genuinebackpacks.content.sewingtable.SewingTableMenu;
import com.doodles.genuinebackpacks.content.sewingtable.SewingTableScreen;
import com.doodles.genuinebackpacks.content.sewingtable.SewingTableTileEntity;
import com.doodles.genuinebackpacks.network.BackpackPacket;
import com.doodles.genuinebackpacks.network.GBPacketHandler;
import com.doodles.genuinebackpacks.recipe.SewingRecipe;
import com.doodles.genuinebackpacks.recipe.SewingRecipeSerializer;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(GenuineBackpacks.MODID)
public class GenuineBackpacks
{
    public static final String MODID = "genuinebackpacks";
    public static SimpleChannel NETWORK;
    
    // Define logger
    public static final Logger LOGGER = LogUtils.getLogger();
    
    
    /* REGISTRATION */
    // Create Deferred Registers
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_JSONS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, MODID);
    
    // Sewing Table
    public static final RegistryObject<SewingTableBlock> SEWING_TABLE = BLOCKS.register("sewing_table", () -> new SewingTableBlock(BlockBehaviour.Properties.of()
		.destroyTime(2.5F)
		.sound(SoundType.WOOD)
		.mapColor(MapColor.WOOD)
	));
    public static final RegistryObject<Item> SEWING_TABLE_ITEM = ITEMS.register("sewing_table", () -> new BlockItem(SEWING_TABLE.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<SewingTableTileEntity>> SEWING_TABLE_ENTITY = BLOCK_ENTITY_TYPES.register("sewing_table",
		() -> BlockEntityType.Builder.of(SewingTableTileEntity::new, SEWING_TABLE.get()).build(null)
	);
	public static final RegistryObject<MenuType<SewingTableMenu>> SEWING_TABLE_MENU = MENU_TYPES.register("sewing_table",
		() -> IForgeMenuType.create((windowid, inv, data) -> new SewingTableMenu(windowid, inv.player, data.readBlockPos()))
	);
	// Sewing Recipe
	public static final RegistryObject<RecipeType<SewingRecipe>> SEWING_RECIPE_TYPE = RECIPE_TYPES.register("sewing", () -> new RecipeType<SewingRecipe>(){});
	public static final RegistryObject<RecipeSerializer<SewingRecipe>> SEWING_RECIPE_JSON = RECIPE_JSONS.register("sewing", SewingRecipeSerializer::new);
	public static final RegistryObject<SoundEvent> SEWING_CRAFT_SOUND = SOUND_EVENTS.register("sewing_table", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "sewing_table")));
	
    // Etc Items
	//NOTE: This is a stupid way to do this. Don't do this.
	@SuppressWarnings("serial")
	public static final LinkedHashMap<String, RegistryObject<Item>> items = new LinkedHashMap<String, RegistryObject<Item>>() {{
		put("spool",          	ITEMS.register("spool", 			() -> new Item(new Item.Properties())));
		put("bound_leather",  	ITEMS.register("bound_leather", 	() -> new Item(new Item.Properties())));
		put("tanned_leather",	ITEMS.register("tanned_leather",	() -> new Item(new Item.Properties())));
		put("tiny_pocket",		ITEMS.register("tiny_pocket", 		() -> new Item(new Item.Properties()) {
			@Override
			public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) { pocketText(tooltip, 3, 3); }
		}));
		put("medium_pocket",	ITEMS.register("medium_pocket", 	() -> new Item(new Item.Properties()) {
			@Override
			public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) { pocketText(tooltip, 6, 3); }
		}));
		put("large_pocket",		ITEMS.register("large_pocket", 		() -> new Item(new Item.Properties()) {
			@Override
			public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) { pocketText(tooltip, 9, 2); }
		}));
	}};
	
	// Backpack
	public static final RegistryObject<BackpackItem> BACKPACK = ITEMS.register("backpack", () -> new BackpackItem(new Item.Properties().stacksTo(1)));
	public static final RegistryObject<MenuType<BackpackMenu>> BACKPACK_MENU = MENU_TYPES.register("backpack",
		() -> IForgeMenuType.create((windowid, inv, data) -> new BackpackMenu(windowid, inv.player, data.readItem()))
	);
    public static final RegistryObject<BackpackBlock> BACKPACK_BLOCK = BLOCKS.register("backpack", () -> new BackpackBlock(BlockBehaviour.Properties.of()
		.destroyTime(0.2F)
		.sound(SoundType.WOOL)
		.mapColor(MapColor.WOOL)
	));
    public static final RegistryObject<BlockEntityType<BackpackTileEntity>> BACKPACK_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("backpack",
		() -> BlockEntityType.Builder.of(BackpackTileEntity::new, BACKPACK_BLOCK.get()).build(null)
	);
    public static final RegistryObject<SoundEvent> BACKPACK_OPEN_SOUND = SOUND_EVENTS.register("backpack_open", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "backpack_open")));
    public static final RegistryObject<SoundEvent> BACKPACK_CLOSE_SOUND = SOUND_EVENTS.register("backpack_close", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "backpack_close")));
	
    // Ender Backpack
    public static final RegistryObject<EnderBackpackItem> ENDER_BACKPACK = ITEMS.register("ender_backpack", () -> new EnderBackpackItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<SoundEvent> ENDER_BACKPACK_OPEN_SOUND = SOUND_EVENTS.register("ender_backpack_open", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "ender_backpack_open")));
    public static final RegistryObject<SoundEvent> ENDER_BACKPACK_CLOSE_SOUND = SOUND_EVENTS.register("ender_backpack_close", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "ender_backpack_close")));
    
    
    // Add Items to Creative Tab
    public static final RegistryObject<CreativeModeTab> BACKPACK_TAB = CREATIVE_MODE_TABS.register("backpack_tab", () -> CreativeModeTab.builder()
		.title(ct("itemGroup.%s.backpack_tab"))
        .icon(() -> SEWING_TABLE_ITEM.get().getDefaultInstance())
        .displayItems((parameters, output) -> {
            output.accept(SEWING_TABLE_ITEM.get());
            for (RegistryObject<Item> i : items.values()) output.accept(i.get());
            for (DyeColor dye : GenuineBackpacks.WOOL.keySet()) {
            	ItemStack pack = new ItemStack(BACKPACK.get());
            	BackpackItem.setDye(pack, BackpackItem.extractColor(dye));
            	output.accept(pack);
            }
            output.accept(ENDER_BACKPACK.get());
        }).build());
  
    @SuppressWarnings("removal")
	public GenuineBackpacks() {    	
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);

		LOGGER.info("REGISTERING BACKPACK MOD");
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        MENU_TYPES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        RECIPE_TYPES.register(modEventBus);
        RECIPE_JSONS.register(modEventBus);
        SOUND_EVENTS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        GBPacketHandler.registerNetworkHandler();
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
    	
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {    
        	// Register Screens
        	event.enqueueWork(() -> MenuScreens.register(GenuineBackpacks.SEWING_TABLE_MENU.get(), SewingTableScreen::new));
        	event.enqueueWork(() -> MenuScreens.register(GenuineBackpacks.BACKPACK_MENU.get(), BackpackScreen::new));
        	
        	// Texture for Open state
        	event.enqueueWork(() -> {
        		Item[] bp = {BACKPACK.get(), ENDER_BACKPACK.get()};
        		for (int i = 0; i < bp.length; i++) {
	        		ItemProperties.register(bp[i], ResourceLocation.fromNamespaceAndPath(MODID, "backpack_open"), 
	    				(stack, level, entity, seed) -> {
	    					return AbstractBackpackItem.isOpen(stack) ? 1.0f : 0.0f;
	    				}
					);
        		}
        	});
        	// Textures for Easter Eggs
        	event.enqueueWork(() -> {
        		ItemProperties.register(BACKPACK.get(), ResourceLocation.fromNamespaceAndPath(MODID, "easter_egg"),
    				(stack, level, entity, seed) -> {
    					switch (BackpackItem.getSpecial(stack)) {
							case TRANS: return 1;
							case BEE:   return 2;
    						default:    return 0;
    					}
    				}
				);
        	});
        }
        
        // Tint Backpacks
        @SubscribeEvent
        public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        	event.register((itemstack, color) -> {
        			int c = BackpackItem.getDye(itemstack);
        			if (BackpackItem.getSpecial(itemstack) != BackpackItem.Special.NONE) c = 0xffffff;
        			return color > 0 ? -1 : c;
        		},
        		GenuineBackpacks.BACKPACK.get()
    		);
        }
        @SubscribeEvent
        public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        	event.register((state, getter, pos, color) -> {
        			if (getter.getBlockEntity(pos) instanceof BackpackTileEntity blockEntity) {
        				int c = blockEntity.backpack.is(BACKPACK.get()) ? BackpackItem.getDye(blockEntity.backpack) : 0xffffff;
        				if (BackpackItem.getSpecial(blockEntity.backpack) != BackpackItem.Special.NONE) c = 0xffffff;
        				return color > 0 ? -1 : c;
        			}
        			return -1;
        		},
        		GenuineBackpacks.BACKPACK_BLOCK.get()
    		);
        }
        
        // Display Backpacks being worn
        @SuppressWarnings("unchecked")
		@SubscribeEvent
        public static void addPlayerLayer(EntityRenderersEvent.AddLayers event) {
        	String[] models = {"default", "slim"};
        	for (String m : models) {
        		EntityRenderer<? extends Player> r = event.getSkin(m);
        		if (r instanceof LivingEntityRenderer renderer) {
        			renderer.addLayer(new BackpackLayer(renderer));
        		}
        	}
        }

        // Keybinding
        public static final Lazy<KeyMapping> BACKPACK_MAPPING = Lazy.of(() -> new KeyMapping(
    		String.format("key.%s.backpack", MODID),
    		KeyConflictContext.IN_GAME,
    		InputConstants.Type.KEYSYM,
    		GLFW.GLFW_KEY_B,
    		"key.categories.inventory"
		));
        
        @SubscribeEvent
        public static void registerBindings(RegisterKeyMappingsEvent event) {
        	event.register(BACKPACK_MAPPING.get());
        }
    }
    
    @Mod.EventBusSubscriber(modid = MODID, bus = Bus.FORGE, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        private static final Minecraft client = Minecraft.getInstance();
        
        // Detect keybinding to open backpacks when worn
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
        	if (client.player != null) {
	        	if (event.phase == TickEvent.Phase.END) {	            
	        		while (ClientModEvents.BACKPACK_MAPPING.get().consumeClick()) {
	    	            LocalPlayer player = client.player;
	        			ItemStack pack = ItemStack.EMPTY;
	        			boolean ender = false;
	        			for (ItemStack a : player.getArmorSlots()) {
	        				if (!a.isEmpty()) {
	        					if (a.is(BACKPACK.get())) { pack = a; break; }
	        					if (a.is(ENDER_BACKPACK.get())) { pack = a; ender = true; break; }
	        				}
	        			}
	        			if (!pack.isEmpty()) NETWORK.sendToServer(new BackpackPacket(pack, ender));
	        		}
	        	}
        	}
        }
    }

	@SuppressWarnings("serial")
	public static final LinkedHashMap<DyeColor, Item> WOOL = new LinkedHashMap<DyeColor, Item>() {{
		put(DyeColor.WHITE, Items.WHITE_WOOL);
		put(DyeColor.LIGHT_GRAY, Items.LIGHT_GRAY_WOOL);
		put(DyeColor.GRAY, Items.GRAY_WOOL);
		put(DyeColor.BLACK, Items.BLACK_WOOL);
		put(DyeColor.BROWN, Items.BROWN_WOOL);
		put(DyeColor.RED, Items.RED_WOOL);
		put(DyeColor.ORANGE, Items.ORANGE_WOOL);
		put(DyeColor.YELLOW, Items.YELLOW_WOOL);
		put(DyeColor.LIME, Items.LIME_WOOL);
		put(DyeColor.GREEN, Items.GREEN_WOOL);
		put(DyeColor.LIGHT_BLUE, Items.LIGHT_BLUE_WOOL);
		put(DyeColor.CYAN, Items.CYAN_WOOL);
		put(DyeColor.BLUE, Items.BLUE_WOOL);
		put(DyeColor.PURPLE, Items.PURPLE_WOOL);
		put(DyeColor.MAGENTA, Items.MAGENTA_WOOL);
		put(DyeColor.PINK, Items.PINK_WOOL);
	}};

    public static MutableComponent ct (String s, Object... args) { return Component.translatable(String.format(s, MODID), args); } 
    
	private static void pocketText(List<Component> tooltip, int inc, int max) {
		tooltip.add(ct("gui.%s.pockets").withStyle(ChatFormatting.YELLOW));
		tooltip.add(Component.empty());
		tooltip.add(ct("gui.%s.pockets.increase", inc).withStyle(ChatFormatting.GRAY));
		tooltip.add(ct("gui.%s.pockets.capacity", max).withStyle(ChatFormatting.GRAY));
	}
}
