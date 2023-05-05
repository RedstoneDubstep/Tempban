package redstonedubstep.mods.tempban;

import net.minecraft.commands.Commands;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkConstants;

@Mod("tempban")
public class TempbanForge {
	public TempbanForge() {
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
		MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
	}

	public void registerCommands(RegisterCommandsEvent event){
		if (event.getCommandSelection() != Commands.CommandSelection.INTEGRATED) {
			BetterBanListCommand.register(event.getDispatcher());
			TempbanCommand.register(event.getDispatcher());
			TempbanIpCommand.register(event.getDispatcher());
		}
	}
}
