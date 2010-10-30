package edu.wustl.xipHost.application;

import java.io.File;
import java.util.UUID;

import org.nema.dicom.wg23.State;

import edu.wustl.xipHost.avt2ext.iterator.IterationTarget;

import junit.framework.TestCase;

public class RemoveApplicationTest extends TestCase {
	ApplicationManager mgr;
	File exePath;
	File iconFile;
	protected void setUp() throws Exception {
		super.setUp();
		mgr = ApplicationManagerFactory.getInstance();
		exePath = new File("./src-tests/edu/wustl/xipHost/application/test.bat");
		iconFile = new File("src-tests/edu/wustl/xipHost/application/test.png");
	}

	//ApplicationManager 1A - basic flow. Application UUID found, State = null.
	public void testRemoveApplication1A() {						
		Application app = new Application("Application1", exePath, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);		
		UUID uuid = app.getID();
		mgr.addApplication(app);		
		boolean btnRemove = mgr.removeApplication(uuid);
		assertTrue("UUID and State were correct, but system was unable to remove application.", btnRemove);
		//verify removal
		//Number of application must be zero
		assertTrue("All parameters were correct but system did not remove application.", mgr.getNumberOfApplications() == 0);
	}
	
	//ApplicationManager 1Ba - alternative flow. Application UUID not found, State = null.
	public void testRemoveApplication1Ba() {
		Application app = new Application("Application1", exePath, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);				
		mgr.addApplication(app);		
		boolean btnRemove = mgr.removeApplication(UUID.randomUUID());
		assertFalse("UUID not found but system did not report problem.", btnRemove);		
	}

	//ApplicationManager 1Bb - alternative flow. Application UUID is null, State = null.
	public void testRemoveApplication1Bb() {
		Application app = new Application("Application1", exePath, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);				
		mgr.addApplication(app);		
		boolean btnRemove = mgr.removeApplication(null);
		assertFalse("UUID is null but system did not report problem.", btnRemove);		
	}
	
	//ApplicationManager 1C - alternative flow. Application UUID is valid and found, State <> null and EXIT.
	public void testRemoveApplication1C() {
		Application app = new Application("Application1", exePath, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);
		UUID uuid = app.getID();
		mgr.addApplication(app);
		app.setState(State.INPROGRESS);
		boolean btnRemove = mgr.removeApplication(uuid);
		assertFalse("Application 'State'<>null and EXIT but system removed application.", btnRemove);		
	}
}
