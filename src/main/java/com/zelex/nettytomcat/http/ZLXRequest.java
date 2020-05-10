package com.zelex.nettytomcat.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;

import java.io.IOException;
import java.io.InputStream;

public class ZLXRequest {
    
    private String method;
    private String url;


        /**
         * Netty Channel封装
         */
        private ChannelHandlerContext ctx;

        /**
         * http请求
         */
        private HttpRequest req;

    public ZLXRequest(ChannelHandlerContext ctx, HttpRequest req) {
            this.ctx = ctx;
            this.req = req;
        }


        public String getUrl() {

            return req.uri();
        }

        public HttpMethod getMethod() {

            return req.method() ;
        }

    }
