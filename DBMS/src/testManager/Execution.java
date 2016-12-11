package testManager;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.omg.CORBA.portable.ApplicationException;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import storageManager.*;
import edu.tamu.db.ui.*;
import edu.tamu.db.queryplan.Condition;
import edu.tamu.db.queryplan.LogicQueryNode;
import edu.tamu.db.queryplan.Node;

public class Execution {
	String result = null;
	static MainMemory mem = new MainMemory();
	static Disk disk = new Disk();
	static SchemaManager schema_manager;
	private int index;

	public Execution(MainMemory memory, Disk diskSpace) {
		mem = memory;
		disk = diskSpace;
		System.out.print("The memory contains " + mem.getMemorySize() + " blocks" + "\n");
		System.out.print(mem + "\n" + "\n");
		schema_manager = new SchemaManager(mem, disk);
		disk.resetDiskIOs();
		disk.resetDiskTimer();
		index = 0;
	}

	public Execution() {
		mem = new MainMemory();
		disk = new Disk();
		System.out.print("The memory contains " + mem.getMemorySize() + " blocks" + "\n");
		System.out.print(mem + "\n" + "\n");
		schema_manager = new SchemaManager(mem, disk);
		createdTablesList = new ArrayList<>();
		// schema_manager=new SchemaManager(UILoader.mem,UILoader.disk);
	}

	private List<String> createdTablesList;

	/**
	 * 
	 * this method applies select condition
	 * 
	 * @param tableName
	 * @param condition
	 * @return
	 * @throws Exception
	 */

