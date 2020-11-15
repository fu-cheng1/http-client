package com.zijin.httpclient;

import com.zijin.httpclient.domain.Payload;

import java.util.List;

/**
 * @author 陈尚宇
 * @since 2020/11/14 18:08
 */
public interface FilePathDatasource {


    /**
     * 每次调用一次该方法应该返回不同的数据
     * 直到返回为null才意味着处理完毕
     *
     * @param begin 从第多少条开始
     * @return 数据源
     */
    List<Payload> getFilePath(int begin);


}
