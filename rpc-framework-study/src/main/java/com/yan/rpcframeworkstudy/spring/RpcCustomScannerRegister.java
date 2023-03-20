package com.yan.rpcframeworkstudy.spring;

import com.yan.rpcframeworkstudy.annotation.RpcScan;
import com.yan.rpcframeworkstudy.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;

/**
 * custom rpc scanner register, to scan custom annotations.
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/3/19 0019
 * @since JDK 1.8.0
 */
@Slf4j
public class RpcCustomScannerRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private static final String RPC_BASE_PACKAGE_NAME = "basePackage";

    private static final String SPRING_BASE_PACKAGE = "com.yan";

    private ResourceLoader resourceLoader;

    /**
     * Set the ResourceLoader that this object runs in.
     * <p>This might be a ResourcePatternResolver, which can be checked
     * through {@code instanceof ResourcePatternResolver}. See also the
     * {@code ResourcePatternUtils.getResourcePatternResolver} method.
     * <p>Invoked after population of normal bean properties but before an init callback
     * like InitializingBean's {@code afterPropertiesSet} or a custom init-method.
     * Invoked before ApplicationContextAware's {@code setApplicationContext}.
     *
     * @param resourceLoader the ResourceLoader object to be used by this object
     * @see ResourcePatternResolver
     * @see ResourcePatternUtils#getResourcePatternResolver
     */
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Register bean definitions as necessary based on the given annotation metadata of
     * the importing {@code @Configuration} class.
     *
     * @param importingClassMetadata annotation metadata of the importing class
     * @param registry               current bean definition registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // first, get the base package from annotation of name "RpcScan"
        final AnnotationAttributes annotationAttributes =
                AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(RpcScan.class.getName()));

        String[] rpcScanBasePackages = new String[0];
        if (null != annotationAttributes) {
            rpcScanBasePackages = annotationAttributes.getStringArray(RPC_BASE_PACKAGE_NAME);
        }

        if (0 == rpcScanBasePackages.length) {
            rpcScanBasePackages = new String[] { ((StandardAnnotationMetadata) importingClassMetadata).getIntrospectedClass().getPackage().getName() };
        }

        // second, create scanner to scan annotation RpcService and Spring Component annotation.
        // And the "RpcReference" field annotation is located in a class that is annotated with the "Component" annotation.
        final RpcCustomScanner rpcServiceCustomScanner = new RpcCustomScanner(registry, RpcService.class);
        final RpcCustomScanner springRpcCustomScanner = new RpcCustomScanner(registry, Component.class);

        // set resourceLoader if exist
        if (null != resourceLoader) {
            rpcServiceCustomScanner.setResourceLoader(this.resourceLoader);
            springRpcCustomScanner.setResourceLoader(this.resourceLoader);
        }

        final int springScannedCount = springRpcCustomScanner.scan(SPRING_BASE_PACKAGE);
        if (log.isInfoEnabled()) {
            log.info("The [{}] spring base path is scanned [{}] @Component.", SPRING_BASE_PACKAGE, springScannedCount);
        }
        final int rpcScannedCount = rpcServiceCustomScanner.scan(rpcScanBasePackages);
        if (log.isInfoEnabled()) {
            log.info("The [{}] rpc base path is scanned [{}] @RpcService.", rpcScanBasePackages, rpcScannedCount);
        }
    }
}
