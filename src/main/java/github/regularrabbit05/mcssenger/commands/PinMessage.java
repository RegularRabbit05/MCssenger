package github.regularrabbit05.mcssenger.commands;

import github.regularrabbit05.mcssenger.Plugin;
import github.regularrabbit05.mcssenger.types.PinnedMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PinMessage implements CommandExecutor {
    private final Plugin plugin;
    public PinMessage(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can run this!");
            return true;
        }

        if (args.length == 0) return false;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            StringBuilder builder = new StringBuilder();
            for (String arg : args) builder.append(arg).append(' ');
            String messageText = builder.toString();
            messageText = messageText.replace('\n', ' ').replace('\t', ' ').trim();

            PinnedMessage message = new PinnedMessage(System.currentTimeMillis(), sender.getName(), ((Player) sender).getUniqueId(), messageText);
            plugin.pushMessage(message);
            sender.sendMessage("Your message has been pinned!");
        });

        return true;
    }
}
