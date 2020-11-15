package com.zijin.httpclient.domain;

import java.io.File;
import java.util.Objects;

/**
 * @author 陈尚宇
 * @since 2020/11/14 17:09
 */
public class Payload {

    private Object result;


    private Object data;

    private String id;

    private String localPath;

    private File file;

    public boolean exists() {
        return file != null;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getLocalPath() {
        return localPath;
    }




    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payload payload = (Payload) o;
        return Objects.equals(result, payload.result) &&
                Objects.equals(data, payload.data) &&
                Objects.equals(id, payload.id) &&
                Objects.equals(localPath, payload.localPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, data, id, localPath);
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Payload").append('[')
                .append("result=")
                .append(result)
                .append(",data=")
                .append(data)
                .append(",id=")
                .append(id)
                .append(",localPath=")
                .append(localPath)
                .append(']');
        return sb.toString();
    }
}
