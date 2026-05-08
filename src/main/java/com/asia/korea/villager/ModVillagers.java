package com.asia.korea.villager;

import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.entity.Entity;
import com.asia.korea.KOR;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.List;

@Mod.EventBusSubscriber(modid = KOR.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModVillagers {

    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        // 检查是否是泡菜师职业
        if (event.getType() == com.asia.korea.villager.ModVillagerProfessions.KIMCHI_MAKER.get()) {
            List<VillagerTrades.ItemListing> level1Trades = event.getTrades().get(1);
            List<VillagerTrades.ItemListing> level2Trades = event.getTrades().get(2);
            List<VillagerTrades.ItemListing> level3Trades = event.getTrades().get(3);
            List<VillagerTrades.ItemListing> level4Trades = event.getTrades().get(4);
            List<VillagerTrades.ItemListing> level5Trades = event.getTrades().get(5);

            // 清除现有的交易
            level1Trades.clear();
            level2Trades.clear();
            level3Trades.clear();
            level4Trades.clear();
            level5Trades.clear();

            // 1级交易 - 只卖泡菜
            level1Trades.add((trader, rand) -> new MerchantOffer(
                new net.minecraft.world.item.ItemStack(Items.EMERALD, 3),
                new net.minecraft.world.item.ItemStack(KOR.SPICY_KIMCHI.get(), 2),
                16,
                10,
                0.02F
            ));

            // 2级交易 - 只卖海泡菜
            level2Trades.add((trader, rand) -> new MerchantOffer(
                new net.minecraft.world.item.ItemStack(Items.EMERALD, 1),
                new net.minecraft.world.item.ItemStack(Items.SEA_PICKLE, 6),
                16,
                10,
                0.02F
            ));

            // 3级交易 - 只卖韩式炸鸡
            level3Trades.add((trader, rand) -> new MerchantOffer(
                new net.minecraft.world.item.ItemStack(Items.EMERALD, 2),
                new net.minecraft.world.item.ItemStack(KOR.KOREAN_FRIED_CHICKEN.get(), 1),
                12,
                20,
                0.02F
            ));

            // 4级交易 - 只卖酸菜
            level4Trades.add((trader, rand) -> new MerchantOffer(
                new net.minecraft.world.item.ItemStack(Items.EMERALD, 2),
                new net.minecraft.world.item.ItemStack(KOR.SOUR_CABBAGE.get(), 3),
                12,
                20,
                0.02F
            ));

            // 5级交易 - 卖腌制缸和辣椒酱
            level5Trades.add((trader, rand) -> new MerchantOffer(
                new net.minecraft.world.item.ItemStack(Items.EMERALD, 8),
                new net.minecraft.world.item.ItemStack(KOR.KIMCHI_JAR_BLOCK.get(), 1),
                8,
                30,
                0.05F
            ));

            level5Trades.add((trader, rand) -> new MerchantOffer(
                new net.minecraft.world.item.ItemStack(Items.EMERALD, 2),
                new net.minecraft.world.item.ItemStack(KOR.HOT_SAUCE.get(), 1),
                16,
                30,
                0.05F
            ));
        }
    }
}
