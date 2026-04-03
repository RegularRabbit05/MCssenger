package github.regularrabbit05.mcssenger.types;

import java.io.Serial;
import java.io.Serializable;

@SuppressWarnings("ClassCanBeRecord")
public class Storage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final PinnedMessage[] messages;

    public Storage(PinnedMessage[] messages) {
        this.messages = messages;
    }

    public PinnedMessage[] getMessages() {
        return this.messages;
    }
}
