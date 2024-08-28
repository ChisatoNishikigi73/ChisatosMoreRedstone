package org.lycorecocafe.cmrs.mixin.mixins.client;

import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@OnlyIn(Dist.CLIENT)
@Mixin(SoundManager.class)
public interface SoundManagerMixin {
    @Accessor("soundEngine")
    SoundEngine getSoundEngine();
}
