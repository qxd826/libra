package com.qxd.libra;

import lombok.Data;

import java.util.List;

/**
 * Created by qxd on 2017/8/31.
 */
@Data
public class FileSystemInfo {

    //ha 标志位  true： ha模式 false：单机模式
    private Boolean haFlag;

    //hdfs  hdfs://host:port 地址 单机模式下
    private String address;

    //服务名称  ha模式下
    private String nameService;

    //node 服务信息 ha模式下
    private List<String> serverList;
}
