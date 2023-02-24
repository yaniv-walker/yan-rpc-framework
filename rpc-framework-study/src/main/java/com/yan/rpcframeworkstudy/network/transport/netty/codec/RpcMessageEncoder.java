package com.yan.rpcframeworkstudy.network.transport.netty.codec;

import com.yan.rpcframeworkcommon.enums.RpcCodecEnum;
import com.yan.rpcframeworkstudy.network.contants.RpcConstants;
import com.yan.rpcframeworkstudy.network.dto.RpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectOutputStream;

/**
 * Encoding message according to the protocol below.
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
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {

    /**
     * Encode a message into a {@link ByteBuf}. This method will be called for each written message that can be handled
     * by this encoder.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link MessageToByteEncoder} belongs to
     * @param msg the message to encode
     * @param out the {@link ByteBuf} into which the encoded message will be written
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage msg, ByteBuf out) {
        if (log.isInfoEnabled()) {
            log.info("start to encode the message");
        }

        try {
            // 1. set magic code and version into byteBuf
            out.writeBytes(RpcConstants.MAGIC_CODE);
            out.writeByte(RpcConstants.VERSION);

            // 2. mark the 'writeIndex' and skip four units of length in the byteBuf, where these four units represent
            // the total length of the byte of message. Until all the rest of protocol has been written completely into
            // the byteBuf, the length field could be written into the marked index of the byteBuf.
            final int fullLengthIndex = out.writerIndex();
            out.writerIndex(fullLengthIndex + RpcConstants.LENGTH_FIELD_LENGTH);

            // 3. Write the remaining contents of the protocol in order.
            out.writeByte(msg.getMessageType());
            out.writeByte(msg.getCodec());
            out.writeByte(msg.getCompress());
            out.writeInt(msg.getRequestId());

            // 4. TODO: serialize the data of message with the codecType.
//        if (RpcCodecEnum.JAVA_BASIC.getCode() == msg.getCodec()) {
//
//        }

            // 5. compute the full length of above contents.
            int fullLength = RpcConstants.HEADER_LENGTH;
            final int tailIndex = out.writerIndex();
            out.writerIndex(fullLengthIndex);
            out.writeInt(fullLength);
            out.writerIndex(tailIndex);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Encode request failed", e);
            }
        }

    }
}
