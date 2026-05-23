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

package com.viaversion.viafabricplus.util.bedrock;

import com.google.common.collect.Maps;
import com.viaversion.viafabricplus.features.entity.custom.renderer.BedrockCustomEntityRenderer;
import com.viaversion.viafabricplus.injection.access.bedrock.model.IModelPart;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.Direction;
import org.cube.converter.model.element.Cube;
import org.cube.converter.model.element.Parent;
import org.cube.converter.model.impl.bedrock.BedrockGeometryModel;
import org.cube.converter.util.element.UVMap;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class GeometryUtil {
    public static Model<BedrockCustomEntityRenderer.@NotNull CustomEntityState> buildModel(final BedrockGeometryModel geometry) {
        final float uvWidth = geometry.getTextureSize().getX();
        final float uvHeight = geometry.getTextureSize().getY();

        final Map<String, PartInfo> stringToPart = new HashMap<>();
        for (final Parent bone : geometry.getParents()) {
            final Map<String, ModelPart> children = new HashMap<>();
            final ModelPart part = new ModelPart(List.of(), children);

            part.setPos(bone.getPivot().getX(), -bone.getPivot().getY() + 24, bone.getPivot().getZ());
            part.setRotation(bone.getRotation().getX() * 0.017453292519943295f, bone.getRotation().getY() * 0.017453292519943295f, bone.getRotation().getZ() * 0.017453292519943295f);
            ((IModelPart)((Object)part)).viaFabricPlus$bedrockModelSet();

            for (final Cube cube : bone.getCubes().values()) {
                final float sizeX = cube.getSize().getX(), sizeY = cube.getSize().getY(), sizeZ = cube.getSize().getZ();
                final float inflate = cube.getInflate() + 1.0E-3F;

                final UVMap uvMap = cube.getUvMap().clone();

                final Set<Direction> set = new HashSet<>();
                for (final Direction direction : Direction.values()) {
                    if (uvMap.getUvMap().containsKey(org.cube.converter.util.element.Direction.values()[direction.ordinal()])) {
                        set.add(direction);
                    }
                }

                final ModelPart.Cube cuboid = new ModelPart.Cube(0, 0, cube.getPosition().getX(), -(cube.getPosition().getY() - 24 + sizeY), cube.getPosition().getZ(), sizeX, sizeY, sizeZ, inflate, inflate, inflate, cube.isMirror(), uvWidth, uvHeight, set);
                correctUv(cuboid, set, uvMap, uvWidth, uvHeight, cube.getInflate(), cube.isMirror());

                final ModelPart cubePart = new ModelPart(List.of(cuboid), Map.of());
                cubePart.setPos(cube.getPivot().getX(), -cube.getPivot().getY() + 24, cube.getPivot().getZ());
                cubePart.setRotation(cube.getRotation().getX() * 0.017453292519943295f, cube.getRotation().getY() * 0.017453292519943295f, cube.getRotation().getZ() * 0.017453292519943295f);
                ((IModelPart)((Object)cubePart)).viaFabricPlus$bedrockModelSet();
                children.put(cube.getParent() + cube.hashCode(), cubePart);
            }

            stringToPart.put(bone.getName(), new PartInfo(bone.getParent(), part, children));
        }

        final Map<String, ModelPart> rootParts = new HashMap<>();

        for (Map.Entry<String, PartInfo> entry : stringToPart.entrySet()) {
            if (entry.getValue().parent.isBlank()) {
                rootParts.put(entry.getKey(), entry.getValue().part());
                continue;
            }

            PartInfo parentPart = stringToPart.get(entry.getValue().parent);
            if (parentPart != null) {
                parentPart.children.put(entry.getKey(), entry.getValue().part);
            }
        }

        return new Model<>(new ModelPart(List.of(), rootParts), RenderTypes::entityTranslucentCullItemTarget) {
            @Override
            public void setupAnim(final BedrockCustomEntityRenderer.CustomEntityState state) {
            }
        };
    }

    private record PartInfo(String parent, ModelPart part, Map<String, ModelPart> children) {
    }

    private static void correctUv(final ModelPart.Cube cuboid, final Set<Direction> set, final UVMap map, final float uvWidth, final float uvHeight, final float inflate, final boolean mirror) {
        float x = cuboid.minX, y = cuboid.minY, z = cuboid.minZ;
        float f = cuboid.maxX, g = cuboid.maxY, h = cuboid.maxZ;

        x -= inflate;
        y -= inflate;
        z -= inflate;
        f += inflate;
        g += inflate;
        h += inflate;

        if (mirror) {
            float i = f;
            f = x;
            x = i;
        }

        ModelPart.Vertex vertex = new ModelPart.Vertex(x, y, z, 0.0F, 0.0F);
        ModelPart.Vertex vertex2 = new ModelPart.Vertex(f, y, z, 0.0F, 8.0F);
        ModelPart.Vertex vertex3 = new ModelPart.Vertex(f, g, z, 8.0F, 8.0F);
        ModelPart.Vertex vertex4 = new ModelPart.Vertex(x, g, z, 8.0F, 0.0F);
        ModelPart.Vertex vertex5 = new ModelPart.Vertex(x, y, h, 0.0F, 0.0F);
        ModelPart.Vertex vertex6 = new ModelPart.Vertex(f, y, h, 0.0F, 8.0F);
        ModelPart.Vertex vertex7 = new ModelPart.Vertex(f, g, h, 8.0F, 8.0F);
        ModelPart.Vertex vertex8 = new ModelPart.Vertex(x, g, h, 8.0F, 0.0F);

        final ModelPart.Polygon[] sides = cuboid.polygons;
        int s = 0;

        if (set.contains(Direction.DOWN)) {
            final Float[] uv = map.getUvMap().get(org.cube.converter.util.element.Direction.DOWN);
            sides[s++] = new ModelPart.Polygon(new ModelPart.Vertex[]{vertex6, vertex5, vertex, vertex2}, uv[0], uv[1], uv[2], uv[3], uvWidth, uvHeight, mirror, Direction.DOWN);
        }

        if (set.contains(Direction.UP)) {
            final Float[] uv = map.getUvMap().get(org.cube.converter.util.element.Direction.UP);
            sides[s++] = new ModelPart.Polygon(new ModelPart.Vertex[]{vertex3, vertex4, vertex8, vertex7}, uv[0], uv[1], uv[2], uv[3], uvWidth, uvHeight, mirror, Direction.UP);
        }

        if (set.contains(Direction.WEST)) {
            final Float[] uv = map.getUvMap().get(org.cube.converter.util.element.Direction.WEST);
            sides[s++] = new ModelPart.Polygon(new ModelPart.Vertex[]{vertex, vertex5, vertex8, vertex4}, uv[0], uv[1], uv[2], uv[3], uvWidth, uvHeight, mirror, Direction.WEST);
        }

        if (set.contains(Direction.NORTH)) {
            final Float[] uv = map.getUvMap().get(org.cube.converter.util.element.Direction.NORTH);
            sides[s++] = new ModelPart.Polygon(new ModelPart.Vertex[]{vertex2, vertex, vertex4, vertex3}, uv[0], uv[1], uv[2], uv[3], uvWidth, uvHeight, mirror, Direction.NORTH);
        }

        if (set.contains(Direction.EAST)) {
            final Float[] uv = map.getUvMap().get(org.cube.converter.util.element.Direction.EAST);
            sides[s++] = new ModelPart.Polygon(new ModelPart.Vertex[]{vertex6, vertex2, vertex3, vertex7}, uv[0], uv[1], uv[2], uv[3], uvWidth, uvHeight, mirror, Direction.EAST);
        }

        if (set.contains(Direction.SOUTH)) {
            final Float[] uv = map.getUvMap().get(org.cube.converter.util.element.Direction.SOUTH);
            sides[s] = new ModelPart.Polygon(new ModelPart.Vertex[]{vertex5, vertex6, vertex7, vertex8}, uv[0], uv[1], uv[2], uv[3], uvWidth, uvHeight, mirror, Direction.SOUTH);
        }
    }
}
