package com.briup.smart.env;

import com.briup.smart.env.client.Client;
import com.briup.smart.env.client.Gather;
import com.briup.smart.env.server.DBStore;
import com.briup.smart.env.server.Server;
import com.briup.smart.env.support.ConfigurationAware;
import com.briup.smart.env.support.PropertiesAware;
import com.briup.smart.env.util.Backup;
import com.briup.smart.env.util.Log;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ConfigurationImpl implements Configuration {
    private static Map<String,Object> map = new HashMap<>();
    private static Properties properties = new Properties();
    private static Configuration config = new ConfigurationImpl();
    public static Configuration getInstance(){
        return config;
    }
    private ConfigurationImpl(){
        //私有构造 防止直接创建对象
    }
    static {
        try {
            parseXml();
            initModule();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void parseXml(){
//        1、解析xml文件
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read("env-gather-impl/src/main/resources/conf.xml");
            Element rootElement = document.getRootElement();
            List<Element> element = rootElement.elements();
            for (Element ele : element) {
                //获取class属性的值
                Attribute attr = ele.attribute("class");
                //根据值生成对象
                Object instance = Class.forName(attr.getValue()).newInstance();
                //放入集合
                map.put(ele.getName(),instance);
                //遍历子节点
                List<Element> childs = ele.elements();
                for (Element child : childs) {
                    //获取标签名
                    String name = child.getName();
                    //获取标签文本值
                    String value = child.getText();
                    properties.setProperty(name,value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void initModule() throws Exception {
        //1、循环遍历map
        for (Object value : map.values()) {
            if(value instanceof ConfigurationAware){
                ((ConfigurationAware) value).setConfiguration(config);
            }
            if(value instanceof PropertiesAware){
                ((PropertiesAware) value).init(properties);
            }
        }
    }
    
    @Override
    public Log getLogger() throws Exception {
        return (Log) map.get("logger");
    }

    @Override
    public Server getServer() throws Exception {
        return (Server) map.get("server");
    }

    @Override
    public Client getClient() throws Exception {
        return (Client) map.get("client");
    }

    @Override
    public DBStore getDbStore() throws Exception {
        return (DBStore) map.get("dbStore");
    }

    @Override
    public Gather getGather() throws Exception {
        return (Gather) map.get("gather");
    }

    @Override
    public Backup getBackup() throws Exception {
        return (Backup) map.get("backup");
    }
}
