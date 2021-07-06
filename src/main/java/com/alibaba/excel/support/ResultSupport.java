/*
 * Copyright (c) 2021 GetWrath.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */
package com.alibaba.excel.support;

/**
 * 操作结果.
 *
 * @author zhoupan.
 */
public class ResultSupport implements java.io.Serializable {

    public static final String MESSAGE_SUCCESS = "操作成功";
    public static final String MESSAGE_ERROR = "操作失败";
    /** 成功状态代码 */
    public static final int CODE_SUCCESS = 0;
    /** 失败状态代码. */
    public static final int CODE_ERROR = 500;
    /** */
    private static final long serialVersionUID = 1L;

    /** 状态代码(0表示成功,其他表示失败) */
    private int code;

    /** 是否成功. */
    private boolean success = true;

    /** 成功、错误消息. */
    private String message = MESSAGE_SUCCESS;

    /**
     * Gets the code.
     *
     * @return the code
     */
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 设置异常结果.
     *
     * @param e
     *            异常.
     */
    public void onException(Throwable e) {
        this.onException(MESSAGE_ERROR, e);
    }

    /**
     * 设置异常结果.
     *
     * @param message
     *            异常消息.
     */
    public void onException(String message) {
        this.setSuccess(false);
        this.setMessage(message);
        this.setCode(CODE_ERROR);
    }

    /**
     * 设置异常结果.
     *
     * @param message
     * @param e
     */
    public void onException(String message, Throwable e) {
        this.setSuccess(false);
        this.setCode(CODE_ERROR);
        this.setMessage(e.getMessage());
        LoggerSupport.error("{}", e);
    }

    /**
     * 设置成功结果.
     *
     * @param message
     *            成功消息.
     */
    public void onSuccess(String message) {
        this.setSuccess(true);
        this.setMessage(message);
        this.setCode(CODE_SUCCESS);
    }

}
