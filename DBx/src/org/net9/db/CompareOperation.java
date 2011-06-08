package org.net9.db;

import java.io.Serializable;


public class CompareOperation implements Serializable {
	private String left, right; 
	private String op;
	boolean oneVariable;
	
	CompareOperation() {
		oneVariable = false;
	}
	
	CompareOperation(String left, String op, String right, boolean oneVariable) { 
		this.left = left;
		this.right = right;
		this.op = op;
		this.oneVariable = oneVariable;
	}
	
	public void setLeft(String left) {
		this.left = left;
	}
	
	public void setRight(String right) {
		this.right = right;
	}
	
	public void setOp(String op) {
		this.op = op;
	}
	
	public void setOneV(boolean oneVariable) {
		this.oneVariable = oneVariable;
	}
	
	public String getLeft() {
		return left;
	}
	
	public String getRight() {
		return right;
	}
	
	public String getOP() {
		return op;
	}
}