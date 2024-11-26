package net.chenjiaf.catcherorbmod.item;

import net.chenjiaf.catcherorbmod.CatcherOrbMod;
import net.chenjiaf.catcherorbmod.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CatcherOrbMod.MODID);

    public static final RegistryObject<CreativeModeTab> CATCHER_ORB_TAB = CREATIVE_MODE_TABS.register("catcher_orb_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.ORB.get()))
                    .title(Component.translatable("creativetab.catcher_orb_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.ORB.get());
                        pOutput.accept(ModItems.CREATIVE_ORB.get());
                        pOutput.accept(ModBlocks.MOD_PORTAL.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
