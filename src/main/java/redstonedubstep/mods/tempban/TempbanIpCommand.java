package redstonedubstep.mods.tempban;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.command.impl.BanIpCommand;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.IPBanEntry;
import net.minecraft.server.management.IPBanList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.time.DateUtils;

public class TempbanIpCommand {
	private static final SimpleCommandExceptionType IP_INVALID = new SimpleCommandExceptionType(new TranslationTextComponent("commands.banip.invalid"));
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.banip.failed"));

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("tempban-ip").requires(p -> p.hasPermissionLevel(3))
				.then(Commands.argument("target", StringArgumentType.word())
						.then(Commands.argument("months", IntegerArgumentType.integer(0))
								.then(Commands.argument("days", IntegerArgumentType.integer(0))
										.then(Commands.argument("hours", IntegerArgumentType.integer(0))
												.executes(TempbanIpCommand::tempbanUsernameOrIp)
												.then(Commands.argument("reason", MessageArgument.message())
														.executes(TempbanIpCommand::tempbanUsernameOrIp)))))));
	}

	private static int tempbanUsernameOrIp(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
		return tempbanUsernameOrIp(ctx.getSource(), StringArgumentType.getString(ctx, "target"), IntegerArgumentType.getInteger(ctx, "months"), IntegerArgumentType.getInteger(ctx, "days"), IntegerArgumentType.getInteger(ctx, "hours"), MessageArgument.getMessage(ctx, "reason"));
	}

	private static int tempbanUsernameOrIp(CommandSource source, String username, int monthDuration, int dayDuration, int hourDuration, ITextComponent reason) throws CommandSyntaxException {
		Matcher matcher = BanIpCommand.IP_PATTERN.matcher(username);
		if (matcher.matches()) {
			return tempbanIpAddress(source, username, monthDuration, dayDuration, hourDuration, reason);
		} else {
			ServerPlayerEntity serverplayerentity = source.getServer().getPlayerList().getPlayerByUsername(username);
			if (serverplayerentity != null) {
				return tempbanIpAddress(source, serverplayerentity.getPlayerIP(), monthDuration, dayDuration, hourDuration, reason);
			} else {
				throw IP_INVALID.create();
			}
		}
	}

	private static int tempbanIpAddress(CommandSource source, String ip, int monthDuration, int dayDuration, int hourDuration, ITextComponent reason) throws CommandSyntaxException {
		IPBanList ipbanlist = source.getServer().getPlayerList().getBannedIPs();
		Date date = DateUtils.addMonths(DateUtils.addDays(DateUtils.addHours(new Date(), hourDuration), dayDuration), monthDuration);

		if (ipbanlist.isBanned(ip)) {
			throw FAILED_EXCEPTION.create();
		} else {
			List<ServerPlayerEntity> list = source.getServer().getPlayerList().getPlayersMatchingAddress(ip);
			IPBanEntry ipbanentry = new IPBanEntry(ip,null, source.getName(), date, reason == null ? null : reason.getString());
			ipbanlist.addEntry(ipbanentry);
			source.sendFeedback(new TranslationTextComponent("Banned %s for %s months, %s days and %s hours: %s", ip, monthDuration, dayDuration, hourDuration, ipbanentry.getBanReason()), true);
			if (!list.isEmpty()) {
				source.sendFeedback(new TranslationTextComponent("commands.banip.info", list.size(), EntitySelector.joinNames(list)), true);
			}

			for(ServerPlayerEntity serverplayerentity : list) {
				serverplayerentity.connection.disconnect(new TranslationTextComponent("multiplayer.disconnect.ip_banned"));
			}

			return list.size();
		}
	}
}
