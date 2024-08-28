package org.lycorecocafe.cmrs.mixin.mixins.client;

import com.google.common.collect.Multimap;
import com.mojang.blaze3d.audio.Library;
import com.mojang.blaze3d.audio.Listener;
import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.SharedConstants;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lycorecocafe.cmrs.mixin.SoundBufferLibraryHelper;
import org.lycorecocafe.cmrs.mixin.SoundEngineHelper;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@OnlyIn(Dist.CLIENT)
@Mixin(SoundEngine.class)
public class SoundEngineMixin implements SoundEngineHelper {
    @Final
    @Shadow
    private static Set<ResourceLocation> ONLY_WARN_ONCE;
    @Final
    @Shadow
    private static Marker MARKER;
    @Final
    @Shadow
    private static Logger LOGGER;
    @Unique
    SoundEngine soundEngine_f1 = (SoundEngine) (Object) this; // 将this转为SoundEngine类型
    @Unique
    SoundEngineInvoker soundEngine_this = (SoundEngineInvoker) soundEngine_f1;
    @Shadow
    private boolean loaded;
    @Final
    @Shadow
    private ChannelAccess channelAccess;
    @Final
    @Shadow
    private Listener listener;
    @Final
    @Shadow
    private List<SoundEventListener> listeners;
    @Final
    @Shadow
    private Map<SoundInstance, Integer> soundDeleteTime;
    @Final
    @Shadow
    private Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel;
    @Shadow
    private int tickCount;
    @Final
    @Shadow
    private Multimap<SoundSource, SoundInstance> instanceBySource;
    @Final
    @Shadow
    private SoundBufferLibrary soundBuffers;
    @Final
    @Shadow
    private List<TickableSoundInstance> tickingSounds;

//    /**
//     * @author
//     * @reason
//     */
//    @Overwrite
//    public void play(SoundInstance p_120313_) {
//        if (this.loaded) {
//
//            p_120313_ = net.minecraftforge.client.ForgeHooksClient.playSound(soundEngine_f1, p_120313_);
//            if (p_120313_ != null && p_120313_.canPlaySound()) {
//                WeighedSoundEvents weighedsoundevents = p_120313_.resolve(soundEngine_f1.soundManager);
//                ResourceLocation resourcelocation = p_120313_.getLocation();
//                if (weighedsoundevents == null) {
//                    if (ONLY_WARN_ONCE.add(resourcelocation)) {
//                        LOGGER.warn(MARKER, "Unable to play unknown soundEvent: {}", (Object) resourcelocation);
//                    }
//
//                } else {
//                    Sound sound = p_120313_.getSound();
//                    if (sound == SoundManager.EMPTY_SOUND) {
//                        if (ONLY_WARN_ONCE.add(resourcelocation)) {
//                            LOGGER.warn(MARKER, "Unable to play empty soundEvent: {}", (Object) resourcelocation);
//                        }
//
//                    } else {
//                        float f = p_120313_.getVolume();
//                        float f1 = Math.max(f, 1.0F) * (float) sound.getAttenuationDistance();
//                        SoundSource soundsource = p_120313_.getSource();
//
//                        float f2 = soundEngine_this.invokeCalculateVolume(f, soundsource);
////                        float f2 = soundEngine_this.calculateVolume(f, soundsource);
//
//                        float f3 = soundEngine_this.invokeCalculatePitch(p_120313_);
//
//                        SoundInstance.Attenuation soundinstance$attenuation = p_120313_.getAttenuation();
//                        boolean flag = p_120313_.isRelative();
//                        if (f2 == 0.0F && !p_120313_.canStartSilent()) {
//                            LOGGER.debug(MARKER, "Skipped playing sound {}, volume was zero.", (Object) sound.getLocation());
//                        } else {
//                            Vec3 vec3 = new Vec3(p_120313_.getX(), p_120313_.getY(), p_120313_.getZ());
//                            if (!this.listeners.isEmpty()) {
//                                boolean flag1 = flag || soundinstance$attenuation == SoundInstance.Attenuation.NONE || this.listener.getListenerPosition().distanceToSqr(vec3) < (double) (f1 * f1);
//                                if (flag1) {
//                                    for (SoundEventListener soundeventlistener : this.listeners) {
//                                        soundeventlistener.onPlaySound(p_120313_, weighedsoundevents);
//                                    }
//                                } else {
//                                    LOGGER.debug(MARKER, "Did not notify listeners of soundEvent: {}, it is too far away to hear", (Object) resourcelocation);
//                                }
//                            }
//
//                            if (this.listener.getGain() <= 0.0F) {
//                                LOGGER.debug(MARKER, "Skipped playing soundEvent: {}, master volume was zero", (Object) resourcelocation);
//                            } else {
//                                boolean flag2 = SoundEngineHelper.shouldLoopAutomatically(p_120313_);
//                                //TODO: do something
//                                boolean flag3 = sound.shouldStream();
//                                CompletableFuture<ChannelAccess.ChannelHandle> completablefuture = this.channelAccess.createHandle(sound.shouldStream() ? Library.Pool.STREAMING : Library.Pool.STATIC);
//                                ChannelAccess.ChannelHandle channelaccess$channelhandle = completablefuture.join();
//                                if (channelaccess$channelhandle == null) {
//                                    if (SharedConstants.IS_RUNNING_IN_IDE) {
//                                        LOGGER.warn("Failed to create new sound handle");
//                                    }
//
//                                } else {
//                                    LOGGER.debug(MARKER, "Playing sound {} for event {}", sound.getLocation(), resourcelocation);
////                                    System.out.println("Playing sound " + sound.getLocation() + " for event " + resourcelocation);
//                                    this.soundDeleteTime.put(p_120313_, this.tickCount + 20);
//                                    this.instanceToChannel.put(p_120313_, channelaccess$channelhandle);
//                                    this.instanceBySource.put(soundsource, p_120313_);
//                                    channelaccess$channelhandle.execute((p_194488_) -> {
//                                        p_194488_.setPitch(f3);
//                                        p_194488_.setVolume(f2);
//                                        if (soundinstance$attenuation == SoundInstance.Attenuation.LINEAR) {
//                                            p_194488_.linearAttenuation(f1);
//                                        } else {
//                                            p_194488_.disableAttenuation();
//                                        }
//
//                                        p_194488_.setLooping(flag2 && !flag3);
//                                        p_194488_.setSelfPosition(vec3);
//                                        p_194488_.setRelative(flag);
//                                    });
//                                    final SoundInstance soundinstance = p_120313_;
//                                    if (!flag3) {
//
//                                        InputStream inputStream = Base64ToInputStream.convertBase64ToInputStream("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");
//                                        SoundBufferLibrary soundBufferLibrary = this.soundBuffers;
//                                        CompletableFuture<SoundBuffer> bufferFuture = ((SoundBufferLibraryHelper) soundBufferLibrary)
//                                                .getCompleteBufferFromStream(inputStream);
//
//                                        bufferFuture.thenAccept((p_194501_) -> {
//                                            channelaccess$channelhandle.execute((p_194495_) -> {
//                                                p_194495_.attachStaticBuffer(p_194501_);
//                                                p_194495_.play();
//                                                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.sound.PlaySoundSourceEvent(soundEngine_f1, soundinstance, p_194495_));
//                                            });
//                                        });
//
////                                        this.soundBuffers.getCompleteBuffer(sound.getPath()).thenAccept((p_194501_) -> {
////                                            channelaccess$channelhandle.execute((p_194495_) -> {
////                                                p_194495_.attachStaticBuffer(p_194501_);
////                                                p_194495_.play();
////                                                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.sound.PlaySoundSourceEvent(soundEngine_f1, soundinstance, p_194495_));
////                                            });
////                                        });
//
//                                    } else {
//                                        CompletableFuture<AudioStream> futureBuffer = NetworkSoundBuffer.getAudioStreamFromURL("https://pan.lycorecocafe.com/directlink/r/%E6%9D%82%E4%B8%83%E6%9D%82%E5%85%AB%E7%9A%84%E4%B8%9C%E8%A5%BF/ill_never_break_your_heart.ogg");
//
////                                        futureBuffer.thenAccept(soundBuffer -> {
////                                            ChannelAccess.ChannelHandle channelHandle = this.channelAccess.createHandle(Library.Pool.STATIC).join();
////                                            if (channelHandle != null) {
////                                                channelHandle.execute(channel -> {
////                                                    channel.attachStaticBuffer(soundBuffer);
////                                                    channel.play();
////                                                    net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.sound.PlayStreamingSourceEvent(soundEngine_f1, soundinstance, p_194498_));
////
////                                                });
////                                            }
////                                        });
//
//
//                                        futureBuffer.thenAccept((p_194504_) -> {
//                                            channelaccess$channelhandle.execute((p_194498_) -> {
//                                                p_194498_.attachBufferStream(p_194504_);
//                                                p_194498_.play();
//                                                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.sound.PlayStreamingSourceEvent(soundEngine_f1, soundinstance, p_194498_));
//                                            });
//                                        });
////
////
////
////                                        soundinstance.getStream(this.soundBuffers, sound, flag2).thenAccept((p_194504_) -> {
////                                            channelaccess$channelhandle.execute((p_194498_) -> {
////                                                p_194498_.attachBufferStream(p_194504_);
////                                                p_194498_.play();
////                                                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.sound.PlayStreamingSourceEvent(soundEngine_f1, soundinstance, p_194498_));
////                                            });
////                                        });
//
//
//                                        if (p_120313_ instanceof TickableSoundInstance) {
//                                            this.tickingSounds.add((TickableSoundInstance) p_120313_);
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
////    @Override
////    @Unique
////    public void playFromStream(SoundInstance soundInstance, InputStream inputStream){
////
////    }
//    }

