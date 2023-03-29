package earth.terrarium.stitch.mixins.fabric;

import com.mojang.datafixers.util.Pair;
import earth.terrarium.stitch.client.models.fabric.BlockModelHook;
import earth.terrarium.stitch.client.models.fabric.FactoryManagerImpl;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.Reader;
import java.util.Map;

@Mixin(ModelManager.class)
public class ModelManagerMixin {

    @Inject(
            method = "method_45898",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lcom/mojang/datafixers/util/Pair;of(Ljava/lang/Object;Ljava/lang/Object;)Lcom/mojang/datafixers/util/Pair;"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void stitch$onLoadModel(Map.Entry<ResourceLocation, Resource> entry, CallbackInfoReturnable<Pair<ResourceLocation, BlockModel>> cir, Reader reader, Pair<ResourceLocation, BlockModel> info) {
        if (info.getSecond() instanceof BlockModelHook hook && hook.stitch$hasModelData()) {
            String namespace = info.getFirst().getNamespace();
            String path = info.getFirst().getPath().substring("models/".length(), info.getFirst().getPath().length() - ".json".length());
            FactoryManagerImpl.addData(hook.stitch$getModelType(), new ResourceLocation(namespace, path), hook.stitch$getModelData());
        }
    }
}