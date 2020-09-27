package com.hjj.common.domain;

import com.dbs.common.util.PinyinUtil;

public abstract class PinyinDomain {
	public String getPinyin() {
		String mc = this.getMc();
		if(null == mc) mc = this.getText();
		
		if(null != mc){
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<mc.length();i++){
				String pinyin = PinyinUtil.getStringPinYin(mc.substring(i, i+1));
				sb.append(pinyin.substring(0, 1));
			}
			return sb.toString();
		}
		return null;
	}

	public String getMc(){
		return null;
	};

	public String getText(){
		return null;
	};

	
}
