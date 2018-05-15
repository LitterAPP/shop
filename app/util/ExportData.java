package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

 
import jws.Logger;
import modules.shop.ddl.ShopOrderDDL;
import modules.shop.service.ShopOrderService;
 

public class ExportData {
	
	public static ByteArrayInputStream reportOrderByCondition(String shopId,String orderId,String keyword,String startTime,String endTime,int status){
		try{ 
			
			//最多导出1万条
			List<ShopOrderDDL> list = ShopOrderService.listMngOrder(shopId,orderId, keyword, startTime,endTime,status, 1, 10000);

			
			String[] fieldNames = null;

//			LinkedHashMap<Integer, String> fieldIdxNameMap = new LinkedHashMap<Integer, String>(); 
			fieldNames = new String[]{
					"订单ID","订单状态","是否团购","团购价","非团购价","商品组名称","商品名称","购买数量",
					"应付总额","余额支付","现金支付","代金券支付","下单时间","支付时间"
			};
			
			HSSFWorkbook wb = ExcelUtil.createExcel("订单列表", fieldNames);
			HSSFRow row = null;
			HSSFSheet sheet = wb.getSheetAt(0);			
		 
			
			if(list != null && list.size() > 0){
				int size = list.size();
				int rowNum = 0;
				for (int i=0;i < size;i++) {
					rowNum  = i+1;
					row = sheet.createRow( rowNum );
					ShopOrderDDL one = list.get(i);
					row.createCell(0).setCellValue(one.getOrderId());
					row.createCell(1).setCellValue(one.getStatus());
					row.createCell(2).setCellValue(StringUtils.isEmpty(one.getTogetherId())?"否":"是");
					row.createCell(3).setCellValue(one.getGroupTogetherPrice()==null?"-": ""+AmountUtil.f2y(one.getGroupTogetherPrice()));
					row.createCell(4).setCellValue( ""+AmountUtil.f2y(one.getGroupTogetherPrice()));
					row.createCell(5).setCellValue(one.getGroupName());
					row.createCell(6).setCellValue(one.getProductName());
					row.createCell(7).setCellValue(one.getBuyNum());
					
					double totalPrice=0.0d;
					if(StringUtils.isEmpty(one.getTogetherId())){
						totalPrice = AmountUtil.f2y(one.getGroupPrice()*one.getBuyNum());
					}else{
						totalPrice = AmountUtil.f2y(one.getGroupTogetherPrice()*one.getBuyNum());
					} 
					
					row.createCell(8).setCellValue(totalPrice+"");
					
					if(one.getUseUserAmount()!=null){
						row.createCell(9).setCellValue(AmountUtil.f2y(one.getUseUserAmount()));
					}else{
						row.createCell(9).setCellValue("-");
					}
					if(one.getUseCash()!=null){
						row.createCell(10).setCellValue(AmountUtil.f2y(one.getUseCash()));
					}else{
						row.createCell(10).setCellValue("-");
					}
					if(one.getUseCouponAmount()!=null){
						row.createCell(11).setCellValue(AmountUtil.f2y(one.getUseCouponAmount()));
					}else{
						row.createCell(11).setCellValue("-");
					}
					 
					row.createCell(12).setCellValue(DateUtil.format(one.getOrderTime()));
					row.createCell(13).setCellValue(DateUtil.format(one.getPayTime())); 
				} 
			}	
			ByteArrayOutputStream stream = null;
			ByteArrayInputStream inputStream = null;
	        try {
	        	stream = new ByteArrayOutputStream();
	            wb.write(stream);
	            inputStream = new ByteArrayInputStream(stream.toByteArray());
	        } catch (IOException e) {
	            Logger.error(e, "export day list data failed");
	        }finally{
	            try {
	                if(stream != null){
	                    stream.close();
	                } 
	            } catch (IOException e) {
	                Logger.error(e , "export day list data failed - stream close");
	            }
	        }
	        return inputStream;
		}catch(Exception e){
			Logger.error(e, e.getMessage()); 
			return null;
		}
	}
}
