/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.worklist;

/**
 * @author Jaroslaw Krych
 *
 */
public class WorklistFactory {
	private static WorklistImpl worklist = new WorklistImpl();
	
	private WorklistFactory(){}
	
	public static WorklistImpl getInstance(){
		return worklist;
	}	
}
