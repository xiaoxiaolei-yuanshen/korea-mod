package com.asia.korea.gui;

import com.asia.korea.Config;
import com.asia.korea.KOR;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModSettingsScreen extends Screen {
    private final Screen parentScreen;
    private Checkbox gratitudeModeCheckbox;
    private Checkbox textTriggerCheckbox;
    private Checkbox mobJumpToPlayerCheckbox;
    private Checkbox hostileMobDeathCheckbox;
    private Checkbox musicPlayCheckbox;
    private Checkbox villageHeroCheckbox;
    private Checkbox loyaltyModeCheckbox;

    public ModSettingsScreen(Screen parentScreen) {
        super(Component.translatable("gui.kor.settings.title"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        int y = this.height / 4 - 16;
        int spacing = 24;

        gratitudeModeCheckbox = new Checkbox(
            this.width / 2 - 100, y,
            200, 20,
            Component.translatable("gui.kor.settings.gratitude_mode"),
            KOR.gratitudeModeEnabled()
        );
        this.addRenderableWidget(gratitudeModeCheckbox);

        textTriggerCheckbox = new Checkbox(
            this.width / 2 - 100, y + spacing,
            200, 20,
            Component.translatable("gui.kor.settings.text_trigger"),
            KOR.textTriggerEnabled()
        );
        this.addRenderableWidget(textTriggerCheckbox);

        mobJumpToPlayerCheckbox = new Checkbox(
            this.width / 2 - 100, y + spacing * 2,
            200, 20,
            Component.translatable("gui.kor.settings.mob_jump_to_player"),
            KOR.mobJumpToPlayerEnabled()
        );
        this.addRenderableWidget(mobJumpToPlayerCheckbox);

        hostileMobDeathCheckbox = new Checkbox(
            this.width / 2 - 100, y + spacing * 3,
            200, 20,
            Component.translatable("gui.kor.settings.hostile_mob_death"),
            KOR.hostileMobDeathEnabled()
        );
        this.addRenderableWidget(hostileMobDeathCheckbox);

        musicPlayCheckbox = new Checkbox(
            this.width / 2 - 100, y + spacing * 4,
            200, 20,
            Component.translatable("gui.kor.settings.music_play"),
            KOR.musicPlayEnabled()
        );
        this.addRenderableWidget(musicPlayCheckbox);

        villageHeroCheckbox = new Checkbox(
            this.width / 2 - 100, y + spacing * 5,
            200, 20,
            Component.translatable("gui.kor.settings.village_hero"),
            KOR.villageHeroEnabled()
        );
        this.addRenderableWidget(villageHeroCheckbox);

        loyaltyModeCheckbox = new Checkbox(
            this.width / 2 - 100, y + spacing * 6,
            200, 20,
            Component.translatable("gui.kor.settings.loyalty_mode"),
            KOR.loyaltyModeEnabled()
        );
        this.addRenderableWidget(loyaltyModeCheckbox);

        Button doneButton = Button.builder(
            Component.translatable("gui.done"),
            button -> this.onClose()
        ).bounds(
            this.width / 2 - 100, this.height / 4 + 168,
            200, 20
        ).build();
        this.addRenderableWidget(doneButton);
    }

    @Override
    public void onClose() {
        // 保存设置
        boolean newGratitudeMode = gratitudeModeCheckbox.selected();
        boolean newTextTrigger = textTriggerCheckbox.selected();
        boolean newMobJump = mobJumpToPlayerCheckbox.selected();
        boolean newHostileMobDeath = hostileMobDeathCheckbox.selected();
        boolean newMusicPlay = musicPlayCheckbox.selected();
        boolean newVillageHero = villageHeroCheckbox.selected();
        boolean newLoyaltyMode = loyaltyModeCheckbox.selected();

        Config.setGratitudeModeEnabled(newGratitudeMode);
        Config.setTextTriggerEnabled(newTextTrigger);
        Config.setMobJumpToPlayerEnabled(newMobJump);
        Config.setHostileMobDeathEnabled(newHostileMobDeath);
        Config.setMusicPlayEnabled(newMusicPlay);
        Config.setVillageHeroEnabled(newVillageHero);
        Config.setLoyaltyModeEnabled(newLoyaltyMode);

        // 如果关闭恩情模式，停止当前正在播放的音乐
        if (!newGratitudeMode) {
            com.asia.korea.events.SunArmorMusicHandler.stopCurrentMusic();
        }

        // 如果关闭音乐播放模式，停止当前正在播放的音乐
        if (!newMusicPlay) {
            com.asia.korea.events.SunArmorMusicHandler.stopCurrentMusic();
        }

        this.minecraft.setScreen(parentScreen);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 4 - 40, 16777215);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}