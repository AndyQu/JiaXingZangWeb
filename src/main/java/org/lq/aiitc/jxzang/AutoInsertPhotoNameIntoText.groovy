package org.lq.aiitc.jxzang

import com.google.common.collect.Lists
import com.google.common.collect.Maps
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.*

class AutoInsertPhotoNameIntoText {
    static void main(String[]args){
        final String TextRootFolderPath="E:\\嘉兴藏图文整理\\all"
        final String PhotoRootFolderPath="E:\\嘉兴藏图文整理\\photos"
        final String LabelRootFolderPath="E:\\嘉兴藏图文整理\\label"

        final String OutputFolderPath="E:\\嘉兴藏图文整理\\output"
        final String LabelDataFolderPath="E:\\嘉兴藏图文整理\\output-label"

        doAutoInserting(LabelRootFolderPath,PhotoRootFolderPath,TextRootFolderPath,OutputFolderPath,LabelDataFolderPath)
    }

    static void doAutoInserting(String LabelRootFolderPath, String PhotoRootFolderPath,
                                String TextRootFolderPath, String OutputFolderPath,
                                String LabelDataFolderPath) {
        new File(OutputFolderPath).mkdirs()
        new File(LabelDataFolderPath).mkdirs()
        new File(LabelRootFolderPath).listFiles().findAll{
            f->!f.getName().startsWith("~") && (f.getName().endsWith(".xls") || f.getName().endsWith(".xlsx"))
        }each {
            labelExcelFile->
                println("process Excel File:${labelExcelFile.getName()}")
                Workbook workbook=WorkbookFactory.create(new FileInputStream(labelExcelFile))
                for(int index=0;index<workbook.getNumberOfSheets()-1;index++){
                    Sheet sheet=workbook.getSheetAt(index)
                    println("\tprocess Sheet:${sheet.getSheetName()}")
                    doAutoInsertingForSheet(sheet, PhotoRootFolderPath, TextRootFolderPath,OutputFolderPath,LabelDataFolderPath)
                }
        }
    }

    static String doAutoInsertingForSheet(Sheet sheet, String PhotoRootFolderPath,
                                        String TextRootFolderPath, String OutputFolderPath, String LabelDataFolderPath) {
        String bookSNum = sheet.getSheetName()//藏经序号
        String bookPhotoFolderPath=findBookPhotoFolderBy(bookSNum,PhotoRootFolderPath)
        if(bookPhotoFolderPath==null){
            println("!!!找不到藏经的图片文件夹:${bookSNum}。图片根目录是：${PhotoRootFolderPath}")
            return
        }
        List<String> photoNameLst=collectBookPhotoNames(bookPhotoFolderPath)
        List<LabelRow> labelRowLst=complementLabelData(photoNameLst,sheet)
        storeLabelData(labelRowLst,new File(LabelDataFolderPath,"${sheet.getSheetName()}_完整标注结果.csv"))
        List<String> textFilePaths=findBookTextsBy(bookSNum,TextRootFolderPath)
        return autoInsert(labelRowLst,textFilePaths,OutputFolderPath)
    }

    static void storeLabelData(List<LabelRow> labelRows, File file) {
        file.createNewFile()
        file.withWriter("utf-8") {
            writer->
                writer.write("")
                labelRows.each {
                    row->
                        writer<<"${row.photoName},${row.rowCount},${row.remarks}\n"
                }
        }
    }

    static String autoInsert(List<LabelRow> labelRows, List<String> textFilePaths, String OutputFolderPath) {
        StringBuffer sb=new StringBuffer()
        int textRowNum=0
        int labelRowIndex=0
        int nextTargetRowNum=0
        for(int i=0;i<textFilePaths.size();i++){
            File srcFile=new File(textFilePaths.get(i))
            File targetFile=new File(OutputFolderPath,srcFile.getName())
            targetFile.createNewFile()
            targetFile.withWriter("utf-8") {
                writer->
                    writer.write("")
                    Scanner scanner=new Scanner(new FileInputStream(srcFile))
                    while (scanner.hasNextLine()){
                        String line=scanner.nextLine()
                        if(StringUtils.isBlank(line)){
                            sb.append("${line}\n")
                            writer<<"${line}\n"
                            continue
                        }
                        if(nextTargetRowNum==textRowNum){
                            while (labelRowIndex<labelRows.size() && labelRows.get(labelRowIndex).rowCount<=0){
                                writer << "${labelRows.get(labelRowIndex).photoName} ${labelRows.get(labelRowIndex).rowCount}\n"
                                sb.append("${labelRows.get(labelRowIndex).photoName} ${labelRows.get(labelRowIndex).rowCount}\n")
                                labelRowIndex++
                            }
                            if(labelRowIndex<labelRows.size()) {
                                writer << "${labelRows.get(labelRowIndex).photoName} ${labelRows.get(labelRowIndex).rowCount}\n"
                                sb.append("${labelRows.get(labelRowIndex).photoName} ${labelRows.get(labelRowIndex).rowCount}\n")
                                nextTargetRowNum+=labelRows.get(labelRowIndex).rowCount
                                labelRowIndex++
                            }
                        }
                        writer<< "${line}\n"
                        sb.append("${line}\n")
                        textRowNum++
                    }
            }
        }
        return sb.toString()
    }

