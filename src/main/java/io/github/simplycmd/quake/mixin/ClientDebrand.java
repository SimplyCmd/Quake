package io.github.simplycmd.quake.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public class ClientDebrand {
	@Redirect(method = "getWindowTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;isModded()Z"))
	public boolean isModded(MinecraftClient client) {
        return false;
	}
}