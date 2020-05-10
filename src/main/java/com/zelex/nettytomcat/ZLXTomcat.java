package com.zelex.nettytomcat;

import com.zelex.nettytomcat.http.ZLXRequest;
import com.zelex.nettytomcat.http.ZLXResponse;
import com.zelex.nettytomcat.http.ZLXServlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
@Slf4j
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
        // 配置服务端的 NIO 线程池,用于网络事件处理，实质上他们就是 Reactor 线程组

        // BOSS线程 用于服务端接受客户端连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // Worker线程 用于进行 SocketChannel 网络读写
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // ServerBootstrap 是 Netty 用于启动 NIO 服务端的辅助启动类，用于降低开发难度
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)

                    // 针对主线程的配置 分配线程最大数量 128
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 针对子线程的配置 保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer() {

                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            // HttpResponseEncoder 编码器
                            pipeline.addLast(new HttpResponseEncoder());
                            // HttpRequestDecoder 解码器
                            pipeline.addLast(new HttpRequestDecoder());
                            // 业务逻辑处理
                            pipeline.addLast(new ZLXTomcatServerHandler());
                        }
                    });;
            // bind绑定端口，sync同步等待
            ChannelFuture future = bootstrap.bind(port).sync();
            log.debug("{},服务器开始监听端口，等待客户端连接.........",Thread.currentThread().getName() );
            // 等待端口关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 关闭线程池
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    class ZLXTomcatServerHandler extends ChannelInboundHandlerAdapter {

        /**
         * 收到客户端消息，自动触发
         * @param ctx
         * @param msg
         * @throws Exception
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

            // http请求
            if (msg instanceof HttpRequest) {
                HttpRequest req = (HttpRequest) msg;
                ZLXRequest request = new ZLXRequest(ctx,req);
                ZLXResponse response = new ZLXResponse(ctx,req);

                String url = request.getUrl();

                ZLXServlet servlet = servletMap.get(url);

                if (Objects.nonNull(servlet)) {

                    servlet.service(request,response);

                } else {
                    response.write("404 - Not Found");
                }


            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        /**
         * 发生异常
         * @param ctx
         * @param cause
         * @throws Exception
         */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error(cause.getMessage(),cause);
            ctx.close();
        }
    }

    public static void main(String[] args) {
        new ZLXTomcat(8080).start();
    }



}
