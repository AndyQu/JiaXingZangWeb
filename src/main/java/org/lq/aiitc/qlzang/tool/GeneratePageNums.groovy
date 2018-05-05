//package org.lq.aiitc.qlzang.tool
int startPage=this.args[0].toInteger()
def serials=[
        [1,	6],
        [1,	6],
        [2,	11],
        [2,	11],
        [1,	6],
        [1,	6],
        [2,	11],
        [2,	11],
        [1,	6],
        [1,	6],
        [2,	11],
        [2,	11],
]
def diffs=[0,1,1,2,5,6,6,7,10,11,11,12]
for(int i=0;i<diffs.size();i++){
//    println("${startPage+diffs[i]} ${serials[i][0]} ${serials[i][1]}")
    println("${startPage+diffs[i]}")
}

//class GeneratePageNums {
//    static void main(String[]args){
//
//    }
//}
