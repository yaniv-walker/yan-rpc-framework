package com.yan.rpcframeworkstudy.annotation;

import com.yan.rpcframeworkstudy.spring.RpcCustomScannerRegister;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * scan custom annotations.
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/3/19 0019
 * @since JDK 1.8.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(RpcCustomScannerRegister.class)
@Documented
public @interface RpcScan {
    /**
     * the basePackage where the scanner will scan custom annotations.
     * @return base package
     */
    String[] basePackage();
}
