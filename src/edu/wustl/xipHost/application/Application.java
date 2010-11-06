/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import java.io.File;
import java.util.UUID;
import edu.wustl.xipHost.avt2ext.iterator.IterationTarget;

public class Application {	
	
	UUID id;
	String name;
	File exePath;
	String vendor;
	String version;
	File iconFile;
	String type;
	boolean requiresGUI;
	String wg23DataModelType;
	int concurrentInstances;
	IterationTarget iterationTarget;
	
	public Application(){
		
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public File getExePath() {
		return exePath;
	}

	public void setExePath(File exePath) {
		this.exePath = exePath;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public File getIconFile() {
		return iconFile;
	}

	public void setIconFile(File iconFile) {
		this.iconFile = iconFile;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isRequiresGUI() {
		return requiresGUI;
	}

	public void setRequiresGUI(boolean requiresGUI) {
		this.requiresGUI = requiresGUI;
	}

	public String getWg23DataModelType() {
		return wg23DataModelType;
	}

	public void setWg23DataModelType(String wg23DataModelType) {
		this.wg23DataModelType = wg23DataModelType;
	}

	public int getConcurrentInstances() {
		return concurrentInstances;
	}

	public void setConcurrentInstances(int concurrentInstances) {
		this.concurrentInstances = concurrentInstances;
	}

	public IterationTarget getIterationTarget() {
		return iterationTarget;
	}

	public void setIterationTarget(IterationTarget iterationTarget) {
		this.iterationTarget = iterationTarget;
	}
}
