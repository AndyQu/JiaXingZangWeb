package org.lq.aiitc.jxzang.tool

import com.google.common.collect.Lists
import org.apache.commons.lang3.StringUtils

/**
 * 嘉兴藏文字切分，出现了连续的错位。用这个程序fix一下
 */
class FixLineOffset {
    static final OutputFolder="E:\\嘉兴藏-行错位输出"
    static void main(String[]args){
        new File(OutputFolder).mkdirs()
//        part3()
//        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J23nB122",
//                "JX_267_3",
//                1,
//                183,2)
        part4()
    }
    static void part4(){
        //JX_278_7_131
        //JX_278_7_145
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J25nB164",
                "JX_278_7",131,145,4)
        //JX_286_6_218
        //JX_286_6_233 E:\嘉兴藏图文整理\output_除去标点\text-slices\正常\J27nB189
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J27nB189",
                "JX_286_6",218,233,3)

        //JX_287_5_9
        //JX_287_5_62
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J27nB191",
                "JX_287_5",9,62,1)

        //JX_288_3_173
        //JX_288_3_180
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J27nB193",
                "JX_288_3",173,180,-9)

        //JX_294_2_81
        //JX_294_2_124
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J28nB212",
                "JX_294_2",81,124,2)
        //JX_294_2_125
        //JX_294_2_161
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J28nB212",
                "JX_294_2",125,161,2)
        //JX_294_2_162
        //JX_294_2_167
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J28nB212",
                "JX_294_2",162,167,3)

        //JX_329_7_165
        //JX_329_7_173
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J36nB363",
                "JX_329_7",165,173,-1)

        //JX_329_9_133
        //JX_329_9_141
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J36nB367",
                "JX_329_9",133,141,1)

        //JX_329_9_278
        //JX_329_9_315
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J36nB368",
                "JX_329_9",278,315,-1)

        //JX_333_4_106
        //JX_333_4_159
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J37nB386",
                "JX_333_4",106,159,-1)

        //JX_345_2_157
        //JX_345_2_167
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J39nB453",
                "JX_345_2",157,167,1)

        //JX_373_1_31
        //JX_373_1_44
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J40nB473",
                "JX_373_1",31,44,1)
    }
    static void part3(){
        //JX_343_1_2
        // JX_343_1_43
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J39nB438",
                "JX_343_1",2,45,-1)
        //JX_344_6_18
        //JX_344_6_70
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J39nB450",
                "JX_344_6",18,70,-2)
        //JX_344_6_71
        //JX_344_6_103
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J39nB450",
                "JX_344_6",71,103,-1)
    }
    static void part2(){
        //JX_334_2_98 JX_334_2_173
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J37nB392",
                "JX_334_2",
                98,173,
                1)

        //JX_334_3_2 JX_334_3_181
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J37nB392",
                "JX_334_3",
                2,181,
                1)

        //JX_334_4_2  JX_334_4_88
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J37nB392",
                "JX_334_4",
                2,88,
                1)

        //JX_334_9_146 JX_334_9_184
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J37nB402",
                "JX_334_9",
                146,184,
                1)
        //JX_344_9_65 JX_344_9_176
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J39nB452",
                "JX_344_9",65,176,1)

        //JX_344_9_194 JX_344_9_225
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J39nB452",
                "JX_344_9",194,225,-1)
    }
    static void  part1(){
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J23nB122",
                "JX_267_2",
                21,
                92,1)

        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J23nB122",
                "JX_267_3",
                1,
                183,2)
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J23nB122",
                "JX_267_3",
                184,
                190,1)
//        JX_276_5_137  -  JX_276_5_143
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J25nB156",
                "JX_276_5",
                137,
                143,1)
//        JX_285_4_71  -  JX_285_4_104
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J26nB186",
                "JX_285_4",
                71,
                103,-1)
        //        JX_287_3_27  -  JX_287_3_34
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J27nB190",
                "JX_287_3",
                27,34,
                -1
        )
//        JX_287_4_94  -  JX_287_4_109
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J27nB190",
                "JX_287_4",
                95,109,
                -1
        )
