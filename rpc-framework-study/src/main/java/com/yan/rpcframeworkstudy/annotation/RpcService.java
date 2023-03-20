package com.yan.rpcframeworkstudy.annotation;

import java.lang.annotation.*;

/**
 * Rpc service annotation, publish service.
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/3/20 0020
 * @since JDK 1.8.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface RpcService {
    /**
     * service group, default value is empty string.
     * @return service group
     */
    String group() default "";

    /**
     * service version, default value is empty string.
     * @return service version
     */
    String version() default "";

}
