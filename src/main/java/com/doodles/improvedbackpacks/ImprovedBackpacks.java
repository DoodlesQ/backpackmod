package com.doodles.improvedbackpacks;

import com.doodles.improvedbackpacks.content.SewingTableEntity;
import com.doodles.improvedbackpacks.content.SewingTableBlock;
import com.doodles.improvedbackpacks.content.SewingTableContainer;
import com.doodles.improvedbackpacks.content.SewingTableScreen;
import com.doodles.improvedbackpacks.recipe.SewingRecipe;
import com.doodles.improvedbackpacks.recipe.SewingRecipeSerializer;
import com.doodles.improvedbackpacks.recipe.SewingRecipeType;
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
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
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
import net.minecraftforge.common.extensions.IForgeMenuType;
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

import java.util.function.Supplier;

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
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_JSONS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    // Register Sewing Table
    public static final RegistryObject<SewingTableBlock> SEWING_TABLE = BLOCKS.register("sewing_table", () -> new SewingTableBlock(BlockBehaviour.Properties.of()
    		.destroyTime(2.5F)
    		.sound(SoundType.WOOD)
    		.mapColor(MapColor.WOOD)
    		.instrument(NoteBlockInstrument.BASS)
    		));
    public static final RegistryObject<Item> SEWING_TABLE_ITEM = ITEMS.register("sewing_table", () -> new BlockItem(SEWING_TABLE.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<SewingTableEntity>> SEWING_TABLE_ENTITY = BLOCK_ENTITY_TYPES.register("sewing_table",
    		() -> BlockEntityType.Builder.of(SewingTableEntity::new, SEWING_TABLE.get()).build(null)
	);
	public static final RegistryObject<MenuType<SewingTableContainer>> SEWING_TABLE_MENU = MENU_TYPES.register("sewing_table",
			() -> IForgeMenuType.create((windowid, inv, data) -> new SewingTableContainer(windowid, inv.player, data.readBlockPos()))
	);
	public static final RegistryObject<RecipeType<SewingRecipe>> SEWING_RECIPE_TYPE = RECIPE_TYPES.register("sewingtype", () -> RecipeType.simple(ImprovedBackpacks.SEWING_RECIPE_TYPE.getId()));
	public static final RegistryObject<RecipeSerializer<SewingRecipe>> SEWING_RECIPE_JSON = RECIPE_JSONS.register("sewingjson", SewingRecipeSerializer::new);
    
    // Sewing Spool
    public static final RegistryObject<Item> SEWING_SPOOL = ITEMS.register("spool", () -> new Item(new Item.Properties()));

    // Add Items to Creative Tab
    public static final RegistryObject<CreativeModeTab> BACKPACK_TAB = CREATIVE_MODE_TABS.register("backpack_tab", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup." + MODID + ".backpack_tab"))
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
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {        	
        	event.enqueueWork(
        		// Register Screens
    	        () -> MenuScreens.register(ImprovedBackpacks.SEWING_TABLE_MENU.get(), SewingTableScreen::new)
    	    );
        }
    }
}
