package org.lq.aiitc.jxzang.tool

import com.google.common.collect.Lists
import groovy.transform.ToString
import org.apache.commons.lang3.StringUtils
import org.lq.aiitc.jxzang.AutoInsertPhotoNameIntoText
import org.lq.aiitc.jxzang.controller.HomeController

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * 处理嘉兴藏文字切片乱码问题
 */
class CompareAndMend {
    final static String OriginalTextRootFolderPath="E:\\嘉兴藏图文整理\\all"
    final static String ManualCheckedTextFolderPath="E:\\嘉兴藏图文整理\\二次校对结果\\切分文本"
//    final static String ManualCheckedTextFolderPath="E:\\嘉兴藏图文整理\\2nd_slice"
    final static String OutputFolderPath="E:\\嘉兴藏图文整理\\output_encode"
    final static String LogFolderPath="E:\\嘉兴藏图文整理\\output_encode_log"
    static void main(String[] args){
        new File(LogFolderPath).mkdirs()
        def outFolder=new File(OutputFolderPath)
        outFolder.mkdirs()
        new File(ManualCheckedTextFolderPath).listFiles().findAll {
            it.isFile() && it.getName().endsWith(".txt")
        }.each {
//        [new File("E:\\嘉兴藏图文整理\\二次校对结果\\切分文本\\J26nB186.txt")].each {
            manualCheckTextFile->
                File logFile=new File(new File(LogFolderPath),manualCheckTextFile.getName()+".log")
                File targetFile=new File(outFolder,manualCheckTextFile.getName())
                targetFile.createNewFile()
                List<String> originalTextFiles=AutoInsertPhotoNameIntoText.findBookTextsBy(
                        manualCheckTextFile.getName().replaceAll(".txt",""),OriginalTextRootFolderPath
                )
                List<String> originalLines= Lists.newArrayList()
                for (int i = 0; i < originalTextFiles.size(); i++) {
                    new File(originalTextFiles.get(i)).eachLine("utf-8"){
                        it=HomeController.rmBomChar(it)
                        if(!StringUtils.isBlank(it)) {
                            originalLines.add(it)
                        }
                    }
                }
                int origLineIndex=0
                String prevLine=null
                logFile.withWriter("utf-8") {
                    log->
                        targetFile.withWriter("utf-8") {
                            writer ->
                                writer.write("")
                                Scanner scanner = new Scanner(new FileInputStream(manualCheckTextFile), "GBK")
                                int lineCnt = 0
                                while (scanner.hasNextLine()) {
                                    String line = scanner.nextLine()
                                    lineCnt++
                                    line = HomeController.rmBomChar(line)
                                    if (StringUtils.isBlank(line)) {
                                        continue
                                    }
                                    if (line.contains(".tif")) {
                                        writer.write(line + "\n")
                                    } else if (!line.contains("?")) {
                                        writer.write(line + "\n")
                                        prevLine=line
                                    } else if(line.equalsIgnoreCase("?")){
                                        continue
                                    }else {
                                        Pattern p = Pattern.compile("^"+line.replaceAll("\\?", ".{0,1}")+"\$")
                                        List<Line> matchLines = findMatchingLines(p, originalLines)
                                        if (matchLines.size() <= 0) {
                                            log.write("not found[${lineCnt}]:${line}\n")
                                        } else if (matchLines.size() >= 2) {
                                            //先看匹配的行是不是两行？内容是不是相同？
                                            if(isSameContent(matchLines)){
                                                writer.write(matchLines.get(0).content + "\n")
                                                println("${line} -> ${matchLines.get(0).content}")
                                                prevLine=matchLines.get(0)
                                                continue
                                            }
                                            //拿上一行做匹配
                                            boolean ok=false
                                            if(prevLine!=null){
                                                List<Line> prevMatches=findMatchingLines(Pattern.compile("^${prevLine}\$"),originalLines)
                                                if(prevMatches.size()==1){
                                                    Line l=findNearestLine(matchLines,prevMatches.get(0).num)
                                                    writer.write(l.content + "\n")
                                                    println("${line} -> ${l.content}")
                                                    prevLine=l.content
                                                    ok=true
                                                }else if(prevMatches.size()<=0){
                                                    log.write("prev line not found:${prevLine}.\n")
                                                    prevLine=null
                                                }else{
                                                    log.write("found multiple prev lines[${lineCnt}]:${line}.\n")
                                                    prevMatches.each { log.write("\t${it.num}:${it.content}\n") }
                                                    prevLine=null
                                                }
                                            }
                                            if(!ok){
                                                Line nearestLine=findNearestLine(matchLines,lineCnt)
//                                                log.write("found multiple[${lineCnt}]:${line}.\n")
//                                                matchLines.each { log.write("\t${it.num}:${it.content}\n") }
//                                                log.write("\tuse ${nearestLine}")


                                                writer.write(nearestLine.content + "\n")
                                                println("${line} -> ${nearestLine.content}")
                                                prevLine=nearestLine.content
                                            }
                                        } else {
                                            writer.write(matchLines.get(0).content + "\n")
                                            println("${line} -> ${matchLines.get(0).content}")
                                            prevLine=matchLines.get(0)
                                        }
                                    }
                                }
                        }
                }
        }
    }

    static boolean isSameContent(List<Line> lines) {
        return lines.size()==2 && lines.get(0).content.equalsIgnoreCase(lines.get(1).content)
    }

    static Line findNearestLine(List<Line> matchLines, int lineNum) {
        int distance=Math.abs(matchLines.get(0).num-lineNum)
        int index=0
        for(int i=1;i<matchLines.size();i++){
            if(Math.abs(matchLines.get(i).num-lineNum)<distance){
                distance=Math.abs(matchLines.get(i).num-lineNum)
                index=i
            }
        }
        return matchLines.get(index)
    }

    static List<Line> findMatchingLines(Pattern pattern, List<String> originalLines) {
        List<Line> data=Lists.newArrayList()
        for(int i=0;i<originalLines.size();i++){
            if(match(pattern,originalLines.get(i))){
                data.add(new Line(num:i,content:originalLines.get(i)))
            }
        }
        return data
    }

    static boolean match(Pattern pattern, String targetLine) {
        Matcher matcher=pattern.matcher(targetLine)
        return matcher.find()
    }
    @ToString
    static class Line{
        int num
        String content
    }
}
