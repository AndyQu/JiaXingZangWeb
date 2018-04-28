package org.lq.aiitc.jxzang.tool

class DiffBooks {
    static void main(String[]args){
        def lableFileNames=new File("E:\\嘉兴藏图文整理\\二次校对结果\\Excel表").listFiles().collect {
            f->f.getName()
                    .replaceAll(".xlsx","")
                    .replaceAll(".xls","")
        }.toSet()
        def sliceFileNames=new File("E:\\嘉兴藏图文整理\\text-slices\\正常").listFiles().collect {
            f->f.getName()
        }.toSet()
        lableFileNames.removeAll(sliceFileNames)
        lableFileNames.sort().each {println(it)}
    }
}
