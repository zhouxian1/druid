package com.hjj.common.tablib;

import com.alibaba.fastjson.JSON;
import com.dbs.common.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import javax.servlet.jsp.JspWriter;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zzy on 2015/10/9 0009.
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class DicMapDataTaglib extends RequestContextAwareTag {

    private String dicServiceImplAlias;
    private String keyField;
    private String textField;

    @Override
    protected int doStartTagInternal() throws Exception {
		BaseService baseService = (BaseService)this.getRequestContext().getWebApplicationContext().getBean(dicServiceImplAlias);
        Map map = listToMap(baseService.selectAll());
        JspWriter out = pageContext.getOut();
        out.print(JSON.toJSONString(map));
        return EVAL_PAGE; // 表示处理完标签后继续执行以下的JSP网页
    }

    public Map listToMap(List list) throws Exception {
        Map map = new LinkedHashMap<>();
        for(Object t : list){
            if(StringUtils.isBlank(keyField)){
                keyField = "id";
            }
            Method m = t.getClass().getDeclaredMethod("get"+ keyField.substring(0,1).toUpperCase()+ keyField.substring(1));
            Object id = m.invoke(t);

            if(StringUtils.isBlank(textField)){
                textField = "name";
            }
            m = t.getClass().getDeclaredMethod("get"+ textField.substring(0,1).toUpperCase()+ textField.substring(1));
            Object name = m.invoke(t);
            map.put(id,name);
        }
        return map;
    }

    public void setDicServiceImplAlias(String dicServiceImplAlias) {
        this.dicServiceImplAlias = dicServiceImplAlias;
    }

    public void setKeyField(String keyField) {
        this.keyField = keyField;
    }

    public void setTextField(String textField) {
        this.textField = textField;
    }
}
