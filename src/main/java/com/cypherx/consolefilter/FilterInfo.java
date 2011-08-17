package com.cypherx.consolefilter;

import java.util.logging.Level;

public class FilterInfo {
	private FilterType type;
	private String value;
	private String replace;
	private Level level;

	public FilterInfo(FilterType type, String value, String replace, Level level) {
		this.type = type;
		this.value = value;
		this.replace = replace;
		this.level = level;
	}

	public void setType(FilterType type) {
		this.type = type;
	}

	public FilterType getType() {
		return type;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setReplace(String replace) {
		this.replace = replace;
	}

	public String getReplace() {
		return replace;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public Level getLevel() {
		return level;
	}

	
}