package com.hjj.common.security;

import com.dbs.ep.sys.domain.SysBehaviorTrace;
import com.dbs.ep.sys.domain.User;
import com.dbs.ep.sys.mapper.UserMapper;
import com.dbs.ep.sys.service.SysBehaviorTraceService;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.cas.CasFilter;
import org.apache.shiro.subject.Subject;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.Date;


public class MyCasFilter extends CasFilter {

	private SysBehaviorTraceService sysBehaviorTraceService;
	private UserMapper userMapper;

	@Override
	protected boolean onLoginSuccess(AuthenticationToken token,
			Subject subject, ServletRequest request, ServletResponse response)
			throws Exception {
		//保存日志
    	SysBehaviorTrace sbt =new SysBehaviorTrace();
    	//进行UTF-8解码
    	String casUserName = token.getPrincipal().toString();
    	casUserName = URLDecoder.decode(casUserName, "UTF-8");
    	
    	User loginUser=userMapper.selectOne(new User(casUserName));
    	
    	sbt.setUserName(casUserName);
    	sbt.setUserId(loginUser.getId().toString());
    	sbt.setSjsj(new Date());
    	sbt.setType(0);
    	sbt.setOperationType("登录");
    	sysBehaviorTraceService.insertSelective(sbt);
    	
    	HttpServletRequest req = (HttpServletRequest)request;
    	req.getSession().setAttribute("user", loginUser);
    	
		return super.onLoginSuccess(token, subject, request, response);
	}
	public void setSysBehaviorTraceService(
			SysBehaviorTraceService sysBehaviorTraceService) {
		this.sysBehaviorTraceService = sysBehaviorTraceService;
	}
	
	public void setUserMapper(UserMapper userMapper) {
		this.userMapper = userMapper;
	}
	
}
