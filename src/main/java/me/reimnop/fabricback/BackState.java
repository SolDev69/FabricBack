package me.reimnop.fabricback;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class BackState extends PersistentState {
    public static final String BACK_KEY = "back";

    private HashMap<UUID, Location> lastDeathLoc = new HashMap<>();

    public static BackState fromNbt(NbtCompound nbt) {
        BackState state = new BackState();
        return state.readNbt(nbt);
    }

    public BackState readNbt(NbtCompound nbt) {
        lastDeathLoc.clear();
        for (String key : nbt.getKeys()) {
            NbtCompound compound = nbt.getCompound(key);
            Identifier id = new Identifier(compound.getString("World"));
            NbtList list = compound.getList("Pos", 6); // 6 means double I assume??

            lastDeathLoc.put(UUID.fromString(key), new Location(id, new Vec3d(list.getDouble(0), list.getDouble(1), list.getDouble(2))));
        }
        return this;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        for (Map.Entry<UUID, Location> entry : lastDeathLoc.entrySet()) {
            Location location = entry.getValue();
            NbtCompound compound = new NbtCompound();
            compound.putString("World", location.world.toString());
            compound.put("Pos", toNbtList(location.position.x, location.position.y, location.position.z));
            nbt.put(entry.getKey().toString(), compound);
        }
        return nbt;
    }

    public Optional<Location> getLastDeathPos(UUID uuid) {
        return lastDeathLoc.containsKey(uuid) ? Optional.of(lastDeathLoc.get(uuid)) : Optional.empty();
    }

    public void setLastDeathPos(UUID uuid, Optional<Location> location) {
        location.ifPresentOrElse(loc -> lastDeathLoc.put(uuid, loc), () -> lastDeathLoc.remove(uuid));
        markDirty();
    }

    private NbtList toNbtList(double... values) {
        NbtList nbtList = new NbtList();
        for (double d : values) {
            nbtList.add(NbtDouble.of(d));
        }
        return nbtList;
    }
}
