package weather2.mixin;

import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.filefix.FileFix;
import net.minecraft.util.filefix.fixes.DimensionStorageFileFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import weather2.Weather;

import static net.minecraft.util.filefix.operations.FileFixOperations.move;

@Mixin(DimensionStorageFileFix.class)
public abstract class DimensionStorageFileFixMixin extends FileFix {
    public DimensionStorageFileFixMixin(final Schema schema) {
        super(schema);
    }

    @Inject(method = "makeFixer", at = @At("HEAD"))
    private void fileFixWeatherData(final CallbackInfo ci) {
        addFileFixOperation(move("data/" + Weather.MODID + "-" + "weather_data.dat", "data/" + Weather.MODID + "/weather_data.dat"));
    }
}
