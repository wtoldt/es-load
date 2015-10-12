package com.wtoldt.es.domain;

public class GhcndStation {

	private String id;
	private String lat;
	private String lon;
	private String location;
	private String elevation;
	private String state;
	private String name;
	private String gsnFlag;
	private String hcnCrnFlag;
	private String wmoId;



	public GhcndStation() {
		super();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getElevation() {
		return elevation;
	}
	public void setElevation(String elevation) {
		this.elevation = elevation;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGsnFlag() {
		return gsnFlag;
	}
	public void setGsnFlag(String gcnFlag) {
		this.gsnFlag = gcnFlag;
	}
	public String getHcnCrnFlag() {
		return hcnCrnFlag;
	}
	public void setHcnCrnFlag(String hcnCrnFlag) {
		this.hcnCrnFlag = hcnCrnFlag;
	}
	public String getWmoId() {
		return wmoId;
	}
	public void setWmoId(String wmoId) {
		this.wmoId = wmoId;
	}
	@Override
	public String toString() {
		return "GhcndStation [id=" + id + ", lat=" + lat + ", lon=" + lon
				+ ", location=" + location + ", elevation=" + elevation
				+ ", state=" + state + ", name=" + name + ", gsnFlag="
				+ gsnFlag + ", hcnCrnFlag=" + hcnCrnFlag + ", wmoId=" + wmoId
				+ "]";
	}
}