	public String applyCondition(String tableName, Condition condition) throws Exception {
		clearMemory();
		Relation relation_reference = schema_manager.getRelation(tableName);
		Schema schema = relation_reference.getSchema();
		String[] conditionList;
		String leftFieldName = "", rightFieldName = "";
		Object leftFieldValue = new Object(), rightFieldValue = new Object();

		String new_relation_name = "select_" + Integer.toString(index++) + "_" + tableName;
		createTable(new_relation_name, schema.getFieldNames(), schema.getFieldTypes());
		Relation new_relation_reference = schema_manager.getRelation(new_relation_name);
		int leftOperatorType = condition.getLeftOperandColumn();
		int rightOperatorType = condition.getRightOperandColumn();
		if (condition.isLeftOperandList()) {
			if (condition.getLeftOperandList()[0].contains("\"")) {
				throw new Exception("arithmetic operations only on int");
			}
			switch (leftOperatorType) {
			case 0:
				if (condition.getLeftOperandList()[1].equals("+")) {
					leftFieldValue = Integer.parseInt(condition.getLeftOperandList()[0])
							+ Integer.parseInt(condition.getLeftOperandList()[2]);
				} else if (condition.getLeftOperandList()[1].equals("-")) {
					leftFieldValue = Integer.parseInt(condition.getLeftOperandList()[0])
							- Integer.parseInt(condition.getLeftOperandList()[2]);
				} else if (condition.getLeftOperandList()[1].equals("*")) {
					leftFieldValue = Integer.parseInt(condition.getLeftOperandList()[0])
							* Integer.parseInt(condition.getLeftOperandList()[2]);
				}
				break;
			case 1:
				break;
			case 10:
				break;
			case 11:
				// it is a condition with two columns
				String leftFieldName1, leftFieldName2;
				String[] conditionColumnName1 = condition.getLeftOperandList()[0].toString().split("\\.");
				if (schema.getFieldNames().contains(conditionColumnName1[1])) {
					leftFieldName1 = conditionColumnName1[1];
				} else if (schema.getFieldNames().contains(conditionColumnName1[0] + "_" + conditionColumnName1[1])) {
					leftFieldName1 = conditionColumnName1[0] + "_" + conditionColumnName1[1];
				}

				String[] conditionColumnName2 = condition.getLeftOperandList()[2].toString().split("\\.");
				if (schema.getFieldNames().contains(conditionColumnName2[1])) {
					leftFieldName2 = conditionColumnName2[1];
				} else if (schema.getFieldNames().contains(conditionColumnName2[0] + "_" + conditionColumnName2[1])) {
					leftFieldName2 = conditionColumnName2[0] + "_" + conditionColumnName2[1];
				}

				break;
			}

		} else {
			if (condition.getLeftOperandColumn() == 0) {// it is a value
				leftFieldValue = condition.getLeftOperand();
				if (leftFieldValue.toString().contains("\""))
					leftFieldValue = leftFieldValue.toString();
				if (!condition.getOperator().equals("="))
					throw new Exception("Only = operation for String");
				else
					leftFieldValue = Integer.parseInt(leftFieldValue.toString());
			} else {// it is a column
				conditionList = condition.getLeftOperand().split("\\.");
				if (schema.getFieldNames().contains(conditionList[1])) {
					leftFieldName = conditionList[1];
				} else if (schema.getFieldNames().contains(conditionList[0] + "_" + conditionList[1])) {
					leftFieldName = conditionList[0] + "_" + conditionList[1];
				}
			}
		}
		if (condition.isRightOperandList()) {

			if (condition.getRightOperandList()[0].contains("\"")) {
				throw new Exception("arithmetic operations only on int");
			}
			switch (rightOperatorType) {
			case 0:
				if (condition.getRightOperandList()[1].equals("+")) {
					rightFieldValue = Integer.parseInt(condition.getRightOperandList()[0])
							+ Integer.parseInt(condition.getRightOperandList()[1]);
				} else if (condition.getRightOperandList()[1].equals("-")) {
					rightFieldValue = Integer.parseInt(condition.getRightOperandList()[0])
							- Integer.parseInt(condition.getRightOperandList()[1]);
				} else if (condition.getRightOperandList()[1].equals("*")) {
					rightFieldValue = Integer.parseInt(condition.getRightOperandList()[0])
							* Integer.parseInt(condition.getRightOperandList()[1]);
				}
				break;
			case 1:
				break;
			case 10:
				break;
			case 11:
				String rightFieldName1, rightFieldName2;
				String[] conditionColumnName1 = condition.getRightOperandList()[0].toString().split("\\.");
				if (schema.getFieldNames().contains(conditionColumnName1[1])) {
					rightFieldName1 = conditionColumnName1[1];
				} else if (schema.getFieldNames().contains(conditionColumnName1[0] + "_" + conditionColumnName1[1])) {
					rightFieldName1 = conditionColumnName1[0] + "_" + conditionColumnName1[1];
				}

				String[] conditionColumnName2 = condition.getRightOperandList()[2].toString().split("\\.");
				if (schema.getFieldNames().contains(conditionColumnName2[1])) {
					rightFieldName2 = conditionColumnName2[1];
				} else if (schema.getFieldNames().contains(conditionColumnName2[0] + "_" + conditionColumnName2[1])) {
					rightFieldName2 = conditionColumnName2[0] + "_" + conditionColumnName2[1];
				}
				break;
			}

		} else {
			if (condition.getRightOperandColumn() == 0) {// it is a value
				rightFieldValue = condition.getRightOperand();
				if (rightFieldValue.toString().contains("\"")) {
					rightFieldValue = rightFieldValue.toString();
					if (!condition.getOperator().equals("="))
						throw new Exception("Only = operation for String");
				} else
					rightFieldValue = Integer.parseInt(rightFieldValue.toString());
			} else {// it is a column
				conditionList = condition.getRightOperand().split("\\.");
				if (schema.getFieldNames().contains(conditionList[1])) {
					rightFieldName = conditionList[1];
				} else if (schema.getFieldNames().contains(conditionList[0] + "_" + conditionList[1])) {
					rightFieldName = conditionList[0] + "_" + conditionList[1];
				}
			}
		}

		if (condition.getLeftOperandColumn() == 0)
			throw new Exception("Left operand cannot be a value");

		if (!condition.isLeftOperandList() && !condition.isRightOperandList()) {
			if (condition.getRightOperandColumn() == 0) {// right operand is
															// value
				applyConditionWhenRightOperandValue(new_relation_reference, relation_reference, condition,
						leftFieldName, rightFieldValue);
			} else { // right operand is column
				applyConditionWhenRightOperandColumn(new_relation_reference, relation_reference, condition,
						leftFieldName, rightFieldName);
			}
		}

		if (condition.isLeftOperandList() && !condition.isRightOperandList()) {
			if (condition.getRightOperandColumn() == 0) {// right operand is
															// value

				switch (leftOperatorType) {
				case 0:
					result = "Invalid condition";
					break;
				case 1:
					break;
				case 10:
					break;
				case 11:
					// it is a condition with two columns
					String leftFieldName1 = "", leftFieldName2 = "";
					String[] conditionColumnName1 = condition.getLeftOperandList()[0].split("\\.");
					if (schema.getFieldNames().contains(conditionColumnName1[1])) {
						leftFieldName1 = conditionColumnName1[1];
					} else if (schema.getFieldNames()
							.contains(conditionColumnName1[0] + "_" + conditionColumnName1[1])) {
						leftFieldName1 = conditionColumnName1[0] + "_" + conditionColumnName1[1];
					}

					String[] conditionColumnName2 = condition.getLeftOperandList()[2].split("\\.");
					if (schema.getFieldNames().contains(conditionColumnName2[1])) {
						leftFieldName2 = conditionColumnName2[1];
					} else if (schema.getFieldNames()
							.contains(conditionColumnName2[0] + "_" + conditionColumnName2[1])) {
						leftFieldName2 = conditionColumnName2[0] + "_" + conditionColumnName2[1];
					}
					applyConditionWhenRightOperandValueLeftListColumn(new_relation_reference, relation_reference,
							condition, leftFieldName1, leftFieldName2, rightFieldValue);
					break;
				}

				// applyConditionWhenRightOperandValue( new_relation_reference,
				// relation_reference,condition, leftFieldName,
				// rightFieldValue);
			} else { // right operand is column
				switch (leftOperatorType) {
				case 0:
					result = "Invalid condition";
					break;
				case 1:
					break;
				case 10:
					break;
				case 11:
					// it is a condition with two columns
					String leftFieldName1 = "", leftFieldName2 = "";
					String[] conditionColumnName1 = condition.getLeftOperandList()[0].split("\\.");
					if (schema.getFieldNames().contains(conditionColumnName1[1])) {
						leftFieldName1 = conditionColumnName1[1];
					} else if (schema.getFieldNames()
							.contains(conditionColumnName1[0] + "_" + conditionColumnName1[1])) {
						leftFieldName1 = conditionColumnName1[0] + "_" + conditionColumnName1[1];
					}

					String[] conditionColumnName2 = condition.getLeftOperandList()[2].split("\\.");
					if (schema.getFieldNames().contains(conditionColumnName2[1])) {
						leftFieldName2 = conditionColumnName2[1];
					} else if (schema.getFieldNames()
							.contains(conditionColumnName2[0] + "_" + conditionColumnName2[1])) {
						leftFieldName2 = conditionColumnName2[0] + "_" + conditionColumnName2[1];
					}
					// applyConditionWhenRightOperandValueLeftListColumn(
					// new_relation_reference, relation_reference,condition,
					// leftFieldName1, leftFieldName2 , rightFieldValue);
					applyConditionWhenRightOperandColumnLeftListColumn(new_relation_reference, relation_reference,
							condition, leftFieldName1, leftFieldName2, rightFieldName);
					// applyConditionWhenRightOperandColumn(new_relation_reference,
					// relation_reference,condition, leftFieldName,
					// rightFieldName);
					break;
				}

			}
		}

//		System.out.println("Final table");
		System.out.print(new_relation_reference + "\n" + "\n");
		result = new_relation_name;
		createdTablesList.add(new_relation_name);
		return result;
	}

