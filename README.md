# small-java-tools

some small tools for java learn

## 在jdbcTools包中有两个类：

- JDBCUtils类主要是jdbc数据库连接，关闭，以及预编译查询、插入数据库语句的方法。

- RegisterSqlUtil类中主要是前端注册表的数据映射至模式的方法。

  ``在JDBCUtils类中的一些方法。``

  ```java
  static {//静态加载变量
      try {
        /*系统类加载器获取配置文件*/
        // InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc.properties");
        /*加载jdbc配置文件,使用当前类加载器获取配置文件*/
        InputStream in = JDBCUtils.class.getClassLoader().getResourceAsStream("jdbc.properties");
        pros.load(in);
        driver = pros.getProperty("jdbc.driver");
        url = pros.getProperty("jdbc.url");
        username = pros.getProperty("jdbc.username");
        pwd = pros.getProperty("jdbc.password");
        Class.forName(driver);
      } catch (Exception e) {
        throw new ExceptionInInitializerError(e);
      }
    }
  ```

  

  |                            方法名                            |                             说明                             |
  | :----------------------------------------------------------: | :----------------------------------------------------------: |
  |                       getConnection()                        |                          连接数据库                          |
  |       preparedSqlForSelect(String sql,String ... args)       |           预编译查询语句，参数为sql语句和多个条件            |
  |               preparedSqlForSelect(String sql)               |                   不需要预编译的查全部语句                   |
  |        preparedSqlForInsert(String sql,Object object)        |          预编译插入语句，其中object主要是映射至模式          |
  | preparedSqlForUpdate(String sql, Object object, String ... args) |             预编译更新语句，主要使用对象进行更新             |
  | selectForSingleFactor(HttpServletRequest request, JSONArray jsonArray) | 需要搭配jsTools.js文件实现任意单条件的数据库查询,并将其转换为json数组形式 |
  | selectForSingleFactor(String sql, JSONArray jsonArray, String ... args) |                                                              |
  | selectForSingleFactor2(String sql, JSONArray jsonArray, Integer delete, Integer update, String ... args) |            用于向查询出的数据中添加删除、修改功能            |
  | selectForSingleFactor2(String sql, JSONArray jsonArray, Integer delete, Integer update) |                                                              |
  | selectInitialize(HttpServletRequest request, JSONArray jsonArray) |                      初始化查询选择数据                      |
  | deleteForSingleFactor(String sql, JSONArray jsonArray, String ... args) |                      实现单条件数据删除                      |
  | deleteForSingleFactor(HttpServletRequest request, JSONArray jsonArray) |                     可实现多条件数据删除                     |
  |  resultSetToJson(ResultSet resultSet, JSONArray jsonArray)   | 需要先查询。将ResultSet结果集转换成json数组，主要使用ResultSetMetaData来封装ResultSet |
  | resultSetToJson(String sql, JSONArray jsonArray, String ... args) | 不需要先查询。将ResultSet结果集转换成json数组，主要使用ResultSetMetaData来封装ResultSet |
  |       resultSetToJson(String sql, JSONArray jsonArray)       | 不需要先查询。但是仅针对无条件查询所有数据，将ResultSet结果集转换成json数组，主要使用ResultSetMetaData来封装ResultSet |
  | T resultSetToModel(String sql, Class<T> cls, String ... args) |                将查询出的单个结果集映射成对象                |
  | List<T> resultSetToModelList(String sql, Class<T> cls, String ... args) |                  查询出来的结果集映射成list                  |
  |                          close(..)                           |                     重载的数据库关闭方法                     |
  |                    changeToDate(String s)                    |                 将时间格式化为数据库格式时间                 |
  
  

``在RegisterSqlUtil类中的主要方法``

