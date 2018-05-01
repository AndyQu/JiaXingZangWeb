package templates
layout 'layout.tpl',
        pageTitle:"乾龙藏-永乐北藏",
        cssAndJs:contents{
            script(type: "text/javascript",src: "/js/page/auto_insert.js"){}
        },
        bodyContent:contents {
            div(class: "panel-head") {
                span("将图片名自动插入到文本文件中")
            }
            div(class: "panel-body") {
                form(enctype: 'multipart/form-data', action: '/rest/gen-excel', method: 'POST') {
                    div(class: "form-group") {
                        label(style: "color:red;") { span("藏经名称") }
                        input(type: 'text', name: 'bookName') {}
                        p(class: "help-block") {
                            yieldUnescaped """乾龙藏 或者 永乐北藏"""
                        }
                    }
                    div(class: "form-group") {
                        label(style: "color:red;") { span("第几册") }
                        input(type: 'text', name: 'volumnNum') {}
                        p(class: "help-block") {
                            yieldUnescaped """如：13"""
                        }
                    }
                    div(class: "form-group") {
                        label(style: "color:red;") { span("标注人") }
                        input(type: 'text', name: 'labelor') {}
                        p(class: "help-block") {
                            yieldUnescaped """如：曲国铖"""
                        }
                    }
                    div(class: "form-group") {
                        label(style: "color:red;") { span("起始字") }
                        input(type: 'text', name: 'startChar') {}
                        p(class: "help-block") {
                            yieldUnescaped """如：'天二  三'中的'天'"""
                        }
                    }
                    div(class: "form-group") {
                        label(style: "color:red;") { span("结束字") }
                        input(type: 'text', name: 'endChar') {}
                        p(class: "help-block") {
                            yieldUnescaped """如：'玄十 十九'中的'玄'"""
                        }
                    }
                    div(class:"checkbox"){
                        label{
                            input(type: "checkbox",name:"skip11121718"){}
                            yieldUnescaped "跳过十一、十二、十七、十九"
                        }
                    }
                    button(type: "submit") {
                        span("生成Excel文件")
                    }
                }
            }
        }
        scriptContent:contents{}