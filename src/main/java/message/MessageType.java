package message;

import java.io.Serializable;

public enum MessageType implements Serializable {
    TEXT_MESSAGE,
    REQUEST_USER_NAME,
    REPLY_USER_NAME,
    NAME_NOT_AVAILABLE,
    NAME_ACCEPTED,
    DISCONNECT_USER,
    ADD_USER,
    DELETE_USER,
    PING
}
