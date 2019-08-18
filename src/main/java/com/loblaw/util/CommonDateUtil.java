package com.loblaw.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

public class CommonDateUtil {

	static final Logger logger = Logger.getLogger(CommonDateUtil.class.getName());
	
	 public static String ConvertDate(String strDate) {
		 String date1 = null;
		 try {
		 
		//input date format
		 SimpleDateFormat df_in = new SimpleDateFormat("yyyyMMdd");
		 
		//output date format
		 SimpleDateFormat df_output = new SimpleDateFormat("yyyy-MM-dd");
		 Date date = df_in.parse(strDate);
		 date1 = df_output.format(date);
		 //System.out.println(date1);
		 } catch (Exception e) {
			 logger.error("Invalid Date: "+e.getMessage());
		System.out.println("Invalid Date: "+e.getMessage());
		 }
		 return date1;
		 }
	 
	 public static String changeDateFormat(Date date){	
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			String format = formatter.format(date);
			return format;
		}
	 
	 public int numberOfTokenizerInFile(String fileName){	 
     char ch=fileName.charAt(fileName.indexOf('_'));
     int count = 0; 
     for(int i=0;i<fileName.length();i++) {
         if(fileName.charAt(i)=='_'){                
             count++;
         }
     }
     return count;
	 }
}
