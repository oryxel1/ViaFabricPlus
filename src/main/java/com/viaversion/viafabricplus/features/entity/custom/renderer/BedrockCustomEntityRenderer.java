/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.viaversion.viafabricplus.features.entity.custom.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.viaversion.viafabricplus.features.entity.custom.BedrockCustomEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class BedrockCustomEntityRenderer extends EntityRenderer<@NotNull BedrockCustomEntity, BedrockCustomEntityRenderer.@NotNull CustomEntityState> {
    public BedrockCustomEntityRenderer(final EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void submit(final CustomEntityState state, final @NotNull PoseStack poseStack, final @NotNull SubmitNodeCollector nodeCollector, final @NotNull CameraRenderState camera) {
        if (state.models == null) {
            return;
        }

        for (BedrockCustomEntity.CachedModel model : state.models) {
            poseStack.pushPose();

            poseStack.mulPose(Axis.YP.rotationDegrees(180 - state.yaw));
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            poseStack.translate(0.0F, -1.501F, 0.0F);
//            this.animators.values().forEach(animator -> {
//                try {
//                    animator.animate(model.model(), state);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            });

            RenderType renderType = RenderTypes.itemTranslucent(model.texture());
            int overlayCoords = OverlayTexture.pack(OverlayTexture.u(0), OverlayTexture.v(false));
            nodeCollector.submitModel(model.model(), state,
                poseStack, renderType, state.lightCoords, overlayCoords,
                -1, null,
                state.outlineColor, null);

            poseStack.popPose();
        }
    }

    @Override
    public void extractRenderState(@NotNull final BedrockCustomEntity entity, final CustomEntityState state, final float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.yaw = entity.getYRot();
        state.models = entity.models();
    }

    @Override
    public CustomEntityState createRenderState() {
        return new CustomEntityState();
    }

    public static class CustomEntityState extends EntityRenderState {
        private float yaw;
        private List<BedrockCustomEntity.CachedModel> models;
    }


}
