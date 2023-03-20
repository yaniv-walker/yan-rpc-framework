package com.yan.rpcframeworkstudy.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;

/**
 * .
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/3/19 0019
 * @since JDK 1.8.0
 */
public class RpcCustomScanner extends ClassPathBeanDefinitionScanner {

    /**
     * add annotations of annoType to scan.
     * @param registry
     * @param annoType
     */
    public RpcCustomScanner(BeanDefinitionRegistry registry, Class<? extends Annotation> annoType) {
        super(registry);
        super.addIncludeFilter(new AnnotationTypeFilter(annoType));
    }

    @Override
    public int scan(String... basePackages) {
        return super.scan(basePackages);
    }
}
