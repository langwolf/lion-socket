package com.lioncorp.common.exception;

public class SysException extends RuntimeException {

    private static final long serialVersionUID = -1972925542520532318L;

    public SysException(String e) {
        super(e);
    }

    public SysException(Exception e) {
        super(e);
    }

}
