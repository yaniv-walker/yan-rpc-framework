package com.yan.hello;

import com.yan.rpcframeworkstudy.annotation.RpcService;
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
@RpcService(group = "test1", version = "version1")
public class HelloServiceImpl1 implements IHelloService {

    /**
     * @param hello com.yan.hello message
     * @return result
     */
    @Override
    public String hello(final Hello hello) {
        if (log.isInfoEnabled()) {
            log.info("HelloServiceImpl1 received: [{}]", hello);
        }
        final String result = "com.yan.hello description is " + hello.getDescription();
        if (log.isInfoEnabled()) {
            log.info("HelloServiceImpl1 return: [{}]", result);
        }

        return result;
    }
}
