package com.hjj.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 
 * @ProjectNmae：util.
 * @ClassName：NumberUtils
 * @Description： (数字处理工具类)
 * @author：chl
 * @crateTime：2013-5-16 上午9:51:46
 * @editor：
 * @editTime：
 * @editDescription：
 * @version 1.0.0
 */
public class NumberUtils {

    private static final String ZHENG = "整";

    private static final String YUAN = "元";

    private static final String YI = "亿";

    private static final String WAN = "万";

    private static final String QIAN = "仟";

    private static final String BAI = "佰";

    private static final String SHI = "拾";

    private static final String FEN = "分";

    private static final String JIAO = "角";

    private static final String LING = "零";

    private static final String[] CHARS = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };

    private static final String PLACE_HOLDER = "零零零零";

    /**
     * 
     * @doTransfer(去掉字符串中的零).
     * @author： Chl
     * @createTime：2013-5-30 上午9:43:38
     * @param str
     *            源字符串
     * @return 清除零后的字符串
     * @return StringBuilder
     */
    private static StringBuilder doTransfer(String str) {
        StringBuilder sb = new StringBuilder();

        if (PLACE_HOLDER.equals(str)) { // 零零零零
            return sb;
        }
        int idx = str.indexOf(PLACE_HOLDER.substring(0, 3));
        if (idx == 0) {// 零零零*
            sb.append(LING).append(str.charAt(3));
        } else if (idx == 1) {// *零零零
            sb.append(str.charAt(0)).append(QIAN);
        } else {
            idx = str.indexOf(PLACE_HOLDER.substring(0, 2));
            if (idx == 0) {// 零零**
                sb.append(LING).append(str.charAt(2)).append(SHI).append(str.charAt(3));
            } else if (idx == 1) {// *零零*
                sb.append(str.charAt(0)).append(QIAN).append(LING).append(str.charAt(3));
            } else if (idx == 2) { // **零零
                sb.append(str.charAt(0)).append(QIAN).append(str.charAt(1)).append(BAI);
            } else if (str.startsWith(LING)) { // 零***
                sb.append(LING).append(str.charAt(1)).append(BAI).append(str.charAt(2)).append(SHI).append(str.charAt(3));
            } else {
                sb.append(str.charAt(0)).append(QIAN).append(str.charAt(1)).append(BAI).append(str.charAt(2)).append(SHI).append(str.charAt(3));
            }
        }
        if (sb.charAt(sb.length() - 1) == PLACE_HOLDER.charAt(0)) { // 去掉个位的零
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb;
    }

    /**
     * 
     * @numberToChinese(小写转换为大写).
     * @author： Chl
     * @createTime：2013-5-30 上午9:41:40
     * @param amount
     *            小写数字
     * @return 转换后的大写字符串
     * @return String
     */
    public static String numberToChinese(BigDecimal amount) {
        String[] val = amount.toString().split("\\.");
        String integer = val[0];
        StringBuffer sb = new StringBuffer();

        if (!"0".equals(integer) && !"".equals(integer)) {
            long number = Long.parseLong(integer);
            while (number > 0) {
                int i = (int) (number % 10);
                sb.insert(0, CHARS[i]);
                number /= 10;
            }

            String tmp = sb.toString();
            sb = new StringBuffer();

            // 亿
            int l = tmp.length();
            if (l > 8) {
                tmp = PLACE_HOLDER.substring(0, 12 - l) + tmp;
                String str = tmp.substring(0, 4);
                sb.append(doTransfer(str)).append(YI);
                tmp = tmp.substring(4);
                l = tmp.length();
            }
            // 万
            if (l > 4) {
                tmp = PLACE_HOLDER.substring(0, 8 - l) + tmp;
                String str = tmp.substring(0, 4);
                StringBuilder result = doTransfer(str);
                if (result.length() > 0) {
                    sb.append(result).append(WAN);
                }
                tmp = tmp.substring(4);
                l = tmp.length();
            }
            // 个十百千
            tmp = PLACE_HOLDER.substring(0, 4 - l) + tmp;
            sb.append(doTransfer(tmp));
            sb.append(YUAN);
            if (LING.equals(sb.subSequence(0, 1))) { // 去掉开头的零
                sb.deleteCharAt(0);
            }
        } else {
            sb.append(PLACE_HOLDER.substring(0, 1)).append(YUAN);
        }

        if (val.length > 1 && !"00".equals(val[1])) {
            String decimal = val[1];
            sb.append(CHARS[decimal.charAt(0) - '0']).append(JIAO);
            if (decimal.length() >= 2) {
                sb.append(CHARS[decimal.charAt(1) - '0']).append(FEN);
            }
        } else {
            sb.append(ZHENG);
        }

        String s = sb.toString();
        s = s.replaceAll(LING + SHI, LING); // 去掉“零拾”
        s = s.replaceAll(LING + BAI, LING); // 去掉“零佰”
        s = s.replaceAll(LING + QIAN, LING); // 去掉“零仟”
        s = s.replaceAll("^(" + SHI + "|" + BAI + "|" + QIAN + "|" + WAN + "|" + YI + ")", ""); // 去掉开头的“佰”“拾”“仟”“万”“亿”
        return s;
    }

    /**
     * 
     * @saveDecimals(BigDecimal类型数字保留n位小数).
     * @author： chl
     * @createTime：2013-5-16 上午9:04:45
     * @param big
     *            原数字
     * @param n
     *            保留小数位数
     * @return BigDecimal
     */
    public static BigDecimal saveDecimals(BigDecimal big, int n) {
        return big.setScale(n, RoundingMode.HALF_EVEN);
    }

    /**
     * 
     * @numberToPercentString(数字转换为百分数，转换后的小数个数为n).
     * @author： chl
     * @createTime：2013-5-16 上午9:06:47
     * @param big
     *            原数字
     * @param n
     *            保留小数位数
     * @return String 转换后的百分数
     */
    public static String numberToPercentString(BigDecimal big, int n) {
        if (big.equals(BigDecimal.ZERO)) {
            return "0.00%";
        } else {
            return big.setScale(n + 2, RoundingMode.HALF_EVEN).movePointRight(2).toString() + "%";
        }
    }
}
