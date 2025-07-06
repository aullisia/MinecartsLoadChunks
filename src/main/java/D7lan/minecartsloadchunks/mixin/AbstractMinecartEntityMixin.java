package D7lan.minecartsloadchunks.mixin;

import D7lan.minecartsloadchunks.util.LoadsChunksAccessor;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecartEntity.class)
public class AbstractMinecartEntityMixin implements LoadsChunksAccessor {
    @Unique
    private boolean loadsChunks = false;

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void writeLoadsChunks(WriteView view, CallbackInfo ci) {
        view.putBoolean("LoadsChunks", this.loadsChunks);
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void readLoadsChunks(ReadView view, CallbackInfo ci) {
        this.loadsChunks = view.getBoolean("LoadsChunks", false);
    }


    @Override
    public boolean getLoadsChunks() {
        return this.loadsChunks;
    }

    @Override
    public void setLoadsChunks(boolean loadsChunks) {
        this.loadsChunks = loadsChunks;
    }
}
