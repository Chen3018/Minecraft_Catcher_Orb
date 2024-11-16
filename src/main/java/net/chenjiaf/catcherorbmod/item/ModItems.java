package net.chenjiaf.catcherorbmod.item;

import net.chenjiaf.catcherorbmod.CatcherOrbMod;
import net.chenjiaf.catcherorbmod.item.custom.OrbItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CatcherOrbMod.MODID);

    public static final RegistryObject<Item> ORB = ITEMS.register("orb",
            () -> new OrbItem(new Item.Properties()));
    public static final RegistryObject<Item> CREATIVE_ORB = ITEMS.register("creative_orb",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
