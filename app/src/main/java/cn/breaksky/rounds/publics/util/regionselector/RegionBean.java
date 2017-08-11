package cn.breaksky.rounds.publics.util.regionselector;

import java.util.ArrayList;
import java.util.List;

public class RegionBean {
	private String code;
	private String name;
	private List<RegionBean> childs;

	/**
	 * @param code
	 * 
	 * @param name
	 * */
	public RegionBean(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public List<RegionBean> getChilds() {
		return childs;
	}

	public RegionBean addChilds(RegionBean child) {
		if (this.childs == null) {
			this.childs = new ArrayList<RegionBean>();
		}
		this.childs.add(child);
		return this;
	}

	public void setChilds(List<RegionBean> childs) {
		this.childs = childs;
	}

}
