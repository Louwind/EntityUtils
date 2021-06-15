package github.Louwind.entityutils.core.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(MemoryModuleType.class)
public class MemoryModuleTypeInvoker {

    @Invoker("<init>")
    static <U> MemoryModuleType<U> create(Optional<Codec<U>> codec) {
        throw new AssertionError();
    }

}