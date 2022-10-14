package com.briup.smart.env.util;

import com.briup.smart.env.Configuration;
import com.briup.smart.env.ConfigurationImpl;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.Properties;

public class LogTest {
    @Test
    public void logTest(){
        Logger logger = Logger.getRootLogger();
        logger.fatal("打印了fatal日志");
        logger.error("打印了error日志");
        logger.warn("打印了warn日志");
        logger.info("打印了info日志");
        logger.debug("打印了debug日志");
        logger.trace("打印了trace日志");
    }

    @Test
    public void configTest(){
//        Configuration configuration = new ConfigurationImpl();
//        System.out.println(configuration);
    }
}
