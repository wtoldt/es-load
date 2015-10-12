package com.wtoldt.es.domain;

public class GhcndDataDay {

	private String stationId;
	private String year;
	private String month;
	private String day;
	private String date;
	private String element;
	private String elementDesc;
	private String value;
	private String mFlag;
	private String mFlagDesc;
	private String qFlag;
	private String qFlagDesc;
	private String sFlag;
	private String sFlagDesc;
	private String units;
	private String scale;
	private GhcndStation station;

	public GhcndDataDay() {
		super();
	}

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getElement() {
		return element;
	}

	public void setElement(String element) {
		this.element = element;
	}

	public String getElementDesc() {
		return elementDesc;
	}

	public void setElementDesc(String elementDesc) {
		this.elementDesc = elementDesc;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getmFlag() {
		return mFlag;
	}

	public void setmFlag(String mFlag) {
		this.mFlag = mFlag;
	}

	public String getmFlagDesc() {
		return mFlagDesc;
	}

	public void setmFlagDesc(String mFlagDesc) {
		this.mFlagDesc = mFlagDesc;
	}

	public String getqFlag() {
		return qFlag;
	}

	public void setqFlag(String qFlag) {
		this.qFlag = qFlag;
	}

	public String getqFlagDesc() {
		return qFlagDesc;
	}

	public void setqFlagDesc(String qFlagDesc) {
		this.qFlagDesc = qFlagDesc;
	}

	public String getsFlag() {
		return sFlag;
	}

	public void setsFlag(String sFlag) {
		this.sFlag = sFlag;
	}

	public String getsFlagDesc() {
		return sFlagDesc;
	}

	public void setsFlagDesc(String sFlagDesc) {
		this.sFlagDesc = sFlagDesc;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public String getScale() {
		return scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public GhcndStation getStation() {
		return station;
	}

	public void setStation(GhcndStation station) {
		this.station = station;
	}

	@Override
	public String toString() {
		return "GhcndDataDay [stationId=" + stationId + ", year=" + year
				+ ", month=" + month + ", day=" + day + ", date=" + date
				+ ", element=" + element + ", elementDesc=" + elementDesc
				+ ", value=" + value + ", mFlag=" + mFlag + ", mFlagDesc="
				+ mFlagDesc + ", qFlag=" + qFlag + ", qFlagDesc=" + qFlagDesc
				+ ", sFlag=" + sFlag + ", sFlagDesc=" + sFlagDesc + ", units="
				+ units + ", scale=" + scale + ", station=" + station + "]";
	}



}
