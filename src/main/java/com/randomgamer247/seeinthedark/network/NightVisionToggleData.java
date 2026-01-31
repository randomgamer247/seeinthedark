package com.randomgamer247.seeinthedark.network;

import com.randomgamer247.seeinthedark.SeeintheDark;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * A payload carrying a boolean telling whether the players night vision goggles should be enabled or disabled
 * @param shouldEnable true if the goggles should be enabled and false if they should be disabled instead
 */
public record NightVisionToggleData(boolean shouldEnable) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<NightVisionToggleData> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SeeintheDark.MODID, "night_vision_toggle_data"));

    public static final StreamCodec<ByteBuf, NightVisionToggleData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            NightVisionToggleData::shouldEnable,
            NightVisionToggleData::new
    );

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
