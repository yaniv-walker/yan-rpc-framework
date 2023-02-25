package com.yan.rpcframeworkstudy.network.transport.netty.codec;

import com.yan.rpcframeworkcommon.enums.RpcMessageTypeEnum;
import com.yan.rpcframeworkstudy.network.contants.RpcConstants;
import com.yan.rpcframeworkstudy.network.dto.RpcMessage;
import com.yan.rpcframeworkstudy.network.dto.RpcRequest;
import com.yan.rpcframeworkstudy.network.dto.RpcResponse;
import com.yan.rpcframeworkstudy.serializing.context.SerializerContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.TooLongFrameException;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * Decoding message according to the protocol below.
 * <pre>
 *   0     1     2     3     4        5    6    7    8    9            10      11         12   13   14   15   16
 *   +-----+-----+-----+-----+--------+----+----+----+----+------------+-------+----------+----+----+----+----+
 *   |   magic   code        |version |    full length    | messageType| codec | compress |    RequestId      |
 *   +-----------------------+--------+---------------------+-----------+-----------+-----------+-------------+
 *   |                                                                                                        |
 *   |                                         body                                                           |
 *   |                                                                                                        |
 *   |                                        ... ...                                                         |
 *   +--------------------------------------------------------------------------------------------------------+
 *   4B magic code    1B version    4B full length    1B messageType    1B codec(serialization type)
 *   1B compress    4B requestId
 *   body(RpcResponse object that length is 'full length')
 * </pre>
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/2/23 0023
 * @since JDK 1.8.0
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {

    /**
     * Creates a new instance of decoder.
     */
    public RpcMessageDecoder() {
        super(RpcConstants.MAX_FRAME_LENGTH, RpcConstants.LENGTH_FIELD_OFFSET, RpcConstants.LENGTH_FIELD_LENGTH,
                RpcConstants.LENGTH_ADJUSTMENT, RpcConstants.INITIAL_BYTES_TO_STRIP, true);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if (log.isInfoEnabled()) {
            log.info("start to decode the byteBuf");
        }

        final Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf) {
            // build the RpcMessage with the decoded
            final ByteBuf frame = (ByteBuf) decoded;
            if (frame.readableBytes() >= RpcConstants.HEADER_LENGTH) {
                try {
                    return this.decodeFrame(frame);
                } catch (Exception e) {
                    if (log.isErrorEnabled()) {
                        log.error("Decode frame error");
                    }
                    throw e;
                }
            }
        }

        return decoded;
    }

    /**
     * decode the decoded.
     * @param in decoded
     * @return RpcMessage data
     */
    private Object decodeFrame(final ByteBuf in) {
        if (log.isInfoEnabled()) {
            log.info("start to decode the frame");
        }

        // |   magic   code        |version |    full length    | messageType| codec | compress |    RequestId      |
        checkMagicCode(in);
        checkVersion(in);

        final int fullLength = in.readInt();
        final byte messageType = in.readByte();
        final byte codec = in.readByte();
        final byte compress = in.readByte();
        final int requestId = in.readInt();

        final int dataLength = fullLength - RpcConstants.HEADER_LENGTH;
        if (in.readableBytes() < dataLength) {
            throw new IllegalArgumentException("Message data is incorrect");
        }

        // deserialize data
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        Object dataInstance = null;
        final SerializerContext serializerContext = new SerializerContext();
        if (RpcMessageTypeEnum.REQUEST.getCode() == messageType) {
            // request message
            if (log.isInfoEnabled()) {
                log.info("decode request message");
            }

            dataInstance = serializerContext.deserialize(codec, data, RpcRequest.class);
        } else if (RpcMessageTypeEnum.RESPONSE.getCode() == messageType) {
            // response message
            if (log.isInfoEnabled()) {
                log.info("decode response message");
            }

            dataInstance = serializerContext.deserialize(codec, data, RpcResponse.class);
        }

        return RpcMessage.builder().messageType(messageType).codec(codec)
                .compress(compress).requestId(requestId).data(dataInstance).build();
    }

    private void checkVersion(ByteBuf in) {
        final byte version = in.readByte();
        if (RpcConstants.VERSION != version) {
            throw new IllegalArgumentException("Version isn't compatible: " + version);
        }
    }

    private void checkMagicCode(ByteBuf in) {
        final int length = RpcConstants.MAGIC_CODE.length;
        final byte[] magicCode = new byte[length];
        in.readBytes(magicCode);
        for (int i = 0; i < length; i++) {
            if (RpcConstants.MAGIC_CODE[i] != magicCode[i]) {
                throw new IllegalArgumentException("Unknown magic code: " + Arrays.toString(magicCode));
            }
        }
    }
}
