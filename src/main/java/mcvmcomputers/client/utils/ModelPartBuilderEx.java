package mcvmcomputers.client.utils;

import net.minecraft.client.model.ModelPartBuilder;

import java.util.ArrayList;

public final class ModelPartBuilderEx {
    public static ModelPartBuilder createMPB(MPB[] mpbs) {
        ModelPartBuilder mpb = new ModelPartBuilder();
        for (MPB m : mpbs) {
            mpb.cuboid(m.offsetX, m.offsetY, m.offsetZ, m.sizeX, m.sizeY, m.sizeZ, m.mirror);
            mpb.uv(m.uvX, m.uvY);
        }
        return mpb;
    }

    public static ModelPartBuilder createMPB(float offsetX, float offsetY, float offsetZ, float sizeX, float sizeY, float sizeZ, boolean mirror) {
        ModelPartBuilder mpb = new ModelPartBuilder();
        mpb.cuboid(offsetX,offsetY,offsetZ,sizeX,sizeY,sizeZ,mirror);
        return mpb;
    }

    public static ModelPartBuilder createMPB(float offsetX, float offsetY, float offsetZ, float sizeX, float sizeY, float sizeZ, boolean mirror, int uvX, int uvY) {
        ModelPartBuilder mpb = new ModelPartBuilder();
        mpb.cuboid(offsetX,offsetY,offsetZ,sizeX,sizeY,sizeZ,mirror);
        mpb.uv(uvX,uvY);
        return mpb;
    }

    public static class MPB extends ArrayList {
        public float offsetX;
        public float offsetY;
        public float offsetZ;
        public float sizeX;
        public float sizeY;
        public float sizeZ;
        public boolean mirror = false;
        public int uvX = 0;
        public int uvY = 0;

        public MPB(float offsetX, float offsetY, float offsetZ, float sizeX, float sizeY, float sizeZ, boolean mirror, int uvX, int uvY) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            this.sizeX = sizeX;
            this.sizeY = sizeY;
            this.sizeZ = sizeZ;
            this.mirror = mirror;
            this.uvX = uvX;
            this.uvY = uvY;
        }
    }
}
