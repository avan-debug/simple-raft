package com.avan.simpleraft.rpc.core;

import java.io.Serializable;

public class RpcRequest implements Serializable{
    private String className;
    private String methodName;
    private Object[] params;
    private Class<?> [] paramTypes;

    
    public RpcRequest() {
    }


    public RpcRequest(String className, String methodName, Object[] params, Class<?>[] paramTypes) {
        this.className = className;
        this.methodName = methodName;
        this.params = params;
        this.paramTypes = paramTypes;
    }


    public String getClassName() {
        return className;
    }


    public void setClassName(String className) {
        this.className = className;
    }


    public String getMethodName() {
        return methodName;
    }


    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }


    public Object[] getParams() {
        return params;
    }


    public void setParams(Object[] params) {
        this.params = params;
    }


    public Class<?>[] getParamTypes() {
        return paramTypes;
    }


    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    

    
}
