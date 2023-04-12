package com.xhu.jdbc.utils;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
