package org.lycorecocafe.cmrs.utils.music;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.lycorecocafe.cmrs.mixin.SoundEngineHelper;
import org.lycorecocafe.cmrs.mixin.mixins.client.SoundManagerMixin;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.lycorecocafe.cmrs.utils.music.NetworkSoundBuffer.getInputStreamFromURL;

public class MusicPlayer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final BlockEntity blockEntity;
    private SoundInstance soundInstance;
    private String musicUrl;
    private byte[] musicData;
    private STATUS status;

    public MusicPlayer(BlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        this.status = STATUS.NONE;
    }

    public static byte[] saveStreamToMusicData(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytesRead);
        }
        return buffer.toByteArray();
    }

    public static InputStream createStreamFromMusicData(byte[] data) {
        return new ByteArrayInputStream(data);
    }

    public String getMusicUrl() {
        return this.musicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    public STATUS getStatus() {
        return status;
    }

    public void play() {
        this.soundInstance = createDefaultSoundInstance(blockEntity);
        playSoundFromStream();
        status = STATUS.PLAYING;
    }

    public void stopSound() {
        if (this.soundInstance != null) {
            SoundManager soundManager = Minecraft.getInstance().getSoundManager();
            soundManager.stop(this.soundInstance);
            this.soundInstance = null;
            this.status = STATUS.PAUSE;
        }
    }

    public void downloadMusic() {
        LOGGER.info("Downloading music[url={}] for Entity[{}]", this.musicUrl, this.blockEntity.getBlockPos());
        status = STATUS.DOWNLOADING;
        stopSound();
        getInputStreamFromURL(musicUrl).thenAccept(inputStream -> {
            try {
                this.musicData = saveStreamToMusicData(inputStream);
                status = STATUS.STOPPING;
            } catch (IOException e) {
                status = STATUS.ERROR;
                LOGGER.error("Error downloading music", e);
            }
        });
    }

    private void playSoundFromStream() {
        SoundManager soundManager = Minecraft.getInstance().getSoundManager();
        SoundEngine soundEngine = ((SoundManagerMixin) soundManager).getSoundEngine();

        if (soundEngine instanceof SoundEngineHelper) {
            InputStream musicStream = createStreamFromMusicData(musicData);
            ((SoundEngineHelper) soundEngine).playFromStream(soundInstance, musicStream);
        } else {
            throw new IllegalStateException("SoundEngine is not properly mixed in.");
        }
    }

    private SimpleSoundInstance createDefaultSoundInstance(BlockEntity blockEntity) {
        ResourceLocation soundLocation = new ResourceLocation("cmrs", "cmrs-music-" + UUID.randomUUID());
        SoundEvent soundEvent = new SoundEvent(soundLocation);
        SoundSource soundSource = SoundSource.BLOCKS;
        float volume = 3.0f;
        float pitch = 1.0f;
        RandomSource randomSource = RandomSource.create();
        BlockPos pos = blockEntity.getBlockPos();

        return new SimpleSoundInstance(
                soundEvent.getLocation(), // ResourceLocation
                soundSource,              // SoundSource
                volume,                   // 音量
                pitch,                    // 音高
                randomSource,             // RandomSource
                false,                    // 是否循环
                0,                        // 延迟
                SoundInstance.Attenuation.LINEAR, // 衰减
                pos.getX(),               // x 坐标
                pos.getY(),               // y 坐标
                pos.getZ(),               // z 坐标
                false                     // 是否相对
        );
    }

    public enum STATUS {
        NONE,
        DOWNLOADING,
        STOPPING,
        PLAYING,
        PAUSE,
        ERROR;

        @Override
        public String toString() {
            return switch (this) {
                case NONE -> "No music in this MusicBox";
                case DOWNLOADING -> "Downloading";
                case STOPPING -> "Stopping Playing";
                case PLAYING -> "Playing";
                case PAUSE -> "Playing Paused";
                case ERROR -> "An Error Occurred";
                default -> super.toString();
            };
        }
    }
}
