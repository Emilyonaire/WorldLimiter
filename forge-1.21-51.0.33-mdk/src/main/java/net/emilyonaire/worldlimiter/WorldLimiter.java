package net.emilyonaire.worldlimiter;

import java.io.File;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
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
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(WorldLimiter.MODID)
public class WorldLimiter
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "worldlimiter";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();




    public WorldLimiter()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);



        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }

    }
    //Forge event bus subscriber for client-side events
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientForgeEvents
    {
        @SubscribeEvent
        public static void onScreenInit(ScreenEvent.Init.Post event) {
            if (event.getScreen() instanceof SelectWorldScreen selectWorldScreen) {
                //create text for limiter/counter (not a button, just saying how many worlds we have vs how many we can have)

                //get count of worlds


                File savesDir = new File(Minecraft.getInstance().gameDirectory, "saves");
                int worldCount = 0;
                if (savesDir.exists() && savesDir.isDirectory()) {
                    File[] files = savesDir.listFiles(File::isDirectory);
                    if (files != null) {
                        worldCount = files.length;
                    }
                }

                //log world count
                LOGGER.info("World Count: " + worldCount);

                int worldLimit = Config.WORLD_LIMIT.get();

                //IF world limit is -1, set to infinite
                if (worldLimit == -1) {
                    worldLimit = Integer.MAX_VALUE;
                }

                //NEED TO GREY OUT CREATE NEW WORLD BUTTON IF LIMIT REACHED/OVER LIMIT
                Component limiterText = Component.literal("" + worldCount + "/" + worldLimit);
                if (worldCount >= worldLimit) {
                    //find create new world button and grey it out
                    for (var widget : event.getListenersList()) {
                        if (widget instanceof Button button) {
                            Component buttonText = button.getMessage();
                            if (buttonText.getString().equals("Create New World")) {
                                button.active = false;
                                LOGGER.info("Create New World button disabled due to world limit reached.");

                                //set text on limit button to red.
                                limiterText = Component.literal("" + worldCount + "/" + worldLimit).withStyle(style -> style.withColor(0xFF0000));
                            }
                        }
                    }
                }

                //text to show world count vs limit
//                Component counterText = Component.literal("" + worldCount + "/" + worldLimit);

                int pWidth = 40;
                Button limiterLabel = Button.builder(limiterText, button -> {
                        // No action needed, just a label
                        LOGGER.info("hehehe someone got curious if it was a button.");
                    })
                    .bounds(selectWorldScreen.width / 2 + (160), selectWorldScreen.height - 52, pWidth, 20)

                    .build();

                event.addListener(limiterLabel);


            }
        }
    }

}
