package com.loblaw.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hpsf.HPSFException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.IndexedColors;

public class CommonUtil {
	static final Logger logger = Logger.getLogger(CommonUtil.class.getName());
	
	@SuppressWarnings("unchecked")
	public static void exportToExcel(String sheetName, ArrayList<String> headers,
			List<List<String>> data, String outputFile) throws HPSFException {
		try {
	        HSSFWorkbook wb = new HSSFWorkbook();
	        HSSFSheet sheet = wb.createSheet(sheetName);
	        int rowIdx = 0;
	        int cellIdx = 0;
	        // Header
	        HSSFRow hssfHeader = sheet.createRow(rowIdx);        
	        HSSFCellStyle cellStyle = setHeaderStyle(wb);        
	        for (Iterator cells = headers.iterator(); cells.hasNext();) {
	            HSSFCell hssfCell = hssfHeader.createCell(cellIdx++);
	            hssfCell.setCellStyle(cellStyle);
	            hssfCell.setCellValue((String) cells.next());
	        }
	        // Data
	        rowIdx = 1;
	        for (Iterator rows = data.iterator(); rows.hasNext();) {
	            ArrayList row = (ArrayList) rows.next();
	            HSSFRow hssfRow = sheet.createRow(rowIdx++);
	            cellIdx = 0;
	            for (Iterator cells = row.iterator(); cells.hasNext();) {
	                HSSFCell hssfCell = hssfRow.createCell(cellIdx++);
	                hssfCell.setCellValue((String) cells.next());
	            }
	        }	        
	        
            FileOutputStream outs = new FileOutputStream(outputFile);
            wb.write(outs);
            outs.close();
            
            HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext
			.getCurrentInstance().getExternalContext().getResponse();
			ServletOutputStream servletOutputStream = httpServletResponse
			.getOutputStream();
			httpServletResponse.setHeader("Content-disposition",
					"attachment; filename="+outputFile);
			httpServletResponse.setContentType("application/x-download");

			byte[] b = new byte[1024];
			int i = 0;
			FileInputStream fis = new java.io.FileInputStream(outputFile);
			while ((i = fis.read(b)) > 0) {
				servletOutputStream.write(b, 0, i);
			}
        } catch (IOException e) {
            throw new HPSFException(e.getMessage());
        }

    }	
	
	private static HSSFCellStyle setHeaderStyle(HSSFWorkbook sampleWorkBook)

	{

		HSSFFont font = sampleWorkBook.createFont();
		font.setFontName(HSSFFont.FONT_ARIAL);
		font.setColor(IndexedColors.PLUM.getIndex());
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		HSSFCellStyle cellStyle = sampleWorkBook.createCellStyle();
		cellStyle.setFont(font);
		return cellStyle;

	}

}
