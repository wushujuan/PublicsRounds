package cn.breaksky.rounds.publics.bean;

import java.io.Serializable;
import java.util.List;

public class GatherTaskBean implements Serializable {
	private static final long serialVersionUID = -6032100286619255054L;
	private Long rgt_id;
	private String name;
	private String explains;
	private String type;
	private Integer intervals;
	private Long begintime;
	private Long endtime;
	private Long createtime;
	private String createuser;
	private String regioncode;

	private List<DangerFactorBean> beans;

	public List<DangerFactorBean> getBeans() {
		return beans;
	}

	public void setBeans(List<DangerFactorBean> beans) {
		this.beans = beans;
	}

	public Long getRgt_id() {
		return rgt_id;
	}

	public void setRgt_id(Long rgt_id) {
		this.rgt_id = rgt_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExplains() {
		return explains;
	}

	public void setExplains(String explains) {
		this.explains = explains;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getIntervals() {
		return intervals;
	}

	public void setIntervals(Integer intervals) {
		this.intervals = intervals;
	}

	public Long getBegintime() {
		return begintime;
	}

	public void setBegintime(Long begintime) {
		this.begintime = begintime;
	}

	public Long getEndtime() {
		return endtime;
	}

	public void setEndtime(Long endtime) {
		this.endtime = endtime;
	}

	public Long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Long createtime) {
		this.createtime = createtime;
	}

	public String getCreateuser() {
		return createuser;
	}

	public void setCreateuser(String createuser) {
		this.createuser = createuser;
	}

	public String getRegioncode() {
		return regioncode;
	}

	public void setRegioncode(String regioncode) {
		this.regioncode = regioncode;
	}

}
