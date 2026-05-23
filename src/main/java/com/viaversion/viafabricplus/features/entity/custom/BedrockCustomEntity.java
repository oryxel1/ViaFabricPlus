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

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.raphimc.viabedrock.api.model.entity.CustomEntity;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class BedrockCustomEntity extends Entity {
    private final CustomEntity bedrockEntity;
    private final List<CustomEntity.EvaluatedModel> models;

    public CustomEntity entity() {
        return bedrockEntity;
    }

    public List<CustomEntity.EvaluatedModel> models() {
        return models;
    }

    public BedrockCustomEntity(final EntityType<?> type,
                               final Level level,
                               final CustomEntity entity,
                               List<CustomEntity.EvaluatedModel> models) {
        super(type, level);
        this.models = models;
        this.bedrockEntity = entity;
    }

    @Override
    public void tick() {
        super.tick();
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
}
