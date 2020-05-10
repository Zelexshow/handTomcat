package com.zelex.nettytomcat.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class ZLXResponse {
    /**
     * Netty Channel封装
     */
    private ChannelHandlerContext ctx;

    /**
     * http请求
     */
    private HttpRequest req;

    public ZLXResponse(ChannelHandlerContext ctx, HttpRequest req) {
        this.ctx = ctx;
        this.req = req;
    }

    public void write(String out) {
        try {
            if (StringUtils.isBlank(out)) {
                return;
            }
            FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK, Unpooled.wrappedBuffer(out.getBytes("UTF-8")));
            httpResponse.headers().set("Content-Type", "text/html;");

            ctx.writeAndFlush (httpResponse);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            ctx.close();
        }
    }
}
