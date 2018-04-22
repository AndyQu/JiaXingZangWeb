package templates
layout 'layout.tpl',
        pageTitle:"嘉兴藏",
        bodyContent:contents{
            div(class: "panel-head") {
                span("将图片名自动插入到文本文件中")
            }
            div(class: "panel-body") {
                form(enctype: 'multipart/form-data', action: '/autoInsert', method: 'POST') {
                    div(class:"form-group") {
                        label(style: "color:red;") { span("包含标注结果的excel文件") }
                        input(type: 'file', name: 'label_excel',multiple:"multiple") {}
                        p(class:"help-block"){
                            yieldUnescaped """一次只处理一本藏经，如：J23nB122。若excel中包含多个sheet，则只处理第一个sheet"""
                        }
                    }
                    div(class:"form-group") {
                        label(style: "color:red;") { span("藏经所有文本文件") }
                        input(type: 'file', name: 'txt_files') {}
                        p(class:"help-block"){
                            yieldUnescaped """这里请指定某部藏经的所有文本文件，例如J23nB122的文本文件包括：J23nB122_001.txt J23nB122_002.txt ...... J23nB122_008.txt"""
                        }
                    }
                    button(type:'submit') {
                        span("上传")
                    }
                }
            }
        },
        scriptContent:contents{}