    static List<String> findBookTextsBy(String bookSNum, String TextRootFolderPath) {
        return new File(TextRootFolderPath).listFiles().findAll{
            f->
                f.isFile() && f.getName().startsWith(bookSNum) && f.getName().endsWith(".txt")
        }.collect {
            f->f.getPath()
        }.sort{
            a,b->a.compareTo(b)
        }
    }

    static List<LabelRow> complementLabelData(List<String> photoNameLst, Sheet sheet) {
        Row firstRow=sheet.getRow(0)
        int normalRowCount=fetchIntValueFromCell(firstRow.getCell(1))
        Map<String,LabelRow> photoName2LabelRow= Maps.newHashMap()
        for(int i=1;i<=sheet.getLastRowNum();i++){
            Row row=sheet.getRow(i)
            if(row==null){
                println("\t\trow ${i+1} does not exist.")
                continue
            }
            println("\t\textrace label info from excel row:${i+1},${fetchCells(row,3)}")
            try {
                LabelRow labelRow=extractFromRow(row)
                photoName2LabelRow.put(labelRow.photoName+".tif",labelRow)
            }catch (Exception e){
                println("\t\t\t${e.getMessage()}")
            }
        }

        List<LabelRow> labelRowList= Lists.newArrayList()
        for(int i=0;i<photoNameLst.size();i++){
            LabelRow labelRow=photoName2LabelRow.get(photoNameLst.get(i))
            if(labelRow==null){
                labelRow=new LabelRow(photoName: photoNameLst.get(i),rowCount: normalRowCount,remarks: "")
            }
            labelRow.photoName=labelRow.photoName.replaceAll(".tif","")+".tif"
            labelRowList.add(labelRow)
        }
        return labelRowList
    }

    static String fetchCells(Row row, int cnt) {
        StringBuffer sb=new StringBuffer()
        for(int i=0;i<cnt && i<=row.getLastCellNum();i++){
            sb.append("${row.getCell(i)} ")
        }
        return sb.toString()
    }

    static int fetchIntValueFromCell(Cell cell) {
        try{
            return cell.getNumericCellValue().intValue()
        }catch (Exception e){
            return 0
        }
    }

    static LabelRow extractFromRow(Row row) {
        return new LabelRow(
                photoName: row.getCell(0).getStringCellValue(),
                rowCount: row.getCell(1).getNumericCellValue().intValue(),
                remarks: row.getCell(2)?.getStringCellValue()
        )
    }

    static List<String> collectBookPhotoNames(String bookPhotoFolderPath) {
        new File(bookPhotoFolderPath).listFiles().findAll {
            f->f.isFile() && f.getName().endsWith(".tif")
        }.collect {f->f.getName()}.sort{
            a,b->
                VolPageNum volPageNumA = extractVolPageNum(a)
                VolPageNum volPageNumB = extractVolPageNum(b)
                return volPageNumA.compareTo(volPageNumB)
        }
    }

    /**
     * @param photoName 例如：JX_313_1_14.tif 。1是卷号，14是页号
     * @return
     */
    static VolPageNum extractVolPageNum(String photoName) {
        String[] parts=photoName.split("_")
        return new VolPageNum(
                volNum: Integer.parseInt(parts[2]),
                pageNum: Integer.parseInt(parts[3].replaceAll(".tif",""))
        )
    }

    static String findBookPhotoFolderBy(String bookSNum, String PhotoRootFolderPath) {
        return new File(PhotoRootFolderPath).listFiles().find {
            f->
                f.isDirectory() && f.getName().equalsIgnoreCase(bookSNum)
        }
    }
}


