package org.lycorecocafe.cmrs.mixin;

import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.client.sounds.AudioStream;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

@OnlyIn(Dist.CLIENT)
public interface SoundBufferLibraryHelper {
    CompletableFuture<SoundBuffer> getCompleteBufferFromStream(InputStream inputStream);

    CompletableFuture<AudioStream> getStreamFromStream(InputStream inputStream, boolean looping);
}