//        JX_287_4_127  -  JX_287_4_165
        fix("E:\\嘉兴藏图文整理\\output_除去标点\\text-slices\\正常\\J27nB190",
                "JX_287_4",
                127,165,
                -1
        )
    }

    static def fix(String textSliceFolder,String photoPrefix, int startNum, int endNum,int offset) {
        if(offset>0) {
            List<String> previousLines = cutLastLine(textSliceFolder, photoPrefix, startNum, offset)
            for (int index = startNum + 1; index <= endNum - 1; index++) {
                if(new File("${textSliceFolder}\\${photoPrefix}_${index}.txt").readLines("UTF-8").size()<=0){
                    continue
                }
                previousLines = addLineToHeadAndCutLastLine(textSliceFolder, photoPrefix, index, previousLines, offset)
            }
            addLineToHead(textSliceFolder, photoPrefix, endNum, previousLines)
        }else if(offset<0){
            offset=Math.abs(offset)
            List<String> nextHeadLine=cutHeadLines(textSliceFolder, photoPrefix, endNum, offset)
            for (int index = endNum-1; index >=startNum+1; index--) {
                if(new File("${textSliceFolder}\\${photoPrefix}_${index}.txt").readLines("UTF-8").size()<=0){
                    continue
                }
                nextHeadLine=appendToTailAndCutHeadLines(textSliceFolder, photoPrefix, index, nextHeadLine, offset)
            }
            appendToTail(textSliceFolder, photoPrefix, startNum, nextHeadLine)
        }
    }

    static List<String> appendToTailAndCutHeadLines(String textSliceFolder, String photoPrefix, int num, List<String> targetLines,int offset) {
        List<String> headLines= Lists.newArrayList()
        new File("${OutputFolder}\\${photoPrefix}_${num}.txt").withWriter("UTF-8"){
            writer->
                def lines=new File("${textSliceFolder}\\${photoPrefix}_${num}.txt").readLines("UTF-8")
                def a=lines.subList(offset,lines.size())
                a.addAll(targetLines)
                a.each {
                    writer.write(it)
                    writer.write("\n")
                }
                headLines.addAll(lines.subList(0,offset))
        }
        return headLines
    }

    static def appendToTail(String textSliceFolder, String photoPrefix, int num, List<String> targetLines) {
        new File("${OutputFolder}\\${photoPrefix}_${num}.txt").withWriter("UTF-8"){
            writer->
                def lines=new File("${textSliceFolder}\\${photoPrefix}_${num}.txt").readLines("UTF-8")
                lines.addAll(targetLines)
                lines.subList(0,lines.size()).each {
                    writer.write(it)
                    writer.write("\n")
                }
        }
    }

    static List<String> cutHeadLines(String textSliceFolder, String photoPrefix, int num, int offset) {
        List<String> targetLines= Lists.newArrayList()
        new File("${OutputFolder}\\${photoPrefix}_${num}.txt").withWriter("UTF-8"){
            writer->
                def lines=new File("${textSliceFolder}\\${photoPrefix}_${num}.txt").readLines("UTF-8")
                lines.subList(offset,lines.size()).each {
                    writer.write(it)
                    writer.write("\n")
                }
                targetLines.addAll(lines.subList(0,offset))
        }
        return targetLines
    }

    static List<String> addLineToHeadAndCutLastLine(String textSliceFolder, String photoPrefix, int num, List<String> previousLines, int offset) {
        List<String> lastLines= Lists.newArrayList()
        new File("${OutputFolder}\\${photoPrefix}_${num}.txt").withWriter("UTF-8"){
            writer->
                def fromFile="${textSliceFolder}\\${photoPrefix}_${num}.txt"
                def lines=new File(fromFile).readLines("UTF-8")
                println(fromFile+":${lines.size()},${offset}")
                lastLines.addAll(lines.subList(lines.size()-offset,lines.size()))
                lines.addAll(0,previousLines)
                lines.findAll{it->!StringUtils.isBlank(it)}.subList(0,lines.size()-offset).each {
                    writer.write(it)
                    writer.write("\n")
                }
        }
        return lastLines
    }

    static def addLineToHead(String textSliceFolder, String photoPrefix, int num, List<String> previousLines) {
        new File("${OutputFolder}\\${photoPrefix}_${num}.txt").withWriter("UTF-8"){
            writer->
                def lines=new File("${textSliceFolder}\\${photoPrefix}_${num}.txt").readLines("UTF-8")
                lines.addAll(0,previousLines)
                lines.subList(0,lines.size()).each {
                    writer.write(it)
                    writer.write("\n")
                }
        }
    }

    static List<String> cutLastLine(String textSliceFolder, String photoPrefix, int startNum,int offset) {
        List<String> lastLines= Lists.newArrayList()
        new File("${OutputFolder}\\${photoPrefix}_${startNum}.txt").withWriter("UTF-8"){
            writer->
                def lines=new File("${textSliceFolder}\\${photoPrefix}_${startNum}.txt").readLines("UTF-8")
                lines.findAll{it->!StringUtils.isBlank(it)}.subList(0,lines.size()-offset).each {
                    writer.write(it)
                    writer.write("\n")
                }
                lastLines.addAll(lines.subList(lines.size()-offset,lines.size()))
        }
        return lastLines
    }
}
