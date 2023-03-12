package com.yan.hello;

import lombok.*;

import java.io.Serializable;

/**
 * .
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/3/12 0012
 * @since JDK 1.8.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Hello implements Serializable {

    private String message;
    private String description;
}
