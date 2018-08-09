package com.qxd.libra.fileUpload;

import com.alibaba.fastjson.JSONObject;
import com.dtwave.common.dubbo.DubboResult;
import com.dtwave.common.exception.BizException;
import com.dtwave.common.http.Request;
import com.dtwave.common.http.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.channels.FileChannel;
import java.util.Properties;
import java.util.UUID;

/**
 * Description:
 *
 * @author: qxd
 * Version: 1.0
 * Create Date Time: 2018/8/3 上午1:23.
 * Update Date Time:
 */
@Slf4j
public class FileUpload {

    private static final String userDir = "user.dir";
    private static final String propertiesName = "application.properties";

    private static final String CATA_CREATE = "/cata/create";
    private static final String RESOURCE_CREATE = "/resource/create";

    private static String fsUrl = "";
    private static String namenodes = "";
    private static String serverAddress = "";
    private static String nodeAddress = "";
    private static String filePath = "";
    private static String hdfsPath = "";
    private static Long tenantId = 0L;
    private static Long userId = 0L;
    private static Long wsId = 0L;
    private static Long cataId = 0L;
    private static JSONObject fsServer = null;
    private static Boolean register = null;

    public static void main(String[] args) throws Exception {
        String propertiesPath = "";
        if (args != null && args.length > 0) {
            propertiesPath = args[0];
        } else {
            //propertiesPath = "/Users/qxd/Documents/my-project/libra/common/src/main/resources/application.properties";
            propertiesPath = "./" + propertiesName;
        }

        log.info("读入配置文件地址:{}", propertiesPath);

        //获取配置信息
        Properties properties = new Properties();
        //使用InPutStream流读取properties文件
        BufferedReader bufferedReader = new BufferedReader(new FileReader(propertiesPath));
        properties.load(bufferedReader);

        //获取key对应的value值
        log.info("打印读入参数 >>>>>>>>>>> 开始");
        serverAddress = properties.getProperty("dubhe.server.address");
        log.info("dubhe-server服务地址: serverAddress:{}", serverAddress);
        nodeAddress = properties.getProperty("dubhe.node.address");
        log.info("dubhe-node服务地址:nodeAddress:{}", nodeAddress);
       /* hdfsPath = properties.getProperty("dubhe.hdfs.path");
        if (!hdfsPath.endsWith("/")) {
            hdfsPath = hdfsPath + "/";
        }
        log.info("hdfsPath:" + hdfsPath);*/
        filePath = properties.getProperty("dubhe.file.path");
        log.info("filePath:{}", filePath);
        userId = Long.valueOf(properties.getProperty("dubhe.userId"));
        log.info("userId:{}", userId);
        tenantId = Long.valueOf(properties.getProperty("dubhe.tenantId"));
        log.info("tenantId:{}", tenantId);
        wsId = Long.valueOf(properties.getProperty("dubhe.wsId"));
        log.info("wsId:{}", wsId);
        cataId = Long.valueOf(properties.getProperty("dubhe.cataId"));
        log.info("cataId:{}", cataId);
        fsUrl = properties.getProperty("dubhe.fs.url");
        log.info("fsUrl:{}", fsUrl);
        namenodes = properties.getProperty("dubhe.nameNodes");
        log.info("nameNodes:{}", namenodes);
        register = Boolean.valueOf(properties.getProperty("dubhe.register"));
        log.info("register:{}", register);

        fsServer = new JSONObject();
        fsServer.put("fsUrl", fsUrl);
        if (namenodes == null || namenodes.trim().length() < 1) {
            fsServer.put("nameNodes", new String[]{});
        } else {
            fsServer.put("nameNodes", namenodes.split(","));
        }
        log.info("fsServer:{}", fsServer);
        log.info("打印读入参数 <<<<<<<<<<< 结束");

        //读取目录下文件列表并上传
        recursiveFiles(cataId, filePath);
    }

