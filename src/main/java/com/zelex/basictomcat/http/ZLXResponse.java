package com.zelex.basictomcat.http;

import java.io.IOException;
import java.io.OutputStream;

public class ZLXResponse {
    private OutputStream out;
    public ZLXResponse(OutputStream out){
        this.out=out;
    }

    public void write(String s) throws IOException {
        //由于使用一个HTTP协议，所以需要遵循协议，补充状态码等条件
        //给出一个状态码200
        StringBuilder sb = new StringBuilder();
         sb.append("HTTP/1.1 200 OK\n")
            .append("Content-Type:text/html;\n")
            .append("\r\n")
            .append(s);
         out.write(sb.toString().getBytes());
    }
}
