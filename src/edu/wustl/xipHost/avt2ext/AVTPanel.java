/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.Border;
import org.apache.log4j.Logger;
import com.pixelmed.dicom.AttributeList;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dicom.DicomUtil;
import edu.wustl.xipHost.localFileSystem.FileManager;
import edu.wustl.xipHost.localFileSystem.FileManagerFactory;

public class AVTPanel extends JPanel implements ItemListener, AVTListener {
	final static Logger logger = Logger.getLogger(AVTPanel.class);	
	JPanel leftPanel = new JPanel();
	JPanel rightPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();	
	Color xipColor = new Color(51, 51, 102);
	Color xipBtn = new Color(56, 73, 150);
	Color xipLightBlue = new Color(156, 162, 189);
	Font font_1 = new Font("Tahoma", 0, 13);
	Border border = BorderFactory.createLoweredBevelBorder();		
	JCheckBox cbxDicom = new JCheckBox("DICOM", false);
	JCheckBox cbxAimSeg = new JCheckBox("AIM plus SEG", false);
	JPanel cbxPanel = new JPanel();
	JPanel btnPanel = new JPanel();
	AVTListener l;
	Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);	
	Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);	
	
	public AVTPanel(){
		l = this;
		setBackground(xipColor);					    
	    //resultTree.addTreeSelectionListener(this);
		cbxDicom.setBackground(xipColor);
		cbxDicom.setForeground(Color.WHITE);
		cbxDicom.addItemListener(this);
		cbxDicom.setSelected(false);
		cbxAimSeg.setBackground(xipColor);
		cbxAimSeg.setForeground(Color.WHITE);
		cbxAimSeg.addItemListener(this);
		cbxAimSeg.setSelected(false);
		cbxPanel.setLayout(new GridLayout(1, 2));		
		cbxPanel.add(cbxDicom);
		cbxPanel.add(cbxAimSeg);
		cbxPanel.setBackground(xipColor);
		rightPanel.add(cbxPanel);	
		leftPanel.setBackground(xipColor);
		rightPanel.setBackground(xipColor);
		add(leftPanel);
		add(rightPanel);
		progressBar.setIndeterminate(false);
	    progressBar.setString("");	    
	    progressBar.setStringPainted(true);	    
	    progressBar.setBackground(new Color(156, 162, 189));
	    progressBar.setForeground(xipColor);
	    add(progressBar);
	    buildRightPanelLayout();
	    buildLayout();
	}
		
	AttributeList criteria;	
	void setCriteriaList(AttributeList criteria){
		this.criteria = criteria;
	}
	
	AVTQuery avtQuery;
	
	void buildLayout(){				
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(layout);         
                
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 0;        
        constraints.insets.top = 10;
        constraints.insets.left = 20;
        constraints.insets.right = 15;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(leftPanel, constraints);        
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 1;
        constraints.gridy = 0;        
        constraints.insets.top = 10;
        constraints.insets.left = 15;
        constraints.insets.right = 20;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(rightPanel, constraints);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 0;
        constraints.gridy = 3; 
        constraints.gridwidth = 2;
        constraints.insets.top = 10;
        constraints.insets.left = 0;
        constraints.insets.right = 0;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(progressBar, constraints);
	}
	
	void buildRightPanelLayout(){
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        rightPanel.setLayout(layout);         
                       
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 1;        
        constraints.insets.top = 5;
        constraints.insets.left = 5;
        constraints.insets.right = 20;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(cbxPanel, constraints);
	}
	
	public static void main(String[] args){
		JFrame frame = new JFrame();
		AVTPanel panel = new AVTPanel();
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);		
	}

	SearchResult result;
	public void searchResultsAvailable(AVTSearchEvent e) {
		result = (SearchResult) e.getSource();				
		if(result == null){			
			
		}else{
			
		}											
		progressBar.setString("AVT AD Search finished");
		progressBar.setIndeterminate(false);				
	}
	
	List<File> retrivedFiles;
	List<File> allRetrivedFiles;
	int numRetrieveThreadsStarted;
	int numRetrieveThreadsReturned;
	@SuppressWarnings("unchecked")
	public void retriveResultsAvailable(AVTRetrieveEvent e) {		
		retrivedFiles = (List<File>) e.getSource();	
		synchronized(retrivedFiles){
			allRetrivedFiles.addAll(retrivedFiles);
		}		
		numRetrieveThreadsReturned++;
		if(numRetrieveThreadsStarted == numRetrieveThreadsReturned){
			finalizeRetrieve();
		}
	}	
		
	synchronized void finalizeRetrieve(){		
		progressBar.setString("AD Retrieve finished");
		progressBar.setIndeterminate(false);						
		//allretrivedFiles are checked for duplicate items, both DICOM and AIM
		//File names are compared. Duplicate items are removed.
		int size = allRetrivedFiles.size();
		Map<String, String> filePaths = new LinkedHashMap<String, String>();
		for(int i = 0; i < size; i++){
			try {
				String fileName = allRetrivedFiles.get(i).getName();
				String filePath = allRetrivedFiles.get(i).getCanonicalPath();
				filePaths.put(fileName, filePath);
			} catch (IOException e) {
				logger.error(e, e);
				notifyException(e.getMessage());
			}
		}
		allRetrivedFiles.clear();
		Iterator<String> iter = filePaths.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			String filePath = filePaths.get(key);
			allRetrivedFiles.add(new File(filePath));
		}
		File[] files = new File[allRetrivedFiles.size()];		 
		allRetrivedFiles.toArray(files);		
		FileManager fileMgr = FileManagerFactory.getInstance();						
        fileMgr.run(files);				
	}
	
	@Override
	public void notifyException(String message) {
		progressBar.setIndeterminate(false);		
		progressBar.setForeground(Color.RED);
		progressBar.setBackground(Color.GREEN);
		progressBar.setString("Exception: " + message);
		result = null;								
		cbxDicom.setSelected(false);
		cbxAimSeg.setSelected(false);
	}
	
	Object selectedNode;
	int queryNodeIndex = 0;
	MouseListener ml = new MouseAdapter(){
	     public void mousePressed(MouseEvent e) {
	    	
	     }
	     	    
	     public void mouseClicked(MouseEvent e) {	        
	        if (e.getClickCount() == 2){
		     
	        }	        	        
	    }
	};

	@Override
	public void itemStateChanged(ItemEvent e) {
		JCheckBox source = (JCheckBox)e.getItemSelectable();
		if (source == cbxDicom){
			if (e.getStateChange() == ItemEvent.SELECTED){
				cbxDicom.setSelected(true);
			} else if (e.getStateChange() == ItemEvent.DESELECTED){
				cbxDicom.setSelected(false);
			}
		} else if (source == cbxAimSeg){
			if (e.getStateChange() == ItemEvent.SELECTED){
				cbxAimSeg.setSelected(true);
			} else if (e.getStateChange() == ItemEvent.DESELECTED){
				cbxAimSeg.setSelected(false);
			}
		}	
	}
}
