package edu.tamu.db.queryplan;

import java.util.ArrayList;
import java.util.List;

public class LogicQueryNode {
	private String data;
	private LogicQueryNode parent;
	private List<LogicQueryNode> children;
	private String tag;
	private Condition condition;
	private List<String> columnList;

	public LogicQueryNode(String data, LogicQueryNode parent) {
		this.setData(data);
		this.parent = parent;
		this.children = new ArrayList<LogicQueryNode>();
	}

	public LogicQueryNode(String data) {
		this.setData(data);
		this.parent = null;
		this.children = new ArrayList<LogicQueryNode>();
	}

	public void addChild(String data) {
		LogicQueryNode child = new LogicQueryNode(data, this);
		this.children.add(child);
	}

	public void addChild(LogicQueryNode child) {
		child.setParent(this);
		this.children.add(child);
	}

	public LogicQueryNode getParent() {
		return parent;
	}

	public void setParent(LogicQueryNode parent) {
		this.parent = parent;
	}

	public List<LogicQueryNode> getChildren() {
		return children;
	}

	public void setChildren(List<LogicQueryNode> children) {
		this.children = children;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public List<String> getColumnList() {
		return columnList;
	}

	public void setColumnList(List<String> columnList) {
		this.columnList = columnList;
	}

	public LogicQueryNode createCopy() {
		LogicQueryNode temp = new LogicQueryNode(this.getData());
		temp.setTag(this.getTag());
//		if (this.getParent() != null)
		temp.setParent(this.getParent());
		if (this.getChildren().size() != 0) {
			for (int i = 0; i < this.getChildren().size(); i++) {
				temp.addChild(this.getChildren().get(i).createCopy());
			}
		}
		if (this.condition != null) {
			temp.setCondition(this.getCondition());
		}
		
		temp.setColumnList(this.getColumnList());
		return temp;
	}
}
