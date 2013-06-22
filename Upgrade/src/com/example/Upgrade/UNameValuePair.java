package com.example.Upgrade;

public class UNameValuePair{
	private String Name;
	private String Value;
	
	public UNameValuePair(String name, String value) {
		Name = name;
		Value = value;
	}
	public String getName() {
		return Name;
	}
	
	public void setName(String name) {
		Name = name;
	}
	
	public String getValue() {
		return Value;
	}
	
	public void setValue(String value) {
		Value = value;
	}
}
