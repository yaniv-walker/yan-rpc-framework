package com.yan.rpcframeworkstudy.network.transport.socket;

import com.yan.rpcframeworkstudy.network.dto.RpcRequest;
import com.yan.rpcframeworkstudy.network.transport.IRpcRequestTransport;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Socket implement of the rpc client.
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/2/20 0020
 * @since JDK 1.8.0
 */
@Slf4j
public class SocketRpcClient implements IRpcRequestTransport {

    private static final String SERVER_IP_ADDRESS = "10.122.3.56";
    private static final Integer SERVER_PORT = 9998;

    /**
     * send request data to server.
     *
     * @param rpcRequest rpc request data
     * @return server response
     */
    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        // TODO: 1. Need to look up the connection info of the server
        final InetSocketAddress socketAddress = new InetSocketAddress(SERVER_IP_ADDRESS, SERVER_PORT);

        // 2. Connect with the socket of the server using info 1.
        try(final Socket socket = new Socket()) {
            socket.connect(socketAddress);
            final ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            final ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            // 3. Send data to the server through the output stream.
            output.writeObject(rpcRequest);
            output.flush();

            // 4. Read RpcResponse from the input stream.
            return input.readObject();

        } catch (IOException | ClassNotFoundException e) {
            if (log.isErrorEnabled()) {
                log.error(String.format("connect the client to the server[%s:%d] failed.",
                        SERVER_IP_ADDRESS, SERVER_PORT), e);
            }
        }


        return null;
    }
}
