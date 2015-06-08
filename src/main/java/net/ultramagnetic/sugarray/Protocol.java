package net.ultramagnetic.sugarray;

/**
 * 通信方式を表すenumです。
 */
public enum Protocol {

    /**
     * HTTPSを表す
     */
    HTTPS("https"),
    /**
     * HTTPを表す
     */
    HTTP("http");

    private String value;

    Protocol(String value) {
        this.value = value;
    }

    /**
     * String値を返す。
     *
     * @return
     */
    public String value() {
        return value;
    }

    /**
     * StringからProtocolを生成する。
     *
     * @param value
     * @return
     */
    public static Protocol fromString(String value) {
        for (Protocol protocol : Protocol.values()) {
            if (protocol.value().equals(value)) {
                return protocol;
            }
        }
        return null;
    }
}
