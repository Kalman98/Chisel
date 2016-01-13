package team.chisel.common.util.json;

import java.util.List;

import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.ResourceLocation;
import team.chisel.api.render.IChiselFace;
import team.chisel.api.render.IChiselTexture;
import team.chisel.client.ChiselFace;

import com.google.common.base.Preconditions;

/**
 * Json version of ChiselFace
 */
public class JsonFace extends JsonObjectBase<IChiselFace> {

    /**
     * If this is the type COMBINED then these are the identifiers of the child textures
     */
    private String[] textures;

    @Override
    protected IChiselFace create(ResourceLocation loc) {
        Preconditions.checkNotNull(textures, JsonHelper.FACE_EXTENSION + " files must have a textures field!");
        IChiselFace face = new ChiselFace(loc);
        for (String child : textures) {
            if (JsonHelper.isLocalPath(child)) {
                child = JsonHelper.toAbsolutePath(child, loc);
            }
            ResourceLocation childLoc = new ResourceLocation(child);
            if (JsonHelper.isFace(childLoc)) {
                face.addChildFace(JsonHelper.getOrCreateFace(childLoc));
            } else if (JsonHelper.isTex(childLoc)) {
                face.addTexture(JsonHelper.getOrCreateTexture(childLoc));
            } else {
                if (JsonHelper.isValidFace(childLoc)) {
                    face.addChildFace(JsonHelper.getOrCreateFace(childLoc));
                } else {
                    face.addTexture(JsonHelper.getOrCreateTexture(childLoc));
                }
            }
        }
        face.setLayer(getLayer(face.getTextureList()));
        return face;
    }

    private EnumWorldBlockLayer getLayer(List<IChiselTexture<?>> list) {
        EnumWorldBlockLayer layer = EnumWorldBlockLayer.SOLID;
        for (IChiselTexture<?> tex : list) {
            EnumWorldBlockLayer texLayer = tex.getLayer();
            if (texLayer.ordinal() > layer.ordinal()) {
                layer = texLayer;
            }
        }
        return layer;
    }
}