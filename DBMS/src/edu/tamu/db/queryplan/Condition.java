package edu.tamu.db.queryplan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Condition {

	private String leftOperand;
	private String rightOperand;
	private String operator;

	private boolean sameRelationFlag = false;
	// 11 - both left operands are column, 01 - 1st value 2nd column ,10 - 1st
	// column 2nd value, 00- both values
	// for single case 00 - leftOperand is value , 11- column.
	private int leftOperandColumn;
	// 11 - both left operands are column, 01 - 1st value 2nd column ,10 - 1st
	// column 2nd value, 00- both values
	// for single case 00 - leftOperand is value , 11- column.
	private int rightOperandColumn;
	
	private boolean isProcessed = false;

	public String getLeftOperand() {
		return leftOperand;
	}

	public void setLeftOperand(String leftOperand) {
		this.leftOperand = leftOperand;
	}

	public String getRightOperand() {
		return rightOperand;
	}

	public void setRightOperand(String rightOperand) {
		this.rightOperand = rightOperand;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	private String[] leftOperandList;
	private String[] rightOperandList;
	public String[] getLeftOperandList() {
		return leftOperandList;
	}

	public void setLeftOperandList(String[] leftOperandList) {
		this.leftOperandList = leftOperandList;
	}

	public String[] getRightOperandList() {
		return rightOperandList;
	}

	public void setRightOperandList(String[] rightOperandList) {
		this.rightOperandList = rightOperandList;
	}

	private boolean isLeftOperandList = false;
	private boolean isRightOperandList = false;

	public void checkOperands() {
		String pattern = "[\\*\\+\\-]";

		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(leftOperand);
		int start;
		if (m.find()) {
			start = m.start();
			leftOperandList = new String[3];
			leftOperandList[0] = leftOperand.substring(0, start);
			leftOperandList[1] = leftOperand.substring(start, start + 1);
			leftOperandList[2] = leftOperand.substring(start + 1);
			if ((checkColumn(leftOperandList[0]) && (checkColumn(leftOperandList[2])))) {
				leftOperandColumn = 00;
			} else if ((checkColumn(leftOperandList[0]) && (!checkColumn(leftOperandList[2])))) {
				leftOperandColumn = 01;
				leftOperandRelationName = leftOperandList[2].substring(0, leftOperandList[2].indexOf("."));
			} else if ((!checkColumn(leftOperandList[0]) && (checkColumn(leftOperandList[2])))) {
				leftOperandColumn = 10;
				leftOperandRelationName = leftOperandList[0].substring(0, leftOperandList[0].indexOf("."));
			} else {
				leftOperandColumn = 11;
				leftOperandRelationName = leftOperandList[0].substring(0, leftOperandList[0].indexOf("."));
				leftOperandRelationName = leftOperandList[2].substring(0, leftOperandList[2].indexOf("."));
			}
			setLeftOperandList(true);
		} else {
			if (checkColumn(leftOperand)) {
				leftOperandColumn = 00;
			} else {
				leftOperandRelationName = leftOperand.substring(0, leftOperand.indexOf("."));
				leftOperandColumn = 11;
			}
		}

		m = r.matcher(rightOperand);
		if (m.find()) {
			start = m.start();
			rightOperandList = new String[3];
			rightOperandList[0] = rightOperand.substring(0, start);
			rightOperandList[1] = rightOperand.substring(start, start + 1);
			rightOperandList[2] = rightOperand.substring(start + 1);
			if ((checkColumn(rightOperandList[0]) && (checkColumn(rightOperandList[2])))) {
				rightOperandColumn = 00;
			} else if ((checkColumn(rightOperandList[0]) && (!checkColumn(rightOperandList[2])))) {
				rightOperandColumn = 01;
				rightOperandRelationName = rightOperandList[2].substring(0, rightOperandList[2].indexOf("."));
			} else if ((!checkColumn(rightOperandList[0]) && (checkColumn(rightOperandList[2])))) {
				rightOperandColumn = 10;
				rightOperandRelationName = rightOperandList[0].substring(0, rightOperandList[0].indexOf("."));
			} else {
				rightOperandColumn = 11;
				rightOperandRelationName = rightOperandList[0].substring(0, rightOperandList[0].indexOf("."));
				rightOperandRelationName = rightOperandList[2].substring(0, rightOperandList[2].indexOf("."));
			}
			setRightOperandList(true);
		} else {
			if (checkColumn(rightOperand)) {
				rightOperandColumn = 00;
			} else {
				rightOperandRelationName = rightOperand.substring(0, rightOperand.indexOf("."));
				rightOperandColumn = 11;
			}
		}
		if (rightOperandColumn != 0 && leftOperandColumn != 0) {
			if (rightOperandRelationName.equals(leftOperandRelationName))
				sameRelationFlag = true;
		}else{
			if(rightOperandColumn != 0 && leftOperandColumn ==0){
				sameRelationFlag = true;
				leftOperandRelationName = rightOperandRelationName;
			}else if(leftOperandColumn !=0 && rightOperandColumn ==0){
				sameRelationFlag = true;
				rightOperandRelationName  = leftOperandRelationName;
			}
		}
		
		
	}

	public void checkOperands(String relationName) {
		String pattern = "[\\*\\+\\-]";

		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(leftOperand);
		int start;
		if (m.find()) {
			start = m.start();
			leftOperandList = new String[3];
			leftOperandList[0] = leftOperand.substring(0, start);
			leftOperandList[1] = leftOperand.substring(start, start + 1);
			leftOperandList[2] = leftOperand.substring(start + 1);
			if ((checkColumn(leftOperandList[0]) && (checkColumn(leftOperandList[2])))) {
				leftOperandColumn = 00;
			} else if ((checkColumn(leftOperandList[0]) && (!checkColumn(leftOperandList[2])))) {
				leftOperandColumn = 01;
				if (!rightOperandList[2].contains("."))
					leftOperandList[2] = relationName + "." + leftOperandList[2];
			} else if ((!checkColumn(leftOperandList[0]) && (checkColumn(leftOperandList[2])))) {
				leftOperandColumn = 10;
				if (!rightOperandList[0].contains("."))
					leftOperandList[0] = relationName + "." + leftOperandList[0];
			} else {
				leftOperandColumn = 11;
				if (!rightOperandList[0].contains("."))
					leftOperandList[0] = relationName + "." + leftOperandList[0];
				if (!rightOperandList[2].contains("."))
					leftOperandList[2] = relationName + "." + leftOperandList[2];
			}
			setLeftOperandList(true);
		} else {
			if (checkColumn(leftOperand)) {
				leftOperandColumn = 00;
			} else {
				leftOperandColumn = 11;
				if (!rightOperand.contains("."))
					leftOperand = relationName + "." + leftOperand;
			}
		}

		m = r.matcher(rightOperand);
		if (m.find()) {
			start = m.start();
			rightOperandList = new String[3];
			rightOperandList[0] = rightOperand.substring(0, start);
			rightOperandList[1] = rightOperand.substring(start, start + 1);
			rightOperandList[2] = rightOperand.substring(start + 1);
			if ((checkColumn(rightOperandList[0]) && (checkColumn(rightOperandList[2])))) {
				rightOperandColumn = 00;
			} else if ((checkColumn(rightOperandList[0]) && (!checkColumn(rightOperandList[2])))) {
				rightOperandColumn = 01;
				if (!rightOperandList[2].contains("."))
					rightOperandList[2] = relationName + "." + rightOperandList[2];
			} else if ((!checkColumn(rightOperandList[0]) && (checkColumn(rightOperandList[2])))) {
				rightOperandColumn = 10;
				if (!rightOperandList[0].contains("."))
					rightOperandList[0] = relationName + "." + rightOperandList[0];
			} else {
				rightOperandColumn = 11;
				if (!rightOperandList[0].contains("."))
					rightOperandList[0] = relationName + "." + rightOperandList[0];
				if (!rightOperandList[2].contains("."))
					rightOperandList[2] = relationName + "." + rightOperandList[2];
			}
			setRightOperandList(true);
		} else {
			if (checkColumn(rightOperand)) {
				rightOperandColumn = 00;
			} else {
				rightOperandColumn = 11;
				if (!rightOperand.contains("."))
					rightOperand = relationName + "." + rightOperand;
			}
		}

	}

	private String leftOperandRelationName;
	private String rightOperandRelationName;

	public boolean checkColumn(String operand) {
		try {
			Integer.parseInt(operand);
			return true;
		} catch (Exception e) {
			if (operand.contains("\"")) {
				return true;
			} else {
				return false;
			}
		}
	}

	public int getLeftOperandColumn() {
		return leftOperandColumn;
	}

	public void setLeftOperandColumn(int leftOperandColumn) {
		this.leftOperandColumn = leftOperandColumn;
	}

	public int getRightOperandColumn() {
		return rightOperandColumn;
	}

	public void setRightOperandColumn(int rightOperandColumn) {
		this.rightOperandColumn = rightOperandColumn;
	}

	public boolean isLeftOperandList() {
		return isLeftOperandList;
	}

	public void setLeftOperandList(boolean isLeftOperandList) {
		this.isLeftOperandList = isLeftOperandList;
	}

	public boolean isRightOperandList() {
		return isRightOperandList;
	}

	public void setRightOperandList(boolean isRightOperandList) {
		this.isRightOperandList = isRightOperandList;
	}

	public boolean isSameRelationFlag() {
		return sameRelationFlag;
	}

	public void setSameRelationFlag(boolean sameRelationFlag) {
		this.sameRelationFlag = sameRelationFlag;
	}

	public String getLeftOperandRelationName() {
		return leftOperandRelationName;
	}

	public void setLeftOperandRelationName(String leftOperandRelationName) {
		this.leftOperandRelationName = leftOperandRelationName;
	}

	public String getRightOperandRelationName() {
		return rightOperandRelationName;
	}

	public void setRightOperandRelationName(String rightOperandRelationName) {
		this.rightOperandRelationName = rightOperandRelationName;
	}

	@Override
	public String toString() {
		return "Condition [leftOperand=" + leftOperand + ", rightOperand=" + rightOperand + ", operator=" + operator
				+ ", sameRelationFlag=" + sameRelationFlag + ", leftOperandColumn=" + leftOperandColumn
				+ ", rightOperandColumn=" + rightOperandColumn + ", leftOperandList=" + Arrays.toString(leftOperandList)
				+ ", rightOperandList=" + Arrays.toString(rightOperandList) + ", isLeftOperandList=" + isLeftOperandList
				+ ", isRightOperandList=" + isRightOperandList + ", leftOperandRelationName=" + leftOperandRelationName
				+ ", rightOperandRelationName=" + rightOperandRelationName + "]";
	}

	public boolean isProcessed() {
		return isProcessed;
	}

	public void setProcessed(boolean isProcessed) {
		this.isProcessed = isProcessed;
	}
	
	
	public void updateRelationInCondition(List<LogicQueryNode> relationList){
		String oldRelationName;
		String newRelationName;
		for(int i=0; i<relationList.size(); i++){
			if(this.getLeftOperandRelationName().equals(relationList.get(i).getData())){
				oldRelationName = this.getLeftOperandRelationName();
				newRelationName = relationList.get(i).getTag();
				this.setLeftOperandRelationName(relationList.get(i).getTag());
				if(isLeftOperandList){
					if(this.getLeftOperandColumn() ==0){
						
					}
					else if(this.getLeftOperandColumn() == 10){
						this.getLeftOperandList()[0]=this.getLeftOperandList()[0].replaceAll(oldRelationName, newRelationName);
					}else if(this.getLeftOperandColumn()==1){
						this.getLeftOperandList()[2]=this.getLeftOperandList()[2].replaceAll(oldRelationName, newRelationName);
					}else{
						this.getLeftOperandList()[0]=this.getLeftOperandList()[0].replaceAll(oldRelationName, newRelationName);
						this.getLeftOperandList()[2]=this.getLeftOperandList()[2].replaceAll(oldRelationName, newRelationName);
					}
				}
				else{
					if(this.getLeftOperandColumn() ==0){
						this.setLeftOperand(this.getLeftOperand().replaceAll(oldRelationName, newRelationName));
					}else{
						this.setLeftOperand(this.getLeftOperand().replaceAll(oldRelationName, newRelationName));
					}
				}
			}else if(this.getRightOperandRelationName().equals(relationList.get(i).getData())){
				oldRelationName = this.getRightOperandRelationName();
				newRelationName = relationList.get(i).getTag();
				this.setRightOperandRelationName(relationList.get(i).getTag());
				if(isRightOperandList){
					if(this.getRightOperandColumn() ==0){
						
					}
					else if(this.getRightOperandColumn() == 10){
						this.getRightOperandList()[0]=this.getRightOperandList()[0].replaceAll(oldRelationName, newRelationName);
					}else if(this.getRightOperandColumn()==1){
						this.getRightOperandList()[2]=this.getRightOperandList()[2].replaceAll(oldRelationName, newRelationName);
					}else{
						this.getRightOperandList()[0]=this.getRightOperandList()[0].replaceAll(oldRelationName, newRelationName);
						this.getRightOperandList()[2]=this.getRightOperandList()[2].replaceAll(oldRelationName, newRelationName);
					}
				}
				else{
					if(this.getRightOperandColumn() ==0){
						this.setRightOperand(this.getRightOperand().replaceAll(oldRelationName, newRelationName));
					}else{
						this.setRightOperand(this.getRightOperand().replaceAll(oldRelationName, newRelationName));
					}
				}
			}
		}
	}

}
