package com.zelex.nettytomcat.servlet;

import com.zelex.nettytomcat.http.ZLXRequest;
import com.zelex.nettytomcat.http.ZLXResponse;
import com.zelex.nettytomcat.http.ZLXServlet;

import java.io.IOException;

public class FirstServlet extends ZLXServlet {
    @Override
    protected void doPost(ZLXRequest request, ZLXResponse response) throws IOException {
        doGet(request,response);
    }

    @Override
    protected void doGet(ZLXRequest request, ZLXResponse response) throws IOException {
        response.write("This is SecondServlet");
    }
}
