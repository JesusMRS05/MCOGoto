package com.github.jesusmrs05.client.mixin;

import com.github.jesusmrs05.client.MCOGotoClient;
import com.github.jesusmrs05.client.config.MCOGotoConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class MCOGotoCommandMixin {

	@Inject(
			method = "sendCommand",
			at = @At("HEAD"),
			cancellable = true
	)
	private void mcoGoto$sendCommand(
			String command,
			CallbackInfo ci
	) {
		if (!isGotoCommand(command)) {
			return;
		}

		MCOGotoConfig config =
				MCOGotoClient.getConfig();

		if (config == null) {
			return;
		}

		Minecraft client =
				Minecraft.getInstance();

		if (config.isEnabled()) {
			Component message = Component.literal(
					"MCO Goto is "
			).withStyle(
					ChatFormatting.WHITE
			).append(
					Component.literal(
							"ENABLED"
					).withStyle(
							ChatFormatting.BOLD,
							ChatFormatting.GREEN
					)
			);

			if (client.player != null) {
				client.player.sendSystemMessage(
						message
				);
			}

			ci.cancel();
			return;
		}

		Component message = Component.literal(
				"MCO Goto is "
		).withStyle(
				ChatFormatting.WHITE
		).append(
				Component.literal(
						"DISABLED"
				).withStyle(
						ChatFormatting.BOLD,
						ChatFormatting.RED
				)
		);

		if (client.player != null) {
			client.player.sendSystemMessage(
					message
			);
		}
	}

	private static boolean isGotoCommand(
			String command
	) {
		return command.equals("goto")
				|| command.startsWith("goto ");
	}
}