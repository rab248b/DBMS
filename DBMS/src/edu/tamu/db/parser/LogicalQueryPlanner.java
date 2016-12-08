package edu.tamu.db.parser;

public class LogicalQueryPlanner {
//	void convertIntoLQT(Node root){
//		List<Node> rootChildren = root.getChildren();
//		Node temp, projection, selection,join;
//		LogicQueryNode lqRoot = new LogicQueryNode(new ArrayList<String>());
//		for(int i =0; i < rootChildren.size(); i++){
//			if(rootChildren.get(i).getData().equals("SelectList")){
//				lqRoot.setData(rootChildren.get(i).getChildren());
//				lqRoot.setTag("projection");
//			}
//			else if(rootChildren.get(i).getData().equals("WhereChild")){
//				temp = new Node(rootChildren.get(i).getChildren());
//				temp.setTag("selection");
//				lqRoot.addChild(temp);
//			}else if(rootChildren.get(i).getData().equals("FromChild")){
//				join = new Node("Joins");
//				join.setTag("Joins");
//				temp = new Node(rootChildren.get(i).getChildren());
//				temp.setTag("tables");
//				join.addChild(temp);
//				lqRoot.addChild(join);
//			}
//		}
//		
//		System.out.println(lqRoot.getData()+"===>"+lqRoot.getTag());
//		for(int i=0; i< lqRoot.getChildren().size(); i++){
//			temp = (Node) lqRoot.getChildren().get(i);
//			System.out.println(temp.getTag());
//		}
//	}
//	
////	void printChilds(Node root){
////		Node temp;
////		for(int i=0; i< root.getChildren().size();i++){
////			temp = (Node) root.getChildren().get(i);
////			System.out.println(temp.getData();
////		}
////	}
}
