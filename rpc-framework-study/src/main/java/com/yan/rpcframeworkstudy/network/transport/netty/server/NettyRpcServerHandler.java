package com.yan.rpcframeworkstudy.network.transport.netty.server;

import com.yan.rpcframeworkcommon.enums.RpcMessageTypeEnum;
import com.yan.rpcframeworkcommon.enums.RpcResponseCodeEnum;
import com.yan.rpcframeworkstudy.network.dto.RpcMessage;
import com.yan.rpcframeworkstudy.network.dto.RpcRequest;
import com.yan.rpcframeworkstudy.network.dto.RpcResponse;
import com.yan.rpcframeworkstudy.network.handler.RpcRequestHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * server handler.
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/2/25 0025
 * @since JDK 1.8.0
 */
@Slf4j
public class NettyRpcServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//        super.channelRead(ctx, msg);
        try {
            if (msg instanceof RpcMessage) {
                if (log.isInfoEnabled()) {
                    log.info("server read channel: [{}]", msg);
                }
                // 1. get rpc message received by the server and build response message
                final RpcMessage receivedRpcMessage = (RpcMessage) msg;
                final RpcMessage rpcMessage = buildRpcMessageForResponse(receivedRpcMessage);

                // 2. Retrieve the request and invoke the method requested by the request.
                if (RpcMessageTypeEnum.REQUEST.getCode() == receivedRpcMessage.getMessageType()) {
                    RpcRequest rpcRequest = (RpcRequest) rpcMessage.getData();
                    if (null == rpcRequest) {
                        rpcRequest = RpcRequest.builder().requestId(String.valueOf(receivedRpcMessage.getRequestId())).build();
                    }

                    final RpcRequestHandler handler = new RpcRequestHandler();
                    final Object data = handler.handle(rpcRequest);
                    handleResponse(ctx, rpcMessage, rpcRequest, data);
                } else {
                    handleFailedResponse("the message received by the server is unrecognized", rpcMessage);
                }

                ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private RpcMessage buildRpcMessageForResponse(RpcMessage receivedRpcMessage) {
        final RpcMessage rpcMessage = new RpcMessage();
        rpcMessage.setMessageType(RpcMessageTypeEnum.RESPONSE.getCode());
        rpcMessage.setRequestId(receivedRpcMessage.getRequestId());
        rpcMessage.setCodec(receivedRpcMessage.getCodec());
        rpcMessage.setCompress(receivedRpcMessage.getCompress());
        rpcMessage.setData(receivedRpcMessage.getData());
        return rpcMessage;
    }

    private void handleResponse(ChannelHandlerContext ctx, RpcMessage rpcMessage, RpcRequest rpcRequest, Object data) {
        if (ctx.channel().isActive() && ctx.channel().isWritable()) {
            handleSuccessfulResponse(rpcMessage, rpcRequest, data);
        } else {
            handleFailedResponse("not writable now, message dropped", rpcMessage);
        }
    }

    private void handleSuccessfulResponse(RpcMessage rpcMessage, RpcRequest rpcRequest, Object data) {
        if (log.isInfoEnabled()) {
            log.info("the server successfully handled the request");
        }
        rpcMessage.setData(RpcResponse.success(data, rpcRequest.getRequestId()));
    }

    private void handleFailedResponse(final String msg, final RpcMessage rpcMessage) {
        if (log.isErrorEnabled()) {
            log.error(msg);
        }
        rpcMessage.setData(RpcResponse.fail(RpcResponseCodeEnum.FAIL));
    }
}
