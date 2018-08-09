package com.qxd.libra.fileUpload;

import lombok.Data;

/**
 * Description:
 *
 * @author: qxd
 * Version: 1.0
 * Create Date Time: 2018/8/3 上午3:15.
 * Update Date Time:
 */
@Data
public class ResourceCreate {
    private Long tenantId;
    private Long userId;
    private Long wsId; //项目空间id
    private String fileName;//资源名称
    private String filePath;//资源路径
    private Long cataId;//目录id
    private Integer resourceType;//资料类别 1:jar 2:py 3:txt
    private Long dutyUserId;//责任人ID
    private String remark;//资源用途描述
}
