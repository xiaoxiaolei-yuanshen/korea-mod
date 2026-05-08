package com.asia.korea.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.chat.Component;

import java.util.List;

public class SunLightBook extends Item {
    
    private static final int MAX_DAMAGE = 380;
    private static final int BURN_DURATION = 200;
    private static final int SLOW_DURATION = 200;
    private static final double SEARCH_RADIUS = 25.0D;
    private static final int BURN_DAMAGE = 5;
    private static final int COOLDOWN_TICKS = 100;
    
    public SunLightBook() {
        super(new Item.Properties()
                .durability(MAX_DAMAGE)
                .rarity(Rarity.RARE)
                .setNoRepair());
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }
        
        if (!level.isClientSide) {
            applySunLightEffect(level, player);
            stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
            level.playSound(null, player.getX(), player.getY(), player.getZ(), 
                    SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }
        
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
    
    private void applySunLightEffect(Level level, Player player) {
        AABB searchBox = player.getBoundingBox().inflate(SEARCH_RADIUS);
        List<Mob> nearbyMobs = level.getEntitiesOfClass(Mob.class, searchBox);
        
        for (Mob mob : nearbyMobs) {
            if (mob.isAlive() && !(mob instanceof net.minecraft.world.entity.npc.Villager)) {
                applyEffectsToEntity(mob, player, player.getItemInHand(InteractionHand.MAIN_HAND));
            }
        }
    }
    
    private void applyEffectsToEntity(LivingEntity entity, Player player, ItemStack stack) {
        int fireAspectLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, stack);
        int totalDamage = BURN_DAMAGE + fireAspectLevel;
        MobEffectInstance burnEffect = new MobEffectInstance(
                MobEffects.GLOWING,
                BURN_DURATION,
                0,
                false,
                true
        );
        entity.addEffect(burnEffect);
        
        MobEffectInstance slowEffect = new MobEffectInstance(
                MobEffects.MOVEMENT_SLOWDOWN,
                SLOW_DURATION,
                2,
                false,
                true
        );
        entity.addEffect(slowEffect);
        
        entity.setSecondsOnFire(BURN_DURATION / 20);
        
        entity.hurt(entity.level().damageSources().indirectMagic(player, player), totalDamage);
    }
    
    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == net.minecraft.world.item.Items.PAPER;
    }
    
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }
    
    @Override
    public int getEnchantmentValue() {
        return 15;
    }
    
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.world.item.enchantment.Enchantment enchantment) {
        String enchantmentId = net.minecraft.core.registries.BuiltInRegistries.ENCHANTMENT.getKey(enchantment).toString();
        return enchantmentId.equals("minecraft:mending") || 
               enchantmentId.equals("minecraft:unbreaking") ||
               enchantmentId.equals("minecraft:fire_aspect");
    }
    
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }
    
    @Override
    public int getUseDuration(ItemStack stack) {
        return 0;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.level.Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("item.kor.sun_light_book.desc"));
    }
}
