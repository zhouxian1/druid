package com.hjj.common.util;

import org.lionsoul.jcseg.core.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 争洋 on 2015/7/1 0001.
 */
public class JcsegUtil {

    //JcsegTaskConfig 分词任务配置
    private static JcsegTaskConfig entryConfig;

    //分词词库对象
    private static ADictionary entryDic;

    /**
     * 初始化所有分词器
     */
    public static void init() {
        initEntryJcseg();
    }

    /**
     * 初始化筛选模式分词器分词器
     */
    private static void initEntryJcseg(){
        entryConfig = new JcsegTaskConfig(Thread.currentThread().getContextClassLoader().getResource("jcseg-entry.properties").getFile());
        entryDic = DictionaryFactory.createDefaultDictionary(entryConfig);
    }

    /**
     * 获取选择模式分词器
     * @return
     * @throws Exception
     */
    public static ISegment getEntrySegment() throws Exception{
        if(null == entryDic){
            initEntryJcseg();
        }
        return SegmentFactory.createJcseg(JcsegTaskConfig.DETECT_MODE, new Object[]{entryConfig, entryDic});
    }


    public static String linkEpzhString(String content) throws Exception {
        if(StringUtils.isBlank(content)) return null;
        //依据给定的ADictionary和JcsegTaskConfig来创建ISegment
        StringReader sr = new StringReader(content);
        ISegment seg = getEntrySegment();
        seg.reset(sr);
        //获取分词结果
        IWord word;
        List<String> words = new ArrayList<>();
        while ((word = seg.next()) != null) {
            words.add(word.getValue());
        }

        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            Matcher matcher = Pattern.compile("(?i)"+w).matcher(content);
            if (matcher.find()){
                String matcherWord =  matcher.group();
                int index = content.indexOf(matcherWord) + matcherWord.length();
                sb.append(content.substring(0,index).replace(matcherWord, "<a href=\"javascript:top.showEntry('" + matcherWord + "')\">" + matcherWord + "</a>"));
                content = content.substring(index);
            }
        }
        sb.append(content);

        return sb.toString();
    }

    /**
     * 获取前5个关键词，
     * @param content 文本内容
     * @return
     * @throws Exception
     */
    public static Set<String> getAllKeywords(String content) throws Exception {
        //依据给定的ADictionary和JcsegTaskConfig来创建ISegment
        ISegment seg = getEntrySegment();
        seg.reset(new StringReader(content));
        //获取分词结果
        IWord word;
        Set<String> words = new HashSet<>();
        while ((word = seg.next()) != null) {
            words.add(word.getValue());
        }
        return words;
    }

}
