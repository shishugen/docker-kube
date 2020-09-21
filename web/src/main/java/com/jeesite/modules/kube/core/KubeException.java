package com.jeesite.modules.kube.core;

/**
 * @ClassName: KubeException
 * @Description: TODO
 * @Author: shugen
 * @Date: 2020/9/8 10:45
 */
public class KubeException extends RuntimeException {
    public KubeException() {
        super();
    }

    public KubeException(String message) {
        super(message);
    }

    public KubeException(String message, Throwable cause) {
        super(message, cause);
    }

    public KubeException(Throwable cause) {
        super(cause);
    }

    protected KubeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
