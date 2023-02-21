package com.yan.rpcframeworkstudy.network.transport.socket;

import com.yan.rpcframeworkstudy.network.dto.RpcResponse;
import com.yan.rpcframeworkstudy.network.transport.IRpcServer;
import com.yan.rpcframeworkstudy.network.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Socket implement of the rpc server.
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/2/21 0021
 * @since JDK 1.8.0
 */
@Slf4j
public class SocketRpcServer implements IRpcServer {

    private static final Integer SERVER_PORT = 9998;

    /**
     * start RPC server.
     */
    @Override
    public void start() {
        // TODO: Use thread pool


        // 1. To Bind a local port.
        // 2. Waiting connection of the client.
        try(final ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            final Socket socket = serverSocket.accept();
            final ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            final ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {

            // 3. To receive the request of the client through the input stream.
            final RpcRequest rpcRequest = (RpcRequest) input.readObject();

            // TODO: 4. Invoke the method that was included in the request from the previous step.
            final Object result = null;

            // 5. To send the response that corresponds to the request from previous step 3 through the output stream.
            output.writeObject(RpcResponse.success(result, rpcRequest.getRequestId()));
            output.flush();

        } catch (IOException | ClassNotFoundException e) {
            if (log.isErrorEnabled()) {
                log.error("The connection between the server and the client is failed.", e);
            }
        }


    }
}
