package com.nokia.wordprocessor;

import java.io.File;

import javax.print.attribute.standard.JobMessageFromOperator;
import javax.swing.JOptionPane;

public class App 
{
    public static void main( String[] args ) throws Exception
    {
		// TODO Auto-generated method stub
		
		//final String BASE_LOACATION = "D:\\DocProcess\\";
    	final String BASE_LOACATION = JOptionPane.showInputDialog(null, "Enter the path of Input", "HLR",JOptionPane.QUESTION_MESSAGE);
		final String OUTPUT_LOACATION = "outp";
		final String INPUT_FILE = JOptionPane.showInputDialog(null, "Enter the Name of File", "HLR",JOptionPane.QUESTION_MESSAGE);
		//final String INPUT_FILE = "ONENDS-WX-HSSEPS_irn";
		//final String INPUT_FILE = "ONENDS-WX-3GPPHSS_irn";
		//final String INPUT_FILE = "OneNDS_WX_HLR16_irn";
		
		final String INPUT_ZIP_FILE = BASE_LOACATION + File.separator + INPUT_FILE;
		final String OUTPUT_UNZIP_FOLDER = BASE_LOACATION + File.separator + OUTPUT_LOACATION;
		
		final String XML_INPUT_FILE = OUTPUT_UNZIP_FOLDER+File.separator+"word"+File.separator+"document.xml";
		
		
		final String OUTPUT_DOC_FILE_NAME = INPUT_FILE +"_OUT";
		
		FileUnziper.unZipIt(INPUT_ZIP_FILE, OUTPUT_UNZIP_FOLDER);
		
		XMLGenerator.processXMLfile(XML_INPUT_FILE);
		
		FolderZiper.zipDirectory(new File(OUTPUT_UNZIP_FOLDER), new File(BASE_LOACATION), OUTPUT_DOC_FILE_NAME);
		
		//JOptionPane.showMessageDialog(null, "SUccessFull", ,JOptionPane.INFORMATION_MESSAGE);
		JOptionPane.showMessageDialog(null, "OutPut File path : " +BASE_LOACATION + " "  + "\nSuccessfull");
	}
}
