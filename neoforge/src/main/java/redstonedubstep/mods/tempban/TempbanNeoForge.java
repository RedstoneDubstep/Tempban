package redstonedubstep.mods.tempban;

import net.minecraft.commands.Commands;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod("tempban")
public class TempbanNeoForge {
	public TempbanNeoForge() {
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
