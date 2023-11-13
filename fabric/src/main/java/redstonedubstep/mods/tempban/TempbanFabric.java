package redstonedubstep.mods.tempban;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;

public class TempbanFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            if (environment != Commands.CommandSelection.INTEGRATED) {
                BetterBanListCommand.register(dispatcher);
                TempbanCommand.register(dispatcher);
                TempbanIpCommand.register(dispatcher);
            }
        }));
    }
}
