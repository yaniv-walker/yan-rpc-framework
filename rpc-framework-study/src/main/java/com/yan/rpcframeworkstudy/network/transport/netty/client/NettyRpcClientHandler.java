package com.yan.rpcframeworkstudy.network.transport.netty.client;

import com.yan.rpcframeworkcommon.factory.SingletonFactory;
import com.yan.rpcframeworkstudy.network.dto.RpcMessage;
import com.yan.rpcframeworkstudy.network.dto.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Handle the message that received from the server.
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/2/23 0023
 * @since JDK 1.8.0
 */
@Slf4j
public class NettyRpcClientHandler extends ChannelInboundHandlerAdapter {

    private final UnprocessedRequests unprocessedRequests;

    public NettyRpcClientHandler() {
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (log.isInfoEnabled()) {
            log.info("client receive msg: [{}]", msg);
        }

        try {
            // 1. msg is RpcMessage.
            if (msg instanceof RpcMessage) {
                final RpcMessage rpcMessage = (RpcMessage) msg;

                // 2. if message is not null,
                // then complete the response future that corresponds to the requestId.
                // And we need singleton factory to generate the common UnprocessedRequests.
                if (rpcMessage.getData() instanceof RpcResponse) {
                    final RpcResponse<Object> rpcResponse = (RpcResponse<Object>) rpcMessage.getData();
                    this.unprocessedRequests.complete(rpcResponse);
                    if (log.isInfoEnabled()) {
                        log.info("the rpc response future is completed.");
                    }
                }
            }

        } finally {
            ReferenceCountUtil.release(msg);
        }



    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (log.isErrorEnabled()) {
            log.error("read channel failed", cause);
        }
        ctx.close();
    }
}
