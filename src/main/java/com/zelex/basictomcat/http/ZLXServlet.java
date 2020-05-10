package com.zelex.basictomcat.http;


import java.io.IOException;

public abstract class ZLXServlet {
    public void service(ZLXRequest request,ZLXResponse response) throws Exception{
        // 判断调用的方法
        if ("GET".equalsIgnoreCase(request.getMethod())){
            doGet(request,response);
        }else{
            doPost(request,response);
        }
    }

    protected abstract void doPost(ZLXRequest request, ZLXResponse response) throws IOException;

    protected abstract void doGet(ZLXRequest request, ZLXResponse response) throws IOException;
}
