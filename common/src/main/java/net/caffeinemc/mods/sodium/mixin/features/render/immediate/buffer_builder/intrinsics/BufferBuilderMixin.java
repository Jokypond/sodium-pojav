package net.caffeinemc.mods.sodium.mixin.features.render.immediate.buffer_builder.intrinsics;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.caffeinemc.mods.sodium.api.texture.SpriteUtil;
import net.caffeinemc.mods.sodium.api.util.ColorABGR;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView;
import net.caffeinemc.mods.sodium.client.render.immediate.model.BakedModelEncoder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings({ "SameParameterValue" })
@Mixin(BufferBuilder.class)
public abstract class BufferBuilderMixin implements VertexConsumer {
    @Shadow
    @Final
    private boolean fastFormat;

    @Override
    public void putBulkData(PoseStack.Pose matrices, BakedQuad bakedQuad, float r, float g, float b, float a, int light, int overlay) {
        if (!this.fastFormat) {
            VertexConsumer.super.putBulkData(matrices, bakedQuad, r, g, b, a, light, overlay);

            if (bakedQuad.sprite() != null) {
                SpriteUtil.INSTANCE.markSpriteActive(bakedQuad.sprite());
            }

            return;
        }

        if (bakedQuad.vertices().length < 32) {
            return; // we do not accept quads with less than 4 properly sized vertices
        }

        VertexBufferWriter writer = VertexBufferWriter.of(this);

        ModelQuadView quad = (ModelQuadView) (Object) bakedQuad;

        int color = ColorABGR.pack(r, g, b, a);
        BakedModelEncoder.writeQuadVertices(writer, matrices, quad, color, light, overlay, false);

        if (quad.getSprite() != null) {
            SpriteUtil.INSTANCE.markSpriteActive(quad.getSprite());
        }
    }

    @Override
    public void putBulkData(PoseStack.Pose matrices, BakedQuad bakedQuad, float[] brightnessTable, float r, float g, float b, float a, int[] light, int overlay, boolean colorize) {
        if (!this.fastFormat) {
            VertexConsumer.super.putBulkData(matrices, bakedQuad, brightnessTable, r, g, b, a, light, overlay, colorize);

            if (bakedQuad.sprite() != null) {
                SpriteUtil.INSTANCE.markSpriteActive(bakedQuad.sprite());
            }

            return;
        }

        if (bakedQuad.vertices().length < 32) {
            return; // we do not accept quads with less than 4 properly sized vertices
        }

        VertexBufferWriter writer = VertexBufferWriter.of(this);

        ModelQuadView quad = (ModelQuadView) (Object) bakedQuad;

        BakedModelEncoder.writeQuadVertices(writer, matrices, quad, r, g, b, a, brightnessTable, colorize, light, overlay);

        if (quad.getSprite() != null) {
            SpriteUtil.INSTANCE.markSpriteActive(quad.getSprite());
        }
    }
}