|                            方法名                            |                          说明                          |
| :----------------------------------------------------------: | :----------------------------------------------------: |
| requestToModelHaveFile(String tbName, Object object, HttpServletRequest request) | 前端表单如果传递的数据含有文件就使用该方法进行对象映射 |
|  requestToModel(Object object, HttpServletRequest request)   |                 不带文件数据的对象映射                 |
|                   traversal(Object object)                   |                     遍历映射的对象                     |
|       modelToJsonArray(Object c0, JSONArray jsArray0)        |      将对象添加转换成JSON格式的数组，便于传向前端      |
|   modelToJsonArray(Object []objects, JSONArray jsonArray)    |    将一个对象数组转换成JSON格式的数组，便于传向前端    |
| setCode(HttpServletRequest request, HttpServletResponse response) |      设置request、response的编码格式（UTF-8格式）      |

## js文件下的方法及使用

``jsTools.js``

|                            方法名                            |                             说明                             |
| :----------------------------------------------------------: | :----------------------------------------------------------: |
|                     getQueryString(name)                     |                   获取url中对应key的value                    |
|                       lengthRate(json)                       |                    表格自适应--目前未成功                    |
|                   JsonsTOTable(divn, json)                   |                    是jsonArray用表格展示                     |
|                   fillList(elementId, re)                    |                      填充指定选择框内容                      |
|               processRe(elementId, re, flage)                |                     ajax中处理返回的数据                     |
| SelectForSingleFactor0( url0, tableName, filedName, elementId) | 向后台查询,通用后台程序为select——单条件查询,传值(后台程序的url, 数据库表名, 列名, 控件元素的id) |
| SelectForSingleFactor1(url0, tableName, filedName, elementId, divId, filedNames) | 传值(后台程序的url, 数据库表名, 列名, 控件元素的id,查询出的列名) |
|        SelectAll(url0, tableName, divId, filedNames)         |                查询全部信息，并指定查询的列名                |
| InitialList(url0, tableName, filedName, elementId, sortMethod) | 初始化界面时将数据库中的信息填充进选择框中便于用户查询,传值(后台程序的url, 数据库表名, 所要查询的列名, 控件元素的id, 以何种方式进行排序[1为升序，0为降序]) |
|        myDelete(url0, tableName, filedName, selectId)        |                          单条件删除                          |
| myDelete2(url0, tableName, filedName1, filedName2, selectId1, selectId2) |                          多条件删除                          |
|         myUpdate(url0, tableName, filedName, value)          |                     修改数据前进行的查询                     |
| myUpdate1(url0, tableName, filedName1, filedName2, value1, value2) |                  两个主键的修改数据前的查询                  |
|                 getFormAllData(url0, formId)                 |             获取表单数据，向指定的后台程序传数据             |

```js
function JsonsTOTable(divn, json) { //把json数组数据展示在一个“表格”中
    $("#" + divn).empty();//所原有内容清空
    var s1 = $("<table class='table0'>");
    let length0 = lengthRate(json);
    console.log(length0)
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
        var tdHead = $("<th>" + keys0[i] + "</th>");
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
```

```js
// 填充指定选择框内容
function fillList(elementId, re){
     // 清除原有内容
     $("#"+elementId).empty();
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
```

```js
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
```



```js
//向后台查询,通用后台程序为select——单条件查询,传值(后台程序的url, 数据库表名, 列名, 控件元素的id, 展示的divID)
function SelectForSingleFactor0(url0, tableName, filedName, elementId, divId) {
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
            processRe(divId, re, 2);
        },
        error: function (re) {
            alert("不成功");
        }
    })
}
```

```js
// 初始化界面时将数据库中的信息填充进选择框中便于用户查询,传值(后台程序的url, 数据库表名, 所要查询的列名, 控件元素的id, 以何种方式进行排序[1为升序，0为降序])
function InitialList(url0, tableName, filedName, elementId, sortMethod){
    let re0 = {};
    $.ajax({
        type: "post",
        url: url0,
        cache: false,
        async:false,
        data: {
            "tableName" : tableName,
            "filedName" : filedName,
            "sortMethod" : sortMethod
        },
        dataType: "Json",
        success: function (re) {
            console.log(re);
            re0 = re;
            processRe(elementId,re,1);
        },
        error: function (re) {
            alert("不成功");
        }
    })
    return re0;
}
```

