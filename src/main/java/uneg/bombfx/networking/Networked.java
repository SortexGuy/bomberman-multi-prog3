package uneg.bombfx.networking;

/**
 * Networked
 */
public interface Networked {
    public enum ConnFlags {
        Invalid,
        Message,
        SyncPlayer,
        SendIds,
        PlayerConnected,
        PlayerDisconnected,
        StartGame,
        CloseConnection;

        public static ConnFlags fromByte(byte flag) {
            int real_flag = (int) Integer.toUnsignedLong(flag);
            for (ConnFlags enum_flag : ConnFlags.values()) {
                if (enum_flag.ordinal() == real_flag)
                    return enum_flag;
            }
            return Invalid;
        }

        public String getAsByteString() {
            byte chara = (byte) Integer.toUnsignedLong(ordinal());
            return String.format("%c", chara);
        }

        public byte getAsByte() {
            return (byte) Integer.toUnsignedLong(ordinal());
        }
    }

    // public enum SyncFlags {
    // Message,
    // PlayerConnected,
    // PlayerDisconnected,
    // StartGame,
    // CloseConnection,
    // Invalid;
    //
    // public static ConnFlags fromByte(byte flag) {
    // int real_flag = (int) Integer.toUnsignedLong(flag);
    // for (ConnFlags enum_flag : ConnFlags.values()) {
    // if (enum_flag.ordinal() == real_flag)
    // return enum_flag;
    // }
    // return Invalid;
    // }
    //
    // public String getAsByteString() {
    // byte chara = (byte) Integer.toUnsignedLong(ordinal());
    // return String.format("%c", chara);
    // }
    //
    // public byte getAsByte() {
    // return (byte) Integer.toUnsignedLong(ordinal());
    // }
    // }
}
