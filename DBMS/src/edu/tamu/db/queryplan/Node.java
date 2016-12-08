package edu.tamu.db.queryplan;

import java.util.ArrayList;
import java.util.List;

public class Node {
        private String data;
        private Node parent;
        private List<Node> children;
        public Node(String data, Node parent){
        	this.data =  data;
        	this.parent = parent;
        	this.children = new ArrayList<Node>();
        }
        public Node(String data){
        	this.data =  data;
        	this.parent = null;
        	this.children = new ArrayList<Node>();
        }
        public void addChild(String data){
        	Node child = new Node(data,this);
        	children.add(child);
        }
        
        public void addChild(Node child){
        	child.setParent(this);
        	children.add(child);
        }
        
		public String getData() {
			return data;
		}
		public void setData(String data) {
			this.data = data;
		}
		public Node getParent() {
			return parent;
		}
		public void setParent(Node parent) {
			this.parent = parent;
		}
		public List<Node> getChildren() {
			return children;
		}
		public void setChildren(List<Node> children) {
			this.children = children;
		}        
    }