package com.zijin.httpclient;

import com.zijin.httpclient.domain.Payload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

/**
 * 管理批量上传的文件，本身不做任何有关上传文件的操作
 * 负责创建上传文件的批次、配置处理返回值调用客户接口
 *
 * @author 陈尚宇
 * @since 2020/11/14 22:26
 */
public class FileUploadBootstrap {

    private static final Logger log = LoggerFactory.getLogger(FileUploadBootstrap.class);

    private final FilePathDatasource filePathDatasource;

    private final UploadConfig uploadConfig;


    public FileUploadBootstrap(FilePathDatasource filePathDatasource, UploadConfig uploadConfig) {
        this.filePathDatasource = filePathDatasource;
        this.uploadConfig = uploadConfig;
    }

    public void start(Consumer<List<Payload>> consumer) throws ExecutionException, InterruptedException {
        int begin = 0;
        List<Payload> filePath;
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        while ((filePath = filePathDatasource.getFilePath(begin)) != null && filePath.size() > 0) {
            int size = filePath.size();
            begin += size;

            log.info("共加载数据 size 为: [{}], 下一批起始位置为: [{}]", size, begin);
            log.info("开始上传这批文件");
            long start = System.currentTimeMillis();
            FileBatchFileUpload fileBatchFileUpload = new FileBatchFileUpload(filePath, uploadConfig);
            List<Payload> payloads = forkJoinPool.submit(fileBatchFileUpload).get();
            long end = System.currentTimeMillis();
            log.info("上传完成共耗时: [{}], 实际上传成功的文件数为: [{}]", (end - start), payloads.size());
            try {
                consumer.accept(payloads);
            } catch (Exception e) {
                log.error("客户端处理出现异常------------", e);
            }
        }
    }

}
