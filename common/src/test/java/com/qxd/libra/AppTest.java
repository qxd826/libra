package com.qxd.libra;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }


    @Test
    public void regexTest() {
        String regex = new String("^[A-Za-z][A-Za-z0-9_]+$");
        Matcher m = Pattern.compile(regex).matcher("2222");
        System.out.println(m);

        m = Pattern.compile(regex).matcher("2x22_");
        System.out.println(m);

        m = Pattern.compile(regex).matcher("_2123");
        System.out.println(m);

        m = Pattern.compile(regex).matcher("x212_12");
        System.out.println(m);

        m = Pattern.compile(regex).matcher("sss_s+");
        System.out.println(m);

        m = Pattern.compile(regex).matcher("!ssasd");
        System.out.println(m);

    }
}
