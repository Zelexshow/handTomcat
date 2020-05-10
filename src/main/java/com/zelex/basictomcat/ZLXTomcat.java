package com.zelex.basictomcat;

import com.zelex.basictomcat.http.ZLXRequest;
import com.zelex.basictomcat.http.ZLXResponse;
import com.zelex.basictomcat.http.ZLXServlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ZLXTomcat {
    //1、配置好启动端口，默认8080 ServerSocket
    //2、配置web.xml //自己写的servlet继承HttpServlet
    //servlet-name servlet-class url-pattern
    //3、读取配置文件 url-pattern 和 Servlet建立一个映射关系
    //Map servletMapping
    //4、Http请求，发送的数据就是字符串，有规律的字符串（HTTP协议）

    //6、调用实例化对象的service()方法，执行具体的逻辑doGet/doPost
    //7、Request(InputStream)/Response(OutputStream)的封装
    /**
     * 端口号
     */
    private int port;

    /**
     * servlet Map容器
     */
    private Map<String, ZLXServlet> servletMap = new HashMap<>();

    /**
     * 解析配置文件
     */
    private Properties webxml = new Properties();

    public ZLXTomcat(int port) {
        this.port = port;
    }


    /**
     * tomcat 初始化
     */
    private void init() {

        String path = this.getClass().getResource("/").getPath();
        String webPath = path + "web.properties";

        try {
            FileInputStream fis = new FileInputStream(webPath);
            webxml.load(fis);

            for (Object obj : webxml.keySet() ) {
                String key = obj.toString();
                if (key.endsWith(".url")) {

                    String classKey = key.replace(".url",".className");

                    String url = webxml.getProperty(key);
                    String className = webxml.getProperty(classKey);
                    //单实例多线程
                    ZLXServlet servlet = (ZLXServlet) Class.forName(className).newInstance();
                    servletMap.put(url,servlet);
                }
            }
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * tomcat启动
     */
    public void start(){
        init();
        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("ZLXTomcat已经启动，监听端口为："+port);
            while (true){
                Socket client = server.accept();
                process(client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void process(Socket client) throws Exception {
        InputStream in = client.getInputStream();
        OutputStream out = client.getOutputStream();

        ZLXRequest request = new ZLXRequest(in);
        ZLXResponse response = new ZLXResponse(out);
        //5、从协议内容中拿到URL，把相应的Servlet用反射进行实例化
        String url=request.getURL();
        if (servletMap.containsKey(url)){
            servletMap.get(url).service(request,response);
        }else{
            response.write("404 -Not Found");
        }
        out.flush();
        out.close();
        in.close();
        client.close();
    }

    public static void main(String[] args) {
        new ZLXTomcat(8080).start();
    }



}
