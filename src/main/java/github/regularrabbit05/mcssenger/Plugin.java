package github.regularrabbit05.mcssenger;

import github.regularrabbit05.mcssenger.commands.PinMessage;
import github.regularrabbit05.mcssenger.commands.UnpinMessage;
import github.regularrabbit05.mcssenger.handlers.JoinHandler;
import github.regularrabbit05.mcssenger.types.PinnedMessage;
import github.regularrabbit05.mcssenger.types.Storage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class Plugin extends JavaPlugin {
    private final String STORAGE_FILE = "storage.jdb";
    private final HashMap<Long, PinnedMessage> messages = new HashMap<>();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onEnable() {
        try {
            this.getDataFolder().mkdir();
        } catch (Exception ignored) {
            this.getLogger().severe("Unable to create data folder, " + this.getName() + " will disable itself!");
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        try {
            readStorage();
        } catch (Exception ignored) {
            this.getLogger().severe("Unable to read storage. It will be recreated!");
            synchronized (messages) {
                messages.clear();
            }
        }

        this.getServer().getPluginManager().registerEvents(new JoinHandler(this), this);
        PluginCommand pincmd = this.getCommand("pin");
        if (pincmd != null) pincmd.setExecutor(new PinMessage(this));
        PluginCommand pinrmcmd = this.getCommand("pinrm");
        if (pinrmcmd != null) pinrmcmd.setExecutor(new UnpinMessage(this));
    }

    @Override
    public void onDisable() {
        try {
            saveStorage();
        } catch (Exception ignored) {
            this.getLogger().severe("Unable to save storage...");
        }
    }

    public void updatePlayer(Player p) {
        AtomicBoolean shouldUpdate = new AtomicBoolean(false);
        synchronized (messages) {
            messages.forEach((l, m) -> {
                if (!p.getUniqueId().equals(m.getUuid())) return;
                if (p.getName().equals(m.getPlayerName())) return;
                m.setPlayerName(p.getName());
                shouldUpdate.set(true);
            });
        }
        try {
            if (shouldUpdate.get()) saveStorage();
        } catch (Exception ignored) {}
    }

    public void pushMessage(PinnedMessage msg) {
        synchronized (messages) {
            messages.put(msg.getTimestamp(), msg);
        }
        try {
            saveStorage();
        } catch (Exception ignored) {}
    }

    public PinnedMessage popMessage(Long id) {
        synchronized (messages) {
            if (!messages.containsKey(id)) return null;
            return messages.remove(id);
        }
    }

    @SuppressWarnings("deprecation")
    public BaseComponent[] getAsMessage() {
        PinnedMessage[] pinnedMessages;
        synchronized (messages) {
            if (messages.isEmpty()) return null;
            pinnedMessages = messages.values().stream().sorted().toArray(PinnedMessage[]::new);
        }

        final BaseComponent[] delBuilder = new ComponentBuilder("Click to delete this pinned message").color(ChatColor.RED).bold(true).create();
        ComponentBuilder builder = new ComponentBuilder("\n");
        builder.append("--------- Pinned messages ---------").color(ChatColor.GOLD).append("\n").reset();
        for (PinnedMessage pinnedMessage : pinnedMessages) {
            ComponentBuilder line = new ComponentBuilder();
            TextComponent del = new TextComponent("[❌] ");
            del.setColor(ChatColor.RED);
            del.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pinrm " + pinnedMessage.getTimestamp()));
            del.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, delBuilder));

            final String message = ChatColor.translateAlternateColorCodes('&', pinnedMessage.getMessage());
            BaseComponent[] messageFormat = TextComponent.fromLegacyText(message, ChatColor.RESET);

            line = line.append(del).append("From " + pinnedMessage.getPlayerName() + ": ").color(ChatColor.GREEN).append("\n").reset();
            line = line.append(messageFormat).append("\n").reset();
            builder.append(line.build());
        }

        return builder.create();
    }

    public void saveStorage() throws Exception {
        PinnedMessage[] sortedMessages;
        Set<Long> keys;
        synchronized (messages) {
            sortedMessages = new PinnedMessage[messages.size()];
            keys = messages.keySet();
            AtomicInteger idx = new AtomicInteger(0);
            keys.stream().sorted().forEach(v -> sortedMessages[idx.getAndIncrement()] = messages.get(v));
        }

        for (PinnedMessage sortedMessage : sortedMessages) {
            if (sortedMessage == null) throw new NullPointerException("Item can't be null");
        }

        Storage store = new Storage(sortedMessages);

        File f = new File(getDataFolder(), STORAGE_FILE);
        try (
                FileOutputStream fileOutputStream = new FileOutputStream(f);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)
        ) {
            objectOutputStream.writeObject(store);
            objectOutputStream.flush();
        }
    }

    @SuppressWarnings("ExtractMethodRecommender")
    private void readStorage() throws Exception {
        Storage store;
        File f = new File(getDataFolder(), STORAGE_FILE);
        try (
                FileInputStream fileInputStream = new FileInputStream(f);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)
        ) {
            store = (Storage) objectInputStream.readObject();
        }

        if (store == null) throw new NullPointerException("Database is null");

        PinnedMessage[] sortedMessages = store.getMessages();
        if (sortedMessages == null) throw new NullPointerException("List is null");

        for (PinnedMessage sortedMessage : sortedMessages) {
            if (sortedMessage == null) throw new NullPointerException("Item can't be null");
        }

        synchronized (messages) {
            messages.clear();
            for (PinnedMessage sortedMessage : sortedMessages) {
                messages.put(sortedMessage.getTimestamp(), sortedMessage);
            }
        }
    }
}
