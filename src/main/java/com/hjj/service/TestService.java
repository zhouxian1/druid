package com.hjj.service;

import com.hjj.util.DataBaseUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TestService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public  List<Map<String, String>> gettables(HttpServletRequest request)throws Exception{
        DataBaseUtil dataBaseUtil=new DataBaseUtil();
        dataBaseUtil.setJdbcTemplate(jdbcTemplate);
        //创建HSSF工作薄
        HSSFWorkbook workbook=new HSSFWorkbook();
        HSSFCell cell=null;
        //合并单元格
        CellRangeAddress region=null;
        //设置单元格居中
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setWrapText(true);
        List<Map<String, String>>list=dataBaseUtil.getTableName("hjjjqdb");
        //创建一个Sheet页
        HSSFSheet sheet = workbook.createSheet();
        for(int i=0;i<list.size();i++){
            String remarks=(String) list.get(i).get("remarks");//注释
            String tableName=(String) list.get(i).get("tableName");//表名
            List<Map<String,String>>columns=dataBaseUtil.getColumnAndComments(tableName);
            HSSFRow row = sheet.createRow(0);
            HSSFRow row1 = sheet.createRow(1);
            HSSFRow row2=sheet.createRow(2);
            cell=row.createCell(0);
            cell.setCellValue(tableName);
            for(int j=0;j<columns.size();j++){
                cell=row1.createCell(j);
                if(!columns.get(j).get("columns").equals("")&&columns.get(j).get("columns")!=null){
                    cell.setCellValue(columns.get(j).get("columns"));
                }else{
                    cell.setCellValue(columns.get(j).get("null"));
                }
                cell=row2.createCell(j);
                cell.setCellValue(columns.get(j).get("column"));
            }
            //String realPath = request.getSession().getServletContext().getRealPath("excelTemplate/");
           //保存到本地
            File file = new File("D:/"+remarks+".xls");
            FileOutputStream outputStream = new FileOutputStream(file);
            //将Excel写入输出流中
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        }

        return null;
    }

    public static void  importExcel()throws Exception{
        //创建HSSF工作薄
        HSSFWorkbook workbook=new HSSFWorkbook(new FileInputStream("D:/情报_台军、外军情况_目标.xls"));
        //创建一个Sheet页
        HSSFSheet sheet = workbook.getSheetAt(0);
        // 循环行Row
        List<Map<String,String>>list=new ArrayList<>();
        Map<String,String>map=new HashMap<>();
        HSSFRow row=sheet.getRow(1);
        for (int rowNum = 2; rowNum <= sheet.getLastRowNum(); rowNum++) {
            HSSFRow hssfRow = sheet.getRow(rowNum);
            if (hssfRow != null) {
                for(int cellNum=0;cellNum<=hssfRow.getLastCellNum()-1;cellNum++){
                    String cell=row.getCell(cellNum).getStringCellValue();
                    HSSFCell loginName = hssfRow.getCell(cellNum);
                    System.out.println(loginName);
                    if(loginName!=null){
                        loginName.setCellType(CellType.STRING);
                        map.put(cell,loginName.getStringCellValue());
                    }

                }
                list.add(map);
            }
        }

        for(Map<String,String>m:list){
            Mb mb=new Mb();
            BeanUtils.populate(mb, m);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mb.setSJSJ(sdf.parse(sdf.format(new Date())));
            mb.setGXSJ(sdf.parse(sdf.format(new Date())));
            System.out.println(mb.toString());
        }

    }

    public static void main(String[] args) throws  Exception{
        importExcel();
    }
}
