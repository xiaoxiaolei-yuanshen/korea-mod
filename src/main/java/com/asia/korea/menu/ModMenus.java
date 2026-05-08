package com.asia.korea.menu;

import com.asia.korea.KOR;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, KOR.MODID);

    public static final RegistryObject<MenuType<KimchiJarMenu>> KIMCHI_JAR_MENU = MENUS.register("kimchi_jar_menu",
            () -> IForgeMenuType.create((windowId, inv, data) -> new KimchiJarMenu(windowId, inv, data)));
}