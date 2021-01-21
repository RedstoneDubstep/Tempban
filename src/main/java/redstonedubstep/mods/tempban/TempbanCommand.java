package redstonedubstep.mods.tempban;

import java.util.Collection;
import java.util.Date;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.BanList;
import net.minecraft.server.management.ProfileBanEntry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.time.DateUtils;

public class TempbanCommand {
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.ban.failed"));

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("tempban").requires((player) -> player.hasPermissionLevel(3))
				.then(Commands.argument("targets", GameProfileArgument.gameProfile())
						.then(Commands.argument("months", IntegerArgumentType.integer(0))
								.then(Commands.argument("days", IntegerArgumentType.integer(0))
										.then(Commands.argument("hours", IntegerArgumentType.integer(0))
												.executes(TempbanCommand::tempbanPlayers)
												.then(Commands.argument("reason", MessageArgument.message())
														.executes(TempbanCommand::tempbanPlayers)))))));
	}

	private static int tempbanPlayers(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
		return tempbanPlayers(ctx.getSource(), GameProfileArgument.getGameProfiles(ctx, "targets"), IntegerArgumentType.getInteger(ctx, "months"), IntegerArgumentType.getInteger(ctx, "days"), IntegerArgumentType.getInteger(ctx, "hours"), MessageArgument.getMessage(ctx, "reason"));
	}

	private static int tempbanPlayers(CommandSource source, Collection<GameProfile> toBeBanned, int monthDuration, int dayDuration, int hourDuration, ITextComponent reason) throws CommandSyntaxException {
		BanList banlist = source.getServer().getPlayerList().getBannedPlayers();
		int i = 0;
		Date date = DateUtils.addMonths(DateUtils.addDays(DateUtils.addHours(new Date(), hourDuration), dayDuration), monthDuration);

		for(GameProfile gameprofile : toBeBanned) {
			if (!banlist.isBanned(gameprofile)) {
				ProfileBanEntry profilebanentry = new ProfileBanEntry(gameprofile, null, source.getName(), date, reason == null ? null : reason.getString());
				banlist.addEntry(profilebanentry);
				++i;
				source.sendFeedback(new TranslationTextComponent("Banned %s for %s months, %s days and %s hours: %s", TextComponentUtils.getDisplayName(gameprofile), monthDuration, dayDuration, hourDuration, profilebanentry.getBanReason()), true);
				ServerPlayerEntity serverplayerentity = source.getServer().getPlayerList().getPlayerByUUID(gameprofile.getId());
				if (serverplayerentity != null) {
					serverplayerentity.connection.disconnect(new TranslationTextComponent("multiplayer.disconnect.banned"));
				}
			}
		}

		if (i == 0) {
			throw FAILED_EXCEPTION.create();
		} else {
			return i;
		}
	}
}