	public void applyConditionWhenRightOperandColumnLeftListColumn(Relation new_relation_reference,
			Relation relation_reference, Condition condition, String leftFieldName1, String leftFieldName2,
			String rightFieldName) {
		Schema schema = relation_reference.getSchema();
		int numberOfBlocksR1 = relation_reference.getNumOfBlocks();
		ArrayList<Tuple> tuplesOfR = new ArrayList<>();
		int memorySize = mem.getMemorySize();
		int j = numberOfBlocksR1;
		int k = 0;
		int numberOfBlocks = 0;
		do {
			if (j < memorySize - 1) {
				relation_reference.getBlocks(k, 0, j);
				numberOfBlocks = j;
				j = 0;
			} else {
				numberOfBlocks = memorySize - 1;
				relation_reference.getBlocks(k, 0, numberOfBlocks);
				k = k + memorySize - 1;
				j = j - memorySize + 1;
			}
			int LeftField = 0;
			tuplesOfR = mem.getTuples(0, numberOfBlocks);

			for (Tuple tempTupleR : tuplesOfR) {
				// Apply Condition
				if (condition.getLeftOperandList()[1].equals("+")) {
					LeftField = getLeftFieldAdd(condition, tempTupleR, leftFieldName1, leftFieldName2);
				} else if (condition.getLeftOperandList()[1].equals("-")) {
					LeftField = getLeftFieldSubtract(condition, tempTupleR, leftFieldName1, leftFieldName2);
				} else if (condition.getLeftOperandList()[1].equals("*")) {
					LeftField = getLeftFieldMultply(condition, tempTupleR, leftFieldName1, leftFieldName2);
				}

				if (schema.getFieldType(LeftField).toString().equals("INT")
						&& schema.getFieldType(rightFieldName).toString().equals("INT")) {
					if (condition.getOperator().equals("=")) {
						if (LeftField == Integer.parseInt(tempTupleR.getField(rightFieldName).toString())) {
							appendTupleToRelation(new_relation_reference, mem, mem.getMemorySize() - 1, tempTupleR);
						}
					} else if (condition.getOperator().equals(">")) {
						if (LeftField > Integer.parseInt(tempTupleR.getField(rightFieldName).toString())) {
							appendTupleToRelation(new_relation_reference, mem, mem.getMemorySize() - 1, tempTupleR);
						}
					} else if (condition.getOperator().equals("<")) {
						if (LeftField < Integer.parseInt(tempTupleR.getField(rightFieldName).toString())) {
							appendTupleToRelation(new_relation_reference, mem, mem.getMemorySize() - 1, tempTupleR);
						}
					}
				}

				// insertIntoTable(new_relation_name, schema.getFieldNames(),
				// field_values);
			}
		} while (j > 0);
	}

	public void applyConditionWhenRightOperandValueLeftListColumn(Relation new_relation_reference,
			Relation relation_reference, Condition condition, String leftFieldName1, String leftFieldName2,
			Object rightFieldValue) {

		int numberOfBlocksR1 = relation_reference.getNumOfBlocks();
		ArrayList<Tuple> tuplesOfR = new ArrayList<>();
		int memorySize = mem.getMemorySize();
		int j = numberOfBlocksR1;
		int k = 0;
		int numberOfBlocks = 0;
		do {
			if (j < memorySize - 1) {
				relation_reference.getBlocks(k, 0, j);
				numberOfBlocks = j;
				j = 0;
			} else {
				numberOfBlocks = memorySize - 1;
				relation_reference.getBlocks(k, 0, numberOfBlocks);
				k = k + memorySize - 1;
				j = j - memorySize + 1;
			}
			tuplesOfR = mem.getTuples(0, numberOfBlocks);

			int LeftField = 0;

			for (Tuple tempTupleR : tuplesOfR) {
				// Apply Condition
				if (condition.getLeftOperandList()[1].equals("+")) {
					LeftField = getLeftFieldAdd(condition, tempTupleR, leftFieldName1, leftFieldName2);
				} else if (condition.getLeftOperandList()[1].equals("-")) {
					LeftField = getLeftFieldSubtract(condition, tempTupleR, leftFieldName1, leftFieldName2);
				} else if (condition.getLeftOperandList()[1].equals("*")) {
					LeftField = getLeftFieldMultply(condition, tempTupleR, leftFieldName1, leftFieldName2);
				}

				if (condition.getOperator().equals("=")) {
					if (LeftField == Integer.parseInt(rightFieldValue.toString())) {
						appendTupleToRelation(new_relation_reference, mem, mem.getMemorySize() - 1, tempTupleR);
					}

				} else if (condition.getOperator().equals(">")) {
					if (LeftField > Integer.parseInt(rightFieldValue.toString())) {
						appendTupleToRelation(new_relation_reference, mem, mem.getMemorySize() - 1, tempTupleR);
					}
				} else if (condition.getOperator().equals("<")) {
					if (LeftField < Integer.parseInt(rightFieldValue.toString())) {
						appendTupleToRelation(new_relation_reference, mem, mem.getMemorySize() - 1, tempTupleR);
					}
				}
				// insertIntoTable(new_relation_name, schema.getFieldNames(),
				// field_values);
			}
		} while (j > 0);
	}

	public int getLeftFieldAdd(Condition condition, Tuple tempTupleR, String leftFieldName1, String leftFieldName2) {
		return Integer.parseInt(tempTupleR.getField(leftFieldName1).toString())
				+ Integer.parseInt(tempTupleR.getField(leftFieldName2).toString());
	}

	public int getLeftFieldSubtract(Condition condition, Tuple tempTupleR, String leftFieldName1,
			String leftFieldName2) {
		return Integer.parseInt(tempTupleR.getField(leftFieldName1).toString())
				- Integer.parseInt(tempTupleR.getField(leftFieldName2).toString());
	}

	public int getLeftFieldMultply(Condition condition, Tuple tempTupleR, String leftFieldName1,
			String leftFieldName2) {
		return Integer.parseInt(tempTupleR.getField(leftFieldName1).toString())
				* Integer.parseInt(tempTupleR.getField(leftFieldName2).toString());
	}

	public void applyConditionWhenRightOperandValue(Relation new_relation_reference, Relation relation_reference,
			Condition condition, String leftFieldName, Object rightFieldValue) {
		Schema schema = relation_reference.getSchema();
		int numberOfBlocksR1 = relation_reference.getNumOfBlocks();
		ArrayList<Tuple> tuplesOfR = new ArrayList<>();
		int memorySize = mem.getMemorySize();
		int j = numberOfBlocksR1;
		int k = 0;
		int numberOfBlocks = 0;
		do {
			if (j < memorySize - 1) {
				relation_reference.getBlocks(k, 0, j);
				numberOfBlocks = j;
				j = 0;
			} else {
				numberOfBlocks = memorySize - 1;
				relation_reference.getBlocks(k, 0, numberOfBlocks);
				k = k + memorySize - 1;
				j = j - memorySize + 1;
			}
			tuplesOfR = mem.getTuples(0, numberOfBlocks);

			for (Tuple tempTupleR : tuplesOfR) {
				// Apply Condition
				if (condition.getOperator().equals("=")) {
					if (schema.getFieldType(leftFieldName).toString().equals("INT")) {
						if (Integer.parseInt(tempTupleR.getField(leftFieldName).toString()) == Integer
								.parseInt(rightFieldValue.toString())) {
							appendTupleToRelation(new_relation_reference, mem, mem.getMemorySize() - 1, tempTupleR);
						}
					} else {
						if (tempTupleR.getField(leftFieldName).toString().equals(rightFieldValue.toString())) {
							appendTupleToRelation(new_relation_reference, mem, mem.getMemorySize() - 1, tempTupleR);
						}
					}
				} else if (condition.getOperator().equals(">")) {
					if (Integer.parseInt(tempTupleR.getField(leftFieldName).toString()) > Integer
							.parseInt(rightFieldValue.toString())) {
						appendTupleToRelation(new_relation_reference, mem, mem.getMemorySize() - 1, tempTupleR);
					}
				} else if (condition.getOperator().equals("<")) {
					if (Integer.parseInt(tempTupleR.getField(leftFieldName).toString()) < Integer
							.parseInt(rightFieldValue.toString())) {
						appendTupleToRelation(new_relation_reference, mem, mem.getMemorySize() - 1, tempTupleR);
					}
				}
				// insertIntoTable(new_relation_name, schema.getFieldNames(),
				// field_values);
			}
		} while (j > 0);
	}

