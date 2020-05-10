package com.zelex.basictomcat.servlet;

import com.zelex.basictomcat.http.ZLXRequest;
import com.zelex.basictomcat.http.ZLXResponse;
import com.zelex.basictomcat.http.ZLXServlet;

import java.io.IOException;

public class SecondServlet extends ZLXServlet {
    @Override
    protected void doPost(ZLXRequest request, ZLXResponse response) throws IOException {
        doGet(request,response);
    }

    @Override
    protected void doGet(ZLXRequest request, ZLXResponse response) throws IOException {
        response.write("This is SecondServlet");
    }
}
