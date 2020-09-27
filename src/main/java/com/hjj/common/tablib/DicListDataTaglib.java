package com.hjj.common.tablib;

import com.alibaba.fastjson.JSON;
import com.dbs.common.service.BaseService;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import javax.servlet.jsp.JspWriter;

/**
 * Created by zzy on 2015/10/9 0009.
 */
public class DicListDataTaglib extends RequestContextAwareTag {

    private String dicServiceImplAlias;

    @Override
    protected int doStartTagInternal() throws Exception {
        BaseService baseService = (BaseService)this.getRequestContext().getWebApplicationContext().getBean(dicServiceImplAlias);
        JspWriter out = pageContext.getOut();
        out.print(JSON.toJSONString(baseService.select(null)));
        return EVAL_PAGE; // 表示处理完标签后继续执行以下的JSP网页
    }
    public void setDicServiceImplAlias(String dicServiceImplAlias) {
        this.dicServiceImplAlias = dicServiceImplAlias;
    }
}
