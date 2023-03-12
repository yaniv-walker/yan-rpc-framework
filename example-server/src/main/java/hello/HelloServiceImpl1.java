package hello;

import com.yan.hello.Hello;
import com.yan.hello.IHelloService;
import lombok.extern.slf4j.Slf4j;

/**
 * .
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/3/12 0012
 * @since JDK 1.8.0
 */
@Slf4j
public class HelloServiceImpl1 implements IHelloService {

    /**
     * @param hello hello message
     * @return result
     */
    @Override
    public String hello(final Hello hello) {
        if (log.isInfoEnabled()) {
            log.info("HelloServiceImpl1 received: [{}]", hello);
        }
        final String result = "hello description is " + hello.getDescription();
        if (log.isInfoEnabled()) {
            log.info("HelloServiceImpl1 return: [{}]", result);
        }

        return result;
    }
}
