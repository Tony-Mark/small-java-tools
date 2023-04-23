package com.jdbc.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletRequest;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @author TonyJUN
 */
public class JDBCUtils {
  private JDBCUtils() {}

  public static String driver;
  public static String url;
  public static String username;
  public static String pwd;

  private static Properties pros = new Properties();

  static {
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
  
 /**
 *@description: TODO 连接数据库
 *@param: []
 *@return: java.sql.Connection
 */
  public static Connection getConnection() {
    try {
      return DriverManager.getConnection(url, username, pwd);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }
  
 /**
 *@description: TODO 预编译SQL语句
 *@param: [sql, args]
 *@return: java.sql.ResultSet
 */
  public static ResultSet preparedSqlForSelect(String sql,String ... args) throws SQLException {
    Connection connection = getConnection();
    assert connection != null;
    PreparedStatement pst = connection.prepareStatement(sql);
    ResultSet rst = null;
    int index = 1;
    for( ;index<= args.length ;index++){
      pst.setString(index, args[index-1]);
      // rst = pst. executeQuery();
    }
    /*当有多个？时需要将?填充完再执行sql */
    rst = pst.executeQuery();
    return rst;
  }
  /**
  *@description: TODO 不需要预编译的select语句
  *@param: [sql]
  *@return: java.sql.ResultSet
  */
  public static ResultSet preparedSqlForSelect(String sql) throws SQLException {
    Connection connection = getConnection();
    assert connection != null;
    Statement statement = connection.createStatement();
    return statement.executeQuery(sql);
  }
  /**插入数据*/
  public static int preparedSqlForInsert(String sql,Object object) throws NullPointerException, IllegalAccessException, SQLException {
    Connection connection = getConnection();
    PreparedStatement pst = null;
    int index = 1;
    try {
      assert connection != null;
      pst = connection.prepareStatement(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      System.out.println("失败！");
    }
    Field[] fields = object.getClass().getDeclaredFields();
    for (Field field : fields){
      field.setAccessible(true);
      assert pst != null;
      pst.setString(index++, field.get(object).toString());
    }
    assert pst != null;
    return pst.executeUpdate();
  }
  
  /**需要搭配jsTools.js文件实现任意单条件的数据库查询,并将其转换为json数组形式*/
  public static void selectForSingleFactor(HttpServletRequest request, JSONArray jsonArray){
    String args = request.getParameter("args");
    String[] args0 = args.split(":");
    String sql = "select * from " + args0[0] +" where " +args0[1]+" = ?";
    try {
      JDBCUtils.resultSetToJson(sql, jsonArray, args0[2]);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  public static void selectForSingleFactor(String sql, JSONArray jsonArray, String ... args) throws SQLException {
    JDBCUtils.resultSetToJson(sql, jsonArray, args);
  }
  /**初始化查询选择数据*/
  public static void selectInitialize(HttpServletRequest request, JSONArray jsonArray){
    String tableName = request.getParameter("tableName");
    String filedName = request.getParameter("filedName");
    String sortMethod = request.getParameter("sortMethod");
    String sql = null;
    if (Objects.equals(sortMethod, "0")) {
      sql = "select distinct " + filedName + " from " + tableName + " order by 1 DESC";
    } else {
      sql = "select distinct " + filedName + " from " + tableName;
    }
    try {
      JDBCUtils.resultSetToJson(sql, jsonArray);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  /**实现单条件数据删除*/
  public static void deleteForSingleFactor(String sql, JSONArray jsonArray, String ... args) throws SQLException {
    Connection connection = getConnection();
    assert connection != null;
    PreparedStatement pst = connection.prepareStatement(sql);
    int index = 1;
    for( ;index<= args.length ;index++){
      pst.setString(index, args[index-1]);
    }
    /*执行删除操作*/
    int re = pst.executeUpdate();
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("re", re);
    System.out.println(jsonObject);
    jsonArray.add(jsonObject);
  }
  /**可实现多条件数据删除*/
  public static void deleteForSingleFactor(HttpServletRequest request, JSONArray jsonArray) throws SQLException {
    Connection connection = getConnection();
    String args = request.getParameter("args");
    String[] args0 = args.split(":");
    /*分解数据库列名*/
    String[] filedNames = args0[1].split("-");
    /*分解数据值*/
    String[] values = args0[2].split("-");
    String sql = "delete from " + args0[0] + " where ";
    /*拼凑sql串*/
    for (String filedName : filedNames) {
      sql += filedName + " = ? " + "and ";
    }
    /*去掉最后一个and,包含最后一个空格共四个字符*/
    sql = sql.substring(0, sql.length() - 4);
    System.out.println(sql);
    assert connection != null;
    PreparedStatement pst = connection.prepareStatement(sql);
    int index = 1;
    for (; index <= values.length; index++) {
      pst.setString(index, values[index - 1]);
    }
    /*执行删除操作*/
    int re = pst.executeUpdate();
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("re", re);
    System.out.println(jsonObject);
    jsonArray.add(jsonObject);
  }
  
  /**需要先查询。将ResultSet结果集转换成json数组，主要使用ResultSetMetaData来封装ResultSet*/
  public static void resultSetToJson(ResultSet resultSet, JSONArray jsonArray) throws SQLException{
    ResultSetMetaData rsmd = null;
    rsmd = resultSet.getMetaData();
    int colNum = rsmd.getColumnCount();
    while (resultSet.next()) {
      JSONObject jsonObject = new JSONObject(true);
      for (int col = 1; col <= colNum; col++) {
        /*getColumnName和getString下标都是从1开始*/
        jsonObject.put(rsmd.getColumnName(col), resultSet.getString(col));
      }
      jsonArray.add(jsonObject);
    }
  }
  /**不需要先查询。将ResultSet结果集转换成json数组，主要使用ResultSetMetaData来封装ResultSet*/
  public static void resultSetToJson(String sql, JSONArray jsonArray, String ... args) throws SQLException{
    ResultSet resultSet = preparedSqlForSelect(sql, args);
    ResultSetMetaData rsmd = null;
    rsmd = resultSet.getMetaData();
    int colNum = rsmd.getColumnCount();
    while (resultSet.next()) {
      JSONObject jsonObject = new JSONObject(true);
      for (int col = 1; col <= colNum; col++) {
        /*getColumnName和getString下标都是从1开始*/
        jsonObject.put(rsmd.getColumnName(col), resultSet.getString(col));
      }
      jsonArray.add(jsonObject);
    }
  }
  /**不需要先查询。将ResultSet结果集转换成json数组，主要使用ResultSetMetaData来封装ResultSet*/
  public static void resultSetToJson(String sql, JSONArray jsonArray) throws SQLException{
    ResultSet resultSet = preparedSqlForSelect(sql);
    ResultSetMetaData rsmd = null;
    rsmd = resultSet.getMetaData();
    int colNum = rsmd.getColumnCount();
    while (resultSet.next()) {
      JSONObject jsonObject = new JSONObject(true);
      for (int col = 1; col <= colNum; col++) {
        /*getColumnName和getString下标都是从1开始*/
        jsonObject.put(rsmd.getColumnName(col), resultSet.getString(col));
      }
      jsonArray.add(jsonObject);
    }
  }
  
  /**将查询出的单个结果集映射成对象*/
  public static <T> T resultSetToModel(String sql, Class<T> cls, String ... args) throws SQLException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    ResultSet resultSet = preparedSqlForSelect(sql, args);
    /*创建对象，便于后续反射*/
    Object object = null;
    while (resultSet.next()){
      /*创建类的实体对象*/
      object = cls.getConstructor().newInstance();
      /*利用反射获取该类中的所有属性信息*/
      Field[] fields = cls.getDeclaredFields();
      for (Field field : fields){
        /*允许获取private成员变量*/
        field.setAccessible(true);
        /*为对象相应属性赋值*/
        field.set(object, resultSet.getObject(field.getName()));
      }
    }
    return (T) object;
  }
  /**查询出来的结果集映射成list*/
  public static <T> List<T> resultSetToModelList(String sql, Class<T> cls, String ... args) throws SQLException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    List<T> list = new ArrayList<>();
    ResultSet resultSet = preparedSqlForSelect(sql, args);
    /*创建对象，便于后续反射*/
    Object object = null;
    while (resultSet.next()){
      /*创建类的实体对象*/
      object = cls.getConstructor().newInstance();
      /*利用反射获取该类中的所有属性信息*/
      Field[] fields = cls.getDeclaredFields();
      for (Field field : fields){
        /*允许获取private成员变量*/
        field.setAccessible(true);
        /*为对象相应属性赋值*/
        field.set(object, resultSet.getObject(field.getName()));
      }
      list.add((T)object);
    }
    return list;
  }
  
  /**关闭连接的重载close*/
  public static void close(ResultSet rs,Connection conn){
    if (rs != null) {
      try {
        rs.close(); // 如果关闭时，又出现了异常，没有被关闭，所以添加finally
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        rs = null;
      }
    }
    if (conn != null) {
      try {
        conn.close(); // 如果关闭时，又出现了异常，没有被关闭，所以添加finally
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        conn = null;
      }
    }
  }
  
  public static void close(Statement st,Connection conn){
    if (st != null) {
      try {
        st.close(); // 如果关闭时，又出现了异常，没有被关闭，所以添加finally
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        st = null;
      }
    }
    if (conn != null) {
      try {
        conn.close(); // 如果关闭时，又出现了异常，没有被关闭，所以添加finally
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        conn = null;
      }
    }
  }
  
  public static void close(Connection conn) {
      try {
        if (conn != null) {
          conn.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }finally {
        conn = null;
      }
  }
  
  public static void close(ResultSet rs){
    if (rs != null) {
      try {
        rs.close(); // 如果关闭时，又出现了异常，没有被关闭，所以添加finally
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        rs = null;
      }
    }
  }
  
  public static void close(ResultSet rs, Statement st, Connection conn) {
    if (rs != null) {
      try {
        rs.close(); // 如果关闭时，又出现了异常，没有被关闭，所以添加finally
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        rs = null;
      }
    }
    if (st != null) {
      try {
        st.close(); // 如果关闭时，又出现了异常，没有被关闭，所以添加finally
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        st = null;
      }
    }
    if (conn != null) {
      try {
        conn.close(); // 如果关闭时，又出现了异常，没有被关闭，所以添加finally
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        conn = null;
      }
    }
  }
  
  /**
  *@description: TODO 将日期格式化为sql格式
  *@param: [s]
  *@return: java.sql.Date
  */
  public static Date changeToDate(String s) {
    java.util.Date d = null;
    Date birthday = null;
    try {
      d = new SimpleDateFormat("yyyy-MM-dd").parse(s);
      birthday = new Date(d.getTime());
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return birthday;
  }
}