    /**
     * 遍历文件/文件夹 - 函数
     * [String]path        文件路径
     */
    private static void recursiveFiles(Long resourceCataId, String path) {
        //创建File对象
        File file = new File(path);
        if (null == file) {
            log.info("当前目录或文件信息不存在:{},结束上传", path);
            return;
        }
        if (file.isFile()) {
            String destFilePath = getFileNewPath(file.getAbsolutePath());
            long startTime = System.currentTimeMillis();
            log.info("开始上传资源文件  >>>>>>>> {} >>>>>> 上传地址:{}", file.getAbsolutePath(), destFilePath);
            //上传资源
            Boolean upResult = updateFile(file.getAbsolutePath(), destFilePath);
            long endTime = System.currentTimeMillis();
            log.info("结束上传资源文件  <<<<<<<< {} <<<<<< 耗时:{}s", file.getAbsolutePath(), (endTime - startTime) / 1000);
            //获取资源名称
            String fileName = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1, file.getAbsolutePath().length());
            //在系统中注册资源
            if (register && upResult) {
                registerFile(resourceCataId, fileName, destFilePath);
            }
            return;
        } else if (file.isDirectory()) {
            //或取文件/文件夹
            File files[] = file.listFiles();
            //对象为空直接返回
            if (files == null || files.length == 0) {
                log.info("当前目录下不存在资源信息:{},结束上传", path);
                return;
            }
            Long tempCataId = registerCata(resourceCataId, file.getName());
            //存在文件遍历判断
            for (File f : files) {
                //判断是否为文件夹
                if (f.isDirectory()) {
                    Long tempCataId1 = registerCata(tempCataId, f.getName());
                    log.info("开始上传文件夹[{}]下的文件信息", f.getAbsolutePath());
                    //为文件夹继续遍历
                    recursiveFiles(tempCataId1, f.getAbsolutePath());
                    //判断是否为文件
                } else if (f.isFile()) {
                    String destFilePath = getFileNewPath(f.getAbsolutePath());
                    long startTime = System.currentTimeMillis();
                    log.info("开始上传资源文件 >>>>>>>> {} >>>>>> 上传地址:{}", f.getAbsolutePath(), destFilePath);
                    //上传资源
                    Boolean upResult = updateFile(f.getAbsolutePath(), destFilePath);
                    long endTime = System.currentTimeMillis();
                    log.info("结束上传资源文件 <<<<<<<< {} >>>>>>> 耗时:{}s", f.getAbsolutePath(), (endTime - startTime) / 1000);
                    //获取资源名称
                    String fileName = f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf("/") + 1, f.getAbsolutePath().length());
                    //在系统中注册资源
                    if (register && upResult) {
                        registerFile(tempCataId, fileName, destFilePath);
                    }
                } else {
                    log.info("发现未知错误文件:{},跳过上传", f.getAbsolutePath());
                }
            }
        } else {
            log.info("配置目录文件信息有误:{},结束上传", path);
            return;
        }
    }

    /**
     * 上传文件
     *
     * @param filePath     本机文件存储路径
     * @param destFilePath hdfs的文件存储路径
     */
    public static Boolean updateFile(String filePath, String destFilePath) {
        FileChannel fc = null;
        CloseableHttpClient httpclient = null;
        try {
            File tempFile = new File(filePath);
            httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(nodeAddress);
            httpPost.setHeader("Content-Type", "multipart/form-data;boundary=fengefunikandedongma");
            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .setBoundary("fengefunikandedongma")
                    .addPart(FormBodyPartBuilder.create("src", new FileBody(tempFile))
                            .build())
                    .addPart(FormBodyPartBuilder.create("dest", new StringBody(destFilePath, ContentType.TEXT_PLAIN))
                            .build())
                    .addPart(FormBodyPartBuilder.create("srcServer", new StringBody(fsServer.toJSONString(), ContentType.TEXT_PLAIN))
                            .build())
                    .addPart(FormBodyPartBuilder.create("overwrite", new StringBody("false", ContentType.TEXT_PLAIN))
                            .build())
                    .build();

            httpPost.setEntity(reqEntity);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            String content = EntityUtils.toString(httpEntity);
            DubboResult<JSONObject> result = JSONObject.parseObject(content, DubboResult.class);
            if (!result.isSuccess()) {
                log.info("调用node创建资源失败:filePath{},result:{}", filePath, JSONObject.toJSONString(result));
            } else {
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            log.info("调用node创建资源失败:filePath{},result:{}", filePath, e.getMessage());
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (Exception e) {
                    log.info("调用node创建资源失败:filePath:{},result:{}", filePath, e.getMessage());
                }
            }
            if (fc != null) {
                try {
                    fc.close();
                } catch (Exception e) {
                    log.info("调用node创建资源失败:filePath{},result:{}", filePath, e.getMessage());
                }
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 获取文件新路径
     *
     * @param filePath 文件路径
     *
     * @return
     */
    public static String getFileNewPath(String filePath) {
        String fileType = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
        return "/dubhe/resources/" + fileType + "/" + UUID.randomUUID() + "." + fileType;
    }

    /**
     * 注册资源
     *
     * @param destFilePath
     */
    public static void registerFile(Long resourceCataId, String fileName, String destFilePath) {
        ResourceCreate resourceCreate = new ResourceCreate();
        resourceCreate.setTenantId(tenantId);
        resourceCreate.setUserId(userId);
        resourceCreate.setCataId(resourceCataId);
        resourceCreate.setDutyUserId(userId);
        resourceCreate.setFileName(fileName);
        resourceCreate.setFilePath(destFilePath);
        resourceCreate.setRemark("资源");
        resourceCreate.setWsId(wsId);
        Integer resourceType = 0;
        if (fileName.endsWith(".py")) {
            resourceType = 2;
        } else if (fileName.endsWith(".jar")) {
            resourceType = 1;
        } else if (fileName.endsWith(".txt")) {
            resourceType = 3;
        } else if (fileName.endsWith(".parm")) {
            resourceType = 5;
        } else if (fileName.endsWith(".js")) {
            resourceType = 4;
        }
        resourceCreate.setResourceType(resourceType);

        log.info("调用server创建资源  >>>>>>>>>>>>> {}", JSONObject.toJSONString(resourceCreate));
        Response<JSONObject> response = new Request(serverAddress + RESOURCE_CREATE).body(JSONObject.toJSONString(resourceCreate))
                .POST();
        log.info("调用server创建资源返回  <<<<<<<<<<<<<< {}", response);
        if (response.getContent() != null) {
            if (response.getContent().getString("success").equals("false")) {
                throw new BizException(response.getContent().getString("message") + ",请检查计算引擎,数据库名等相关配置信息");
            } else {
                DubboResult<Long> dubboResult = JSONObject.parseObject(response.getContent().toJSONString(), DubboResult.class);
                log.info("创建资源" + fileName + "资源id:" + dubboResult.getContent());
            }
        } else {
            throw new BizException("调用server创建资源" + fileName + "失败");
        }
    }

    /**
     * 注册目录
     *
     * @param cataName
     */
    public static Long registerCata(Long parentCataId, String cataName) {
        CataCreate cataCreate = new CataCreate();
        cataCreate.setUserId(userId);
        cataCreate.setCataName(cataName);
        cataCreate.setTenantId(tenantId);
        cataCreate.setWsId(wsId);
        cataCreate.setParentCataId(parentCataId);
        cataCreate.setType(3);

        log.info("调用server创建目录  >>>>>>>>>>>>> {}", JSONObject.toJSONString(cataCreate));
        Response<JSONObject> response = new Request(serverAddress + CATA_CREATE).body(JSONObject.toJSONString(cataCreate))
                .POST();
        log.info("调用server创建目录返回  <<<<<<<<<<<<<< {}", response);
        if (response.getContent() != null) {
            if (response.getContent().getString("success").equals("false")) {
                throw new BizException(response.getContent().getString("message") + ",请检查计算引擎,数据库名等相关配置信息");
            } else {
                DubboResult<Long> dubboResult = JSONObject.parseObject(response.getContent().toJSONString(), DubboResult.class);
                log.info("创建目录" + cataName + "目录id:" + dubboResult.getContent());
                return dubboResult.getContent();
            }
        } else {
            throw new BizException("调用server创建目录" + cataName + "失败");
        }
    }
}
