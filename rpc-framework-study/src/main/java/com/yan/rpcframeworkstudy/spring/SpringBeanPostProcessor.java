package com.yan.rpcframeworkstudy.spring;

import com.yan.rpcframeworkcommon.extension.ExtensionLoader;
import com.yan.rpcframeworkcommon.factory.SingletonFactory;
import com.yan.rpcframeworkstudy.annotation.RpcReference;
import com.yan.rpcframeworkstudy.annotation.RpcService;
import com.yan.rpcframeworkstudy.config.RpcServiceConfig;
import com.yan.rpcframeworkstudy.network.transport.IRpcRequestTransport;
import com.yan.rpcframeworkstudy.provider.IServiceProvider;
import com.yan.rpcframeworkstudy.provider.zk.ZkServiceProvider;
import com.yan.rpcframeworkstudy.proxy.RpcClientProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

/**
 * .
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/3/20 0020
 * @since JDK 1.8.0
 */
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private final IServiceProvider serviceProvider;
    private final IRpcRequestTransport rpcClient;

    public SpringBeanPostProcessor() {
        serviceProvider = SingletonFactory.getInstance(ZkServiceProvider.class);
        rpcClient = ExtensionLoader.getExtensionLoader(IRpcRequestTransport.class).getExtension("netty");
    }

    /**
     * process the class that is annotated with "RpcService" annotation to publish the service。
     * @param bean the new bean instance
     * @param beanName the name of the bean
     * @return the bean instance to use
     * @throws BeansException errors
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            if (log.isInfoEnabled()) {
                log.info("[{}] is annotated with [{}]", bean.getClass().getName(), RpcService.class.getCanonicalName());
            }
            final RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            final RpcServiceConfig config = RpcServiceConfig.builder()
                    .service(bean)
                    .group(rpcService.group())
                    .version(rpcService.version())
                    .build();
            serviceProvider.publishService(config);
        }

        return bean;
    }


    /**
     * populate bean field, it is an interface, that is annotated with "RpcReference" annotation.
     * @param bean the new bean instance
     * @param beanName the name of the bean
     * @return the bean instance to use
     * @throws BeansException errors
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        final Class<?> targetClass = bean.getClass();
        final Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field field : declaredFields) {
            final RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            if (null == rpcReference) {
                continue;
            }

            final RpcServiceConfig config = RpcServiceConfig.builder()
                    .group(rpcReference.group())
                    .version(rpcReference.version())
                    .build();
            final Object proxyTarget = new RpcClientProxy(rpcClient, config).getProxy(field.getType());
            field.setAccessible(true);
            try {
                field.set(bean, proxyTarget);
            } catch (IllegalAccessException e) {
                if (log.isErrorEnabled()) {
                    log.error(String.format("The [%s] bean sets the field [%s] failed", targetClass.getName(), field.getName()), e);
                }
            }
        }

        return bean;
    }

}
