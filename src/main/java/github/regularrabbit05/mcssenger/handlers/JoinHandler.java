package github.regularrabbit05.mcssenger.handlers;

import github.regularrabbit05.mcssenger.Plugin;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinHandler implements Listener {
    private final Plugin plugin;
    public JoinHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            BaseComponent[] asMessage = plugin.getAsMessage();
            if (asMessage != null) event.getPlayer().spigot().sendMessage(asMessage);
            plugin.updatePlayer(event.getPlayer());
        }, 1L);
    }
}
