package com.hjj.common.controller;

import com.hjj.common.domain.Result;
import com.hjj.common.service.BaseService;
import com.hjj.common.util.ConverVideo;
import io.micrometer.core.instrument.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "unchecked" })
public abstract class BaseController<T> {

	private final Logger logger = Logger.getLogger(this.getClass());

	@SuppressWarnings("rawtypes")
	public abstract BaseService getService();

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
	}

	@ModelAttribute
	public void setAttribute(HttpServletRequest request, HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "x-requested-with,content-type");

		SysContent.setRequest(request);
		SysContent.setResponse(response);
		SysContent.setSession(request.getSession());
		SysContent.setContext(request.getServletContext());
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Object get(@PathVariable Object id) {
		try {
			return getService().selectByPrimaryKey(id);
		} catch (Exception e) {
			catchException(e);
			return null;
		}
	}

	@RequestMapping(value = "/row", method = RequestMethod.GET)
	@ResponseBody
	public Object row(T t) {
		try {
			List<T> list = getService().selectSelective(t);
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
		} catch (Exception e) {
			catchException(e);
			return null;
		}
		return null;
	}
     /**
     * 武器装备 将父级 典型配备方案数据导入 子表
     * @param t
     * @param parentZbnm(父级zbnm)
     * @param zbnm (子表zbnm)
     * @param otherPK (子表主键)
     * @throws Exception
     */
	@RequestMapping(value = "/updateByParentZbnm", method = RequestMethod.GET)
	@ResponseBody
	public String updateByParentZbnm(T t,String parentZbnm,String zbnm,String otherPK,String otherPKName) {
		try {
			getService().updateByParentZbnm(t,parentZbnm, zbnm,otherPK,otherPKName);
			return "success";
		} catch (Exception e) {
			catchException(e);
			return "failed";
		}
	}
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Object add(T t) {
		try {
			getService().insertSelective(t);
			return t;
		} catch (Exception e) {
			catchException(e);
			return error(e);
		}
	}

	@RequestMapping(method = RequestMethod.PUT)
	@ResponseBody
	public Object update(T t) {
		try {
//			getService().updateByPrimaryKeySelective(t);
			getService().updateByPrimaryKey(t);  //根据主键更新实体全部字段，null值会被更新
			return t;
		} catch (Exception e) {
			catchException(e);
			return error(e);
		}
	}

	@RequestMapping(value = "/{keys}", method = RequestMethod.DELETE)
	@ResponseBody
	public Object delete(@PathVariable String keys, T t) {
		try {
			if (StringUtils.isBlank(keys)) {
				return error("参数获取失败!");
			}
			if (keys.startsWith("[{")) {
				getService().delete(JSON.parseArray(keys, t.getClass()));
			} else if (keys.startsWith("[")) {
				List<String> keysArr = JSON.parseArray(keys, String.class);
				getService().deleteByPrimaryKeys(keysArr);
			} else if (keys.startsWith("{")) {
				t = JSON.parseObject(keys, (Class<T>) t.getClass());
				getService().delete(t);
			} else if (keys.contains(",")) {
				getService().deleteByPrimaryKeys(keys.split(","));
			} else {
				getService().deleteByPrimaryKey(keys);
			}
			return success();
		} catch (Exception e) {
			catchException(e);
			return error(e);
		}
	}

	@RequestMapping(value = "/page", method = RequestMethod.GET)
	@ResponseBody
	public Page page(T t, Page page) {
		try {
			return getService().select(t, page);
		} catch (Exception e) {
			catchException(e);
			return null;
		}
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public List<T> pageList(T t, Page page) {
		try {
			return getService().selectList(t, page);
		} catch (Exception e) {
			catchException(e);
			return null;
		}
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	@ResponseBody
	public List<T> all() {
		try {
			return getService().selectAll();
		} catch (Exception e) {
			catchException(e);
			return null;
		}
	}

	@RequestMapping(value = "/queryList", method = RequestMethod.GET)
	@ResponseBody
	public List<T> queryList(T t) {
		try {
			return getService().queryByExample(t);
		} catch (Exception e) {
			catchException(e);
			return null;
		}
	}

	/**
	 * 字典表 树状结构
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/tree", method = RequestMethod.GET)
	@ResponseBody
	public List<Tree> tree(T record, String xhProperty, String mcProperty) throws Exception {
		try {
			return getService().tree(record, xhProperty, mcProperty);
		} catch (Exception e) {
			catchException(e);
			return null;
		}
	}

	public void catchException(Exception e) {
		if (null != e.getCause())
			logger.error(e.getCause().getMessage(), e.getCause());
		else
			logger.error(e.getMessage(), e);
	}

	public Result success() {
		return new Result(true);
	}

	public Result success(String msg) {
		return new Result(true, msg);
	}

	public Result error(String msg) {
		return new Result(false, msg);
	}

	public Result error(Exception e) {
		return new Result(false, null != e.getCause() ? e.getCause().getMessage() : e.getMessage());
	}

	public HttpServletRequest getRequest() {
		return SysContent.getRequest();
	}

	public HttpServletResponse getResponse() {
		return SysContent.getResponse();
	}

	public HttpSession getSession() {
		return SysContent.getSession();
	}

	public ServletContext getContext() {
		return SysContent.getContext();
	}
	
	/**
     * 查找视频文件是否存在
     * @param t
     * @return
     */
    @RequestMapping(value = "/queryVideoFile", method = RequestMethod.GET)
    @ResponseBody
    public String selectFile(T t) throws Exception {
		try {
			Method _mt = t.getClass().getMethod("getWjm");
			String wjm = (String) _mt.invoke(t);
			String wjm1 = wjm.substring(0, wjm.lastIndexOf(".")).toLowerCase() + ".flv";
			String tapth = SysContent.getRequest().getServletContext().getRealPath("");
			String epServicePath = tapth.substring(0, tapth.length()-3) + File.separator + "resources\\";
			File file = new File(epServicePath + "video\\" + wjm1);
			if (file.exists()) {
				return "success";
			} else {
				//视频文件不存在，自动从库中读取视频流下载在指定文件夹，在转换为flv视频文件
				t = (T) getService().selectOne(t);
				Method _m = t.getClass().getMethod("getMtdx");
				byte[] mtdx = (byte[]) _m.invoke(t);
				if (null != mtdx) {
					File scratchRelativePath = new File(tapth + "scratchFile");
					if (!scratchRelativePath.exists()) {
						scratchRelativePath.mkdirs();
					}
					String absolutePath = scratchRelativePath + File.separator + wjm;
					FileOutputStream out = new FileOutputStream(absolutePath);
					IOUtils.write(mtdx, out);
					out.close();
					
					//调用视频转flv文件
	        		ConverVideo cv = new ConverVideo(absolutePath, tapth);
	        		Map<String, String> map = cv.beginConver();

	        		FileUtils.copyFile(new File(tapth + map.get("filepath")), new File(epServicePath + "video\\" + map.get("fileName")));
	        		FileUtils.copyFile(new File(tapth + map.get("videoCaptureUrl")), new File(epServicePath + "v_img\\" + map.get("videoCaptureName")));
	        		cv.deleteFile(tapth + map.get("videoCaptureUrl"));
	        		return "success";
				}
			}
		} catch (Exception e) {
			catchException(e);
		}
		return "failed";
    }
    
    /**查询一行数据**/
    @RequestMapping(value = "/line", method = RequestMethod.GET)
	@ResponseBody
	public Object line(T t) {
		try {
			return getService().selectOne(t);
		} catch (Exception e) {
			catchException(e);
			return null;
		}
	}
    
}
