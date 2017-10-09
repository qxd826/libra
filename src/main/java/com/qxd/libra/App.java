package com.qxd.libra;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {
        //System.out.println(args[0]);
        //System.out.println(StringUtil.isChinese(args[0]));

        //String str = "测试\"\"替换";
        String str = "'\"测试替换\"'";

        String ss = str.replaceAll("\"","'");
        System.out.println(ss);


        String serverList = "asdads,sssssss";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("haFlag", true);
        jsonObject.put("address", "ssss");
        jsonObject.put("nameService", "ddddd");
        jsonObject.put("serverList", null != serverList ? serverList.split(",") : null);

        FileSystemInfo body = JSONObject.parseObject(jsonObject.toJSONString(), FileSystemInfo.class);

        System.out.println(JSONObject.toJSONString(body));
    }
}
