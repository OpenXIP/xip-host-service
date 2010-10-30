/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.jdom.JDOMException;
import org.nema.dicom.wg23.ArrayOfObjectDescriptor;
import org.nema.dicom.wg23.ArrayOfObjectLocator;
import org.nema.dicom.wg23.ArrayOfPatient;
import org.nema.dicom.wg23.ArrayOfSeries;
import org.nema.dicom.wg23.ArrayOfStudy;
import org.nema.dicom.wg23.AvailableData;
import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.ObjectLocator;
import org.nema.dicom.wg23.Patient;
import org.nema.dicom.wg23.Series;
import org.nema.dicom.wg23.Study;
import org.nema.dicom.wg23.Uuid;
import org.xml.sax.SAXException;
import com.siemens.scr.avt.ad.annotation.ImageAnnotation;
import com.siemens.scr.avt.ad.api.ADFacade;
import com.siemens.scr.avt.ad.io.AnnotationIO;
import com.siemens.scr.avt.ad.util.DicomParser;
import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.avt2ext.AVTFactory;
import edu.wustl.xipHost.avt2ext.iterator.IterationTarget;
import edu.wustl.xipHost.hostControl.DataStore;
import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class StoreADTestSeriesLevel extends TestCase {
	ADFacade adService = AVTFactory.getADServiceInstance();
	AvailableData availableData;
	
	protected void setUp() throws Exception {
		super.setUp();	
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	//CAUTION: all AVTStore test should be run on empty AD database.
	//AVTStore 1C - basic flow. Perfect conditions. AIM objects to store are valid XML strings.
	//DICOM SEG object are valid. There is one txt file (txt file are not stored). 
	//AvailableData contains objects descriptors at the series level.
	public void testStoreAimToAD_1C() throws IOException, JDOMException, InterruptedException, SAXException{
		availableData = new AvailableData();
		ArrayOfPatient arrayOfPatient = new ArrayOfPatient();
		List<Patient> listPatients = arrayOfPatient.getPatient();
		Patient patient = new Patient();
		patient.setName("PatientTest");		
		ArrayOfStudy arrayOfStudy = new ArrayOfStudy();
		List<Study> listStudies = arrayOfStudy.getStudy();
		Study study = new Study();
		ArrayOfSeries arrayOfSeries = new ArrayOfSeries();
		List<Series> listSeries = arrayOfSeries.getSeries();
		Series series = new Series();
		ArrayOfObjectDescriptor arrayOfObjectDescSeries = new ArrayOfObjectDescriptor();
		List<ObjectDescriptor> listObjectDescs = arrayOfObjectDescSeries.getObjectDescriptor();
		//ObjectDescriptor for AIM
		ObjectDescriptor objDesc1 = new ObjectDescriptor();					
		Uuid objDescUUID1 = new Uuid();
		objDescUUID1.setUuid(UUID.randomUUID().toString());
		objDesc1.setUuid(objDescUUID1);													
		objDesc1.setMimeType("text/xml");																						
		listObjectDescs.add(objDesc1);
		//ObjectDescriptor for DICOM SEG
		ObjectDescriptor objDesc2 = new ObjectDescriptor();					
		Uuid objDescUUID2 = new Uuid();
		objDescUUID2.setUuid(UUID.randomUUID().toString());
		objDesc2.setUuid(objDescUUID2);													
		objDesc2.setMimeType("application/dicom");																							
		listObjectDescs.add(objDesc2);
		ObjectDescriptor objDesc3 = new ObjectDescriptor();					
		Uuid objDescUUID3 = new Uuid();
		objDescUUID3.setUuid(UUID.randomUUID().toString());
		objDesc3.setUuid(objDescUUID2);																																				
		listObjectDescs.add(objDesc3);
				
		series.setObjectDescriptors(arrayOfObjectDescSeries);
		listSeries.add(series);
		listStudies.add(study);
		patient.setStudies(arrayOfStudy);
		listPatients.add(patient);
		availableData.setPatients(arrayOfPatient);
		
		ArrayOfObjectLocator arrayObjLocs = new ArrayOfObjectLocator();
		List<ObjectLocator> objLocs = arrayObjLocs.getObjectLocator();
		ObjectLocator objLoc1 = new ObjectLocator();
		objLoc1.setUuid(objDescUUID1);
		File file1 = new File("./test-content/AIM_2PlusDICOMSeg/AVT_AIM_RECIST_1.3.6.1.4.1.5962.99.1.2924451380.256323981.1269939836468.1.0.xml");
		String uri1 = file1.toURI().toURL().toExternalForm();
		objLoc1.setUri(uri1);
		objLocs.add(objLoc1);
		
		ImageAnnotation annotation = AnnotationIO.loadAnnotationFromFile(file1);
		String annotationUID = annotation.getDescriptor().getUID();
		
		ObjectLocator objLoc2 = new ObjectLocator();
		objLoc2.setUuid(objDescUUID2);
		File file2 = new File("./test-content/AIM_2PlusDICOMSeg/nodule_1.3.6.1.4.1.5962.99.1.2924451380.256323981.1269939836468.1.0.dcm");
		String uri2 = file2.toURI().toURL().toExternalForm();
		objLoc2.setUri(uri2);
		objLocs.add(objLoc2);
		DicomObject seg = DicomParser.read(file2);
		String dicomSegSOPInstanceUID = seg.getString(Tag.SOPInstanceUID);
		
		//Locator for testObject.txt file
		ObjectLocator objLoc3 = new ObjectLocator();
		objLoc3.setUuid(objDescUUID3);
		File file3 = new File("./src-tests/edu/wustl/xipHost/avt/testObject.txt");
		String uri3 = file3.toURI().toURL().toExternalForm();
		objLoc3.setUri(uri3);
		objLocs.add(objLoc3);
		
		ApplicationStub appStub = new ApplicationStub("TestApp", new File("./src-tests/edu/wustl/xipHost/avt/applicationStub.bat"), "VendorTest", "", null, 
				"analytical", true, "files", 1, IterationTarget.SERIES);
		appStub.setObjectLocators(arrayObjLocs);
		Application app = appStub;
		DataStore ds2 = new DataStore(availableData, app);
		Thread t = new Thread(ds2);
		t.start();
		t.join();
		ds2.getAVTStoreThread().join();
		ImageAnnotation loadedAnnotation = adService.getAnnotation(annotationUID);
		assertTrue(assertAnnotationEquals(annotation, loadedAnnotation));
		DicomObject loadedSeg = adService.getDicomObject(dicomSegSOPInstanceUID);
		assertTrue(assertDicomSegEquals(seg, loadedSeg));
	}
	
	boolean assertAnnotationEquals(ImageAnnotation expected, ImageAnnotation actual) throws SAXException, IOException{
		if(expected == actual){
			return true;
		}
		if(expected != null){
			if(actual != null){
				assertEquals(expected.getDescriptor(), actual.getDescriptor());		
				//assertXMLEqual(expected.getAIM(), actual.getAIM());
				return true;
			} else{
				fail("expected = " + expected + " while actual is null!");
				return false;
			}
		}
		return false;
	}
	
	boolean assertDicomSegEquals(DicomObject expected, DicomObject actual){
		if(expected == actual){
			return true;
		}
		if(expected != null){
			if(actual != null){
				assertEquals(expected.getString(Tag.SOPInstanceUID), actual.getString(Tag.SOPInstanceUID));		
				return true;
			} else{
				fail("expected = " + expected + " while actual is null!");
				return false;
			}
		}
		return false;
	}
	
}
