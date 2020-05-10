package com.zelex.basictomcat.http;

import java.io.IOException;
import java.io.InputStream;

public class ZLXRequest {
    
    private String method;
    private String url;

    public ZLXRequest(InputStream in){
        try{
            //拿到http的数据
            String content="";
            byte[] buff=new byte[1024];
            int len=0;
            if ((len = in.read(buff))>0){
                content=new String(buff,0,len);
            }
            String line=content.split("\\n")[0];
            String [] arr=line.split("\\s");

            this.method=arr[0];
            this.url=arr[1].split("\\?")[0];
            System.out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getURL() {
        return url;
    }
    public String getMethod() {
        return method;
    }
}
