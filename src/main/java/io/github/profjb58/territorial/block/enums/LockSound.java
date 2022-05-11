package io.github.profjb58.territorial.block.enums;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public enum LockSound {
    DENIED_ENTRY(SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, 0.4f, 0.5f),
    LOCK_ADDED(SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, 0.5f, 0.65f),
    LOCK_DESTROYED(SoundEvents.BLOCK_CHAIN_BREAK, 0.05f, 0.05f);

    private final SoundEvent soundEvent;
    private final float volume, pitch;

    LockSound(SoundEvent soundEvent, float volume, float pitch) {
        this.soundEvent = soundEvent;
        this.pitch = pitch;
        this.volume = volume;
    }

    public SoundEvent getSoundEvent() { return soundEvent; }
    public float getVolume() { return volume; }
    public float getPitch() { return pitch; }
}
