package org.lq.aiitc.jxzang.controller

import com.alibaba.fastjson.JSONObject
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.lq.aiitc.jxzang.AutoInsertPhotoNameIntoText
import org.lq.aiitc.jxzang.LabelRow
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView

@Controller
class HomeController {
//    static final String PhotoRootFolderPath="E:\\嘉兴藏图文整理\\photos"
//    static final String OutputFolderPath="E:\\嘉兴藏图文整理\\output"
//    static final String LabelDataFolderPath="E:\\嘉兴藏图文整理\\output-label"
    static final String PhotoRootFolderPath="/home/xian/jxzang/photos"
    static final String OutputFolderPath="/home/xian/jxzang/output"
    static final String LabelDataFolderPath="/home/xian/jxzang/output-label"

    @RequestMapping("/")
    ModelAndView hello(ModelAndView mv) {
        mv.setViewName("home")
        return mv
    }
    @RequestMapping("/autoInsert")
    @ResponseBody JSONObject autoInsert(@RequestParam("label_excel") MultipartFile labelExcelFile,
                                    @RequestParam("txt_files") List<MultipartFile> textFiles) {

        textFiles.sort(new Comparator<MultipartFile>() {
            @Override
            int compare(MultipartFile o1, MultipartFile o2) {
                return o1.getName().compareTo(o2.getName())
            }
        })
        new File(OutputFolderPath).mkdirs()
        new File(LabelDataFolderPath).mkdirs()

        println("process Excel File:${labelExcelFile.getName()}")
        Workbook workbook=WorkbookFactory.create(labelExcelFile.getInputStream())
        Sheet sheet=workbook.getSheetAt(0)
        println("\tprocess Sheet:${sheet.getSheetName()}")

        String bookSNum = sheet.getSheetName()//藏经序号
        String bookPhotoFolderPath=AutoInsertPhotoNameIntoText.findBookPhotoFolderBy(bookSNum,PhotoRootFolderPath)
        if(bookPhotoFolderPath==null){
            println("!!!找不到藏经的图片文件夹:${bookSNum}。图片根目录是：${PhotoRootFolderPath}")
            return
        }
        List<String> photoNameLst=AutoInsertPhotoNameIntoText.collectBookPhotoNames(bookPhotoFolderPath)
        List<LabelRow> labelRowLst=AutoInsertPhotoNameIntoText.complementLabelData(photoNameLst,sheet)
        AutoInsertPhotoNameIntoText.storeLabelData(labelRowLst,new File(LabelDataFolderPath,"${sheet.getSheetName()}_完整标注结果.csv"))
//        List<String> textFilePaths=findBookTextsBy(bookSNum,TextRootFolderPath)
        String result=autoInsert(labelRowLst, textFiles,OutputFolderPath)
        JSONObject ret=new JSONObject()
        ret.put("data",result)
        return ret
    }

    static String autoInsert(List<LabelRow> labelRows, List<MultipartFile> textFiles, String OutputFolderPath) {
        StringBuffer sb=new StringBuffer()
        int textRowNum=1
        int labelRowIndex=0
        int nextTargetRowNum=1
        for(int i=0;i<textFiles.size();i++){
            MultipartFile srcFile=textFiles[i]
            File targetFile=new File(OutputFolderPath,srcFile.getName())
            targetFile.createNewFile()
            targetFile.withWriter("utf-8") {
                writer->
                    writer.write("")
                    Scanner scanner=new Scanner(srcFile.getInputStream())
                    while (scanner.hasNextLine()){
                        String line=scanner.nextLine().replaceAll('\ufeff﻿',"").trim()
                        if(StringUtils.isBlank(line)){
                            sb.append("${line}\n")
                            writer<<"${line}\n"
                            continue
                        }
                        if(nextTargetRowNum==textRowNum){
                            while (labelRowIndex<labelRows.size() && labelRows.get(labelRowIndex).rowCount<=0){
                                writer << "${labelRows.get(labelRowIndex).photoName} ${labelRows.get(labelRowIndex).rowCount} ${labelRows.get(labelRowIndex).remarks}\n"
                                sb.append("${labelRows.get(labelRowIndex).photoName} ${labelRows.get(labelRowIndex).rowCount} ${labelRows.get(labelRowIndex).remarks}\n")
                                labelRowIndex++
                            }
                            if(labelRowIndex<labelRows.size()) {
                                writer << "${labelRows.get(labelRowIndex).photoName} ${labelRows.get(labelRowIndex).rowCount} ${labelRows.get(labelRowIndex).remarks}\n"
                                sb.append("${labelRows.get(labelRowIndex).photoName} ${labelRows.get(labelRowIndex).rowCount} ${labelRows.get(labelRowIndex).remarks}\n")
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
}
