/*
Copyright (c) 2013, Washington University in St.Louis
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package edu.wustl.xipHost.application;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import org.jdom.JDOMException;

/**
 * @author Jaroslaw Krych
 *
 */
public interface ApplicationManager {
	public boolean loadApplications (File xipAppFile) throws JDOMException, IOException;
	public boolean storeApplications(List<Application> applications, File xipAppFile);			
	public boolean addApplication(Application newApplication);
	public boolean modifyApplication(UUID applicationUUID, Application modifiedApplication);
	public boolean removeApplication(UUID applicationUUID);	
	public Application getApplication(UUID uuid);
	public Application getApplication(String applicationName);
	public Application getDefaultApplication();
	public void setDefaultApplication(Application application);
	public boolean hasApplication(UUID uuid);
	public List<Application> getApplications();	
	public int getNumberOfApplications();
	public URL generateNewApplicationServiceURL();	
	public URL generateNewHostServiceURL();	
	public void setTmpDir(File tmpDir);
	public File getTmpDir();
	public void setOutputDir(File outDir);
	public File getOutputDir();
	public boolean loadHostConfigParameters(InputStream inputStream);
}
