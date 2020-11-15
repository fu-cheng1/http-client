package com.zijin.httpclient;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.util.List;

/**
 * @author 陈尚宇
 * @since 2020/11/14 21:51
 */
public class UploadConfig {

    private int batch = 10;

    private String rootPath;

    private List<Header> headers;

    private String url;

    private HttpEntity httpEntity;


    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpEntity getHttpEntity() {
        return httpEntity;
    }

    public void setHttpEntity(HttpEntity httpEntity) {
        this.httpEntity = httpEntity;
    }

    public int getBatch() {
        return batch;
    }

    public void setBatch(int batch) {
        this.batch = batch;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
}
