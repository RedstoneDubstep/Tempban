package redstonedubstep.mods.tempban;

import java.util.Collection;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.players.BanListEntry;
import net.minecraft.server.players.PlayerList;

public class BetterBanListCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("banlist")
				.requires(stack -> stack.hasPermission(3))
				.executes(ctx -> showConcatList(ctx.getSource()))
                .then(Commands.literal("ips")
						.executes(ctx -> showList(ctx.getSource(), ctx.getSource().getServer().getPlayerList().getIpBans().getEntries())))
				.then(Commands.literal("players")
						.executes((ctx) -> showList(ctx.getSource(), ctx.getSource().getServer().getPlayerList().getBans().getEntries()))));
	}

    private static int showConcatList(CommandSourceStack ctx) {
        PlayerList playerlist = ctx.getServer().getPlayerList();

        return showList(ctx, Lists.newArrayList(Iterables.concat(playerlist.getBans().getEntries(), playerlist.getIpBans().getEntries())));
    }

	private static int showList(CommandSourceStack source, Collection<? extends BanListEntry<?>> banListEntries) {
		if (banListEntries.isEmpty())
			source.sendSuccess(() -> Component.translatable("commands.banlist.none"), false);
		else {
			source.sendSuccess(() -> Component.translatable("commands.banlist.list", banListEntries.size()), false);

			for(BanListEntry<?> entry : banListEntries) {
				MutableComponent entryComponent = Component.translatable("commands.banlist.entry", entry.getDisplayName(), entry.getSource(), entry.getReason());

				if (entry.getExpires() != null) {
					entryComponent.append(Component.literal(" (" + entry.getCreated() + " - " + entry.getExpires() + ")").withStyle(ChatFormatting.GRAY));
				}

				source.sendSuccess(() -> entryComponent, false);
			}
		}

		return banListEntries.size();
	}
}
