package com.asia.korea.events;

import com.asia.korea.KOR;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import net.minecraft.util.RandomSource;

@Mod.EventBusSubscriber(modid = "kor", value = Dist.CLIENT)
public class SunArmorMusicHandler {

    // 随机数生成器
    private static final RandomSource random = RandomSource.create();
    
    // 音乐播放冷却时间（刻）- 使用最长音乐的时长
    private static final int MUSIC_COOLDOWN = 258 * 20; // 258秒 * 20刻/秒
    
    // 记录每个玩家的音乐播放状态
    private static final Map<UUID, Long> lastMusicPlayTime = new HashMap<>();
    private static final Map<UUID, String> currentMusicName = new HashMap<>();
    private static final Map<UUID, Long> musicEndTime = new HashMap<>();
    
    // 上次游戏暂停状态
    private static boolean wasPaused = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        
        // 检查游戏是否暂停
        if (minecraft.isPaused()) {
            wasPaused = true;
            return; // 游戏暂停时不进行音乐状态检查
        }
        
        // 检查游戏是否从暂停状态恢复
        if (wasPaused) {
            wasPaused = false;
            // 游戏恢复时不需要特殊处理，让音乐自然继续
        }

        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        // 检查音乐是否应该结束
        UUID playerId = minecraft.player.getUUID();
        if (musicEndTime.containsKey(playerId)) {
            long endTime = musicEndTime.get(playerId);
            if (System.currentTimeMillis() >= endTime) {
                // 音乐结束，重置状态
                KOR.currentSunArmorMusic = 0;
                currentMusicName.remove(playerId);
                musicEndTime.remove(playerId);
            }
        }
    }

    /**
     * 播放随机音乐
     */
    @OnlyIn(Dist.CLIENT)
    public static void playRandomMusic(net.minecraft.world.entity.player.Player player) {
        if (player == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.isPaused()) {
            return;
        }

        UUID playerId = player.getUUID();
        long currentTime = player.level().getGameTime();
        long lastPlayTime = lastMusicPlayTime.getOrDefault(playerId, 0L);

        // 检查冷却时间是否已过
        if (currentTime - lastPlayTime < MUSIC_COOLDOWN && lastPlayTime != 0) {
            return;
        }

        // 检查音乐播放开关是否开启
        if (!KOR.musicPlayEnabled()) {
            return;
        }

        // 如果已经有音乐在播放，先停止
        if (KOR.currentSunArmorMusic != 0) {
            stopCurrentMusic();
        }

        // 随机选择一首音乐
        int musicChoice = random.nextInt(3) + 1; // 1-3
        KOR.currentSunArmorMusic = musicChoice;

        net.minecraft.resources.ResourceLocation musicResource;
        String musicName;
        int musicDuration;

        switch (musicChoice) {
            case 1:
                musicResource = KOR.NIRUOSANDONG.get().getLocation();
                musicName = "阿悠悠 - 你若三冬";
                musicDuration = 258000; // 4分18秒
                break;
            case 2:
                musicResource = KOR.NIRUOSANDONG_DJ.get().getLocation();
                musicName = "阿悠悠 - 你若三冬 (将军进行曲)(DJ沈乐版)";
                musicDuration = 192000; // 3分12秒
                break;
            case 3:
                musicResource = KOR.NIRUOSANDONG_DJSU.get().getLocation();
                musicName = "阿悠悠 - 你若三冬 (将军进行曲)(0.8xDJ沈乐版)";
                musicDuration = 226000; // 3分46秒
                break;
            default:
                return;
        }

        // 创建并播放音乐实例
        net.minecraft.sounds.SoundEvent soundEvent = net.minecraft.sounds.SoundEvent.createVariableRangeEvent(musicResource);
        SimpleSoundInstance soundInstance = new SimpleSoundInstance(
                soundEvent.getLocation(),
                net.minecraft.sounds.SoundSource.RECORDS, // 使用唱片机/音符盒声音栏
                0.3F, // 音量调小
                1.0F,
                random,
                false,
                0,
                net.minecraft.client.resources.sounds.SimpleSoundInstance.Attenuation.NONE,
                player.getX(),
                player.getY(),
                player.getZ(),
                false
        );

        SoundManager soundManager = minecraft.getSoundManager();
        soundManager.play(soundInstance);
        lastMusicPlayTime.put(playerId, currentTime);

        // 记录音乐结束时间
        long endTime = System.currentTimeMillis() + musicDuration;
        musicEndTime.put(playerId, endTime);
        currentMusicName.put(playerId, musicName);

        // 在聊天栏显示播放的音乐
        player.displayClientMessage(
                net.minecraft.network.chat.Component.translatable("message.kor.music_playing", 
                    musicName, 
                    (musicDuration / 60000), 
                    ((musicDuration % 60000) / 1000)),
                true
        );
    }

    /**
     * 停止当前播放的音乐
     */
    @OnlyIn(Dist.CLIENT)
    public static void stopCurrentMusic() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        SoundManager soundManager = minecraft.getSoundManager();
        
        // 停止唱片机/音符盒声音栏的音乐
        soundManager.stop(null, net.minecraft.sounds.SoundSource.RECORDS);

        UUID playerId = minecraft.player.getUUID();
        KOR.currentSunArmorMusic = 0;
        currentMusicName.remove(playerId);
        musicEndTime.remove(playerId);
        lastMusicPlayTime.remove(playerId);
    }

    /**
     * 重置音乐状态（用于世界切换或玩家登录）
     */
    public static void resetMusicState() {
        lastMusicPlayTime.clear();
        currentMusicName.clear();
        musicEndTime.clear();
        KOR.currentSunArmorMusic = 0;
    }
}
