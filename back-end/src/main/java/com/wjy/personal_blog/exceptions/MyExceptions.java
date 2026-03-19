package com.wjy.personal_blog.exceptions;

/**
 * 自定义异常类
 */

public class MyExceptions extends RuntimeException{


    private String errorMsg;

    public MyExceptions(String message) {
        super(message);
        this.errorMsg=message;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
