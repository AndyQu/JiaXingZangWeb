function doAutoInsert() {
    var formData = new FormData();

    formData.append("label_excel", $("#label_excel")[0].files[0]);
    var txtFiles=$("#txt_files")[0].files;
    for(var i=0;i<txtFiles.length;i++){
        formData.append("txt_files", txtFiles[i]); // number 123456 is immediately converted to a string "123456"
    }

// HTML file input, chosen by user
//     formData.append("userfile", fileInputElement.files[0]);

// JavaScript file-like object
//     var content = '<a id="a"><b id="b">hey!</b></a>'; // the body of the new file...
//     var blob = new Blob([content], { type: "text/xml"});
//
//     formData.append("webmasterfile", blob);

    $.ajax({
        type: "POST",
        url: "/autoInsert",  //同目录下的php文件
        timeout:0,
        data:formData,
        dataType:"json", //声明成功使用json数据类型回调

        //如果传递的是FormData数据类型，那么下来的三个参数是必须的，否则会报错
        cache:false,  //默认是true，但是一般不做缓存
        processData:false, //用于对data参数进行序列化处理，这里必须false；如果是true，就会将FormData转换为String类型
        contentType:false,  //一些文件上传http协议的关系，自行百度，如果上传的有文件，那么只能设置为false

        success: function(response){  //请求成功后的回调函数

            alert("转换完成~~~");
            $("#result_text").val(response.data);
            var link = document.getElementById("a_download");
            link.href = 'data:text/plain;charset=UTF-8,' + encodeURIComponent(response.data);
            link.innerHtml = '点击下载';
//set default action on link to force download, and set default filename:
            link.download = $("#label_excel")[0].files[0].name.replace(".xls","").replace(".xlsx","")+"_inserted.txt";

//now put the link somewhere in the html document:
//             document.body.appendChild(link);
        },
        error: function (response) {
            alert("转换失败!!!");
            console.log(JSON.stringify(response))
        }
    });

}