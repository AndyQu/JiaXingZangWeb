package org.lq.aiitc.qlzang.tool

import com.google.common.collect.Lists
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.lq.aiitc.ExcelUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.util.FileCopyUtils

class InsertCatalog {
    static final Logger LOGGER=LoggerFactory.getLogger(InsertCatalog.class)
    static final File OutputFolder=new File("E:\\永乐北藏___乾龙藏\\catalog_inserted_output")
    static void main(String[]args){
        OutputFolder.mkdirs()
        def ybZang=new ScriptureConfig(
                prefix: "YB",
                textFilesPath: "E:\\永乐北藏___乾龙藏\\永乐北藏\\YB_txt",
                labelExcelsPath: "E:\\永乐北藏___乾龙藏\\智能中心-永乐北藏-标注结果"
        )
        def qlZang=new ScriptureConfig(
                prefix: "QL",
                textFilesPath: "E:\\永乐北藏___乾龙藏\\乾隆藏\\QL_txt",
                labelExcelsPath: "E:\\永乐北藏___乾龙藏\\智能中心-乾隆藏-标注结果"
        )
        def testYbZang=new ScriptureConfig(
                prefix: "YB",
                textFilesPath: "E:\\永乐北藏___乾龙藏\\永乐北藏\\YB_txt",
                labelExcelsPath: "E:\\永乐北藏___乾龙藏\\test-yb-标注结果"
        )
//        [ybZang,qlZang].each {
        [testYbZang].each {
            def thisScriptureOutputFolder=new File(OutputFolder,it.prefix)
            thisScriptureOutputFolder.mkdirs()
            new File(it.labelExcelsPath).listFiles().findAll {
                f->f.isFile() &&
                        (f.getName().endsWith(".xls") || f.getName().endsWith(".xlsx")) &&
                        !f.getName().startsWith("~") &&
                        !f.getName().contains("未完成")
            }.each {
                labelExcel->
                    println("Process:${labelExcel.getName()}")
                    int bookNum=labelExcel.getName().split("_")[1].toInteger()
                    def volumnOutputFolder=new File(thisScriptureOutputFolder,bookNum.toString())
                    volumnOutputFolder.mkdirs()

                    Workbook workbook=WorkbookFactory.create(new FileInputStream(labelExcel))
                    Sheet sheet=workbook.getSheetAt(0)

                    List<File> bookTextFiles = findSortedBookTextFiles(it.textFilesPath,bookNum)
                    if(bookTextFiles.isEmpty()){
                        println("\tERROR:没有文本文件")
                    }
                    int notFoundCnt=0
                    boolean warnedNotFinished=false
                    bookTextFiles.each {
                        srcTextFile->
                            println("\tProcess page text file:${srcTextFile.getName()}")
                            def targetFile=new File(volumnOutputFolder,srcTextFile.getName())
                            targetFile.createNewFile()

                            int pageNum=extractPhotoPageNumFrom(srcTextFile.getName())
                            List<PhotoPageLabelRow> labelRows=findSortedLabelRows(sheet,pageNum)

                            if(labelRows.isEmpty()){
                                FileCopyUtils.copy(srcTextFile,targetFile)
                                notFoundCnt++
                                if(notFoundCnt>=7){
                                    if(!warnedNotFinished) {
                                        println("!!!Probably not finished label file: ${labelExcel.getName()}. End PageNum:${pageNum}")
                                        warnedNotFinished=true
                                    }
                                }
                            }else{
                                notFoundCnt=0
                                PhotoPageText pageText=readPageText(srcTextFile)
                                if(labelRows.size()>=2){
                                    if(containSameRows(labelRows)) {
                                        println("\tcontain same label row: ${labelExcel.getName()}")
                                    }
                                }
                                labelRows.each {
                                    labelRow->
                                        insertCatalogTextIntoPageText(labelRow,pageText)
                                }
//                                System.exit(0)
                                writeIntoFile(pageText,targetFile)
                            }
                    }
            }
        }
    }

