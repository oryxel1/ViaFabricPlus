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

package com.viaversion.viafabricplus.features.entity.custom;

import com.viaversion.viafabricplus.features.entity.custom.renderer.BedrockCustomEntityRenderer;
import com.viaversion.viafabricplus.injection.access.core.bedrock.ICustomEntity;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.util.bedrock.GeometryUtil;
import com.viaversion.viaversion.api.connection.UserConnection;
import net.minecraft.client.model.Model;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.raphimc.viabedrock.api.model.entity.CustomEntity;
import net.raphimc.viabedrock.protocol.storage.EntityTracker;
import net.raphimc.viabedrock.protocol.storage.ResourcePackStorage;
import org.cube.converter.model.impl.bedrock.BedrockGeometryModel;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BedrockCustomEntity extends Entity {
    private final List<CachedModel> models = new ArrayList<>();

    public BedrockCustomEntity(final EntityType<?> type, final Level level) {
        super(type, level);
    }

    public List<CachedModel> models() {
        return models;
    }

    @Override
    public void recreateFromPacket(final ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);

        final UserConnection connection = ProtocolTranslator.getPlayNetworkUserConnection();
        if (connection == null) {
            return;
        }

        final ResourcePackStorage storage = connection.get(ResourcePackStorage.class);
        if (storage == null) {
            return;
        }
        final EntityTracker tracker = connection.get(EntityTracker.class);
        if (tracker == null) {
            return;
        }
        final net.raphimc.viabedrock.api.model.entity.Entity entity = tracker.getEntityByJid(this.getId());
        if (!(entity instanceof CustomEntity customEntity)) {
            return;
        }

        for (CustomEntity.EvaluatedModel model : ((ICustomEntity)customEntity).viaFabricPlus$models()) {
            BedrockGeometryModel geometry = storage.getModels().getEntityModel(model.geometryValue());
            if (geometry == null) {
                continue;
            }

            Model<BedrockCustomEntityRenderer.@NotNull CustomEntityState> geometryModel =  GeometryUtil.buildModel(geometry);

            final Identifier texture = Identifier.fromNamespaceAndPath("viabedrock",
                model.textureValue().replace("textures/", "textures/item/entities/").toLowerCase(Locale.ROOT) + ".png");
            this.models.add(new CachedModel(geometryModel, texture));
        }
    }

    @Override
    protected void defineSynchedData(final SynchedEntityData.@NotNull Builder entityData) {
    }

    @Override
    public boolean hurtServer(final @NotNull ServerLevel level, final @NotNull DamageSource source, final float damage) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(final @NotNull ValueInput input) {
    }

    @Override
    protected void addAdditionalSaveData(final @NotNull ValueOutput output) {
    }

    public record CachedModel(Model<BedrockCustomEntityRenderer.@NotNull CustomEntityState> model, Identifier texture) {
    }
}
