package com.tiertagger.tiertagger.mixin;

import com.tiertagger.tiertagger.TierCache;
import com.tiertagger.tiertagger.TierConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {

    @Inject(
        method = "renderLabelIfPresent",
        at = @At("TAIL")
    )
    private void renderTierTag(AbstractClientPlayerEntity player, Text text, MatrixStack matrices,
                               VertexConsumerProvider vertexConsumers, int light, float tickDelta, CallbackInfo ci) {

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        UUID uuid = player.getUuid();

        if (!TierConfig.showOwnTag && uuid.equals(client.player.getUuid())) return;

        String tier = TierCache.getTier(uuid);
        if (tier == null || tier.isBlank()) return;

        Formatting color = TierCache.getColor(tier);
        Text tagText = Text.literal("[" + tier + "]").formatted(color, Formatting.BOLD);

        matrices.push();

        double distance = client.player.squaredDistanceTo(player);
        if (distance > 4096) {
            matrices.pop();
            return;
        }

        float entityHeight = player.getHeight() + 0.75f;
        matrices.translate(0.0, entityHeight, 0.0);
        matrices.multiply(client.getEntityRenderDispatcher().getRotation());
        float scale = 0.025f;
        matrices.scale(-scale, -scale, scale);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        TextRenderer textRenderer = client.textRenderer;

        float x = -textRenderer.getWidth(tagText) / 2.0f;

        boolean hasNameTag = !player.isInvisible();
        int backgroundAlpha = hasNameTag ? (int)(client.options.getTextBackgroundOpacity(0.25f) * 255) << 24 : 0;

        textRenderer.draw(
            tagText,
            x,
            0,
            0xFFFFFFFF,
            false,
            matrix,
            vertexConsumers,
            TextRenderer.TextLayerType.SEE_THROUGH,
            backgroundAlpha,
            light
        );

        matrices.pop();
    }
}
