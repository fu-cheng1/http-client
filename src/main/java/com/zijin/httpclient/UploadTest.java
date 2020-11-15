package com.zijin.httpclient;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.zijin.httpclient.domain.Payload;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * @author 陈尚宇
 * @since 2020/11/14 14:27
 */
public class UploadTest extends RecursiveTask<List<Payload>> implements Runnable {
    static CloseableHttpClient client = HttpClients.createDefault();

//    public static void main(String[] args) throws Exception {
//        String rootDir = "/data/contentftp/" +
//                "";
//        InputStream in = UploadTest.class.getClassLoader().getResourceAsStream("filelist");
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
//        List<File> fileList = new ArrayList<>();
//        String path;
//        while ((path = bufferedReader.readLine()) != null) {
//            File file = new File(rootDir + path);
//            if (file.exists() && file.isFile()) {
//                fileList.add(file);
//            }
//        }
//        long start = System.currentTimeMillis();
//        System.out.println("file size is :" + fileList.size());
//        System.in.read();
//        for (File file : fileList) {
//            upload(file);
//        }
//        System.out.println("cost :" + (System.currentTimeMillis() - start));
//    }

    final List<File> fileList;
    List<Payload> payloadList;

    public UploadTest(List<File> fileList) {

        this.fileList = fileList;

    }

    public UploadTest(List<File> fileList, List<Payload> payloadList) {
        this.fileList = fileList;
        this.payloadList = payloadList;
    }

    public static void main(String[] args) throws Exception {


        String rootDir = "/data/contentftp/";
        InputStream in = UploadTest.class.getClassLoader().getResourceAsStream("filelist");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        List<File> fileList = new ArrayList<>();
        String path;
        while ((path = bufferedReader.readLine()) != null) {
            File file = new File(rootDir + path);
            if (file.exists() && file.isFile()) {
                fileList.add(file);
            }
        }
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        long start = System.currentTimeMillis();
        System.out.println("file size is :" + fileList.size());
        System.in.read();
        ForkJoinTask<List<Payload>> submit = forkJoinPool.submit((ForkJoinTask<List<Payload>>) new UploadTest(fileList));
        List<Payload> payloadList = submit.get();
        System.out.println("cost :" + (System.currentTimeMillis() - start));
        System.out.println(payloadList.size());
        FileOutputStream os = new FileOutputStream("/root/aaa.txt");
        os.write(new ObjectMapper().writeValueAsString(payloadList).getBytes());
        os.flush();

    }


    public static String upload(File file) throws Exception {
//        HttpPost httpPost = new HttpPost("https://open.teewon.net:9009/twasp/fs/v2/upload/");
//        httpPost.addHeader("Access-Token", "812e48d06f6f398bd6afdacff0b68a09");
//        httpPost.addHeader("branchCode", "TWSMECR");
//
//        FileBody fileBody = new FileBody(file);
//        StringBody stringBody = new StringBody("false", ContentType.TEXT_PLAIN);
//        HttpEntity httpEntity = MultipartEntityBuilder.create().addPart("file", fileBody).addPart("unZip", stringBody).build();
//        httpPost.setEntity(httpEntity);
//
//        CloseableHttpResponse httpResponse = client.execute(httpPost);
//        HttpEntity entity = httpResponse.getEntity();
//        InputStream content = entity.getContent();
//
//        byte[] b = new byte[1024 * 8];
//
//        int len;
//        StringBuilder sb = new StringBuilder();
//        while ((len = content.read(b)) != -1) {
//            sb.append(new String(b, 0, len));
//
//        }
//        return sb.toString();
        return null;
    }


    @Override
    protected List<Payload> compute() {
        if (fileList.size() < 5) {
            List<Payload> list = new ArrayList<>();
            for (File file : fileList) {
                try {
                    String upload = upload(file);
                    Payload payload = new Payload();
                    payload.setData(upload);
                    list.add(payload);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return list;
        } else {
            int size = fileList.size();
            int mid = size / 2;
            UploadTest left = new UploadTest(fileList.subList(0, mid));
            UploadTest right = new UploadTest(fileList.subList(mid, size));
            left.fork();
            right.fork();
            List<Payload> join = left.join();
            List<Payload> join1 = right.join();
            ArrayList<Payload> payloads = new ArrayList<>();
            payloads.addAll(join);
            payloads.addAll(join1);
            return payloads;
        }
    }

    @Override
    public void run() {
        for (File file : fileList) {
            String upload = null;
            try {
                upload = upload(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Payload payload = new Payload();
            payload.setResult(upload);
            payloadList.add(payload);
        }
    }
}
