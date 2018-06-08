package org.lq.aiitc.jxzang.tool

import com.google.common.collect.Sets

class RmPunctuation {
    static final String Punctuations= "[：？。，、；！『』「」《》]（）”“【】<>"
    static final Set<Character> PunctuationCharSet=Sets.newHashSet()
    static int LineCntContainingPunc=0
    static int FileCntContainingPunc=0
    static {
//        for(int i=0;i<Punctuations.size();i++){
//            PunctuationCharSet.add(Punctuations.charAt(i))
//        }
    }
    static void main(String[]args){
        final String TextRootFolder="E:\\嘉兴藏图文整理\\嘉兴藏图片文字切片_去除乱码版本"
        final String OutputFolder="E:\\嘉兴藏图文整理\\output_除去标点"
        copyAndRmPunctuationFromTxt(new File(TextRootFolder),new File(OutputFolder))
        println("FileCntContainingPunc:${FileCntContainingPunc}")
        println("LineCntContainingPunc:${LineCntContainingPunc}")
        println("标点符号：")
        for(Character c:PunctuationCharSet){
            println("\t"+c)
        }
    }

    static void copyAndRmPunctuationFromTxt(File srcFolder, File destFolder) {
        if(!destFolder.exists()){
            destFolder.mkdirs()
        }
        srcFolder.listFiles().each {
            f->
                if(f.isDirectory()){
                    copyAndRmPunctuationFromTxt(f,new File(destFolder,f.getName()))
                }else if(f.isFile() && f.getName().endsWith(".txt")){
                    cpFileAndRmPunctuation(f,destFolder)
                }else {
                    println("跳过:${f.getName()}")
                }
        }
    }

    static void cpFileAndRmPunctuation(File srcTextFile, File destFolder) {
        File destFile=new File(destFolder,srcTextFile.getName())
        destFile.createNewFile()

        println("from ${srcTextFile.getPath()} to ${destFile.getPath()}")

        destFile.withWriter("UTF-8"){
            writer->
                writer.write("")
                boolean containPunc=false
                srcTextFile.withReader("UTF-8"){
                    reader->
                        reader.eachLine {
                            lstr->
                                String nstr=rmPunctuation(lstr)
                                writer.write(nstr+"\n")
                                if(nstr.size()<lstr.size()){
                                    containPunc=true
                                }
                        }
                }
                if(containPunc){
                    FileCntContainingPunc++
                }
        }
    }

    // 根据UnicodeBlock方法判断中文标点符号
    static boolean isChinesePunctuation(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
                || ub == Character.UnicodeBlock.VERTICAL_FORMS) {
            PunctuationCharSet.add(c)
            return true;
        } else {
            return false;
        }
    }

    static String rmPunctuation(String s) {
        StringBuffer t=new StringBuffer()
        StringBuffer t1=new StringBuffer()
        for(int i=0;i<s.size();i++){
            if(!isChinesePunctuation(s.charAt(i))||s.charAt(i)=='　'){
//            if(!PunctuationCharSet.contains(s.charAt(i))){
                t.append(s.charAt(i))
            }else {
                t1.append(s.charAt(i))
            }
        }
        if(t1.size()>0){
            println("\nrm ${t1} from ${s}")
            LineCntContainingPunc++
        }
        return t.toString()
    }
}