	public void applyConditionWhenRightOperandColumn(Relation new_relation_reference, Relation relation_reference,
			Condition condition, String leftFieldName, String rightFieldName) {
		Schema schema = relation_reference.getSchema();
		ArrayList<FieldType> fieldTypes = new ArrayList<FieldType>();
		int numberOfBlocksR1 = relation_reference.getNumOfBlocks();
		ArrayList<Tuple> tuplesOfR = new ArrayList<>();
		int memorySize = mem.getMemorySize();
		int j = numberOfBlocksR1;
		int k = 0;
		int numberOfBlocks = 0;
		do {
			if (j < memorySize - 1) {
				relation_reference.getBlocks(k, 0, j);
				numberOfBlocks = j;
				j = 0;
			} else {
				numberOfBlocks = memorySize - 1;
				relation_reference.getBlocks(k, 0, numberOfBlocks);
				k = k + memorySize - 1;
				j = j - memorySize + 1;
			}
			tuplesOfR = mem.getTuples(0, numberOfBlocks);

			for (Tuple tempTupleR : tuplesOfR) {
				// Apply Condition

				if (schema.getFieldType(leftFieldName).toString().equals("STR20")
						&& schema.getFieldType(rightFieldName).toString().equals("STR20")) {
					if (tempTupleR.getField(leftFieldName).toString()
							.equals(tempTupleR.getField(rightFieldName).toString())) {
						appendTupleToRelation(new_relation_reference, mem, mem.getMemorySize() - 1, tempTupleR);
					}
				} else if (schema.getFieldType(leftFieldName).toString().equals("INT")
						&& schema.getFieldType(rightFieldName).toString().equals("INT")) {
					if (condition.getOperator().equals("=")) {
						if (Integer.parseInt(tempTupleR.getField(leftFieldName).toString()) == Integer
								.parseInt(tempTupleR.getField(rightFieldName).toString())) {
							appendTupleToRelation(new_relation_reference, mem, mem.getMemorySize() - 1, tempTupleR);
						}
					} else if (condition.getOperator().equals(">")) {
						if (Integer.parseInt(tempTupleR.getField(leftFieldName).toString()) > Integer
								.parseInt(tempTupleR.getField(rightFieldName).toString())) {
							appendTupleToRelation(new_relation_reference, mem, mem.getMemorySize() - 1, tempTupleR);
						}
					} else if (condition.getOperator().equals("<")) {
						if (Integer.parseInt(tempTupleR.getField(leftFieldName).toString()) < Integer
								.parseInt(tempTupleR.getField(rightFieldName).toString())) {
							appendTupleToRelation(new_relation_reference, mem, mem.getMemorySize() - 1, tempTupleR);
						}
					}
				}

				// insertIntoTable(new_relation_name, schema.getFieldNames(),
				// field_values);
			}
		} while (j > 0);
	}

	/**
	 * 
	 * This method performs projection on th given table . It selects columns
	 * listed in columnNames. If it is * it projects all columns
	 * 
	 * @param performProjection
	 * @param columnNames
	 * @return
	 */

	public String performProjection(String tableName, ArrayList<String> columnNames) throws Exception{
		clearMemory();
		Relation relation_reference = schema_manager.getRelation(tableName);
		ArrayList<FieldType> fieldTypes = new ArrayList<FieldType>();
		ArrayList<Object> field_values = new ArrayList<>();
		Schema schema = relation_reference.getSchema();
		int numberOfBlocksR1 = relation_reference.getNumOfBlocks();
		ArrayList<Tuple> tuplesOfR = new ArrayList<>();
		int memorySize = mem.getMemorySize();
		int j = numberOfBlocksR1;
		int k = 0;
		int numberOfBlocks = 0;
		if (columnNames.get(0).equals("*")) {
			columnNames = new ArrayList<>();
			columnNames.addAll(schema.getFieldNames());
			fieldTypes = schema.getFieldTypes();
		} else {
			for (String tempFieldName : columnNames) {
				fieldTypes.add(schema.getFieldType(tempFieldName));
			}
		}
		String new_relation_name = "proj_" + Integer.toString(index++) + "_" + tableName;
		createTable(new_relation_name, columnNames, fieldTypes);
		do {
			if (j < memorySize - 1) {
				relation_reference.getBlocks(k, 0, j);
				numberOfBlocks = j;
				j = 0;
			} else {
				numberOfBlocks = memorySize - 1;
				relation_reference.getBlocks(k, 0, numberOfBlocks);
				k = k + memorySize - 1;
				j = j - memorySize + 1;
			}
			tuplesOfR = mem.getTuples(0, numberOfBlocks);
			for (Tuple tempTupleR : tuplesOfR) {
				for (String fieldnameR : columnNames) {
					field_values.add(tempTupleR.getField(fieldnameR));
				}
				insertIntoTable(new_relation_name, columnNames, field_values);
				field_values = new ArrayList<>();
			}
		} while (j > 0);
		createdTablesList.add(new_relation_name);
		return result = new_relation_name;
	}

