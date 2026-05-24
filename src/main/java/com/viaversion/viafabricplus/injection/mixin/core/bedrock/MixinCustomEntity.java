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

package com.viaversion.viafabricplus.injection.mixin.core.bedrock;

import com.viaversion.viafabricplus.features.entity.custom.CustomEntityTypes;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Vector3d;
import com.viaversion.viaversion.api.minecraft.entities.EntityTypes1_21_11;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_21_11to26_1.packet.ClientboundPackets26_1;
import net.minecraft.core.registries.BuiltInRegistries;
import net.raphimc.viabedrock.api.model.entity.CustomEntity;
import net.raphimc.viabedrock.api.model.entity.Entity;
import net.raphimc.viabedrock.api.util.MathUtil;
import net.raphimc.viabedrock.protocol.BedrockProtocol;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;
import java.util.UUID;

@Mixin(value = CustomEntity.class, remap = false)
public class MixinCustomEntity extends Entity {
    @Shadow
    @Final
    private List<CustomEntity.EvaluatedModel> models;

    public MixinCustomEntity(final UserConnection user, final long uniqueId, final long runtimeId, final String type, final int javaId, final UUID javaUuid, final EntityTypes1_21_11 javaType) {
        super(user, uniqueId, runtimeId, type, javaId, javaUuid, javaType);
    }

    @Inject(method = "spawn", at = @At("HEAD"), cancellable = true)
    private void spawn(CallbackInfo ci) {
        ci.cancel();

        final PacketWrapper addEntity = PacketWrapper.create(ClientboundPackets26_1.ADD_ENTITY, this.user);
        addEntity.write(Types.VAR_INT, this.javaId); // entity id
        addEntity.write(Types.UUID, this.javaUuid); // uuid
        addEntity.write(Types.VAR_INT, BuiltInRegistries.ENTITY_TYPE.getId(CustomEntityTypes.CUSTOM_ENTITY_TYPE)); // type id
        addEntity.write(Types.DOUBLE, (double) this.position.x()); // x
        addEntity.write(Types.DOUBLE, (double) this.position.y()); // y
        addEntity.write(Types.DOUBLE, (double) this.position.z()); // z
        addEntity.write(Types.LOW_PRECISION_VECTOR, Vector3d.ZERO); // velocity
        addEntity.write(Types.BYTE, MathUtil.float2Byte(this.rotation.x())); // pitch
        addEntity.write(Types.BYTE, MathUtil.float2Byte(this.rotation.y())); // yaw
        addEntity.write(Types.BYTE, MathUtil.float2Byte(this.rotation.z())); // head yaw
        addEntity.write(Types.VAR_INT, 0); // data
        addEntity.send(BedrockProtocol.class);
    }
}
