package com.yan;

import com.yan.hello.Hello;
import com.yan.hello.IHelloService;
import com.yan.rpcframeworkstudy.annotation.RpcReference;
import org.springframework.stereotype.Component;

/**
 * .
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/3/20 0020
 * @since JDK 1.8.0
 */
@Component
public class HelloServiceController {

    @RpcReference(group = "test1", version = "version1")
    private IHelloService helloService;

    public String hello(Hello hello) {
        return helloService.hello(hello);
    }
}
