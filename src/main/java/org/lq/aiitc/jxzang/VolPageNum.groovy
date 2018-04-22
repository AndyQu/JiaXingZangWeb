package org.lq.aiitc.jxzang

class VolPageNum {
    int volNum
    int pageNum
    int compareTo(VolPageNum x){
        if(x==null){return 1}
        if(volNum<x.volNum){
            return -1
        }else if(volNum>x.volNum){
            return 1
        }else{
            return pageNum-x.pageNum
        }
    }
}
