package hello;

import com.yan.hello.Hello;
import com.yan.hello.IHelloService;
import lombok.extern.slf4j.Slf4j;

/**
 * hello service 2.
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/3/13 0013
 * @since JDK 1.8.0
 */
@Slf4j
public class HelloServiceImpl2 implements IHelloService {
    /**
     * @param hello hello message
     * @return result
     */
    @Override
    public String hello(final Hello hello) {
        // log all attributes of hello
        if (log.isInfoEnabled()) {
            log.info("HelloServiceImpl2 received: [{}]", hello);
        }

        // return the hello description
        final String result = "hello description is " + hello.getDescription();
        if (log.isInfoEnabled()) {
            log.info("HelloServiceImpl2 return: [{}]", result);
        }
        return result;
    }
}
