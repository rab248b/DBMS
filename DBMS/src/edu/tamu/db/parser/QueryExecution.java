package edu.tamu.db.parser;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import edu.tamu.db.queryplan.Condition;
import edu.tamu.db.queryplan.LogicQueryNode;
import edu.tamu.db.queryplan.Node;
import edu.tamu.db.queryplan.Relation;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TColumnDefinitionList;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TMultiTarget;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.nodes.TTableList;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import storageManager.FieldType;
import storageManager.Tuple;
import testManager.Execution;

public class QueryExecution {
	// public static void main(String[] args) {
	// QueryExecution qE = new QueryExecution();
	// qE.runQuery("avcd");
	// }

	// static MainMemory mem = new MainMemory();
	// static Disk disk = new Disk();
	private Execution executeInstance;
	private List<Relation> relList = new ArrayList<>();

	boolean selectFlag;
	boolean joinFlag;

	int rowIndex;
	// public QueryExecution(MainMemory memory, Disk diskSpace) {
	// mem = memory;
	// disk = diskSpace;
	// }
	private HSSFWorkbook workbook;
	private HSSFSheet sheet;
	private FileOutputStream fileOut;
	private List<LogicQueryNode> joinChildren;

	public QueryExecution() {
		executeInstance = new Execution();
		workbook = new HSSFWorkbook();
		sheet = workbook.createSheet("Query Output");
		rowIndex = 0;
//		createFile(true);
	}

	private String result;
	
	public void createFile(boolean appendTrue) throws FileNotFoundException{
		
			fileOut = new FileOutputStream("Output.xls", appendTrue);
	}
	public void saveData() throws IOException{
		workbook.write(fileOut);
		fileOut.flush();
		fileOut.close();
	}

	public void runQuery(String sqlQuery) {
		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);

		// sqlparser.sqltext = "SELECT e.last_name AS name,\n" + "
		// e.commission_pct comm,\n"
		// + " e.salary * 12 \"Annual Salary\"\n" + "FROM scott.employees AS
		// e\n"
		// + "WHERE e.salary > 1000\n" + "ORDER BY\n" + " e.first_name,\n" + "
		// e.last_name;";
		// sqlparser.sqltext = "SELECT DISTINCT course.grade, course2.grade FROM
		// course, course2 WHERE course.sid = course2.sid AND course.grade =
		// \"A\" AND course2.grade = \"A\"";
		sqlparser.sqltext = sqlQuery;

