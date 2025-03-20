package com.doodles.improvedbackpacks;

import com.doodles.improvedbackpacks.blockentities.SewingTableEntity;
import com.doodles.improvedbackpacks.blocks.SewingTable;
import com.doodles.improvedbackpacks.menus.SewingTableMenu;
import com.doodles.improvedbackpacks.menus.SewingTableScreen;
import com.mojang.logging.LogUtils;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ImprovedBackpacks.MODID)
public class ImprovedBackpacks
{
    public static final String MODID = "improvedbackpacks";
    // Define logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create Deferred Registers
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Register Sewing Table
    public static final RegistryObject<SewingTable> SEWING_TABLE = BLOCKS.register("sewing_table", () -> new SewingTable(BlockBehaviour.Properties.of()
    		.strength(2.5F)
    		.sound(SoundType.WOOD)
    		.mapColor(MapColor.WOOD)
    		.instrument(NoteBlockInstrument.BASS)
    		.ignitedByLava()
    		));
    public static final RegistryObject<Item> SEWING_TABLE_ITEM = ITEMS.register("sewing_table", () -> new BlockItem(SEWING_TABLE.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<SewingTableEntity>> SEWING_TABLE_ENTITY = BLOCK_ENTITY_TYPES.register("sewing_table_entity", () -> BlockEntityType.Builder.of(SewingTableEntity::new, SEWING_TABLE.get()).build(null));

	public static final RegistryObject<MenuType<SewingTableMenu>> SEWING_TABLE_MENU = MENU_TYPES.register("sewing_table_menu", () -> new MenuType<>(SewingTableMenu::new, FeatureFlags.DEFAULT_FLAGS));
    
    // Example Item
    public static final RegistryObject<Item> SEWING_SPOOL = ITEMS.register("spool", () -> new Item(new Item.Properties()));

    // Add Items to Creative Tab
    public static final RegistryObject<CreativeModeTab> BACKPACK_TAB = CREATIVE_MODE_TABS.register("backpack_tab", () -> CreativeModeTab.builder()
			.title(Component.translatable("item_group." + MODID + ".backpack_tab"))
            .icon(() -> SEWING_TABLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(SEWING_TABLE_ITEM.get());
                output.accept(SEWING_SPOOL.get());
            }).build());

    public ImprovedBackpacks()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        //modEventBus.addListener(this::clientSetup);

        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        MENU_TYPES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        //modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        //LOGGER.info("HELLO FROM COMMON SETUP");

        //if (Config.logDirtBlock)
        //    LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        //Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }
	private void clientSetup(FMLClientSetupEvent event) {
	}

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        //LOGGER.info("Improved Backpacks");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            //LOGGER.info("HELLO FROM CLIENT SETUP");
            //LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        	
        	event.enqueueWork(
        		// Register Screens
    	        () -> MenuScreens.register(SEWING_TABLE_MENU.get(), SewingTableScreen::new)
    	    );
        }
    }
}
