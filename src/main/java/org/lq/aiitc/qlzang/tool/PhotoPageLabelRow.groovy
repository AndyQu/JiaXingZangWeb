package org.lq.aiitc.qlzang.tool

import groovy.transform.ToString

@ToString(includePackage=false)
class PhotoPageLabelRow {
    int page
    int section
    int row
    String text

    boolean isSameAs(PhotoPageLabelRow b) {
        return page==b.page && section==b.section && row==b.row
    }
}
