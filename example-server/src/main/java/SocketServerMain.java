import com.yan.rpcframeworkstudy.network.transport.IRpcServer;
import com.yan.rpcframeworkstudy.network.transport.socket.SocketRpcServer;

/**
 * .
 *
 * @author yanjiaqi
 * @version 1.0.0 2023-02-22
 * @since JDK 1.8.0
 */
public class SocketServerMain {

    public static void main(String[] args) {

        final IRpcServer rpcServer = new SocketRpcServer();
        rpcServer.start();
    }
}
