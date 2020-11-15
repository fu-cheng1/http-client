package com.zijin.httpclient;

import com.zijin.httpclient.domain.Payload;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 * @author 陈尚宇
 * @since 2020/11/14 21:36
 */
public class FileBatchFileUpload extends RecursiveTask<List<Payload>> {

    private static final Logger log = LoggerFactory.getLogger(FileUploadBootstrap.class);

    static CloseableHttpClient client = HttpClients.createDefault();

    private final List<Payload> payloads;

    private final UploadConfig uploadConfig;

    private final int batch;

    private final String rootPath;

    public FileBatchFileUpload(List<Payload> payloads, UploadConfig uploadConfig) {
        this.payloads = payloads == null ? new ArrayList<>() : payloads;
        this.uploadConfig = uploadConfig;
        batch = uploadConfig.getBatch();
        rootPath = uploadConfig.getRootPath();
    }


    @Override
    protected List<Payload> compute() {
        List<Payload> pendPayloads = this.payloads;
        int pendSize = pendPayloads.size();
        if (pendSize < batch) {
            List<Payload> fileList = transFromFile(pendPayloads);
            Iterator<Payload> iterator = fileList.iterator();
            int fileSize = fileList.size();
            log.debug("准备的文件数为: [{}], 其中存在的文件数为: [{}], 不存在的文件数为: [{}]", pendSize, fileSize, (pendSize - fileSize));
            while (iterator.hasNext()){
                Payload payload = iterator.next();
                File file = payload.getFile();
                try {
                    if (payload.exists() && file.exists()) {
                        String result = upload(file);
                        payload.setResult(result);

                    } else iterator.remove();

                } catch (Exception e) {
                    log.error("上传文件出现异常", e);
                    log.error("ID为: [{}], 路径为: [{}] 的文件上传失败", payload.getId(), file.getAbsolutePath());
                    iterator.remove();
                }
            }
            return fileList;
        } else {
            int size = payloads.size();
            int mid = size / 2;
            FileBatchFileUpload left = new FileBatchFileUpload(payloads.subList(0, mid), uploadConfig);
            FileBatchFileUpload right = new FileBatchFileUpload(payloads.subList(mid, size), uploadConfig);
            left.fork();
            right.fork();
            List<Payload> leftJoin = left.join();
            List<Payload> rightJoin = right.join();

            List<Payload> list = new ArrayList<>(26);
            list.addAll(leftJoin);
            list.addAll(rightJoin);
            return list;
        }

    }


    private List<Payload> transFromFile(List<Payload> payloadList) {
        List<Payload> fileList = new ArrayList<>();
        for (Payload payload : payloadList) {
            String localPath = payload.getLocalPath();
            if (rootPath != null && !rootPath.trim().equals("")) {
                localPath = rootPath + File.separator + localPath;
            }
            File file = new File(localPath);
            if (file.exists() && file.isFile()) {
                Payload result = new Payload();
                result.setData(payload.getData());
                result.setResult(payload.getResult());
                result.setId(payload.getId());
                result.setLocalPath(payload.getLocalPath());
                result.setFile(file);
                fileList.add(result);
            } else {
                log.debug("路径为: [{}] 的本地文件不存在, ID为:[{}]", localPath, payload.getId());
            }
        }
        log.debug("实际返回的要上传的文件数为: [{}]", fileList.size());
        return fileList;
    }

    public String upload(File file) throws Exception {
        HttpPost httpPost = new HttpPost(uploadConfig.getUrl());

        FileBody fileBody = new FileBody(file);
        StringBody stringBody = new StringBody("false", ContentType.TEXT_PLAIN);
        HttpEntity httpEntity = MultipartEntityBuilder.create().addPart("file", fileBody).addPart("unZip", stringBody).build();

        List<Header> headers = uploadConfig.getHeaders();
        httpPost.setHeaders(headers.toArray(new Header[0]));

        httpPost.setEntity(httpEntity);
        CloseableHttpResponse httpResponse = client.execute(httpPost);
        HttpEntity entity = httpResponse.getEntity();

        InputStream content = entity.getContent();

        byte[] b = new byte[1024];
        int len;
        StringBuilder sb = new StringBuilder();

        while ((len = content.read(b)) != -1) {

            sb.append(new String(b, 0, len));
        }

        return sb.toString();

    }
}
