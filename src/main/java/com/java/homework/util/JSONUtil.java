package com.java.homework.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;

/**
 * JSON工具类：对象与JSON的序列化/反序列化
 */
public class JSONUtil {
    private static final Gson GSON = new Gson();
    // JSON文件根路径（resources/data/）
    private static final String BASE_PATH;
    
    static {
        // 初始化BASE_PATH，处理资源可能不存在的情况
        URL resourceUrl = JSONUtil.class.getClassLoader().getResource("data");
        if (resourceUrl != null) {
            BASE_PATH = resourceUrl.getPath();
        } else {
            // 如果资源不存在，使用当前工作目录下的data目录
            BASE_PATH = System.getProperty("user.dir") + File.separator + "data";
            // 确保目录存在
            new File(BASE_PATH).mkdirs();
        }
    }

    /**
     * 从JSON文件读取数据，转为List<T>
     */
    public static <T> List<T> readFromJson(String fileName, Type type) {
        File file = new File(BASE_PATH + "/" + fileName);
        if (!file.exists()) {
            return List.of(); // 文件不存在时返回空列表
        }
        try (Reader reader = new FileReader(file)) {
            return GSON.fromJson(reader, type);
        } catch (IOException e) {
            throw new RuntimeException("读取JSON文件失败：" + fileName, e);
        }
    }

    /**
     * 将List<T>写入JSON文件
     */
    public static <T> void writeToJson(String fileName, List<T> data) {
        File file = new File(BASE_PATH + "/" + fileName);
        // 确保父目录存在
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try (Writer writer = new FileWriter(file)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            throw new RuntimeException("写入JSON文件失败：" + fileName, e);
        }
    }
}