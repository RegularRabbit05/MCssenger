package github.regularrabbit05.mcssenger.types;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class PinnedMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Long timestamp;
    private Long expiry;
    private String playerName;
    private final UUID uuid;
    private final String message;

    public PinnedMessage(Long timestamp, String playerName, UUID uuid, String message) {
        this.timestamp = timestamp;
        this.expiry = 0L;
        this.playerName = playerName;
        this.uuid = uuid;
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Long getExpiry() {
        return expiry;
    }

    public String getPlayerName() {
        return playerName;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getMessage() {
        return message;
    }

    public void setExpiry(Long expiry) {
        this.expiry = expiry;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