	public String performSorting(String tableName, String columnNameWithTable) {
		clearMemory();
		String columnName = "";
		Relation relation_reference = schema_manager.getRelation(tableName);
		Schema schema = relation_reference.getSchema();
		String[] columnNameAttributes = columnNameWithTable.split("\\.");
		if (schema.getFieldNames().contains(columnNameAttributes[1])) {
			columnName = columnNameAttributes[1];
		} else if (schema.getFieldNames().contains(columnNameAttributes[0] + "_" + columnNameAttributes[1])) {
			columnName = columnNameAttributes[0] + "_" + columnNameAttributes[1];
		}
		int numberOfBlocksR1 = relation_reference.getNumOfBlocks();
		ArrayList<Tuple> tuplesOfR = new ArrayList<>();
		int memorySize = mem.getMemorySize();
		int j = numberOfBlocksR1;
		int k = 0;
		int numberOfBlocks = 0;
		String new_relation_name = "sort_" + tableName;
		createTable(new_relation_name, schema.getFieldNames(), schema.getFieldTypes());
		String sublist_relation_name = "";
		int h = 0;
		do {
			if (j < memorySize - 1) {
				relation_reference.getBlocks(k, 0, j);
				numberOfBlocks = j;
				j = 0;
			} else {
				numberOfBlocks = memorySize - 1;
				relation_reference.getBlocks(k, 0, numberOfBlocks);
				k = k + memorySize - 1;
				j = j - memorySize + 1;
			}
			tuplesOfR = mem.getTuples(0, numberOfBlocks);
			sublist_relation_name = "sort" + h + "_" + tableName;
			makeSortedSublist(tableName, columnName, sublist_relation_name, tuplesOfR);
			h++;// make a sublist with name sorti_tableName

		} while (j > 0);

		// sort h-1 sublists
		createdTablesList.add(new_relation_name);
		return result = new_relation_name;

	}

