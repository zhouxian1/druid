package com.hjj.common.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 返回前台tree
 * @author NPF
 *
 */
public class Tree extends PinyinDomain implements Serializable{

	/**
	 * ID
	 */
	private String id;
	
	/**
	 * 序号
	 */
	private String xh;
	/**
	 * 显示text
	 */
	private String text;
	/**
	 * 父节点pid
	 */
	private String pid;
	/**
	 * 状态 
	 * state：节点状态，'open' 或 'closed'，默认：'open'。如果为'closed'的时候，将不自动展开该节点
	 */
	private String state;
	/**
	 * checked：表示该节点是否被选中。
	 */
	private Boolean checked;
	
	/**
	 * 自定义属性
	 */
	private Map<String,String> attributes;
	/**
	 * 子集节点集合
	 */
	private List<Tree> children;
	/**
	 * 节点层级
	 */
	private Integer level;
	
	
	public Tree() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Tree(String id, String text,String state,Integer level) {
		super();
		this.id = id;
		this.text = text;
		this.state = state;
		this.level = level;
	}
	public Tree(String id, String text,String state,Integer level,Map<String,String> attributes) {
		super();
		this.id = id;
		this.text = text;
		this.state = state;
		this.level = level;
		this.attributes = attributes;
	}
	
	public Tree(String id, String text) {
		super();
		this.id = id;
		this.text = text;
	}
	
	public Tree(String id, String xh,String text) {
		super();
		this.id = id;
		this.xh = xh;
		this.text = text;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Boolean getChecked() {
		return checked;
	}
	public void setChecked(Boolean checked) {
		this.checked = checked;
	}
	public Map<String, String> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	public List<Tree> getChildren() {
		return children;
	}
	public void setChildren(List<Tree> children) {
		this.children = children;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getXh() {
		return xh;
	}

	public void setXh(String xh) {
		this.xh = xh;
	}
	
	

}
