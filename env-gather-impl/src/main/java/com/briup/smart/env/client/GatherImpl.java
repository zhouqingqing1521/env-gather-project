package com.briup.smart.env.client;

import com.briup.smart.env.Configuration;
import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.support.ConfigurationAware;
import com.briup.smart.env.support.PropertiesAware;
import com.briup.smart.env.util.Backup;
import com.briup.smart.env.util.BackupImpl;
import com.briup.smart.env.util.Log;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class GatherImpl implements Gather, ConfigurationAware, PropertiesAware {

    String filePath,backFilePath;
    Backup backup;
    Log log;
    // 分析过程：
    /*
     * 读文件，一行一行的读
     * 每行数据如下：
     * 100|101|2|16|1|3|5d606f7802|1|1516323596029
     *  0   1  2  3 4 5      6     7        8
     *
     * 封装成Environment
     *
     * 数据存到集合中，并返回
     *
     */
    @Override
    public Collection<Environment> gather() throws Exception {
        File file = new File(filePath);
        //        1、读取文件
        Reader is = new FileReader(file);
        List<Environment> lists = new ArrayList<>();
        BufferedReader br = new BufferedReader(is);
        //获取到记录的长度
        Long len = (Long) backup.load(backFilePath
                , Backup.LOAD_UNREMOVE);
        //跳过对应长度
        if(len!=null){
            br.skip(len);
        }
        String str;
        while ((str = br.readLine()) != null) {
//            1、分割字符串
            String[] arr = str.split("\\|");
//            2、判断长度是否符合 不符合跳出本次循环
            if (arr.length != 9)
                continue;
            Environment env = new Environment();
            //设置发送端id
            env.setSrcId(arr[0]);
            //设置树莓派系统id
            env.setDesId(arr[1]);
            //设置实验箱区域模块id
            env.setDevId(arr[2]);
            //设置传感器地址
            env.setSersorAddress(arr[3]);
            //设置传感器个数
            env.setCount(Integer.valueOf(arr[4]));
            //设置指令标号
            env.setCmd(arr[5]);
            //设置状态标识
            env.setStatus(Integer.valueOf(arr[7]));
            //判断传感器地址 根据不同的值set不同的Name
            //设置时间戳
            env.setGather_date(new Timestamp(Long.parseLong(arr[8])));
            switch (arr[3]) {
                case "16":
                    //处理温度
                    env.setName("温度");
                    //得到温度的十进制数据
                    int warm = this.parseTo10(arr[6], 0, 4);
                    //根据公式计算出真正的温度
                    float warmF = (warm * (0.00268127F)) - 46.85F;
                    env.setData(warmF);
                    lists.add(env);
//                    处理湿度
                    Environment copyEnv = this.copyEnv(env);
                    copyEnv.setName("湿度");
                    //得到湿度的十进制数据
                    int wet = this.parseTo10(arr[6], 4, 8);
                    //根据公式计算真正的湿度
                    float wetF = (wet * 0.00190735F) - 6;
                    copyEnv.setData(wetF);
                    lists.add(copyEnv);
                    break;
                case "256":
                    env.setName("光照强度");
                    //计算光照强度
                    int light = this.parseTo10(arr[6], 0, 4);
                    env.setData(light);
                    lists.add(env);
                    break;
                case "1280":
                    env.setName("二氧化碳");
                    //计算二氧化碳
                    int co2 = this.parseTo10(arr[6], 0, 4);
                    env.setData(co2);
                    lists.add(env);
                    break;
                default:
                    break;
            }

            //加入到集合当中

        }
        //存入文件长度
        backup.store(backFilePath,
                file.length(),Backup.STORE_OVERRIDE);
        return lists;
    }
    //三个参数:str 传进来字符串（需要处理的数据）
//              start 截取开始的下标，end截取结束的下标
    private int parseTo10(String str,int start,int end){
        String sub = str.substring(start, end);
        return Integer.parseInt(sub,16);
    }

    //复制对象
    private Environment copyEnv(Environment env){
        Environment copyEnv = new Environment();
        copyEnv.setSrcId(env.getSrcId());
        copyEnv.setDevId(env.getDevId());
        copyEnv.setDesId(env.getDesId());
        copyEnv.setSersorAddress(env.getSersorAddress());
        copyEnv.setCmd(env.getCmd());
        copyEnv.setCount(env.getCount());
        copyEnv.setGather_date(env.getGather_date());
        return copyEnv;
    }

    @Override
    public void setConfiguration(Configuration configuration) throws Exception {
        this.backup = configuration.getBackup();
        this.log  = configuration.getLogger();
    }
    @Override
    public void init(Properties properties) throws Exception {
        this.filePath = properties.getProperty("data-file-path");
        this.backFilePath = properties.getProperty("file-path");
    }
}
