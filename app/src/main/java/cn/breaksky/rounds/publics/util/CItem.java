package cn.breaksky.rounds.publics.util;

import cn.breaksky.rounds.publics.bean.RoundPersonnelVO;

public class CItem {
	private int noRead = 0;
	private String ID = "";
	private String Value = "";
	private RoundPersonnelVO user;

	public CItem() {
		ID = "";
		Value = "";
	}

	public CItem(String _ID, String _Value) {
		ID = _ID;
		Value = _Value;
	}

	public CItem(String _ID, String _Value, RoundPersonnelVO user) {
		ID = _ID;
		Value = _Value;
		this.user = user;
	}

	// 为什么要重写toString()呢？因为适配器在显示数据的时候，如果传入适配器的对象不是字符串的情况下，直接就使用对象.toString()
	@Override
	public String toString() {
		if (noRead > 0) {
			return Value + " [" + noRead + "未读]";
		} else {
			return Value;
		}
	}

	public int getNoRead() {
		return noRead;
	}

	public void setNoRead(int noRead) {
		this.noRead = noRead;
	}

	public String GetID() {
		return ID;
	}

	public String GetValue() {
		return Value;
	}

	public RoundPersonnelVO getUser() {
		return user;
	}

}
