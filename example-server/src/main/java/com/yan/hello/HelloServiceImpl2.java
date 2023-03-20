package com.yan.hello;

import com.yan.rpcframeworkstudy.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

/**
 * com.yan.hello service 2.
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/3/13 0013
 * @since JDK 1.8.0
 */
@Slf4j
@RpcService(group = "test2", version = "version1")
public class HelloServiceImpl2 implements IHelloService {
    /**
     * @param hello com.yan.hello message
     * @return result
     */
    @Override
    public String hello(final Hello hello) {
        // log all attributes of com.yan.hello
        if (log.isInfoEnabled()) {
            log.info("HelloServiceImpl2 received: [{}]", hello);
        }

        // return the com.yan.hello description
        final String result = "com.yan.hello description is " + hello.getDescription();
        if (log.isInfoEnabled()) {
            log.info("HelloServiceImpl2 return: [{}]", result);
        }
        return result;
    }
}
