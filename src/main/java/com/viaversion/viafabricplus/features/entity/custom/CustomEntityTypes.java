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
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

public class CustomEntityTypes {
    public static EntityType<@NotNull BedrockCustomEntity> CUSTOM_ENTITY_TYPE;

    private static <T extends Entity> EntityType<@NotNull T> register(String name, EntityType.Builder<@NotNull T> builder) {
        ResourceKey<@NotNull EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("viafabricplus", name));
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
    }

    public static void init() {
        CUSTOM_ENTITY_TYPE = register(
            "bedrock_custom_entity",
            EntityType.Builder.of((type, level) -> new BedrockCustomEntity(type, level, null, new ArrayList<>()), MobCategory.MISC)
        );

        EntityRenderers.register(CUSTOM_ENTITY_TYPE, BedrockCustomEntityRenderer::new);
    }
}
