package github.regularrabbit05.mcssenger.commands;

import github.regularrabbit05.mcssenger.Plugin;
import github.regularrabbit05.mcssenger.types.PinnedMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnpinMessage implements CommandExecutor {
    private final Plugin plugin;
    public UnpinMessage(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can run this!");
            return true;
        }

        if (args.length != 1) return false;

        long id;
        try {
            id = Long.parseLong(args[0]);
        } catch (Exception ignored) {
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            PinnedMessage message = plugin.popMessage(id);
            if (message == null) {
                sender.sendMessage("Message not found!");
                return;
            }
            sender.sendMessage("Unpinned " + message.getPlayerName() + "'s message!");
        });

        return true;
    }
}
