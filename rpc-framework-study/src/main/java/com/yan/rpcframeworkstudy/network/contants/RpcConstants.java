package com.yan.rpcframeworkstudy.network.contants;

/**
 * constants.
 *
 * @author yanjiaqi
 * @version 1.0.0 2023-02-24
 * @since JDK 1.8.0
 */
public final class RpcConstants {
    // protocol header
    public static final byte[] MAGIC_CODE = { (byte) 'y', (byte) 'r', (byte) 'p', (byte) 'c' };
    public static final byte VERSION = 1;

    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;
    public static final int LENGTH_FIELD_OFFSET = 5;
    public static final int LENGTH_FIELD_LENGTH = 4;
    public static final int LENGTH_ADJUSTMENT = 0;
    public static final int INITIAL_BYTES_TO_STRIP = 0;

    /**
     * the basic serialization type of Java.
     */
    public static final byte JAVA_CODEC = 1;

    // message type
    public static final byte MESSAGE_TYPE_REQUEST = 1;
    public static final byte MESSAGE_TYPE_RESPONSE = 2;

    public static final int HEADER_LENGTH = 16;

    public static final String SERVER_IP_ADDRESS = "10.122.3.56";
    public static final Integer SERVER_PORT = 9998;
}
