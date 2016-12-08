package edu.tamu.db.queryplan;

import java.util.ArrayList;
import java.util.List;

import storageManager.FieldType;

public class Relation {
	private String relationName;
	private List<String> columnList;
	private List<FieldType> columnTypeList;
	private String aliasName;
	public Relation(){
		columnList = new ArrayList<>();
		columnTypeList = new ArrayList<>();
	}
	public String getRelationName() {
		return relationName;
	}
	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}
	public List<String> getColumnList() {
		return columnList;
	}
	public void setColumnList(List<String> columnList) {
		this.columnList = columnList;
	}
	public List<FieldType> getColumnTypeList() {
		return columnTypeList;
	}
	public void setColumnTypeList(List<FieldType> columnTypeList) {
		this.columnTypeList = columnTypeList;
	}
	@Override
	public String toString() {
		return "Relation [relationName=" + relationName + ", columnList=" + columnList + ", columnTypeList="
				+ columnTypeList + "]";
	}
	public String getAliasName() {
		return aliasName;
	}
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}
}
