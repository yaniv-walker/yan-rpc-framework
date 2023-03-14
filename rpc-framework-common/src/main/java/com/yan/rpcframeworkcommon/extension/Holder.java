package com.yan.rpcframeworkcommon.extension;

import lombok.*;

/**
 * Hold the T object for the multiple thread procession.
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/3/13 0013
 * @since JDK 1.8.0
 */
public class Holder<T> {

    private volatile T value;

    public T get() {
        return value;
    }

    public void set(T value) {
            this.value = value;
        }
}
