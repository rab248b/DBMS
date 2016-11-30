package edu.tamu.db.parser;

import java.util.ArrayList;
import java.util.List;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.nodes.TColumnDefinitionList;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.nodes.TTableList;
import gudusoft.gsqlparser.stmt.TAlterTableStatement;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import storageManager.FieldType;
import testManager.Execution;

public class QueryExecution {
//	public static void main(String[] args) {
//		QueryExecution qE = new QueryExecution();
//		qE.runQuery();
//	}

	private String result;
	public void runQuery(String sqlQuery) {
		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);

//		sqlparser.sqltext = "SELECT e.last_name      AS name,\n" + "       e.commission_pct comm,\n"
//				+ "       e.salary * 12    \"Annual Salary\"\n" + "FROM   scott.employees AS e\n"
//				+ "WHERE  e.salary > 1000\n" + "ORDER  BY\n" + "  e.first_name,\n" + "  e.last_name;";
		sqlparser.sqltext = sqlQuery;

		int ret = sqlparser.parse();
		if (ret == 0) {
			for (int i = 0; i < sqlparser.sqlstatements.size(); i++) {
				analyzeStmt(sqlparser.sqlstatements.get(i));
				System.out.println("");
			}
		} else {
			System.out.println(sqlparser.getErrormessage());
			result = sqlparser.getErrormessage();
			return;
		}
	}

	protected void analyzeStmt(TCustomSqlStatement stmt) {
		TTableList tableList = new TTableList();
		TResultColumnList columnNameList = new TResultColumnList();
		TColumnDefinitionList columnDefList = new TColumnDefinitionList();
		ArrayList<String> table_names=new ArrayList<String>();
		ArrayList<String> field_names=new ArrayList<String>();
	    ArrayList<FieldType> field_types=new ArrayList<FieldType>();
	    Execution executeInstance = new Execution();
		switch (stmt.sqlstatementtype) {
		case sstselect:
			tableList = stmt.getTables();
			columnNameList = stmt.getResultColumnList();
			break;
		case sstupdate:
			TUpdateSqlStatement updatestmt = (TUpdateSqlStatement) stmt;
			tableList = updatestmt.getTables();
			for (int i=0; i< tableList.size(); i++){
				table_names.add(tableList.getTable(i).toString());
			}
			columnNameList = updatestmt.getResultColumnList();
			for(int i =0; i<columnDefList.size(); i++){
				field_names.add(columnDefList.getColumn(i).getColumnName().toString());
				if(columnDefList.getColumn(i).getDatatype().toString() == "INT"){
					field_types.add(FieldType.INT);
				}else if(columnDefList.getColumn(i).getDatatype().toString() == "STR20"){
					field_types.add(FieldType.STR20);
				}
			}
			break;
		case sstcreatetable:
			TCreateTableSqlStatement createstmt = (TCreateTableSqlStatement) stmt;
			tableList = createstmt.getTables();
			for (int i=0; i< tableList.size(); i++){
				table_names.add(tableList.getTable(i).toString());
			}
			columnDefList = createstmt.getColumnList();
			for(int i =0; i<columnDefList.size(); i++){
				field_names.add(columnDefList.getColumn(i).getColumnName().toString());
				if(columnDefList.getColumn(i).getDatatype().toString().toUpperCase().equals("INT")){
					field_types.add(FieldType.INT);
				}else if(columnDefList.getColumn(i).getDatatype().toString().equals("STR20")){
					field_types.add(FieldType.STR20);
				}
				else{
					result = "Invalid datatype for "+ columnDefList.getColumn(i).getColumnName().toString()+ " : "+columnDefList.getColumn(i).getDatatype().toString();
					return;
				}
			}
			result = executeInstance.executeCreateTable(table_names.get(0),field_names,field_types);
			break;
		case sstaltertable:
			break;
		case sstcreateview:
			break;
		default:
			System.out.println(stmt.sqlstatementtype.toString());
		}
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
