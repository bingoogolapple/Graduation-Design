package com.bingoogol.frogcare.domain;

public class BlacklistInfo {
	private String number;
	private Integer mode;

	public BlacklistInfo(String number, Integer mode) {
		this.number = number;
		this.mode = mode;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Integer getMode() {
		return mode;
	}

	public void setMode(Integer mode) {
		this.mode = mode;
	}
}
