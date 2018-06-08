package org.lq.aiitc.qlzang.tool

class DetectEmptyTxtFiles {
    static void main(String[]args){
        String RootFolder="E:\\永乐北藏___乾龙藏\\乾隆藏\\QL_txt"
        new File(RootFolder).listFiles().each {
            volFolder->
                if(volFolder.isFile()){
                    return
                }
                volFolder.listFiles().each {
                    txtFile->
                        if(txtFile.isDirectory()
                                ||!txtFile.getName().endsWith(".txt")
                            ||!txtFile.getName().startsWith("QL_")
                        ){
                            return
                        }
                        if(txtFile.length()<=0){
//                            println(txtFile.getName().replaceAll(".txt","")+" :${txtFile.length()}")
                            println(txtFile.getName().replaceAll(".txt",""))
                        }
                }
        }
    }
}
