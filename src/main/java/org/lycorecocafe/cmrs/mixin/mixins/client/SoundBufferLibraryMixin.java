package org.lycorecocafe.cmrs.mixin.mixins.client;

import com.mojang.blaze3d.audio.OggAudioStream;
import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.Util;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.LoopingAudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lycorecocafe.cmrs.mixin.SoundBufferLibraryHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@OnlyIn(Dist.CLIENT)
@Mixin(SoundBufferLibrary.class)
public class SoundBufferLibraryMixin implements SoundBufferLibraryHelper {

    @Unique
    @Override
    public CompletableFuture<SoundBuffer> getCompleteBufferFromStream(InputStream inputStream) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OggAudioStream oggStream = new OggAudioStream(inputStream);
                SoundBuffer soundBuffer;
                try {
                    ByteBuffer byteBuffer = oggStream.readAll();
                    soundBuffer = new SoundBuffer(byteBuffer, oggStream.getFormat());
                } finally {
                    oggStream.close();
                }
                return soundBuffer;
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        }, Util.backgroundExecutor());
    }

    @Unique
    @Override
    public CompletableFuture<AudioStream> getStreamFromStream(InputStream inputStream, boolean looping) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return looping ? new LoopingAudioStream(OggAudioStream::new, inputStream) : new OggAudioStream(inputStream);
            } catch (IOException ioexception) {
                throw new CompletionException(ioexception);
            }
        }, Util.backgroundExecutor());
    }
}

