/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import java.util.EventObject;

/**
 * @author Jaroslaw Krych
 *
 */
public class GridRetrieveEvent extends EventObject {
	public GridRetrieveEvent(GridRetrieve source){	
		super(source);
	}
	public GridRetrieveEvent(AimRetrieve source){	
		super(source);
	}
	public GridRetrieveEvent(GridRetrieveNCIA source){
		super(source);
	}
}
