package org.lq.aiitc

import org.apache.poi.ss.usermodel.Row

class ExcelUtils {
    static String fetchCells(Row row, int cnt) {
        if(row==null){
            return null
        }
        StringBuffer sb=new StringBuffer()
        for(int i=0;i<cnt && i<=row.getLastCellNum();i++){
            sb.append("${row?.getCell(i)} ")
        }
        return sb.toString()
    }
}