    static boolean containSameRows(List<PhotoPageLabelRow> labelRows) {
        for(int i=0;i<labelRows.size()-1;i++){
            if(labelRows.get(i).isSameAs(labelRows.get(i+1))){
                return true
            }
        }
        return false
    }


    static void writeIntoFile(PhotoPageText photoPageText, File targetFile) {
        targetFile.withWriter("utf-8") {
            writer->
                writer.write("")
                photoPageText.sectionOne.each {
                    line->writer.write(line+"\n")
                }
                writer.write("\n\n")
                photoPageText.sectionTwo.each {
                    line->writer.write(line+"\n")
                }
        }
    }

    static void insertCatalogTextIntoPageText(PhotoPageLabelRow photoPageLabelRow, PhotoPageText photoPageText) {
        switch (photoPageLabelRow.section){
            case 1:
                insertOrAppend(photoPageText.sectionOne,photoPageLabelRow)
                break
            case 2:
                insertOrAppend(photoPageText.sectionTwo,photoPageLabelRow)
                break
            default:
                println("Error Section Num Invalid:${photoPageLabelRow.section}")
        }
    }

    static void insertOrAppend(List<String> lines, PhotoPageLabelRow photoPageLabelRow) {
        if(photoPageLabelRow.row<=0){
            println("\t\t无效的标注行：${photoPageLabelRow}")
            return
        }
        if(lines.size()>photoPageLabelRow.row-1){
            lines.add(photoPageLabelRow.row-1,photoPageLabelRow.text)
        }else {
            lines.add(photoPageLabelRow.text)
        }
        println("\t\tInsert:${photoPageLabelRow}")
    }

    static PhotoPageText readPageText(File textFile) {
        PhotoPageText pageText=new PhotoPageText()
        boolean isFirstSection=true
        textFile.eachLine("utf-8"){
            line->
                if(StringUtils.isBlank(line)){
                    isFirstSection=false
                    return
                }
                if(isFirstSection){
                    pageText.sectionOne.add(line)
                }else {
                    pageText.sectionTwo.add(line)
                }
        }
        return pageText
    }

    static List<PhotoPageLabelRow> findSortedLabelRows(Sheet sheet, int pageNum) {
        List<PhotoPageLabelRow> labelRows=Lists.newArrayList()
        for(int i=1;i<=sheet.getLastRowNum();i++){
            Row row= sheet.getRow(i)
            if(row!=null){
                if(row.getCell(1)==null){
//                    println("无效的行:${ExcelUtils.fetchCells(row,5)}")
                }else {
                    try {
                        if (row.getCell(1).getNumericCellValue().toInteger() == pageNum) {
                            labelRows.add(createPhotoPageLabelRowFrom(row))
                        }
                    } catch (Exception e) {
                        e.printStackTrace()
                    }
                }
            }
        }
        labelRows.sort {
            a,b->
                if(a.section==b.section){
                    return a.row-b.row
                }else {
                    return a.section-b.section
                }
        }
        return labelRows
    }

    static PhotoPageLabelRow createPhotoPageLabelRowFrom(Row row) {
        return new PhotoPageLabelRow(
                page: row.getCell(1).getNumericCellValue().toInteger(),
                section: row.getCell(2).getNumericCellValue().toInteger(),
                row: row.getCell(3).getNumericCellValue().toInteger(),
                text: row.getCell(4).getStringCellValue()
        )
    }

    static List<File> findSortedBookTextFiles(String textFilesPath, int bookNum) {
        def targetFolder=new File(new File(textFilesPath),bookNum.toString())
        if(!targetFolder.exists()){
            return Lists.newArrayList()
        }
        return targetFolder.listFiles().findAll{
            f->
                extractPhotoPageNumFrom(f.getName())!=null
        }.sort {
            a,b->
                extractPhotoPageNumFrom(a.getName())-extractPhotoPageNumFrom(b.getName())
        }
    }

    static Integer extractPhotoPageNumFrom(String textFileName) {
        try {
            return textFileName.split("_")[2].replaceAll(".txt","").toInteger()
        }catch (Exception e){
            return null
        }
    }
}
class ScriptureConfig {
    String prefix
    String textFilesPath
    String labelExcelsPath
}
