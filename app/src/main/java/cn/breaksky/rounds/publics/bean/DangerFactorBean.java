package cn.breaksky.rounds.publics.bean;

import java.io.Serializable;

public class DangerFactorBean implements Serializable {
	public final static String TEXT = "1";
	public final static String FILE = "2";
	public final static String SELECT = "3";
	
	private static final long serialVersionUID = -5693660932950597566L;
	private Long rdf_id;
	private String factor;
	private String value_stage;
	private String code;
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getRdf_id() {
		return rdf_id;
	}

	public void setRdf_id(Long rdf_id) {
		this.rdf_id = rdf_id;
	}

	public String getFactor() {
		return factor;
	}

	public void setFactor(String factor) {
		this.factor = factor;
	}

	public String getValue_stage() {
		return value_stage;
	}

	public void setValue_stage(String value_stage) {
		this.value_stage = value_stage;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
