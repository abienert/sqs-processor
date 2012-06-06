package com.amazonaws.jobprocessor.executors;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.amazonaws.jobprocessor.JobExecutionException;
import com.amazonaws.jobprocessor.JobExecutor;

public class OcrExecutor implements JobExecutor  {

	private static Logger log = Logger.getLogger(OcrExecutor.class);
	
	@Override
	public String execute(String jobDefinition) throws JobExecutionException {
		
		log.info("Executing image OCR message: '" + jobDefinition + "'" );
		
		String imageFile = getImageFile(jobDefinition);
		String outputFile = getOutputFile();
		
		Runtime run = Runtime.getRuntime();
		
		try {
			Process proc = run.exec(new String[]{"tesseract", imageFile, outputFile});
			int returnCode = proc.waitFor();
			if (returnCode != 0)
				throw new JobExecutionException("OCR process failed with return code " + returnCode);
			
		} catch (IOException  e) {
			throw new JobExecutionException("OCR execution exception: " + e.getMessage());
		} catch (InterruptedException e) {
			throw new JobExecutionException("OCR execution exception: " + e.getMessage());
		}
		
		return outputFile;
	}
	
	private String getImageFile(String jobDefinition)
	{
		String homePath = System.getenv("HOME");
		String imagePath = homePath + "/processor/images";
		
		return imagePath + "/eurotext.tif";
	}
	
	private String getOutputFile()
	{
		String homePath = System.getenv("HOME");
		String outputPath = homePath + "/processor/output";
		
		return outputPath + "/out";
	}

}
