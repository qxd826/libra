package com.qxd.libra;

import com.qxd.core.utils.StringUtil;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {
        System.out.println(args[0]);
        System.out.println(StringUtil.isChinese(args[0]));
    }
}
