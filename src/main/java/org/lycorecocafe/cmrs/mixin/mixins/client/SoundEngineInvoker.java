package org.lycorecocafe.cmrs.mixin.mixins.client;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@OnlyIn(Dist.CLIENT)
@Mixin(SoundEngine.class)
public interface SoundEngineInvoker {
    @Invoker("calculateVolume")
    float invokeCalculateVolume(float p_235258_, SoundSource p_235259_);

    @Invoker("calculatePitch")
    float invokeCalculatePitch(SoundInstance p_120325_);
}