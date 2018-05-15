package util;

import jws.Logger;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelUtil {

	/**
	 * 获取excel单元格数值，返回int类型的值
	 * @param cell
	 * @return
	 */
	public static Integer getIntValue(HSSFCell cell) {
		if(cell != null && cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
		    return (int)Math.round(cell.getNumericCellValue());
		}
		Logger.warn("HSSFCell is null or HSSFCell is not numeric ");
		return 0;
	}
	

	
	
	/**
	 * 获取excel单元格数值，返回日期的毫秒数
	 * @param cell
	 * @return
	 */
	/*@SuppressWarnings("deprecation")
	public static Long getDateTimeValue(HSSFCell cell) {
		if(cell != null && cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
		    return DateUtil.getDate(cell.getStringCellValue());
		}
		return null;
	}*/
	
	/**
	 * 创建一个excel文本对象
	 * @param sheetName
	 * @param fieldName
	 * @return
	 * @author chenxx
	 */
	@SuppressWarnings("deprecation")
	public static HSSFWorkbook createExcel(String sheetName, String[] fields) {
		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet(sheetName);
		// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row = sheet.createRow((int) 0);
		// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

		HSSFCell cell = null;
		
		for (int i=0;i< fields.length;i++) {
			cell = row.createCell(i);
			cell.setCellValue(fields[i]);
			cell.setCellStyle(style);
		}
		return wb;
	}
	
	/**
	 * 获取excel单元格字符串内容
	 * guozy add@20140509
	 * @param cell
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getStringValue(HSSFCell cell) {
		if(cell != null && cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
		    return cell.getStringCellValue();
		}
		Logger.warn("HSSFCell is null or HSSFCell is not String ");
		return "";
	}
}
