package org.lycorecocafe.cmrs.mixin;

import net.minecraft.client.resources.sounds.SoundInstance;

import java.io.InputStream;

public interface SoundEngineHelper {
    private static boolean requiresManualLooping(SoundInstance p_120316_) {
        return p_120316_.getDelay() > 0;
    }

    static boolean shouldLoopAutomatically(SoundInstance p_120322_) {
        return p_120322_.isLooping() && !requiresManualLooping(p_120322_);
    }

    void playFromStream(SoundInstance soundInstance, InputStream inputStream);
}