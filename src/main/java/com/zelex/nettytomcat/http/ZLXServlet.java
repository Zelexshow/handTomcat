package com.zelex.nettytomcat.http;


import io.netty.handler.codec.http.HttpMethod;

import java.io.IOException;
import java.util.Objects;

public abstract class ZLXServlet {
    public void service(ZLXRequest request, ZLXResponse response) throws Exception{
        // 判断调用的方法
        if (Objects.equals(HttpMethod.GET,request.getMethod())) {
            doGet(request,response);
        } else {
            doPost(request,response);
        }
    }

    protected abstract void doPost(ZLXRequest request, ZLXResponse response) throws IOException;

    protected abstract void doGet(ZLXRequest request, ZLXResponse response) throws IOException;
}