	public void makeSortedSublist(String tableName, String columnName, String sublist_relation_name,
			ArrayList<Tuple> tuplesOfR) {
		Schema schema = schema_manager.getSchema(tableName);
		Relation new_relation_reference = schema_manager.createRelation(sublist_relation_name, schema);
		int currValue = 0;
		HashMap<Integer, Tuple> mapOfSortColumnTuple = new HashMap<>();
		for (Tuple tempTupleR : tuplesOfR) {

			if (schema.getFieldType(columnName).toString().equals("INT")) {
				currValue = Integer.parseInt(tempTupleR.getField(columnName).toString());
				mapOfSortColumnTuple.put(currValue, tempTupleR);
			} else if (schema.getFieldType(columnName).toString().equals("STR20")) {
			}
		}
		SortedMap<Integer, Tuple> sortedMap = new TreeMap<>(mapOfSortColumnTuple);
		Iterator it = sortedMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			// System.out.println(pair.getKey() + " = " + pair.getValue());

			appendTupleToRelation(new_relation_reference, mem, mem.getMemorySize() - 1, (Tuple) pair.getValue());
			it.remove();
		}

//		System.out.println("sorted sublist " + sublist_relation_name + "\n" + new_relation_reference);
	}

	public String performCrossProduct(String table1Name, String table2Name) {
		clearMemory();
		Relation tableR;// table in memory
		Relation tableS;// table fetched one by one
		Block block_referenceR;
		Block block_referenceS;
		ArrayList<Tuple> tuplesOfR = new ArrayList<>();
		ArrayList<Tuple> tuplesOfS = new ArrayList<>();
		Relation relation_reference1 = schema_manager.getRelation(table1Name);
		Relation relation_reference2 = schema_manager.getRelation(table2Name);
		int numberOfBlocksR1 = relation_reference1.getNumOfBlocks();
		int numberOFBloksR2 = relation_reference2.getNumOfBlocks();
		int memorySize = mem.getMemorySize();
		ArrayList<Object> field_values = new ArrayList<>();
		if (numberOfBlocksR1 > numberOFBloksR2) {
			tableS = relation_reference1;
			tableR = relation_reference2;
		} else {
			tableS = relation_reference2;
			tableR = relation_reference1;
		}
		Schema schemaR = tableR.getSchema();
		Schema schemaS = tableS.getSchema();
		ArrayList<String> field_names = new ArrayList<>();

		for (String fieldName : schemaR.getFieldNames()) {
			field_names.add(tableR.getRelationName() + "_" + fieldName);
		}
		for (String fieldName : schemaS.getFieldNames()) {
			field_names.add(tableS.getRelationName() + "_" + fieldName);
		}
		ArrayList<FieldType> fieldTypes = schemaR.getFieldTypes();
		fieldTypes.addAll(schemaS.getFieldTypes());
		String newRelationName = tableR.getRelationName() + "_" + tableS.getRelationName();
		createTable(newRelationName, field_names, fieldTypes);
		Relation new_relation_reference = schema_manager.getRelation(newRelationName);
		if (numberOfBlocksR1 > memorySize - 2 && numberOFBloksR2 > memorySize - 2) {
			// large tables

			int j = tableR.getNumOfBlocks();
			int k = 0;
			int numberOfBlocks = memorySize - 2;
			do {
				if (j < memorySize - 2) {
					// tableR.getBlocks(k, 0, j);
					tableR.getBlocks(k, 1, j); // get as many blocks of table R
												// and place in memory, starting
												// at index 1
					numberOfBlocks = j;
					j = 0;
				} else {
					tableR.getBlocks(k, 1, numberOfBlocks);
					k = k + memorySize - 2;
					j = j - memorySize + 2;
				}
				tuplesOfR = mem.getTuples(1, numberOfBlocks);

//				System.out.println("All Block  of R (smaller table)in memory in this iteration ");
//				System.out.print(mem + "\n");

				for (int m = 0; m < tableS.getNumOfBlocks(); m++) {
					tableS.getBlock(m, 0); // copy block of S in memory index 0
//					System.out.println(" 1 Block  of S (bigger table)in memory");
//					System.out.print(mem + "\n");
					block_referenceS = mem.getBlock(0);
					tuplesOfS = block_referenceS.getTuples();
					tuplesOfR = mem.getTuples(1, numberOfBlocks);
					for (Tuple tempTupleS : tuplesOfS) {
						// for (int k = 1; k < tableR.getNumOfBlocks(); k++) {
						// block_referenceR = mem.getBlock(k);
						// tuplesOfR = mem.getTuples(1,
						// tableR.getNumOfBlocks());

						for (Tuple tempTupleR : tuplesOfR) {
							for (String fieldnameR : schemaR.getFieldNames()) {
								field_values.add(tempTupleR.getField(fieldnameR));
							}
							for (String fieldnameS : schemaS.getFieldNames()) {
								field_values.add(tempTupleS.getField(fieldnameS));
							}
							insertIntoTable(newRelationName, field_names, field_values);
							field_values = new ArrayList<>();
						}
					}
				}
			} while (j > 0);

		} else {

			// Tuple newTuple = new_relation_reference.createTuple();
			tableR.getBlocks(0, 1, tableR.getNumOfBlocks()); // get all the
																// blocks of
																// table R and
																// place in
																// memory,
																// starting at
																// index 1
//			System.out.println("All Block  of R (smaller table)in memory");
//			System.out.print(mem + "\n");
			for (int j = 0; j < tableS.getNumOfBlocks(); j++) {
				tableS.getBlock(j, 0); // copy block of S in memory index 0
				block_referenceS = mem.getBlock(0);
				tuplesOfS = block_referenceS.getTuples();
				tuplesOfR = mem.getTuples(1, tableR.getNumOfBlocks());
				for (Tuple tempTupleS : tuplesOfS) {
					// for (int k = 1; k < tableR.getNumOfBlocks(); k++) {
					// block_referenceR = mem.getBlock(k);

					for (Tuple tempTupleR : tuplesOfR) {
						for (String fieldnameR : schemaR.getFieldNames()) {
							field_values.add(tempTupleR.getField(fieldnameR));
						}
						for (String fieldnameS : schemaS.getFieldNames()) {
							field_values.add(tempTupleS.getField(fieldnameS));
						}
						insertIntoTable(newRelationName, field_names, field_values);
						field_values = new ArrayList<>();
					}
				}
			}
		}
		createdTablesList.add(newRelationName);
		return result = newRelationName;
	}

	public void clearMemory() {
		for (int k = 0; k < mem.getMemorySize(); k++) {
			Block block = mem.getBlock(k);
			block.clear();
		}
	}

	public void preprocessSelect(LogicQueryNode LogicalQueryTree) {
		/*
		 * try{
		 * 
		 * for()
		 * 
		 * for(){ Relation relation_reference=
		 * schema_manager.getRelation(relationName); }
		 * 
		 * }catch{
		 * 
		 * }
		 */
	}

	public String createTable(String relationName, ArrayList<String> field_names, ArrayList<FieldType> field_types) {
		try {
			Schema schema = new Schema(field_names, field_types);
			Relation relation_reference = schema_manager.createRelation(relationName, schema);
			result = relationName;
			// result = "Relation "+ relationName+ " created successfully.";
		} catch (Exception e) {
			result = "Error while creating relation";
			e.printStackTrace();
		}
		return result;
	}

	public String insertIntoTable(String relationName, ArrayList<String> field_names, ArrayList<Object> field_values) {
		result = "";
		Relation relation_reference = schema_manager.getRelation(relationName);
		Schema schema = relation_reference.getSchema();
		Tuple tuple = relation_reference.createTuple();
		for (int j = 0; j < field_names.size(); j++) {
			if (schema.getFieldType(field_names.get(j)).toString().equals("STR20")) {
				tuple.setField(field_names.get(j), field_values.get(j).toString());
			} else if (schema.getFieldType(field_names.get(j)).toString().equals("INT")) {
				tuple.setField(field_names.get(j), (Integer.parseInt(field_values.get(j).toString())));
			}
		}
		Block block_reference = mem.getBlock(mem.getMemorySize() - 1); // access
																		// to
																		// memory
																		// block
																		// 0
		block_reference.clear(); // clear the block
		block_reference.setTuple(0, tuple);
		appendTupleToRelation(relation_reference, mem, mem.getMemorySize() - 1, tuple);
		result = "Tuple inserted successfully in " + relationName;
		return result;
	}
	
	public String insertIntoTable(String relationName, String conditionTableName){
		result ="";
		Relation relation_reference = schema_manager.getRelation(relationName);
		Schema schema = relation_reference.getSchema();
		Tuple tuple;
		int memorySize = mem.getMemorySize();
		int numberOfBlocks = 0;
		int k = 0;
		Relation condition_relation = schema_manager.getRelation(conditionTableName);
		Schema condition_schema = condition_relation.getSchema();
		int numberOfBlocks1 = condition_relation.getNumOfBlocks();
		ArrayList<String> columnNames = condition_schema.getFieldNames();
		ArrayList<Object> field_values = new ArrayList<>();
		int j = numberOfBlocks1;
		ArrayList<Tuple> tuplesOfR = new ArrayList<>();
//		if(condition_relation.getNumOfTuples()!=0){
//			for(int j =0; j < condition_relation.getNumOfTuples(); j++){
//				tuple = relation_reference.createTuple();
		if (j < memorySize - 1) {
			condition_relation.getBlocks(k, 0, j);
			numberOfBlocks = j;
			j = 0;
		} else {
			numberOfBlocks = memorySize - 1;
			condition_relation.getBlocks(k, 0, numberOfBlocks);
			k = k + memorySize - 1;
			j = j - memorySize + 1;
		}
		tuplesOfR = mem.getTuples(0, numberOfBlocks);
		for (Tuple tempTupleR : tuplesOfR) {
//			tuplesOfTable1.add(tempTupleR);
			for (String fieldnameR : columnNames) {
				field_values.add(tempTupleR.getField(fieldnameR));
			}
			insertIntoTable(relationName, columnNames, field_values);
			field_values = new ArrayList<>();
		}
//				tuple = condition_schema
//			}
			
//			for (int j = 0; j < field_names.size(); j++) {
//				if (schema.getFieldType(field_names.get(j)).toString().equals("STR20")) {
//					tuple.setField(field_names.get(j), field_values.get(j).toString());
//				} else if (schema.getFieldType(field_names.get(j)).toString().equals("INT")) {
//					tuple.setField(field_names.get(j), (Integer.parseInt(field_values.get(j).toString())));
//				}
//			}
//			Block block_reference = mem.getBlock(mem.getMemorySize() - 1); // access
//																			// to
//																			// memory
//																			// block
//																			// 0
//			block_reference.clear(); // clear the block
//			block_reference.setTuple(0, tuple);
//			appendTupleToRelation(relation_reference, mem, mem.getMemorySize() - 1, tuple);
			
//		}
		result = "Tuple inserted successfully in " + relationName;
		return result;
	}

	// An example procedure of appending a tuple to the end of a relation
	// using memory block "memory_block_index" as output buffer
	private static void appendTupleToRelation(Relation relation_reference, MainMemory mem, int memory_block_index,
			Tuple tuple) {
		Block block_reference;
		if (relation_reference.getNumOfBlocks() == 0) {
			block_reference = mem.getBlock(memory_block_index);
			block_reference.clear(); // clear the block
			block_reference.appendTuple(tuple); // append the tuple
			relation_reference.setBlock(relation_reference.getNumOfBlocks(), memory_block_index);
		} else {
			relation_reference.getBlock(relation_reference.getNumOfBlocks() - 1, memory_block_index);
			block_reference = mem.getBlock(memory_block_index);

			if (block_reference.isFull()) {
				block_reference.clear(); // clear the block
				block_reference.appendTuple(tuple); // append the tuple
				relation_reference.setBlock(relation_reference.getNumOfBlocks(), memory_block_index); // write
																										// back
																										// to
																										// the
																										// relation
			} else {
				block_reference.appendTuple(tuple); // append the tuple
				relation_reference.setBlock(relation_reference.getNumOfBlocks() - 1, memory_block_index); 
			}
		}
	}

	public String droptable(ArrayList<String> tableList) {
		result = "";

		for (String temp : tableList) {
			if (null == schema_manager.getRelation(temp)) {
				result = result + "Table temp does not exist ";
			} else {
				Relation relation_reference = schema_manager.getRelation(temp);
				schema_manager.deleteRelation(temp);
//				if (null == schema_manager.getRelation(temp))
				result = "Relation " + temp + " deleted successfully";
			}
		}
		return result;
	}

	// Get the list of all columns in the relation
	public void getColumns(String relationName, List<String> columnList, List<FieldType> columnTypeList) throws Exception{
		Relation relation_reference = schema_manager.getRelation(relationName);
		Schema schema = relation_reference.getSchema();
		columnList = schema.getFieldNames();
		columnTypeList = schema.getFieldTypes();
	}

	public String performSimpleSorting(String tableName, String columnNameWithTable) {
		clearMemory();
		String columnName = "";

		Relation relation_reference = schema_manager.getRelation(tableName);
		Schema schema = relation_reference.getSchema();
		int numberOfBlocksR1 = relation_reference.getNumOfBlocks();
		ArrayList<Tuple> tuplesOfR = new ArrayList<>();
		ArrayList<Tuple> tuplesinNewTable = new ArrayList<>();
		int memorySize = mem.getMemorySize();
		int j = numberOfBlocksR1;
		int k = 0;
		int numberOfBlocks = 0;
		String new_relation_name = "sort_" + tableName;
		createTable(new_relation_name, schema.getFieldNames(), schema.getFieldTypes());
		Relation new_relation_reference = schema_manager.getRelation(new_relation_name);

		String[] columnNameAttributes = columnNameWithTable.split("\\.");
		if (schema.getFieldNames().contains(columnNameAttributes[1])) {
			columnName = columnNameAttributes[1];
		} else if (schema.getFieldNames().contains(columnNameAttributes[0] + "_" + columnNameAttributes[1])) {
			columnName = columnNameAttributes[0] + "_" + columnNameAttributes[1];
		}
		do {
			if (j < memorySize - 1) {
				relation_reference.getBlocks(k, 0, j);
				numberOfBlocks = j;
				j = 0;
			} else {
				numberOfBlocks = memorySize - 1;
				relation_reference.getBlocks(k, 0, numberOfBlocks);
				k = k + memorySize - 1;
				j = j - memorySize + 1;
			}
			tuplesOfR = mem.getTuples(0, numberOfBlocks);
			for (Tuple tempTupleR : tuplesOfR) {
				tuplesinNewTable.add(tempTupleR);
			}
		} while (j > 0);

		Tuple tempTuple;
		if (schema.getFieldType(columnName).toString().equals("INT")) {

			for (int l = 1; l < tuplesinNewTable.size(); l++) { //
				int p = l;
				// currentValue =
				// Integer.parseInt(tuplesinNewTable.get(p).getField(columnName).toString());
				// previousValue =
				// Integer.parseInt(tuplesinNewTable.get(p-1).getField(columnName).toString());
				while (p > 0
						&& (Integer.parseInt(tuplesinNewTable.get(p - 1).getField(columnName).toString())) > (Integer
								.parseInt(tuplesinNewTable.get(p).getField(columnName).toString()))) {
					tempTuple = tuplesinNewTable.get(p);
					tuplesinNewTable.set(p, tuplesinNewTable.get(p - 1));
					tuplesinNewTable.set(p - 1, tempTuple);
					p--;
				}
			}
		} else if (schema.getFieldType(columnName).toString().equals("STR20")) {
		}
		for (Tuple temp : tuplesinNewTable) {
			appendTupleToRelation(new_relation_reference, mem, mem.getMemorySize() - 1, temp);
		}
		createdTablesList.add(new_relation_name);
		return result = new_relation_name;
	}

	/**
	 * This method returns the ArrayList of Objects where first Object in the
	 * list is columnList and second Object is List of Tuples
	 * 
	 * @param tableName
	 * @return
	 */
	public ArrayList<Object> showtable(String tableName) {
		clearMemory();
		ArrayList<Object> resultTable = new ArrayList<>();
		if (null == schema_manager.getRelation(tableName)) {
			result = "Table temp does not exist ";
		} else {
			Relation relation_reference = schema_manager.getRelation(tableName);
			Schema schema = relation_reference.getSchema();
			int numberOfBlocksR1 = relation_reference.getNumOfBlocks();
			ArrayList<Tuple> tuplesOfR = new ArrayList<>();
			int memorySize = mem.getMemorySize();
			int j = numberOfBlocksR1;
			int k = 0;
			int numberOfBlocks = 0;
			ArrayList<Tuple> tableRows = new ArrayList<>();
			ArrayList<String> columnNames = schema.getFieldNames();
			do {
				if (j < memorySize - 1) {
					relation_reference.getBlocks(k, 0, j);
					numberOfBlocks = j;
					j = 0;
				} else {
					numberOfBlocks = memorySize - 1;
					relation_reference.getBlocks(k, 0, numberOfBlocks);
					k = k + memorySize - 1;
					j = j - memorySize + 1;
				}
				tuplesOfR = mem.getTuples(0, numberOfBlocks);
				for (Tuple tempTupleR : tuplesOfR) {
					tableRows.add(tempTupleR);
				}
			} while (j > 0);

			resultTable.add(columnNames);
			resultTable.add(tableRows);
		}
		return resultTable;

	}

	/**
	 * This method performs bag union between table1 and table 2
	 * 
	 * @param table1
	 * @param table2
	 * @return
	 */
	public String performUnion(String table1, String table2) {
		clearMemory();
		Relation relation_reference1 = schema_manager.getRelation(table1);
		Relation relation_reference2 = schema_manager.getRelation(table2);
		Schema schema1 = relation_reference1.getSchema();
		Schema schema2 = relation_reference2.getSchema();
		if (!schema1.getFieldNames().equals(schema2.getFieldNames())) {
			System.out.println("Table column mismatch union cannot be performed");
		}
		int numberOfBlocks1 = relation_reference1.getNumOfBlocks();
		int numberOfBlocks2 = relation_reference2.getNumOfBlocks();
		ArrayList<Tuple> tuplesOfR = new ArrayList<>();
		ArrayList<Tuple> tuplesOfS = new ArrayList<>();
		int memorySize = mem.getMemorySize();
		int j = numberOfBlocks1;
		int k = 0;
		int numberOfBlocks = 0;
		ArrayList<String> columnNames = schema1.getFieldNames();
		ArrayList<FieldType> fieldTypes = schema1.getFieldTypes();
		ArrayList<Object> field_values = new ArrayList<>();
		ArrayList<Tuple> tuplesOfTable1 = new ArrayList<>();
		String new_relation_name = "union_" + Integer.toString(index++) + "_" + table1 + "_" + table2;

		createTable(new_relation_name, columnNames, fieldTypes);
		Relation newRelationReference = schema_manager.getRelation(new_relation_name);
		do {
			if (j < memorySize - 1) {
				relation_reference1.getBlocks(k, 0, j);
				numberOfBlocks = j;
				j = 0;
			} else {
				numberOfBlocks = memorySize - 1;
				relation_reference1.getBlocks(k, 0, numberOfBlocks);
				k = k + memorySize - 1;
				j = j - memorySize + 1;
			}
			tuplesOfR = mem.getTuples(0, numberOfBlocks);
			for (Tuple tempTupleR : tuplesOfR) {
				tuplesOfTable1.add(tempTupleR);
				for (String fieldnameR : columnNames) {
					field_values.add(tempTupleR.getField(fieldnameR));
				}
				insertIntoTable(new_relation_name, columnNames, field_values);
				field_values = new ArrayList<>();
			}
		} while (j > 0);
		j = numberOfBlocks2;
		k = 0;
		numberOfBlocks = 0;

		boolean alreadyPresent = false;
		do {
			if (j < memorySize - 1) {
				relation_reference2.getBlocks(k, 0, j);
				numberOfBlocks = j;
				j = 0;
			} else {
				numberOfBlocks = memorySize - 1;
				relation_reference2.getBlocks(k, 0, numberOfBlocks);
				k = k + memorySize - 1;
				j = j - memorySize + 1;
			}
			tuplesOfS = mem.getTuples(0, numberOfBlocks);
			for (Tuple tempTupleS : tuplesOfS) {
				alreadyPresent = false;
				for (Tuple tempTuple : tuplesOfTable1) {
					if (tempTuple.toString().equals(tempTupleS.toString())) {
						alreadyPresent = true;
						tuplesOfTable1.remove(tempTuple);
						break;
					}
				}

				if (!alreadyPresent) {
					for (String fieldnameS : columnNames) {
						field_values.add(tempTupleS.getField(fieldnameS));
					}
					insertIntoTable(new_relation_name, columnNames, field_values);
					field_values = new ArrayList<>();
				}
			}
		} while (j > 0);

		// System.out.println("Final table contains
		// "+newRelationReference.getNumOfTuples()+" number of tuples");
		createdTablesList.add(new_relation_name);
		return result = new_relation_name;
	}

	public String removeDuplicates(String tableName) {
		clearMemory();
		String newtableName = "unique_" + tableName;
		Relation relation_reference1 = schema_manager.getRelation(tableName);
		Schema schema1 = relation_reference1.getSchema();
		createTable(newtableName, schema1.getFieldNames(), schema1.getFieldTypes());
		performSimpleRemoveDuplicate(tableName, newtableName);
		createdTablesList.add(newtableName);
		return result = newtableName;
	}

	public void performSimpleRemoveDuplicate(String tableName, String newTable) {
		Relation relation_reference1 = schema_manager.getRelation(tableName);
		Relation new_relation_reference = schema_manager.getRelation(newTable);
		Schema schema = schema_manager.getSchema(tableName);
		int memorySize = mem.getMemorySize();
		int j = relation_reference1.getNumOfBlocks();
		int k = 0;
		int numberOfBlocks = 0;
		boolean alreadyPresent;
		ArrayList<Tuple> tuplesOfS = new ArrayList<>();
		ArrayList<Tuple> tuplesInNewTable = new ArrayList<>();
		HashMap<Integer, Tuple> mapOfSortColumnTuple = new HashMap<>();
		HashMap<String, Tuple> mapOfStringColumnTuple = new HashMap<>();
		do {
			if (j < memorySize - 1) {
				relation_reference1.getBlocks(k, 0, j);
				numberOfBlocks = j;
				j = 0;
			} else {
				numberOfBlocks = memorySize - 1;
				relation_reference1.getBlocks(k, 0, numberOfBlocks);
				k = k + memorySize - 1;
				j = j - memorySize + 1;
			}

			tuplesOfS = mem.getTuples(0, numberOfBlocks);

			for (Tuple tempTupleS : tuplesOfS) {
				alreadyPresent = false;
				for (Tuple tempTuple : tuplesInNewTable) {
					if (tempTuple.toString().equals(tempTupleS.toString())) {
						alreadyPresent = true;
						break;
					}
				}
				if (!alreadyPresent) {
					tuplesInNewTable.add(tempTupleS);
					appendTupleToRelation(new_relation_reference, mem, mem.getMemorySize() - 1, tempTupleS);
				}

			}

		} while (j > 0);

	}

	public List<String> getCreatedTablesList() {
		return createdTablesList;
	}

	public void setCreatedTablesList(List<String> createdTablesList) {
		this.createdTablesList = createdTablesList;
	}

}