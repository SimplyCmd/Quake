package io.github.simplycmd.quake;

import org.lwjgl.glfw.GLFW;

import io.github.simplycmd.quake.gui.MenuGui;
import io.github.simplycmd.quake.gui.MenuScreen;
import io.github.simplycmd.quake.mixin.LegacyPvp;
import io.github.simplycmd.quake.mods.Fullbright;
import io.github.simplycmd.quake.mods.Toggles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class Keybinds extends MenuGui implements ClientModInitializer {

    public static boolean done = false;

    public final static double MAX_BRIGHTNESS = 12.0D;

    private boolean prevPressed;
    private KeyBinding fullbrightKey;
    private KeyBinding toggleSprint;
    private KeyBinding toggleSneak;
    private KeyBinding requestBlocks;
    private KeyBinding legacyPvp;
    public static KeyBinding narratorRebind;
    public static KeyBinding menu;
    

	@Override
	public void onInitializeClient() {
        SetupKeybinds(); 
    }


    private void SetupKeybinds() {
        menu = new KeyBinding(
            "key.quake.menu",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "key.categories.quake"
        );
        fullbrightKey = new KeyBinding(
            "key.quake.fullbright",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "key.categories.quake"
        );
        toggleSprint = new KeyBinding(
            "key.quake.sprint",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "key.categories.quake"
        );
        toggleSneak = new KeyBinding(
            "key.quake.sneak",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "key.categories.quake"
        );
        requestBlocks = new KeyBinding(
            "key.quake.reveal",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.quake"
        );
        narratorRebind = new KeyBinding(
            "options.narrator",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            "key.categories.misc"
        );
        /*legacyPvp = new KeyBinding(
            "key.quake.pvp",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "key.categories.quake"
        );*/

        KeyBindingHelper.registerKeyBinding(menu);
        KeyBindingHelper.registerKeyBinding(fullbrightKey);
        KeyBindingHelper.registerKeyBinding(toggleSprint);
        KeyBindingHelper.registerKeyBinding(toggleSneak);
        KeyBindingHelper.registerKeyBinding(requestBlocks);
        KeyBindingHelper.registerKeyBinding(narratorRebind);
        //KeyBindingHelper.registerKeyBinding(legacyPvp);
        
        ClientTickCallback.EVENT.register(e -> {
            MinecraftClient client = MinecraftClient.getInstance();

            //Menu
            if (menu.isPressed()) {
                client.openScreen(new MenuScreen(new MenuGui()));
            }

            //Fullbright
            if (fullbrightKey.isPressed()) {
                if(!prevPressed) {
                    Fullbright.Fullbright();
                    prevPressed = true;
                }
            } else {
                prevPressed = false;
            }

            //Toggle Sprint
            if (toggleSprint.wasPressed()) {
                Toggles.ToggleSprint();
            }
            
            //Toggle Sneak
            if (toggleSneak.wasPressed()) {
                Toggles.ToggleSneak();
            }

            //Ghost Blocks
            if (requestBlocks.wasPressed()) {
                MinecraftClient mc=MinecraftClient.getInstance();
                ClientPlayNetworkHandler conn = mc.getNetworkHandler();
                if (conn==null)
                    return;
                BlockPos pos=mc.player.getBlockPos();
                for (int dx=-4; dx<=4; dx++)
                    for (int dy=-4; dy<=4; dy++)
                        for (int dz=-4; dz<=4; dz++) {
                            PlayerActionC2SPacket packet=new PlayerActionC2SPacket(
                                    PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, 
                                    new BlockPos(pos.getX()+dx, pos.getY()+dy, pos.getZ()+dz),
                                    Direction.UP       // with ABORT_DESTROY_BLOCK, this value is unused
                            );
                            conn.sendPacket(packet);
                        }
                client.player.sendMessage(new TranslatableText("msg.request"), false);
            }

            //Toggle Legacy Pvp
            //if (legacyPvp.wasPressed()) {
                //Pvp.Pvp();
            //}
        });
    }
}