package com.java.homework.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志工具类：支持控制台和文件双输出
 */
public class LogUtil {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static final String LOG_DIR = System.getProperty("user.dir") + File.separator + "logs";
    private static PrintWriter fileWriter = null;
    private static String currentLogDate = null;
    
    static {
        // 确保日志目录存在
        new File(LOG_DIR).mkdirs();
    }
    
    /**
     * 输出普通日志
     */
    public static void info(String message) {
        log("INFO", message);
    }
    
    /**
     * 输出错误日志
     */
    public static void error(String message) {
        log("ERROR", message);
    }
    
    /**
     * 输出错误日志（包含异常信息）
     */
    public static void error(String message, Throwable e) {
        log("ERROR", message + " Exception: " + e.getMessage());
    }
    
    /**
     * 核心日志输出方法
     */
    private static synchronized void log(String level, String message) {
        String timestamp = DATE_FORMAT.format(new Date());
        String logMessage = "[" + timestamp + "] [" + level + "] " + message;
        
        // 输出到控制台
        System.out.println(logMessage);
        
        // 输出到文件
        try {
            PrintWriter writer = getFileWriter();
            writer.println(logMessage);
            writer.flush(); // 确保日志立即写入文件
        } catch (IOException e) {
            // 如果文件写入失败，至少保证控制台输出正常
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
    
    /**
     * 获取文件写入器（按日期滚动日志文件）
     */
    private static PrintWriter getFileWriter() throws IOException {
        String today = FILE_DATE_FORMAT.format(new Date());
        
        // 如果日期变更或writer未初始化，创建新的writer
        if (fileWriter == null || !today.equals(currentLogDate)) {
            if (fileWriter != null) {
                fileWriter.close(); // 关闭旧的writer
            }
            
            String logFileName = LOG_DIR + File.separator + "app_" + today + ".log";
            fileWriter = new PrintWriter(new FileWriter(logFileName, true)); // 追加模式
            currentLogDate = today;
        }
        
        return fileWriter;
    }
    
    /**
     * 关闭日志资源（程序结束时调用）
     */
    public static synchronized void close() {
        if (fileWriter != null) {
            fileWriter.close();
            fileWriter = null;
        }
    }
}