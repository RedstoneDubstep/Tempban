package redstonedubstep.mods.tempban;

import net.minecraft.commands.Commands;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.NetworkConstants;

@Mod("tempban")
public class TempbanForge {
	public TempbanForge() {
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
		NeoForge.EVENT_BUS.addListener(this::registerCommands);
	}

	public void registerCommands(RegisterCommandsEvent event){
		if (event.getCommandSelection() != Commands.CommandSelection.INTEGRATED) {
			BetterBanListCommand.register(event.getDispatcher());
			TempbanCommand.register(event.getDispatcher());
			TempbanIpCommand.register(event.getDispatcher());
		}
	}
}
