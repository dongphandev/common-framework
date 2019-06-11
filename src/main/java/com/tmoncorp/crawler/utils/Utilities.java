package com.tmoncorp.crawler.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.gson.*;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

import org.apache.commons.collections4.ListUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.json.*;
import javax.json.JsonObject;
import java.io.*;
import java.lang.reflect.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.Date;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Created by danhnguyen on 4/11/18.
 */
public class Utilities {

    private static Logger LOG = LoggerFactory.getLogger(Utilities.class);
    private static final Map<Class<?>, Field[]> declaredFieldsCache = new ConcurrentHashMap<>(256);
    private static final Field[] NO_FIELDS = new Field[0];

    private static ObjectMapper MAPPER = new ObjectMapper();

    public static String encryptMD5(String rawData) {
        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(rawData.getBytes());

            byte[] msgStr = mDigest.digest() ;
            for (int i = 0; i < msgStr.length; i++){
                String tmpEncTxt = Integer.toHexString((int)msgStr[i] & 0x00ff) ;
                sb.append(tmpEncTxt) ;
            }
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Failed to encode MD5");
            return "";
        }
        return sb.toString();
    }

    /**
     * Convert date time to String with format.
     * @param date
     * @param format
     * @return String date
     */
    public static String dateToString(Date date, String format) {
        if (format == null)
            format = "yyyy-MM-dd HH:mm:ss";
        return new SimpleDateFormat(format).format(date);
    }

    public static Date stringToDate(String date, String format) {
        if (format == null)
            format = "yyyy-MM-dd HH:mm:ss";
        try {
            return new SimpleDateFormat(format).parse(date);
        } catch (ParseException e) {
            LOG.error("Cannot parse date.", e);
        }
        return null;
    }

    public static Date stringToDate(Long dateNumber, String format) {
        if (format == null)
            format = "yyyy-MM-dd HH:mm:ss";
        Date date = new Date(dateNumber * 1000);
        String strDate = new SimpleDateFormat(format).format(date);
        return stringToDate(strDate, format);
    }

    public static LocalDate asLocalDate(java.util.Date date, ZoneId zone) {
        if (date == null)
            return null;
        if (date instanceof java.sql.Date)
            return ((java.sql.Date) date).toLocalDate();
        else
            return Instant.ofEpochMilli(date.getTime()).atZone(zone).toLocalDate();
    }

    public static LocalDateTime asLocalDateTime(java.util.Date date, ZoneId zone) {
        if (date == null)
            return null;
        return Instant.ofEpochMilli(date.getTime()).atZone(zone).toLocalDateTime();
    }

  

    final static class FieldNamingResolver implements FieldNamingStrategy
    {
        public String translateName(Field field)
        {
            return FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES.translateName(field);
        }
    }

    public static List<String> getFields(Class<?> clazz, List<String> excludeFields){
        List<String> results = new ArrayList<>();

        Field[] fields = clazz.getDeclaredFields();
        for(Field field:fields){
            if(!excludeFields.contains(field.getName())){
                if (!field.getName().equalsIgnoreCase("chkSlit"))
                    results.add(lowCaseToDash(field.getName()));
                else
                    results.add(field.getName());
            }
        }
        return results;
    }

    public static List<String> getFieldsWithCaseToDash(Class<?> clazz, List<String> excludeFields){
        List<String> results = new ArrayList<>();

        Field[] fields = clazz.getDeclaredFields();
        for(Field field:fields){
            if(!excludeFields.contains(field.getName())){
                if (!field.getName().equalsIgnoreCase("chkSlit"))
                    results.add(field.getName());
                else
                    results.add(field.getName());
            }
        }
        return results;
    }

    public static List<String> fieldToParams(List<String> params){
        List<String> results = new ArrayList<>();
        for (Object obj : params) {
            results.add("?");
        }
        return results;
    }

    public static List<String> fieldToParamsUpdate(List<String> params){
        List<String> results = new ArrayList<>();
        for (Object obj : params) {
            results.add(obj + " = ?");
        }
        return results;
    }

    public static List<String> fieldToParamsDuplicate(Class<?> clazz, List<String> excludeFields){

        List<String> results = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!excludeFields.contains(field.getName())) {
                String name = field.getName();
                if (name.equalsIgnoreCase("chkSlit"))
                    results.add(String.format("%s=VALUES(%s)", name, name));
                else {
                    String col = lowCaseToDash(name);
                    results.add(String.format("%s=VALUES(%s)", col, col));
                }
            }
        }
        return results;
    }

    public static Object getFieldValue(Object obj, String fieldName) {
        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            for(Field field:fields){
                if (StringUtils.equalsIgnoreCase(fieldName, field.getName())) {
                    field.setAccessible(true);
                    return field.get(obj);
                }
            }
        } catch (Exception ex) {
            LOG.error("Cannot get value from object <{}>", obj);
        }
        return null;
    }

    public static List<Object> getFieldValueByListFileNames(Object obj, List<String> fieldNames) {
        List<Object> results = new ArrayList<>();

        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            for(Field field:fields){
                if (fieldNames.contains(field.getName())) {
                    field.setAccessible(true);
                    results.add(field.get(obj));
                }
            }
        } catch (Exception ex) {
            LOG.error("Cannot get list value by field list from object <{}>", obj);
        }
        return results;
    }

    public static String lowCaseToDash(String src) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < src.length(); i++) {
            char character = src.charAt(i);
            if(Character.isUpperCase(character)) {
                result.append("_").append(Character.toLowerCase(character));
            }else {
                result.append(character);
            }
        }
        return result.toString();
    }

    public static Object transferObject(Object src, Object desc) {

        try {
            Field[] fields = src.getClass().getDeclaredFields();
            for(Field f1:fields){
                Field f2 = getDeclaredField(desc, f1.getName());
                if (f2 != null) {
                    f1.setAccessible(true);
                    f2.setAccessible(true);
                    f2.set(desc, f1.get(src));
                }
            }
        } catch (Exception ex) {
            LOG.error("Cannot transfer value from object <{}> to <{}>", src, desc);
        }
        return desc;
    }

    public static Object transferObject(Object src, Object desc, List<String> excludeFields) {

        try {
            Field[] fieldObjectSrc = src.getClass().getDeclaredFields();
            for(Field fSrc : fieldObjectSrc){
                if (!excludeFields.contains(fSrc.getName())) {
                    Field fDesc = getDeclaredField(desc, fSrc.getName());
                    if (fDesc != null) {
                        fSrc.setAccessible(true);
                        fDesc.setAccessible(true);
                        fDesc.set(desc, fSrc.get(src));
                    }
                }
            }
        } catch (Exception ex) {
            LOG.error("Cannot transfer value from object <{}> to <{}>", src, desc);
        }
        return desc;
    }

    public static List transferObject(List src, Class desc) {

        List lst = new ArrayList<>();
        try {
            for(Object obj : src) {
                if (obj != null)
                    lst.add(transferObject(obj, desc.newInstance()));
            }
        }catch (Exception ex) {
            LOG.error("Cannot transfer value from object <{}> to <{}>", src, desc);
        }
        return lst;
    }

    private static Field getDeclaredField(Object obj, String name) {
        try {
            return obj.getClass().getDeclaredField(name);
        }catch (Exception ex){
            return null;
        }
    }
    public static void aWait(long timeInMillis) {
        if (timeInMillis > 0) {
            try {
                Thread.sleep(timeInMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOG.error("", e);
            }
        }
    }

    public static boolean isSameObject(Object src1, Object src2) {
        try {
            Field[] fields = src1.getClass().getDeclaredFields();
            for(Field f1:fields){
                Field f2 = getDeclaredField(src2, f1.getName());
                f1.setAccessible(true);
                f2.setAccessible(true);
                Object fValue1 = f1.get(src1);
                Object fValue2 = f2.get(src2);
                if (!Objects.equals(fValue1, fValue2))
                    return false;
            }
        } catch (Exception ex) {
            LOG.error("Cannot check value of object <{}>", src1);
        }
        return true;
    }

   
    public static void storeJsonFile(String path, String content) {
        JsonWriter writer = null;
        try {
            writer = new JsonWriter(new FileWriter(path));
            writer.jsonValue(content).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void waitForThreadPoolTerminate(ExecutorService dealExecutor) {
        dealExecutor.shutdown();
        try {
            while (!dealExecutor.awaitTermination(10, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void waitForThreadPoolTerminate(ExecutorService dealExecutor, int timeAwaitTerminate){
        dealExecutor.shutdown();
        try {
            while (!dealExecutor.awaitTermination(timeAwaitTerminate, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shutdown thread pool and wait for thread pool shutdown in <b>timeAwaitTerminate</b> in seconds.
     * If count wait equal maxCountWaitShutdown then it will force shutdown by ThreadPoolExecutor#shutdownNow() method
     * @param dealExecutor
     * @param maxCountWaitShutdown Count await each every time wait for thread pool termination
     * @param timeAwaitTerminate Time wait for thread pool termination
     */
    public static void waitForThreadPoolTerminate(ThreadPoolExecutor dealExecutor, int maxCountWaitShutdown, int timeAwaitTerminate) {
        if (dealExecutor == null || dealExecutor.isTerminated()) return;
        try {
            dealExecutor.shutdown();
            dealExecutor.getQueue().clear();
            int counter = 0;
            while (!dealExecutor.awaitTermination(timeAwaitTerminate, TimeUnit.SECONDS)){
                LOG.error("Shutting down thread pool {}", dealExecutor);
                if (++counter == maxCountWaitShutdown) {
                    dealExecutor.shutdownNow();
                }
            }
        } catch (InterruptedException ie) {
            dealExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static <T> T convertInstanceOfObject(Object o, Class<T> clazz) {
        try {
            return (T) o;
        } catch(ClassCastException e) {
            return null;
        }
    }

    public static String resolveUrl(String url) {
        if (!url.endsWith("/")) {
            return url + "/";
        }
        return url;
    }
    public static Field findField(Class<?> objClazz, String fieldName) {
        if (objClazz == null) throw new IllegalArgumentException("Class must not be null");
        if (StringUtils.isBlank(fieldName)) throw new IllegalArgumentException("Field name must not be null");
        Field[] fields = getDeclaredFields(objClazz);
        for (Field f : fields) {
            if(fieldName.equals(f.getName())) {
                return f;
            }
        }
        return null;
    }

    public static <T> T getObjectField(Object objClazz, String fieldName, Class<T> typeFieldName) {
        if (objClazz == null) throw new IllegalArgumentException("Class must not be null");
        if (StringUtils.isBlank(fieldName)) throw new IllegalArgumentException("Field name must not be null");
        Field[] fields = getDeclaredFields(objClazz.getClass());
        for (Field f : fields) {
            if(fieldName.equals(f.getName())) {
                f.setAccessible(true);
                try {
                    return (T) f.get(objClazz);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static Field[] newArray(Field[] src, Field[] additional) {
        List<Field> lst1 = Lists.newArrayList(src);
        lst1.addAll(Lists.newArrayList(additional));
        return lst1.toArray(new Field[lst1.size()]);
    }

    public static Field[] getDeclaredFields(Class<?> clazz) {
        if (clazz == null) throw new IllegalArgumentException("Class must not be null");
        Field[] result = (Field[])declaredFieldsCache.get(clazz);
        if(result == null) {
            result = clazz.getDeclaredFields();
            Class<?> clazz1 = clazz.getSuperclass();
            while(clazz1 != null){
                Field[] result2 = clazz1.getDeclaredFields();
                result = newArray(result, result2);
                clazz1 = clazz1.getSuperclass();
            }
            declaredFieldsCache.put(clazz, result.length == 0 ? NO_FIELDS : result);
        }

        return result;
    }

    public static void setValue(Object objClazz, String fieldName, Object value) {
        if (objClazz == null) throw new IllegalArgumentException("Class must not be null");
        if (StringUtils.isBlank(fieldName)) throw new IllegalArgumentException("Field name must not be null");
        Field[] fields = getDeclaredFields(objClazz.getClass());
        for (Field f : fields) {
            if(fieldName.equals(f.getName())) {
                f.setAccessible(true);
                try {
                    f.set(objClazz, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean toBoolean(final Object value) {
        if (value instanceof Integer)
            return (Integer)value != 0;
        return Boolean.parseBoolean((String) value);

    }

    public static <T> List<T> removeNullValue(List<T> lst) {
        return lst.stream().filter(t-> Objects.nonNull(t)).collect(Collectors.toList());
    }

    /**
     * The function will get date2 - date1 and output by <b>TimeUnit</b>
     * @param date1
     * @param date2
     * @param timeUnit
     * @return long diff
     */
    public static long getTimeDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

    public static <T> List<List<T>> splitList(List<T> items, int maxSubArraySize) {
        List<List<T>> result = new ArrayList<>();
        if (items ==null || items.size() == 0) {
            return result;
        }

        int from = 0;
        int to = 0;
        int slicedItems = 0;
        while (slicedItems < items.size()) {
            to = from + Math.min(maxSubArraySize, items.size() - to);
            List subList = new CopyOnWriteArrayList(items.subList(from, to));
            result.add(subList);
            slicedItems += subList.size();
            from = to;
        }
        return result;
    }

    public static JsonObject toJsonObject(String jsonString) {
        JsonReader reader = Json.createReader(new StringReader(jsonString));
        try {
            return reader.readObject();
        } finally {
            if(Objects.nonNull(reader)) {
                reader.close();
            }
        }
    }
}


