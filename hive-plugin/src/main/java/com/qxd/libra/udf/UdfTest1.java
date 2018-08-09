package com.qxd.libra.udf;

import com.google.common.math.IntMath;
import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * Created by qxd on 2017/10/9.
 */
public class UdfTest1 extends UDF {
    public String evaluate(int a, int b) {
        return "result: " + IntMath.checkedAdd(a, b);
    }
}
