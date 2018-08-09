package com.qxd.libra.fileUpload;

import lombok.Data;

/**
 * Description:
 *
 * @author: qxd
 * Version: 1.0
 * Create Date Time: 2018/8/10 上午12:50.
 * Update Date Time:
 */
@Data
public class CataCreate {
    private Long tenantId;
    private Long userId;
    private Long wsId;  //项目空间ID
    private String cataName; //目录名
    private Long parentCataId; //父目录ID
    private Integer type;  //目录类型 (1:任务2:脚本3:资源4:函数5:流任务)
}