    /**
     * @author
     * @reason
     */
    public void playMusic(SoundInstance p_120313_, InputStream inputStream) {
        if (this.loaded) {

            p_120313_ = net.minecraftforge.client.ForgeHooksClient.playSound(soundEngine_f1, p_120313_);
            if (p_120313_ != null && p_120313_.canPlaySound()) {
                WeighedSoundEvents weighedsoundevents = p_120313_.resolve(soundEngine_f1.soundManager);
                ResourceLocation resourcelocation = p_120313_.getLocation();
                Sound sound = p_120313_.getSound();

                float f = p_120313_.getVolume();
                float f1 = Math.max(f, 1.0F) * (float) sound.getAttenuationDistance();
                SoundSource soundsource = p_120313_.getSource();

                float f2 = soundEngine_this.invokeCalculateVolume(f, soundsource);

                float f3 = soundEngine_this.invokeCalculatePitch(p_120313_);

                SoundInstance.Attenuation soundinstance$attenuation = p_120313_.getAttenuation();
                boolean flag = p_120313_.isRelative();
                if (f2 == 0.0F && !p_120313_.canStartSilent()) {
                    LOGGER.debug(MARKER, "Skipped playing sound {}, volume was zero.", sound.getLocation());
                } else {
                    Vec3 vec3 = new Vec3(p_120313_.getX(), p_120313_.getY(), p_120313_.getZ());
                    if (!this.listeners.isEmpty()) {
                        boolean flag1 = flag || soundinstance$attenuation == SoundInstance.Attenuation.NONE || this.listener.getListenerPosition().distanceToSqr(vec3) < (double) (f1 * f1);
                        if (flag1) {
                            for (SoundEventListener soundeventlistener : this.listeners) {
                                soundeventlistener.onPlaySound(p_120313_, weighedsoundevents);
                            }
                        } else {
                            LOGGER.info(MARKER, "Did not notify listeners of soundEvent: {}, it is too far away to hear", resourcelocation);
                        }
                    }

                    if (this.listener.getGain() <= 0.0F) {
                        LOGGER.info(MARKER, "Skipped playing soundEvent: {}, master volume was zero", resourcelocation);
                    } else {
                        boolean flag2 = SoundEngineHelper.shouldLoopAutomatically(p_120313_);
                        //TODO: do something
                        boolean flag3 = sound.shouldStream();
                        CompletableFuture<ChannelAccess.ChannelHandle> completablefuture = this.channelAccess.createHandle(sound.shouldStream() ? Library.Pool.STREAMING : Library.Pool.STATIC);
                        ChannelAccess.ChannelHandle channelaccess$channelhandle = completablefuture.join();
                        if (channelaccess$channelhandle == null) {
                            if (SharedConstants.IS_RUNNING_IN_IDE) {
                                LOGGER.warn("Failed to create new sound handle");
                            }

                        } else {
                            LOGGER.info(MARKER, "Playing sound {} for event {}", sound.getLocation(), resourcelocation);
                            this.soundDeleteTime.put(p_120313_, this.tickCount + 20);
                            this.instanceToChannel.put(p_120313_, channelaccess$channelhandle);
                            this.instanceBySource.put(soundsource, p_120313_);
                            channelaccess$channelhandle.execute((p_194488_) -> {
                                p_194488_.setPitch(f3);
                                p_194488_.setVolume(f2);
                                if (soundinstance$attenuation == SoundInstance.Attenuation.LINEAR) {
                                    p_194488_.linearAttenuation(f1);
                                } else {
                                    p_194488_.disableAttenuation();
                                }

                                p_194488_.setLooping(flag2 && !flag3);
                                p_194488_.setSelfPosition(vec3);
                                p_194488_.setRelative(flag);
                            });
                            final SoundInstance soundinstance = p_120313_;

                            if (!flag3) {
                                SoundBufferLibrary soundBufferLibrary = this.soundBuffers;
                                CompletableFuture<SoundBuffer> bufferFuture = ((SoundBufferLibraryHelper) soundBufferLibrary)
                                        .getCompleteBufferFromStream(inputStream);

                                bufferFuture.thenAccept((p_194501_) -> {
                                    channelaccess$channelhandle.execute((p_194495_) -> {
                                        p_194495_.attachStaticBuffer(p_194501_);
                                        p_194495_.play();
                                        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.sound.PlaySoundSourceEvent(soundEngine_f1, soundinstance, p_194495_));
                                    });
                                });

                            } else {
                                SoundBufferLibrary soundBufferLibrary = this.soundBuffers;
                                CompletableFuture<AudioStream> bufferFuture = ((SoundBufferLibraryHelper) soundBufferLibrary)
                                        .getStreamFromStream(inputStream, flag2);

                                bufferFuture.thenAccept((p_194504_) -> {
                                    channelaccess$channelhandle.execute((p_194498_) -> {
                                        p_194498_.attachBufferStream(p_194504_);
                                        p_194498_.play();
                                        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.sound.PlayStreamingSourceEvent(soundEngine_f1, soundinstance, p_194498_));
                                    });
                                });

                                if (p_120313_ instanceof TickableSoundInstance) {
                                    this.tickingSounds.add((TickableSoundInstance) p_120313_);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void playFromStream(SoundInstance soundInstance, InputStream inputStream) {
        playMusic(soundInstance, inputStream);
    }


}
