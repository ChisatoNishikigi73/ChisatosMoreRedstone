package org.lycorecocafe.cmrs.utils.game.music;

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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lycorecocafe.cmrs.CMRS;
import org.lycorecocafe.cmrs.blockentity.MusicBoxBlockEntity;
import org.lycorecocafe.cmrs.mixin.SoundEngineHelper;
import org.lycorecocafe.cmrs.mixin.mixins.client.SoundManagerMixin;
import org.lycorecocafe.cmrs.network.MusicPlayerStatusChangedPacket;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class MusicPlayer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final MusicBoxBlockEntity blockEntity;
    private SoundInstance soundInstance;
    private byte[] musicData;
    private UUID sessionID;
    private String musicUrl;
    private STATUS status;
    private STATUS statusLocal;

    public MusicPlayer(MusicBoxBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        this.sessionID = UUID.randomUUID();
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

    @OnlyIn(Dist.CLIENT)
    public void play() {
        if (blockEntity instanceof MusicBoxBlockEntity) {
            if (blockEntity.getStatus().equals(STATUS.ERROR) || blockEntity.getStatus().equals(STATUS.NONE) || blockEntity.getStatus().equals(STATUS.DOWNLOADING))
                return;

            this.soundInstance = createDefaultSoundInstance(blockEntity);
            playSoundFromStream();

            blockEntity.setStatusLocal(STATUS.PLAYING);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void stopSound() {
        if (this.soundInstance != null) {
            SoundManager soundManager = Minecraft.getInstance().getSoundManager();
            soundManager.stop(this.soundInstance);
            this.soundInstance = null;

            blockEntity.setStatusLocal(STATUS.PAUSE);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void downloadMusic() {
        LOGGER.info("Downloading music[url={}] for Entity[{}]", blockEntity.getMusicUrl(), this.blockEntity.getBlockPos());
        if (blockEntity.getStatus().equals(STATUS.PLAYING)) {
            stopSound();
        }

        blockEntity.setStatusLocal(STATUS.DOWNLOADING);

        NetworkSoundBuffer.getInputStreamFromURL(blockEntity.getMusicUrl()).thenAccept(inputStream -> {
            try {
                this.musicData = saveStreamToMusicData(inputStream);
                blockEntity.setStatusLocal(STATUS.STOPPING);
            } catch (IOException e) {
                blockEntity.setStatusLocal(STATUS.ERROR);
                LOGGER.error("Error downloading music", e);
            }
        });
        CMRS.CHANNEL.sendToServer(new MusicPlayerStatusChangedPacket(blockEntity.getBlockPos(), getStatus()));
    }

    @OnlyIn(Dist.CLIENT)
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

    public String getMusicUrl() {
        return this.musicUrl;
    }

    // DO NOT USE THIS IN MUSIC BOX BLOCK ENTITY
    @Deprecated
    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    public byte[] getMusicData() {
        return musicData;
    }

    public void setMusicData(byte[] musicData) {
        this.musicData = musicData;
    }

    public STATUS getStatus() {
        return this.status;
    }

    // DO NOT USE THIS IN MUSIC BOX BLOCK ENTITY
    @Deprecated
    public void setStatus(STATUS status) {
        this.status = status;
    }

    public STATUS getStatusLocal() {
        return statusLocal;
    }

    public void setStatusLocal(STATUS statusLocal) {
        this.statusLocal = statusLocal;
    }

    public UUID getSessionID() {
        return sessionID;
    }

    public void setSessionID(UUID sessionID) {
        this.sessionID = sessionID;
    }

    public void clean() {
        this.musicData = null;
    }

    @OnlyIn(Dist.CLIENT)
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
        URL,
        DOWNLOADING,
        STOPPING,
        PLAYING,
        PAUSE,
        ERROR;

        @Override
        public String toString() {
            return switch (this) {
                case NONE -> "No music in this MusicBox";
                case URL -> "Has music";
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
