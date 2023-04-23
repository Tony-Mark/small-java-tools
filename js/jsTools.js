function getQueryString(name) {//如果不会正则法，也可以自己下定义本函数，以在串中查找多个“=”来取出各参数和值
    var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');
    var r = window.location.search.substr(1).match(reg);
    if (r != null) {
        return unescape(r[2]);
    }
    return null;
}

// 是jsonArray用表格展示
function JsonsTOTable(divn, json) { //把json数组数据展示在一个“表格”中
    $("#" + divn).empty();//所原有内容清空
    var s1 = $("<table border='1px' width='90%'>");
    $("#" + divn).append(s1);
    /*alert(json);
    alert("11");*/
    //先输出表头，任何json都能通用
    var keys0 = Object.keys(json[0]);//随便取任意一行，得到键名称列表
    console.log(keys0);
    // alert("12");
    var trHead = $("<tr>");
    s1.append(trHead);

    for (var i = 0; i < keys0.length; i++) {
        var tdHead = $("<td>" + keys0[i] + "</td>");
        trHead.append(tdHead);
    }
    var trEndHead = $("</tr>");
    s1.append(trEndHead);

    for (var i = 0; i < json.length; i++)//处理每行
    {
        var tr0 = $("<tr>");
        s1.append(tr0);
        var keys = Object.keys(json[i]);//得到每个json的键名
        for (var j = 0; j < keys.length; j++) {
            if (json[i][keys[j]] == null)
                var td0 = $("<td>&nbsp</td>");
            else
                var td0 = $("<td>" + json[i][keys[j]] + "</td>"); //输出每i行的第j个键值

            tr0.append(td0);
        }
        var trend0 = $("</tr>");
        s1.append(trend0);
    }
    var send = $("</table>");
    $("#" + divn).append(send);

}
// 填充指定选择框内容
function fillList(elementId, re){
     //随便取一列取出列名
     let keys = Object.keys(re[0]);
     // console.log(keys);
     //将其填充进选择框
     let element = document.getElementById(elementId);
     for (let i = 0; i<re.length; i++){
         let option = document.createElement("option");
         option.value = re[i][keys[0]];
         option.label = re[i][keys[0]];
         element.appendChild(option);
     }
}

// ajax中处理返回的数据
function processRe(elementId, re, flage){
    switch(flage){
     // 填充指定列表内容
     case 1: fillList(elementId, re); break;
     // 将内容展示到指定位置
     case 2: JsonsTOTable(elementId, re); break;
     case 3: console.log("删除成功"); break;
    }
 }


//向后台查询,通用后台程序为select——单条件查询,传值(后台程序的url, 数据库表名, 列名, 控件元素的id)
function SelectForSingleFactor(url0, tableName, filedName, elementId, divId) {
    let factorName = document.getElementById(elementId).value.toString();
    $.ajax({
        type: "post",
        url: url0,
        cache: false,
        data: {"args": tableName + ":" + filedName + ":" + factorName},
        dataType: "Json",
        success: function (re) {
            console.log(re);
            //JsonsTOTable(divId, re);
            // JsonsTOdivTable("info_div",re);
            processRe(elementId, re, 2);
        },
        error: function (re) {
            alert("不成功");
        }
    })
}

// 初始化界面时将数据库中的信息填充进选择框中便于用户查询,传值(后台程序的url, 数据库表名, 所要查询的列名, 控件元素的id, 以何种方式进行排序[1为升序，0为降序])
function InitialList(url0, tableName, filedName, elementId, sortMethod){
    $.ajax({
        type: "post",
        url: url0,
        cache: false,
        data: {
            "tableName" : tableName,
            "filedName" : filedName,
            "sortMethod" : sortMethod
        },
        dataType: "Json",
        success: function (re) {
            console.log(re);
           /* //随便取一列取出列名
            let keys = Object.keys(re[0]);
            console.log(keys);
            //将其填充进选择框
            let element = document.getElementById(elementId);
            for (let i = 0; i<re.length; i++){
                let option = document.createElement("option");
                option.value = re[i][keys[0]];
                option.label = re[i][keys[0]];
                element.appendChild(option);
            }*/
            processRe(elementId,re,1);
        },
        error: function (re) {
            alert("不成功");
        }
    })
}

// 单条件删除
function myDelete(url0, tableName, filedName, selectId){
    let factorName = document.getElementById(selectId).value.toString();
    $.ajax({
        type: "post",
        url: url0,
        cache: false,
        data: {"args": tableName + ":" + filedName + ":" + factorName},
        dataType: "Json",
        success: function (re) {
            console.log(re);
            processRe(selectId, re, 3);
        },
        error: function (re) {
            alert("不成功");
        }
    })
}
// 多条件删除
function myDelete2(url0, tableName, filedName1, filedName2, selectId1, selectId2){
    let factorName1 = document.getElementById(selectId1).value.toString();
    let factorName2 = document.getElementById(selectId2).value.toString();
    filedName = filedName1+"-"+filedName2;
    factorName = factorName1+"-"+factorName2;
    $.ajax({
        type: "post",
        url: url0,
        cache: false,
        data: {"args": tableName + ":" + filedName + ":" + factorName},
        dataType: "Json",
        success: function (re) {
            console.log(re);
            processRe(selectId1, re, 3);
        },
        error: function (re) {
            alert("不成功");
        }
    })
}