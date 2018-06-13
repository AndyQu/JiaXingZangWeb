package org.lq.aiitc.jxzang.tool

import com.google.common.collect.Maps
import org.apache.commons.lang3.StringUtils

class PutTxtFiles {
    static void main(String[]args){
//        String OutputFolder="E:\\嘉兴藏图文整理\\0607_0609解决的行错位输出\\带目录的输出"
//        String OutputFolder="E:\\嘉兴藏图文整理\\0607_0609解决的行错位输出\\J23nB122"
        String OutputFolder="E:\\嘉兴藏图文整理\\0613解决的行错位输出\\带目录的输出"
        Map<String,String> txtName2Path= Maps.newHashMap()
        new File("E:\\嘉兴藏图文整理\\photo_txt_path.txt").withReader("UTF-8"){
            reader->
                reader.eachLine {
                    line->
                        if(StringUtils.isBlank(line))
                            return
                        def pairs=line.trim().replaceAll("\t"," ").replaceAll("\\s+"," ").split(" ")
                        String txtName=pairs[0]
                        String path=pairs[1].substring(0,pairs[1].lastIndexOf("/"))
                        txtName2Path.put(txtName+".txt",path)
                }
        }
        new File(
//                "E:\\嘉兴藏图文整理\\0607_0609解决的行错位输出\\嘉兴藏-行错位输出"
                "E:\\嘉兴藏-行错位输出"
        ).listFiles().findAll {
            f->f.isFile() && f.name.endsWith(".txt")
        }.each {
            txtFile->
                String path=txtName2Path.get(txtFile.name)
                if(path==null){
                    println("${txtFile.name.replaceAll('.txt','')} standard//${txtFile.name}")
                    return
                }
                String targetFolder=OutputFolder+"\\"+path.replaceAll("/","\\\\")
                new File(targetFolder).mkdirs()
                org.apache.commons.io.FileUtils.copyFile(txtFile, new File(targetFolder+"\\${txtFile.name}"))
        }
    }
}
