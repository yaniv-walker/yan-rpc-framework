package com.yan.rpcframeworkstudy.network.transport.socket;

import com.yan.rpcframeworkstudy.network.dto.RpcRequest;
import com.yan.rpcframeworkstudy.network.dto.RpcResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Handle the rpc request for thread.
 *
 * @author yanjiaqi
 * @version 1.0.0 2023-02-22
 * @since JDK 1.8.0
 */
@Slf4j
@AllArgsConstructor
public class SocketRpcRequestHandlerRunnable implements Runnable {

    /**
     * Using to accept client.
     */
    private Socket socket;

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try(final ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            final ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {

            // 3. To receive the request of the client through the input stream.
            final RpcRequest rpcRequest = (RpcRequest) input.readObject();

            // TODO: 4. Invoke the method that was included in the request from the previous step.
            final Object result = "The test result";

            // 5. To send the response that corresponds to the request from previous step 3 through the output stream.
            output.writeObject(RpcResponse.success(result, rpcRequest.getRequestId()));
            output.flush();

        } catch (IOException | ClassNotFoundException e) {
            if (log.isErrorEnabled()) {
                log.error("Handling rpc request is failed.", e);
            }
        }
    }
}
