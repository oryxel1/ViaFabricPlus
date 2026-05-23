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

package com.viaversion.viafabricplus.injection.mixin.features.bedrock.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.viaversion.viafabricplus.injection.access.bedrock.model.IModelPart;
import net.minecraft.client.model.geom.ModelPart;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(ModelPart.class)
public abstract class MixinModelPart implements IModelPart {
    @Shadow @Final private Map<String, ModelPart> children;

    @Shadow public abstract List<ModelPart> getAllParts();

    @Shadow public float xScale;
    @Shadow public float yScale;
    @Shadow public float zScale;

    @Unique
    private String viaFabricPlus$name = "";

    @Unique private boolean viaFabricPlus$isVBUModel;

    @Unique
    private Vector3f viaFabricPlus$pivot = new Vector3f();

    @Unique
    private Vector3f viaFabricPlus$offset = new Vector3f();

    @Unique
    private Vector3f viaFabricPlus$rotation = new Vector3f();
    
    @Unique
    private Vector3f viaFabricPlus$defaultRotation = new Vector3f();

    @Unique
    private boolean viaFabricPlus$defaultDefined = false;

    @Inject(method = "translateAndRotate", at = @At("HEAD"))
    public void translateAndRotate(PoseStack matrices, CallbackInfo ci) {
        matrices.translate(this.viaFabricPlus$offset.x / 16.0F, this.viaFabricPlus$offset.y / 16.0F, this.viaFabricPlus$offset.z / 16.0F);

        matrices.translate(this.viaFabricPlus$pivot.x / 16.0F, this.viaFabricPlus$pivot.y / 16.0F, this.viaFabricPlus$pivot.z / 16.0F);
        matrices.mulPose((new Quaternionf()).rotationXYZ(this.viaFabricPlus$rotation.x * 0.017453292519943295f, this.viaFabricPlus$rotation.y * 0.017453292519943295f, this.viaFabricPlus$rotation.z * 0.017453292519943295f));
        matrices.translate(-this.viaFabricPlus$pivot.x / 16.0F, -this.viaFabricPlus$pivot.y / 16.0F, -this.viaFabricPlus$pivot.z / 16.0F);

        matrices.translate(-this.viaFabricPlus$offset.x / 16.0F, -this.viaFabricPlus$offset.y / 16.0F, -this.viaFabricPlus$offset.z / 16.0F);
    }

    @Inject(method = "translateAndRotate", at = @At("TAIL"))
    public void translateAndRotateTail(PoseStack matrices, CallbackInfo ci) {
        matrices.translate(this.viaFabricPlus$offset.x / 16.0F, this.viaFabricPlus$offset.y / 16.0F, this.viaFabricPlus$offset.z / 16.0F);
    }

    @Inject(method = "getChild", at = @At("HEAD"), cancellable = true)
    private void getChild(String name, CallbackInfoReturnable<ModelPart> cir) {
        if (this.viaFabricPlus$isBedrockModel()) {
            cir.setReturnValue(this.children.getOrDefault(name, new ModelPart(List.of(), Map.of())));
        }
    }
    
    @Override
    public String viaFabricPlus$getName() {
        return this.viaFabricPlus$name;
    }

    @Override
    public void viaFabricPlus$setName(String name) {
        this.viaFabricPlus$name = name;
    }

    @Override
    public boolean viaFabricPlus$isBedrockModel() {
        return this.viaFabricPlus$isVBUModel;
    }

    @Override
    public void viaFabricPlus$bedrockModelSet() {
        this.viaFabricPlus$isVBUModel = true;
    }

    @Override
    public void viaFabricPlus$resetEverything() {
        getAllParts().forEach(part -> {
            ((IModelPart)((Object)part)).viaFabricPlus$setOffset(this.viaFabricPlus$offset);
            ((IModelPart)((Object)part)).viaFabricPlus$setAngles(this.viaFabricPlus$defaultRotation);
            part.xScale = part.yScale = part.zScale = 1.0F;
        });
    }

    @Override
    public void viaFabricPlus$setPivot(Vector3f vec3) {
        this.viaFabricPlus$pivot = vec3;
    }

    @Override
    public void viaFabricPlus$setOffset(Vector3f vec3) {
        this.viaFabricPlus$offset = new Vector3f(vec3.x, -vec3.y, vec3.z);
    }

    @Override
    public void viaFabricPlus$setAngles(Vector3f vec3) {
        if (!this.viaFabricPlus$defaultDefined) {
            this.viaFabricPlus$defaultRotation = new Vector3f(vec3.x, vec3.y, vec3.z);
            this.viaFabricPlus$defaultDefined = true;
        }

        this.viaFabricPlus$rotation = new Vector3f(vec3.x, vec3.y, vec3.z);
    }
}
