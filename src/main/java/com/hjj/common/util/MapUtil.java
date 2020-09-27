package com.hjj.common.util;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MapUtil {

    public final static int ASC = 1;
    public final static int DESC = -1;

    public static List<Map.Entry<String, Double>> sortMapByDoubleValueAsc(Map<String, Double> map){
        return sortMapByDoubleValue(map, MapUtil.ASC);
    }
    public static List<Map.Entry<String, Double>> sortMapByDoubleValueDesc(Map<String, Double> map){
        return sortMapByDoubleValue(map, MapUtil.DESC);
    }
    public static List<Map.Entry<String, Double>> sortMapByDoubleValue(Map<String, Double> map,int orderType) {
        List<Map.Entry<String, Double>> entries = new ArrayList(map.entrySet());
        Collections.sort(entries, (obj1, obj2) -> {
            if (obj1.getValue() > obj2.getValue()) {
                return 1*orderType;
            } else if (obj1.getValue() == obj2.getValue()) {
                return 0;
            } else {
                return -1*orderType;
            }
        });
        return entries;
    }

    public static List<Map.Entry<String, Integer>> sortMapByIntegerValueDesc(Map<String, Integer> map){
        return sortMapByIntegerValue(map,MapUtil.DESC);
    }
    public static List<Map.Entry<String, Integer>> sortMapByIntegerValueAsc(Map<String, Integer> map){
        return sortMapByIntegerValue(map,MapUtil.ASC);
    }
    public static List<Map.Entry<String, Integer>> sortMapByIntegerValue(Map<String, Integer> map,int orderType) {
        List<Map.Entry<String, Integer>> entries = new ArrayList(map.entrySet());
        Collections.sort(entries, (obj1, obj2) -> {
            if (obj1.getValue() > obj2.getValue()) {
                return 1*orderType;
            } else if (obj1.getValue() == obj2.getValue()) {
                return 0;
            } else {
                return -1*orderType;
            }
        });
        return entries;
    }
    
}