		int ret = sqlparser.parse();
		if (ret == 0) {
			for (int i = 0; i < sqlparser.sqlstatements.size(); i++) {
				analyzeStmt(sqlparser.sqlstatements.get(i));
				System.out.println("");
			}
		} else {
			System.out.println(sqlparser.getErrormessage());
			setResult(sqlparser.getErrormessage());
			return;
		}
	}

	protected void analyzeStmt(TCustomSqlStatement stmt) {
		TTableList tableList = new TTableList();
		TResultColumnList columnNameList = new TResultColumnList();
		TColumnDefinitionList columnDefList = new TColumnDefinitionList();
		ArrayList<String> table_names = new ArrayList<String>();
		ArrayList<Object> field_values = new ArrayList<Object>();
		ArrayList<String> field_names = new ArrayList<String>();
		ArrayList<FieldType> field_types = new ArrayList<FieldType>();
		// Execution executeInstance = new Execution();
		switch (stmt.sqlstatementtype) {
		case sstdroptable:
			/*
			 * TCreateTableSqlStatement deletestmt = (TCreateTableSqlStatement)
			 * stmt; tableList = deletestmt.getTables(); for (int i=0; i<
			 * tableList.size(); i++){
			 * table_names.add(tableList.getTable(i).toString()); }
			 * columnDefList = deletestmt.getColumnList(); for(int i =0;
			 * i<columnDefList.size(); i++){
			 * field_names.add(columnDefList.getColumn(i).getColumnName().
			 * toString());
			 * if(columnDefList.getColumn(i).getDatatype().toString().
			 * toUpperCase().equals("INT")){ field_types.add(FieldType.INT);
			 * }else
			 * if(columnDefList.getColumn(i).getDatatype().toString().equals(
			 * "STR20")){ field_types.add(FieldType.STR20); } else{ result =
			 * "Invalid datatype for "+
			 * columnDefList.getColumn(i).getColumnName().toString()+
			 * " : "+columnDefList.getColumn(i).getDatatype().toString();
			 * return; } } result =
			 * executeInstance.createTable(table_names.get(0),field_names,
			 * field_types); break;
			 */

			break;
		case sstselect:

			TSelectSqlStatement selectstmt = (TSelectSqlStatement) stmt;
			tableList = stmt.getTables();

			columnNameList = stmt.getResultColumnList();
			TSelectSqlStatement selectstmt2 = (TSelectSqlStatement) stmt;
			Node rootNode = new Node(selectstmt2.toString());
			List<String> projectionList = new ArrayList<String>();
			String projectionData = "";
			/*List<LogicQueryNode> */joinChildren = new ArrayList<>();
			// ArrayList<String> relationList = new ArrayList<String>();
			String conditionList = "";
			ArrayList<String> joinList = new ArrayList<String>();
			List<Object> outputList;
			selectFlag = false;
			joinFlag = false;
			Node selectListChild = new Node("SelectList");
			String temp = "";
			for (int i = 0; i < columnNameList.size(); i++) {
				temp = columnNameList.getElement(i).toString();
				selectListChild.addChild(temp);
				projectionList.add(temp);
				if (i == 0)
					projectionData = projectionData + temp;
				else
					projectionData = projectionData + "," + temp;
			}
			LogicQueryNode lqProjectionNode = new LogicQueryNode(projectionData, null);
			lqProjectionNode.setTag("projection");
			lqProjectionNode.setColumnList(projectionList);
			rootNode.addChild(selectListChild);
			Node fromChild = new Node("FromChild");
			LogicQueryNode joinChildNode;
			for (int i = 0; i < tableList.size(); i++) {
				temp = tableList.getTable(i).getName();
				Relation rel = new Relation();
				rel.setRelationName(temp);
				if (tableList.getTable(i).getAliasName().length() != 0) {
					temp = temp + ":" + tableList.getTable(i).getAliasName();
					rel.setAliasName(tableList.getTable(i).getAliasName());
				}
				fromChild.addChild(temp);
				joinChildNode = new LogicQueryNode(temp);
				joinChildNode.setTag("Relation");
				joinChildren.add(joinChildNode);
				executeInstance.getColumns(temp, rel.getColumnList(), rel.getColumnTypeList());
				relList.add(rel);
			}
			rootNode.addChild(fromChild);
			LogicQueryNode lqSelectionNode = new LogicQueryNode(conditionList);
			lqSelectionNode.setTag("Selection");
			List<Condition> innerConditionList = null;
			if (tableList.size() > 1) {
				// lqSelectionNode.addChild(lqJoinNode);
				// lqJoinNode.addChild(lqRelationNode);
				joinFlag = true;
			}

			List<String> operatorList = null;
			if (selectstmt.getWhereClause() != null) {
				Node whereChild = new Node("WhereChild");
				innerConditionList = new ArrayList<>();
				operatorList = new ArrayList<>();
				temp = selectstmt.getWhereClause().getCondition().toString();
				parseCondition(selectstmt.getWhereClause().getCondition(), innerConditionList, operatorList);
				// identifyColumnsInCondition(innerConditionList, relList);
				whereChild.addChild(temp);
				conditionList = temp;
				rootNode.addChild(whereChild);

				selectFlag = true;
			}
			// LogicQueryNode lqRelationNode = new LogicQueryNode(relationList);
			LogicQueryNode lqJoinNode = new LogicQueryNode("Joins");
			lqJoinNode.setTag("Join");
			// lqRelationNode.setTag("Relation");

			if (selectstmt.getGroupByClause() != null) {
				Node groupByChild = new Node("GroupByChild");
				for (int i = 0; i < selectstmt.getGroupByClause().getItems().size(); i++) {
					temp = selectstmt.getGroupByClause().getItems().getElement(i).toString();
					groupByChild.addChild(temp);
				}
				if (selectstmt.getGroupByClause().getHavingClause() != null) {
					Node havingChild = new Node("HavingChild");
					havingChild.addChild(selectstmt.getGroupByClause().getHavingClause().toString());
					rootNode.addChild(havingChild);
				}
				rootNode.addChild(groupByChild);
			}
			String orderBy = null;
			if (selectstmt.getOrderbyClause() != null) {
				Node orderByChild = new Node("OrderByChild");
				for (int i = 0; i < selectstmt.getOrderbyClause().getItems().size(); i++) {
					orderByChild.addChild(selectstmt.getOrderbyClause().getItems().getElement(i).toString());
					orderBy = selectstmt.getOrderbyClause().getItems().getElement(i).toString();
				}
				rootNode.addChild(orderByChild);
			}
			boolean or_flag = false;
			if (operatorList != null){
				for (int i = 0; i < operatorList.size(); i++) {
					if (operatorList.get(i).toUpperCase().equals("OR")) {
						or_flag = true;
					}
				}
			}
			LogicQueryNode orNode;
			if (selectFlag == true) {
				// lqProjectionNode.addChild(lqSelectionNode);
				if (joinFlag == true) {
					if (or_flag) {
						orNode = orConditionOperation(innerConditionList, operatorList, joinChildren);
						for (int i = 0; i < innerConditionList.size(); i++) {
							innerConditionList.get(i).setLeftOperandRelationName(joinChildren.get(0).getData());
							innerConditionList.get(i).setRightOperandRelationName(joinChildren.get(0).getData());
						}
						orNode = orConditionOperation(innerConditionList, operatorList, joinChildren);
						executeUnion(orNode);
						if (orderBy != null) {
							orNode.setData(executeInstance.performSimpleSorting(orNode.getData(), orderBy));
						}
						result = executeInstance.performProjection(orNode.getData(),
								(ArrayList<String>) projectionList);
						outputList = executeInstance.showtable(result);
						createExcel(outputList);
						writeDataToFile();
						// lqProjectionNode.addChild(orNode);
					} else {
						lqSelectionNode.addChild(lqJoinNode);
						lqJoinNode.setChildren(joinChildren);
						lqJoinNode = optimizeLogicalTree(lqJoinNode, innerConditionList, operatorList);
						lqJoinNode.setTag("Join");
						executeWithJoin(lqJoinNode);
						String relationName;
						while (lqJoinNode.getParent() != null) {
							relationName = lqJoinNode.getData();
							lqJoinNode = lqJoinNode.getParent().createCopy();
							if (lqJoinNode.getCondition() != null) {
								try {
//									outputList = executeInstance.showtable(relationName);
									updateRelationInCondition(lqJoinNode.getCondition());
									lqJoinNode.setData(
											executeInstance.applyCondition(relationName, lqJoinNode.getCondition()));
									updateRelationList(lqJoinNode.getData(),lqJoinNode.getCondition());
									System.out.println(lqJoinNode.getData());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							else{
								lqJoinNode.setData(relationName);
							}
						}
//						lqJoinNode = lqJoinNode.getChildren().get(0).createCopy();
						if (orderBy != null) {
							lqJoinNode.setData(executeInstance.performSimpleSorting(lqJoinNode.getData(), orderBy));
						}
						result = executeInstance.performProjection(lqJoinNode.getData(),
								(ArrayList<String>) projectionList);
						outputList = executeInstance.showtable(result);
						createExcel(outputList);
						writeDataToFile();
					}
				} else {
					lqSelectionNode.setChildren(joinChildren);
					if (or_flag) {
						for (int i = 0; i < innerConditionList.size(); i++) {
							innerConditionList.get(i).setLeftOperandRelationName(joinChildren.get(0).getData());
							innerConditionList.get(i).setRightOperandRelationName(joinChildren.get(0).getData());
						}
						orNode = orConditionOperation(innerConditionList, operatorList, joinChildren);
						executeUnion(orNode);
						if (orderBy != null) {
							orNode.setData(executeInstance.performSimpleSorting(orNode.getData(), orderBy));
						}
						result = executeInstance.performProjection(orNode.getData(),
								(ArrayList<String>) projectionList);

						outputList = executeInstance.showtable(result);
						createExcel(outputList);
						writeDataToFile();
						// System.out.println("Result = " + result);
					}
				}
			} else {
				lqProjectionNode.setChildren(joinChildren);
				String relationName = joinChildren.get(0).getData();
				List<String> updatedColumnList = new ArrayList<>();
				if (!lqProjectionNode.getColumnList().get(0).equals("*")) {
					for (int i = 0; i < lqProjectionNode.getColumnList().size(); i++) {
						updatedColumnList.add(relationName + "." + lqProjectionNode.getColumnList().get(i));
					}
					lqProjectionNode.setColumnList(updatedColumnList);
				}
				if (orderBy != null) {
					lqProjectionNode
							.setData(executeInstance.performSimpleSorting(joinChildren.get(0).getData(), orderBy));
				} else {
					lqProjectionNode.setData(joinChildren.get(0).getData());
				}
				result = executeInstance.performProjection(lqProjectionNode.getData(),
						(ArrayList<String>) lqProjectionNode.getColumnList());
				outputList = executeInstance.showtable(result);
				createExcel(outputList);
				writeDataToFile();
				System.out.println("Result = " + result);
			}

			showLogicTree(lqProjectionNode);

			// TExpression whereCondition =
			// selectstmt.getWhereClause().getCondition();
			// whereCondition.getSubQuery();
			// boolean AND_FLAG = false;
			// //
			// if(selectstmt.getWhereClause().getCondition().getExpressionType().equals(EExpressionType.logical_and_t)){
			// System.out.println(selectstmt.getWhereClause().getCondition().getLeftOperand());
			// System.out.println(selectstmt.getWhereClause().getCondition().getRightOperand());
			// System.out.println(selectstmt.getWhereClause().getCondition().getComparisonOperator());
			// return new Object[] { lqProjectionNode, rootNode };
			setResult(executeInstance.selectQuery(lqProjectionNode, rootNode));

			break;
		case sstupdate:
			TUpdateSqlStatement updatestmt = (TUpdateSqlStatement) stmt;
			tableList = updatestmt.getTables();
			for (int i = 0; i < tableList.size(); i++) {
				table_names.add(tableList.getTable(i).toString());
			}
			columnNameList = updatestmt.getResultColumnList();
			for (int i = 0; i < columnDefList.size(); i++) {
				field_names.add(columnDefList.getColumn(i).getColumnName().toString());
				if (columnDefList.getColumn(i).getDatatype().toString() == "INT") {
					field_types.add(FieldType.INT);
				} else if (columnDefList.getColumn(i).getDatatype().toString() == "STR20") {
					field_types.add(FieldType.STR20);
				}
			}
			break;
		case sstcreatetable:
			TCreateTableSqlStatement createstmt = (TCreateTableSqlStatement) stmt;
			tableList = createstmt.getTables();
			for (int i = 0; i < tableList.size(); i++) {
				table_names.add(tableList.getTable(i).toString());
			}
			columnDefList = createstmt.getColumnList();
			for (int i = 0; i < columnDefList.size(); i++) {
				field_names.add(columnDefList.getColumn(i).getColumnName().toString());
				if (columnDefList.getColumn(i).getDatatype().toString().toUpperCase().equals("INT")) {
					field_types.add(FieldType.INT);
				} else if (columnDefList.getColumn(i).getDatatype().toString().equals("STR20")) {
					field_types.add(FieldType.STR20);
				} else {
					setResult("Invalid datatype for " + columnDefList.getColumn(i).getColumnName().toString() + " : "
							+ columnDefList.getColumn(i).getDatatype().toString());
					return;
				}
			}
			setResult(executeInstance.createTable(table_names.get(0), field_names, field_types));
			break;
		case sstaltertable:
			break;
		case sstcreateview:
			break;
		case sstinsert:
			TInsertSqlStatement insertStmt = (TInsertSqlStatement) stmt;
			if (insertStmt.getTargetTable() != null) {
				System.out.println("Table name:" + insertStmt.getTargetTable().toString());
			} else {
				table_names.add(insertStmt.getTargetTable().toString());
			}

			System.out.println("insert value type:" + insertStmt.getValueType());

			if (insertStmt.getColumnList() != null) {
				System.out.println("columns:");
				for (int i = 0; i < insertStmt.getColumnList().size(); i++) {
					field_names.add(insertStmt.getColumnList().getObjectName(i).toString());
					System.out.println("\t" + insertStmt.getColumnList().getObjectName(i).toString());
				}
			}

			if (insertStmt.getValues() != null) {
				System.out.println("values:");
				for (int i = 0; i < insertStmt.getValues().size(); i++) {
					TMultiTarget mt = insertStmt.getValues().getMultiTarget(i);
					for (int j = 0; j < mt.getColumnList().size(); j++) {
						System.out.println("\t" + mt.getColumnList().getResultColumn(j).toString());
						field_values.add(mt.getColumnList().getResultColumn(j));
					}
				}
			}

			if (insertStmt.getSubQuery() != null) {
				// analyzeSelectStmt(insertStmt.getSubQuery());
			}
			setResult(
					executeInstance.insertIntoTable(insertStmt.getTargetTable().toString(), field_names, field_values));
			break;
		default:
			System.out.println(stmt.sqlstatementtype.toString());
		}
	}

	public void executeUnion(LogicQueryNode lqr) {
		for (int i = 0; i < lqr.getChildren().size() - 1; i++) {
			lqr.getChildren().get(i + 1).setData(executeInstance.performUnion(lqr.getChildren().get(i).getData(),
					lqr.getChildren().get(i).getData()));
			System.out.println("Union : " + lqr.getChildren().get(i + 1).getData());
		}
		lqr.setData(lqr.getChildren().get(lqr.getChildren().size() - 1).getData());
	}

	public void createExcel(List<Object> outputList) {
		HSSFRow row1 = sheet.createRow(rowIndex++);
		List<String> outputHeading = (List<String>) outputList.get(0);
		HSSFCell cellA1;
		for (int i = 0; i < outputHeading.size(); i++) {
			cellA1 = row1.createCell(i);
			cellA1.setCellValue(outputHeading.get(i));
		}
		List<Tuple> tupleList = (List<Tuple>) outputList.get(1);
		for (int i = 0; i < tupleList.size(); i++) {
			row1 = sheet.createRow(rowIndex++);
			for (int j = 0; j < tupleList.get(i).getNumOfFields(); j++) {
				cellA1 = row1.createCell(j);
				cellA1.setCellValue(tupleList.get(i).getField(j).toString());
			}
		}
	}

	public void writeDataToFile() {
	}

	public void showLogicTree(LogicQueryNode root) {
		System.out.print(root.getTag() + "==>");
		// for (String s : root.getData()) {
		System.out.print(root.getData() + "\n");

		// }
		if (root.getCondition() != null) {
			System.out.println("Condition : " + root.getCondition().toString());
		}
		if (root.getChildren() != null) {
			System.out.println("\n");
			for (LogicQueryNode n : root.getChildren()) {
				showLogicTree(n);
			}
		}
	}

	Condition checkCondition(TExpression condition) {
		if (condition.getComparisonOperator() == null) {
			return null;
		}
		Condition con = new Condition();
		con.setLeftOperand(condition.getLeftOperand().toString().replaceAll("\\s+", ""));
		con.setRightOperand(condition.getRightOperand().toString().replaceAll("\\s+", ""));

		String leftOp = con.getLeftOperand(), rightOp = con.getRightOperand();
		if (joinFlag) {
			con.checkOperands();
			if (leftOp.contains(".")) {
				if (rightOp.contains(".")) {
					if ((leftOp.substring(0, leftOp.indexOf(".") - 1))
							.equals(rightOp.substring(rightOp.indexOf(".") - 1))) {
						con.setSameRelationFlag(true);
					}
				}
			}
		} else {
			con.checkOperands(relList.get(0).getRelationName());
			con.setSameRelationFlag(true);
		}
		con.setOperator(condition.getComparisonOperator().toString());
		return con;
	}

	void parseCondition(TExpression condition, List<Condition> conList, List<String> operatorList) {
		// List<Condition> conList = new ArrayList<>();
		// List<String> operatorList = new ArrayList<>();
		Condition con;
		if ((con = checkCondition(condition)) != null) {
			conList.add(con);
		} else {
			if ((con = checkCondition(condition.getLeftOperand())) == null) {
				recursiveCheckCondition(condition.getLeftOperand(), conList, operatorList);
				operatorList.add(condition.getOperatorToken().toString());
			} else {
				con = checkCondition(condition.getLeftOperand());
				conList.add(con);
				operatorList.add(condition.getOperatorToken().toString());
			}

			if ((con = checkCondition(condition.getRightOperand())) == null) {
				recursiveCheckCondition(condition.getRightOperand(), conList, operatorList);
			} else {
				con = checkCondition(condition.getRightOperand());
				conList.add(con);
			}
		}
	}

	LogicQueryNode getLastNodeOfTree(LogicQueryNode lqr) {
		while (lqr.getChildren().size() != 0) {
			lqr = lqr.getChildren().get(0);
		}
		return lqr;
	}

	void recursiveCheckCondition(TExpression condition, List<Condition> conList, List<String> operatorList) {
		Condition con;

		while (condition.getLeftOperand() != null) {
			if ((con = checkCondition(condition.getLeftOperand())) != null) {
				conList.add(con);
				con = checkCondition(condition.getRightOperand());
				operatorList.add(condition.getOperatorToken().toString());
				conList.add(con);
				break;
			} else {
				operatorList.add(condition.getOperatorToken().toString());
				con = checkCondition(condition.getRightOperand());
				condition = condition.getLeftOperand();
				conList.add(con);
			}
			// condition = recursiveCheckCondition(condition, conList,
			// operatorList);
		}
		// conList.add(con);
		// return condition;

	}

	LogicQueryNode optimizeLogicalTree(LogicQueryNode lqr, List<Condition> condList, List<String> operationList) {
		if (operationList.size() != 0) {
			for (int i = 0; i < operationList.size(); i++) {
				if (operationList.get(i).toUpperCase().contains("AND")) {
					if (!condList.get(i).isProcessed()) {
						if (condList.get(i).isSameRelationFlag()) {
							pushDown(lqr, condList.get(i));
							condList.get(i).setProcessed(true);
						} else {
							lqr = pushUp(lqr, condList.get(i));
							// lqr.setCondition(condList.get(i));
						}
					}
					if (!condList.get(i + 1).isProcessed()) {
						if (condList.get(i + 1).isSameRelationFlag()) {
							pushDown(lqr, condList.get(i + 1));
							condList.get(i + 1).setProcessed(true);
						} else {
							lqr = pushUp(lqr, condList.get(i + 1));
							// lqr.setCondition(condList.get(i + 1));
						}
					}
				} else {
					lqr = pushUp(lqr, condList.get(i));
					if (!operationList.get(i + 1).toUpperCase().equals("AND") || (i == operationList.size() - 1)) {
						lqr = pushUp(lqr, condList.get(i + 1));
					}
				}
			}
		} else {
			if (condList.get(0).isSameRelationFlag()) {
				pushDown(lqr, condList.get(0));
			} else {
				lqr = pushUp(lqr, condList.get(0));
			}
		}
		return lqr;
	}

	void pushDown(LogicQueryNode lqr, Condition condition) {
		int index = Integer.MAX_VALUE;
		List<LogicQueryNode> lqrChildList = new ArrayList<>();
		LogicQueryNode tempNode;
		boolean pushDownFlag = false;
		for (int i = 0; i < lqr.getChildren().size(); i++) {
			if (lqr.getChildren().get(i).getData().equals(condition.getLeftOperandRelationName())) {
				index = i;
				pushDownFlag = true;
			} else {
				tempNode = lqr.getChildren().get(i).createCopy();
				tempNode.setTag("Condition");
				tempNode.setParent(lqr);
				lqrChildList.add(tempNode);
			}
			if (i == lqr.getChildren().size() - 1 && !pushDownFlag) {
				return;
			}
		}

		LogicQueryNode conditionNode = new LogicQueryNode(condition.getLeftOperandRelationName());
		LogicQueryNode childConditionNode = lqr.getChildren().get(index).createCopy();
		conditionNode.addChild(childConditionNode);
		childConditionNode.setParent(conditionNode);
		conditionNode.setCondition(condition);
		conditionNode.setTag("Condition");
		lqrChildList.add(conditionNode);
		lqr.setChildren(lqrChildList);
		conditionNode.setParent(lqr);

		// List<String> dataList = new ArrayList<>();
		// dataList.add(condition.toString());
		// List<LogicQueryNode> childList = lqr.getChildren();
		// LogicQueryNode conditionNode = new LogicQueryNode(dataList);
		// conditionNode.setData(dataList);
		// conditionNode.setCondition(condition);
		// conditionNode.setTag("Condition");
		// List<String> relList = new ArrayList<>();
		// relList.add(condition.getLeftOperandRelationName());
		// LogicQueryNode relationNode = new LogicQueryNode(relList);
		// relationNode.setTag("Relation");
		// conditionNode.addChild(relationNode);
		// // conditionNode.setChildren(lqr.getChildren());
		// // conditionNode.setParent(lqr);
		// // lqr.setChildren(new ArrayList<LogicQueryNode>());
		// lqr.addChild(conditionNode);
	}

	LogicQueryNode pushUp(LogicQueryNode lqr, Condition condition) {
		LogicQueryNode oldJoinNode = lqr.createCopy();
		// newJoinNode.setChildren(lqr.createCopy().getChildren());
		// newJoinNode.setTag(lqr.getTag());
		// newJoinNode.setParent(lqr.getParent());
		lqr.setChildren(new ArrayList<>());
		lqr.setTag("Condition");
		lqr.addChild(oldJoinNode);
		lqr.setCondition(condition);
		return oldJoinNode;
	}

	void execute(LogicQueryNode lqJoinNode) {
		LogicQueryNode lqLastNode = getLastNodeOfTree(lqJoinNode.getChildren().get(0));
		String relationName = null;
		Condition condition = null;
		while (!lqLastNode.getParent().getTag().equals("Join")) {
			relationName = lqLastNode.getData();
			lqLastNode = lqLastNode.getParent();
			condition = lqLastNode.getCondition();
			condition.setLeftOperandRelationName(relationName);
			condition.setRightOperandRelationName(relationName);
			try {
				lqLastNode.setData(executeInstance.applyCondition(relationName, condition));
				System.out.println(lqLastNode.getData());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	void executeWithJoin(LogicQueryNode lqJoinNode) {
		// lqSelectionNode.addChild(lqJoinNode);
		// lqJoinNode.setChildren(joinChildren);
		// optimizeLogicalTree(lqJoinNode, innerConditionList, operatorList);
		LogicQueryNode lqLastNode;
		String relationName = null;
		Condition condition = null;
		for (int i = 0; i < lqJoinNode.getChildren().size(); i++) {
			lqLastNode = getLastNodeOfTree(lqJoinNode.getChildren().get(i));

			while (!lqLastNode.getParent().getTag().equals("Join")) {
				relationName = lqLastNode.getData();
				lqLastNode = lqLastNode.getParent();
				if (lqLastNode.getCondition() != null) {
					condition = lqLastNode.getCondition();
					try {
						lqLastNode.setData(executeInstance.applyCondition(relationName, condition));
						updateRelationList(lqLastNode.getData(), condition);
						condition.setLeftOperandRelationName(relationName);
						condition.setRightOperandRelationName(relationName);
						System.out.println(lqLastNode.getData());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// lqLastNode = lqLastNode.getParent();
			}
		}
		if (joinFlag) {
			for (int j = 0; j < lqJoinNode.getChildren().size() - 1; j++) {
				lqJoinNode.setData(executeInstance.performCrossProduct(lqJoinNode.getChildren().get(j).getData(),
						lqJoinNode.getChildren().get(j + 1).getData()));
				System.out.println(lqJoinNode.getData());
			}
		} else {
			lqJoinNode.setData(lqJoinNode.getChildren().get(0).getData());
		}
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public LogicQueryNode orConditionOperation(List<Condition> innerConditionList, List<String> operatorList,
			List<LogicQueryNode> joinChildren) {
		int prevIndexOR = 0;
		List<Condition> orConditionList;
		List<String> orOperatorList;
		LogicQueryNode orNode = new LogicQueryNode("OR");
		for (int i = 0; i < operatorList.size(); i++) {
			if (operatorList.get(i).toUpperCase().equals("OR")) {
				orConditionList = new ArrayList<>();
				orOperatorList = new ArrayList<>();
				for (int k = prevIndexOR; k < i + 1; k++) {
					orConditionList.add(innerConditionList.get(k));
					if (!((k - prevIndexOR - 1) < 0)) {
						orOperatorList.add(operatorList.get(k - 1));
					}
				}
				LogicQueryNode orJoinChild = new LogicQueryNode("Join", orNode);
				orJoinChild.setTag("Join");
				orJoinChild.setChildren(joinChildren);
				optimizeLogicalTree(orJoinChild, orConditionList, orOperatorList);
				// if (joinFlag) {
				executeWithJoin(orJoinChild);
				// }
				System.out.println("1st table : " + orJoinChild.getData());
				orNode.addChild(orJoinChild);
				// Call merge
				prevIndexOR = i + 1;
			}
			if (i == operatorList.size() - 1) {
				orConditionList = new ArrayList<>();
				orOperatorList = new ArrayList<>();
				for (int k = prevIndexOR; k < i + 2; k++) {
					orConditionList.add(innerConditionList.get(k));
					if (!((k - prevIndexOR - 1) < 0)) {
						orOperatorList.add(operatorList.get(k - 1));
					}
				}
				LogicQueryNode or1JoinChild = new LogicQueryNode("Join");
				or1JoinChild.setTag("Join");
				or1JoinChild.setChildren(joinChildren);
				optimizeLogicalTree(or1JoinChild, orConditionList, orOperatorList);
				// if (joinFlag) {
				executeWithJoin(or1JoinChild);
				// }
				System.out.println("2nd table : " + or1JoinChild.getData());
				orNode.addChild(or1JoinChild);
			}
		}
		return orNode;
	}
	void updateRelationList(String relationName,Condition condition){
		for(int i=0; i<joinChildren.size(); i++){
			if(condition.getLeftOperandRelationName().equals(joinChildren.get(i).getData())){
				joinChildren.get(i).setTag(relationName);
			}
			if(condition.getRightOperandRelationName().equals(joinChildren.get(i).getData())){
				joinChildren.get(i).setTag(relationName);
			}
		}
	}
	
	void updateRelationInCondition(Condition condition){
		condition.updateRelationInCondition(joinChildren);
	}
}
