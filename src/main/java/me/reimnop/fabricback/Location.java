package me.reimnop.fabricback;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class Location {
    public Identifier world;
    public Vec3d position;

    public Location(Identifier world, Vec3d position) {
        this.world = world;
        this.position = position;
    }
}
