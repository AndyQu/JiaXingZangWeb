yieldUnescaped '<!DOCTYPE html>'
html {
    head {
        title{yieldUnescaped pageTitle}
        script(type: "text/javascript", src: "/js/lib/jquery.js") {}

        link(rel: 'stylesheet', href: '/css/lib/bootstrap.css') {}
        script(type: "text/javascript", src: "/js/lib/bootstrap.js") {}
        cssAndJs()
    }
    body {
        nav(class:"navbar navbar-default sidebar",role:"navigation"){
            div(class:"container-fluid"){
                div(class:"collapse navbar-collapse") {
                    ul(class: "nav navbar-nav") {
                    }
                }
            }
        }
        bodyContent()
    }
    script{
        scriptContent()
    }
}