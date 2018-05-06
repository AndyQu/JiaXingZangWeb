package org.lq.aiitc.qlzang.tool

import com.google.common.collect.Lists
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.util.FileCopyUtils

class InsertCatalog {
    static final File OutputFolder=new File("E:\\永乐北藏___乾龙藏\\catalog_inserted_output")
    static void main(String[]args){
        OutputFolder.mkdirs()
        def ybZang=new ScriptureConfig(
                prefix: "YB",
                textFilesPath: "E:\\永乐北藏___乾龙藏\\永乐北藏\\YB_txt",
                labelExcelsPath: "E:\\永乐北藏___乾龙藏\\智能中心-永乐北藏-标注结果"
        )
        [ybZang].each {
            def thisScriptureOutputFolder=new File(OutputFolder,it.prefix)
            thisScriptureOutputFolder.mkdirs()
            new File(it.labelExcelsPath).listFiles().findAll {
                f->f.isFile() && (f.getName().endsWith(".xls") || f.getName().endsWith(".xlsx")) && !f.getName().startsWith("~")
            }.each {
                labelExcel->
                    println("Process:${labelExcel.getName()}")
                    int bookNum=labelExcel.getName().split("_")[1].toInteger()
                    def volumnOutputFolder=new File(thisScriptureOutputFolder,bookNum.toString())
                    volumnOutputFolder.mkdirs()

                    Workbook workbook=WorkbookFactory.create(new FileInputStream(labelExcel))
                    Sheet sheet=workbook.getSheetAt(0)

                    List<File> bookTextFiles = findSortedBookTextFiles(it.textFilesPath,bookNum)
                    bookTextFiles.each {
                        srcTextFile->
                            println("\tProcess page text file:${srcTextFile.getName()}")
                            def targetFile=new File(volumnOutputFolder,srcTextFile.getName())
                            targetFile.createNewFile()

                            int pageNum=extractPhotoPageNumFrom(srcTextFile.getName())
                            List<PhotoPageLabelRow> labelRows=findSortedLabelRows(sheet,pageNum)

                            if(labelRows.isEmpty()){
                                FileCopyUtils.copy(srcTextFile,targetFile)
                            }else{
                                PhotoPageText pageText=readPageText(srcTextFile)
                                if(labelRows.size()>=2){
                                    println("\tmultiple labels at same row")
                                }
                                labelRows.each {
                                    labelRow->
                                        insertCatalogTextIntoPageText(labelRow,pageText)
                                }
                                writeIntoFile(pageText,targetFile)
                            }
                    }
            }
        }
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
                try {
                    if (row.getCell(1).getNumericCellValue().toInteger() == pageNum) {
                        labelRows.add(createPhotoPageLabelRowFrom(row))
                    }
                }catch (Exception e){}
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
        try {
            return new PhotoPageLabelRow(
                    page: row.getCell(1).getNumericCellValue().toInteger(),
                    section: row.getCell(2).getNumericCellValue().toInteger(),
                    row: row.getCell(3).getNumericCellValue().toInteger(),
                    text: row.getCell(4).getStringCellValue()
            )
        }catch (Exception e){
            e.printStackTrace()
        }
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
