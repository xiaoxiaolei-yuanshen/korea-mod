package com.asia.korea.jei;

import com.asia.korea.KOR;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEIKimchiPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_UID = new ResourceLocation(KOR.MODID, "kimchi_jar_plugin");
    public static final ResourceLocation RECIPE_TYPE = new ResourceLocation(KOR.MODID, "kimchi_jar");
    public static final mezz.jei.api.recipe.RecipeType<KimchiJarRecipe> KIMCHI_JAR_RECIPE_TYPE = 
            mezz.jei.api.recipe.RecipeType.create(KOR.MODID, "kimchi_jar", KimchiJarRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        // 注册配方分类
        registration.addRecipeCategories(new KimchiJarRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // 创建配方列表
        List<KimchiJarRecipe> recipes = new ArrayList<>();
        
        // 添加配方：海泡菜 + 辣酱 → 泡菜 + 空瓶子
        recipes.add(new KimchiJarRecipe(
                new ItemStack(Items.SEA_PICKLE),
                new ItemStack(KOR.HOT_SAUCE.get()),
                new ItemStack(KOR.SPICY_KIMCHI.get()),
                new ItemStack(Items.GLASS_BOTTLE)
        ));
        
        // 添加配方：泡菜 + 腐肉 → 酸菜
        recipes.add(new KimchiJarRecipe(
                new ItemStack(KOR.SPICY_KIMCHI.get()),
                new ItemStack(Items.ROTTEN_FLESH),
                new ItemStack(KOR.SOUR_CABBAGE.get()),
                ItemStack.EMPTY
        ));
        
        // 注册配方
        registration.addRecipes(KIMCHI_JAR_RECIPE_TYPE, recipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // 注册腌制缸作为配方催化剂
        registration.addRecipeCatalyst(new ItemStack(KOR.KIMCHI_JAR_BLOCK.get()), KIMCHI_JAR_RECIPE_TYPE);
    }
}