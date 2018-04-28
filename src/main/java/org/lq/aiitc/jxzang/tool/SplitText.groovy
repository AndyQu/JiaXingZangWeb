package org.lq.aiitc.jxzang.tool

import com.google.common.collect.Lists
import com.google.common.collect.Maps
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.lq.aiitc.jxzang.AutoInsertPhotoNameIntoText
import org.lq.aiitc.jxzang.controller.HomeController

class SplitText {
    static final String ExcelFolder="E:\\嘉兴藏图文整理\\二次校对结果\\Excel表"
    static final String TextFolder ="E:\\嘉兴藏图文整理\\二次校对结果\\切分文本"
    static final String OutputFolder="E:\\嘉兴藏图文整理\\text-slices"
    static final String TextCharset="GBK"
    static void main(String[]args){
        doSplit(ExcelFolder,TextFolder,OutputFolder)
    }

    static void doSplit(String excelFolder, String textFolder, String outputFolder) {
        def files=new File(excelFolder).listFiles().findAll {
            it-> it.isFile() &&
                    (it.getName().endsWith(".xlsx")||it.getName().endsWith(".xls"))
        }
        files=[
                new File("E:\\嘉兴藏图文整理\\二次校对结果\\Excel表\\J33nB285.xlsx"),
                new File("E:\\嘉兴藏图文整理\\二次校对结果\\Excel表\\J37nB391.xlsx")
        ]
        files.each {
            labelExcelFile->
                println("process Excel File:${labelExcelFile.getName()}")
                Workbook workbook=WorkbookFactory.create(new FileInputStream(labelExcelFile))
                Sheet sheet=workbook.getSheetAt(0)
                doSplitForEach(sheet,textFolder,outputFolder)
        }
    }

    static def doSplitForEach(Sheet labelSheet, String textFolder, String outputFolder) {
        Map<String,String> photoName2remark=extraceRemarkInfo(labelSheet)
        String bookSnum=labelSheet.getSheetName()
        File textFile=findTextFileBySnum(bookSnum,textFolder)
        if(textFile==null){
            println("\t===Error:${bookSnum} text files not exist.")
            return
        }
        Scanner scanner=new Scanner(new FileInputStream(textFile),TextCharset)
        String startPNameLine=findPhotoNameLine(scanner)
        while (scanner.hasNextLine()){
            List<String> textAndPNameLines=readTextLinesUntilPhotoName(scanner)
            if(textAndPNameLines.get(textAndPNameLines.size()-1).contains(".tif")){
                createSliceFile(bookSnum,photoName2remark,startPNameLine,textAndPNameLines.subList(0,textAndPNameLines.size()-1), outputFolder)
                startPNameLine=textAndPNameLines.get(textAndPNameLines.size()-1)
            }else{
                createSliceFile(bookSnum,photoName2remark,startPNameLine,textAndPNameLines.subList(0,textAndPNameLines.size()), outputFolder)
                println("\tread end:${textAndPNameLines.get(textAndPNameLines.size()-1)}")
                break
            }
        }
    }

    static File findTextFileBySnum(String bookSnum, String textFolder) {
        return new File(textFolder).listFiles().find {
            f-> f.isFile() && f.getName().equalsIgnoreCase(bookSnum+".txt")
        }
    }

    static def createSliceFile(String bookSnum,Map<String,String> photoName2remark,String startPNameLine, List<String> textLines, String outputFolder) {
        String[]args=startPNameLine.split(" ")
        String remark=photoName2remark.get(args[0])
        if(remark==null){
            createSliceFileForPname(args[0],textLines,outputFolder+"\\正常\\${bookSnum}")
        }else if(remark.contains("夹注小字")){
            createSliceFileForPname(args[0],textLines,outputFolder+"\\夹注小字\\${bookSnum}")
        }else{
            createSliceFileForPname(args[0],textLines,outputFolder+"\\其他\\${bookSnum}")
        }
    }

    static def createSliceFileForPname(String pName, List<String> textLines, String folderPath) {
        File normalFolder=new File(folderPath)
        normalFolder.mkdirs()
        File newFile=new File(normalFolder,pName.replaceAll(".tif","")+".txt")
        newFile.createNewFile()
        newFile.withWriter(TextCharset){
            writer->
                writer.write("")
                textLines.each {line->writer.write(line+"\n")}
        }
    }

    static List<String> readTextLinesUntilPhotoName(Scanner scanner) {
        List<String> textAndPNameLines= Lists.newArrayList()
        while (scanner.hasNextLine()){
            String line = scanner.nextLine()
            line = HomeController.rmBomChar(line).trim()
            if(line.contains(".tif")){
                textAndPNameLines.add(line)
                break
            }else {
                textAndPNameLines.add(line)
            }
        }
        return textAndPNameLines
    }

    static String findPhotoNameLine(Scanner scanner) {
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine()
            line = HomeController.rmBomChar(line).trim()
            if (StringUtils.isBlank(line)) {
                continue
            }
            if (line.contains(".tif")) {
                return line
            }else{
                println("\tencountered none-photo-name-line:${line}")
            }
        }
        return null
    }

    static Map<String,String> extraceRemarkInfo(Sheet labelSheet) {
        Map<String,String> photoName2remark= Maps.newHashMap()
        for(int rIndex=1;rIndex<=labelSheet.getLastRowNum();rIndex++){
            println("\tprocess line:${AutoInsertPhotoNameIntoText.fetchCells(labelSheet.getRow(rIndex),3)}")
            try{
                Row r=labelSheet.getRow(rIndex)
                String pName=r.getCell(0).getStringCellValue()
                photoName2remark.put(
                        pName.replaceAll(".tif","")+".tif",
                        r.getCell(2).getStringCellValue()
                )
            }catch (Exception e){
                println("\t\t${e.getMessage()}")
            }
        }
        return photoName2remark
    }
}
