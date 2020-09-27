package com.hjj.common.util;

import java.util.UUID;

/**
 * @ProjectNmae：util.
 * @ClassName：UUIDUtils
 * @Description： (UUID工具类)
 * @author：chl
 * @crateTime：2013-5-21 下午3:41:41
 * @editor：
 * @editTime：
 * @editDescription：
 * @version 1.0.0
 */
public class UUIDUtils {

    /**
     * @getUUID(获取UUID字符串).
     * @author： chl
     * @createTime：2013-5-21 下午3:44:21
     * @return
     * @return String
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * @getUUID(获取去掉“-”的UUID字符串).
     * @author： chl
     * @createTime：2013-5-21 下午3:31:02
     * @return
     * @return String
     */
    public static String getUUIDTrim() {
        String s = UUID.randomUUID().toString();
        s = s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24);
        return s;
    }

    /**
     * @getUUIDAddString(在生成的UUID字符串中在拼接上str).
     * @author： chl
     * @createTime：2013-5-21 下午3:38:17
     * @param str
     *            要拼接的字符串
     * @param flag
     *            当为true时，str拼接在uuid前面，否则拼接在uuid后面
     * @return
     * @return String
     */
    public static String getUUIDAddString(String str, boolean flag) {
        StringBuffer sb = new StringBuffer();
        if (flag) {
            sb.append(str);
            sb.append(getUUID());

        } else {
            sb.append(getUUID());
            sb.append(str);
        }
        return sb.toString();
    }

}
