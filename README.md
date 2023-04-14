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
  |  resultSetToJson(ResultSet resultSet, JSONArray jsonArray)   | 需要先查询。将ResultSet结果集转换成json数组，主要使用ResultSetMetaData来封装ResultSet |
  | resultSetToJson(String sql, JSONArray jsonArray, String ... args) | 不需要先查询。将ResultSet结果集转换成json数组，主要使用ResultSetMetaData来封装ResultSet |
  |       resultSetToJson(String sql, JSONArray jsonArray)       | 不需要先查询。但是仅针对无条件查询所有数据，将ResultSet结果集转换成json数组，主要使用ResultSetMetaData来封装ResultSet |
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
|                   JsonsTOTable(divn, json)                   |                    是jsonArray用表格展示                     |
| SelectForSingleFactor( url0, tableName, filedName, elementId) | 向后台查询,通用后台程序为select——单条件查询,传值(后台程序的url, 数据库表名, 列名, 控件元素的id) |
| InitialList(url0, tableName, filedName, elementId, sortMethod) | 初始化界面时将数据库中的信息填充进选择框中便于用户查询,传值(后台程序的url, 数据库表名, 所要查询的列名, 控件元素的id, 以何种方式进行排序[1为升序，0为降序]) |

