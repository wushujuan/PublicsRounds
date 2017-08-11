package cn.breaksky.rounds.publics.bean;

import java.util.List;

public class WorkBean {
	private Long rw_id;
	private Integer personnel_num;
	private String workname;
	private Integer begintime;
	private Long begindate;
	private Integer endtime;
	private Long enddate;
	private Long createtime;

	private List<Line> lines;
	private List<Area> areas;
	private String points;

	public String getPoints() {
		return points;
	}

	public void setPoints(String points) {
		this.points = points;
	}

	public List<Area> getAreas() {
		return areas;
	}

	public void setAreas(List<Area> areas) {
		this.areas = areas;
	}

	public Long getRw_id() {
		return rw_id;
	}

	public void setRw_id(Long rw_id) {
		this.rw_id = rw_id;
	}

	public Integer getPersonnel_num() {
		return personnel_num;
	}

	public void setPersonnel_num(Integer personnel_num) {
		this.personnel_num = personnel_num;
	}

	public String getWorkname() {
		return workname;
	}

	public void setWorkname(String workname) {
		this.workname = workname;
	}

	public Integer getBegintime() {
		return begintime;
	}

	public void setBegintime(Integer begintime) {
		this.begintime = begintime;
	}

	public Long getBegindate() {
		return begindate;
	}

	public void setBegindate(Long begindate) {
		this.begindate = begindate;
	}

	public Integer getEndtime() {
		return endtime;
	}

	public void setEndtime(Integer endtime) {
		this.endtime = endtime;
	}

	public Long getEnddate() {
		return enddate;
	}

	public void setEnddate(Long enddate) {
		this.enddate = enddate;
	}

	public Long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Long createtime) {
		this.createtime = createtime;
	}

	public List<Line> getLines() {
		return lines;
	}

	public void setLines(List<Line> lines) {
		this.lines = lines;
	}

}
