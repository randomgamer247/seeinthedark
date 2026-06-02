package com.randomgamer247.seeinthedark;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.serialization.Codec;
import com.randomgamer247.seeinthedark.compat.curios.client.CuriosRendererRegistration;
import com.randomgamer247.seeinthedark.compat.curios.item.NightVisionGogglesCurioItem;
import com.randomgamer247.seeinthedark.item.NightVisionGogglesItem;
import com.randomgamer247.seeinthedark.network.NightVisionToggleData;
import com.randomgamer247.seeinthedark.util.GogglesKeyConflictContext;
import com.randomgamer247.seeinthedark.util.ItemEnergyStorage;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.randomgamer247.seeinthedark.Config.NIGHT_VISION_GOGGLES_ENERGY_RECEIVE;
import static com.randomgamer247.seeinthedark.Config.NIGHT_VISION_GOGGLES_MAX_ENERGY;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SeeintheDark.MODID)
public class SeeintheDark {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "seeinthedark";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();


    // Curios compatibility
    public static final boolean CURIOS_LOADED = ModList.get().isLoaded("curios");

    private static final class GuardedCuriosCompatAccess {
        static Item makeNightVisionGogglesCurioItem(Item.Properties props) {
            return new NightVisionGogglesCurioItem(props);
        }
    }


    // Create a key mapping:
    public static final Lazy<KeyMapping> TOGGLE_KEY = Lazy.of(() ->
            new KeyMapping(
                    "key.seeinthedark.toggle_key",
                    new GogglesKeyConflictContext(),
                    InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_V),
                    "key.categories.misc"
            )
    );


    // Create a Deferred Register to hold Blocks which will all be registered under the "seeinthedark" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "seeinthedark" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "seeinthedark" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    // Create a Deferred register for data components
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);
    // Create a Deferred register for attributes
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, MODID);


    // Create items:
    public static final DeferredItem<Item> NIGHT_VISION_GOGGLES_ITEM = ITEMS.registerItem("night_vision_goggles",
            props -> CURIOS_LOADED ?
                GuardedCuriosCompatAccess.makeNightVisionGogglesCurioItem(props) :
                new NightVisionGogglesItem(props),
            new Item.Properties().stacksTo(1));


    // Create data components:
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ITEM_ENERGY_COMPONENT = DATA_COMPONENTS.registerComponentType(
            "item_energy",
            builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> ITEM_ENABLED_COMPONENT = DATA_COMPONENTS.registerComponentType(
            "item_enabled",
            builder -> builder
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
    );


    // Create attributes:
    public static final Holder<Attribute> NIGHT_VISION_SCALE_ATTRIBUTE = ATTRIBUTES.register("night_vision_scale", () -> new RangedAttribute(
            "attributes.seeinthedark.night_vision_scale", 0, 0, 1.0
    ).setSyncable(true));
    public static final ResourceLocation NIGHT_VISION_GOGGLES_SCALE_MODIFIER_RESOURCE_LOCATION = ResourceLocation.fromNamespaceAndPath(MODID, "night_vision_goggles_scale_modifier");


            // Creates a creative tab with the id "seeinthedark:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SITD_TAB = CREATIVE_MODE_TABS.register("sitd_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.seeinthedark")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> NIGHT_VISION_GOGGLES_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(NIGHT_VISION_GOGGLES_ITEM.get());
            }).build());


    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public SeeintheDark(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register data components
        DATA_COMPONENTS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        ATTRIBUTES.register(modEventBus);

        // Optionally register curios capabilities
        if (ModList.get().isLoaded("curios")){
            LOGGER.info("Curios was detected, registering SeeInTheDark curio renderers.");
            modEventBus.addListener(CuriosRendererRegistration::registerCuriosRenderers);
        }

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SPEC_SERVER);
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.SPEC_CLIENT);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // TODO: Remove this if common setup code is not needed
    }

    /**
     * A class for generic seeinthedark events
     */
    @EventBusSubscriber(modid = MODID)
    public static class GenericModEvents {
        // Apply the energy component and the enabled component to night vision goggles
        @SubscribeEvent
        public static void modifyComponents(ModifyDefaultComponentsEvent event) {
            event.modify(NIGHT_VISION_GOGGLES_ITEM.get(), builder ->
                    builder.set(ITEM_ENERGY_COMPONENT.get(), 0)
            );
            event.modify(NIGHT_VISION_GOGGLES_ITEM.get(), builder ->
                    builder.set(ITEM_ENABLED_COMPONENT.get(), false)
            );
        }

        // Register night vision goggles to use the ItemEnergyStorage energy storage capability implementation
        @SubscribeEvent
        public static void registerCapabilities(RegisterCapabilitiesEvent event) {
            event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, context) -> new ItemEnergyStorage(stack, NIGHT_VISION_GOGGLES_MAX_ENERGY, NIGHT_VISION_GOGGLES_ENERGY_RECEIVE), NIGHT_VISION_GOGGLES_ITEM.get());
        }

        // Register a payload handler to transmit night vision goggles toggle key presses to the server
        @SubscribeEvent
        public static void registerPayloadHandlers(final RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar("1");
            registrar.playToServer(
                    NightVisionToggleData.TYPE,
                    NightVisionToggleData.STREAM_CODEC,
                    NightVisionGogglesItem::handleToggleData
            );
        }

        // Add custom attributes
        @SubscribeEvent
        public static void modifyDefaultAttributes(EntityAttributeModificationEvent event) {
            event.add(EntityType.PLAYER, NIGHT_VISION_SCALE_ATTRIBUTE);
        }
    }

    /**
     * A class containing events which only need to be handled on the client side
     */
    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        // Register the night vision goggles toggle key binding
        @SubscribeEvent
        public static void registerBindings(RegisterKeyMappingsEvent event) {
            event.register(TOGGLE_KEY.get());
        }
    }
}
