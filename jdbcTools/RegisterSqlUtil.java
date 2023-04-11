package com.xhu.jdbc.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @BelongsProject: register
 * @Author: XJ
 * @CreateTime: 2023-03-10 11:58
 * @Description: TODO
 * @Version: 1.0
 */
@MultipartConfig
public class RegisterSqlUtil extends HttpServlet {
  private static String savePath = "E:/uploadfile";
  /** 拼接sql字符串 */
  public static String getSqlString(
      String tbName, HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    /*数据库语句*/
    String sqlString = "insert into " + tbName + "(";
    /*累加保存数据库列名*/
    StringBuilder fieldStr0 = new StringBuilder();
    /*累加保存数据值串*/
    StringBuilder valuesStr0 = new StringBuilder();
    Collection<Part> parts = request.getParts();
    System.out.println(parts.size());
    /*文件路径*/
    // String savePath = "E:\\uploadfile";
    /*用于存放一个或多个文件在数据库中的文件名字*/
    StringBuilder fileNameString = new StringBuilder();
    /*存放到数据库文件列名*/
    StringBuilder fieldFilename0 = new StringBuilder();
    /*默认无文件上传*/
    boolean file0 = false;
    /*通过名字获取一个参数枚举类型*/
    Enumeration<String> parameterNames = request.getParameterNames();
    for (Part part : parts) {
      if (part.getContentType() == null) {
        /*处理普通数据*/
        while (parameterNames.hasMoreElements()) {
          String str0 = parameterNames.nextElement();
          if (request.getParameterValues(str0).length > 1) {
            // 表示同一个name有多个选项值，使用数组接收，例如多种爱好,日期，地址
            StringBuilder valueString = new StringBuilder();
            System.out.print(str0 + ":");
            for (int i = 0; i < request.getParameterValues(str0).length; i++) {
              valueString.append(request.getParameterValues(str0)[i]).append("-");
            }
            // 去掉最后多余的-
            valueString = new StringBuilder(valueString.substring(0, valueString.length() - 1));
            System.out.println(valueString);
            valueString.append("'").append(",");
            // 凑数据操作串
            fieldStr0.append(str0).append(",");
            valuesStr0.append("'").append(valueString);
          } else { // 不是集合、日期、地址的一般数据项
            System.out.print(str0 + ":");
            System.out.print(request.getParameter(str0));
            System.out.println();
            // 凑成数据库操作的insert插入串
            fieldStr0.append(str0).append(",");
            valuesStr0.append("'").append(request.getParameter(str0)).append("',");
          }
        }
      } else {
        /*处理文件部分*/
        String header = part.getHeader("Content-Disposition");
        /*取得提交的文件名*/
        String fileName = part.getSubmittedFileName();
        /*获取filename=后面的内容*/
        int start = header.lastIndexOf("=");
        fileName = header.substring(start + 1);
        /*说明至少有一个文件上传了*/
        file0 = true;
        /*判断是否是相对路径名*/
        if (fileName.lastIndexOf(":") == -1) {
          // 说明是相对路径
          fileName = fileName.substring(1, fileName.length() - 1);
        } else {
          /*绝对路径文件名*/
          /*从后往前找\\来取真正的文件名*/
          fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
          /*去掉最后的“*/
          fileName = fileName.substring(0, fileName.length() - 1);
        }
        /*添加文件名前的临时名*/
        String temp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        fileName =tbName+ "/"+ temp + "_" + fileName;
        part.write(savePath + "/" + fileName);
        /*插入数据库命令串*/
        fileNameString.append(fileName).append("*");
        /*取出文件名的name，注意，在HTML要标准化把多个文件的name统一，否则取出与数据库列名不一致*/
        fieldFilename0 = new StringBuilder(part.getName());
      }
    }
    if (file0) {
      /*说明有文件上传，至少一个文件*/
      fieldStr0.append(fieldFilename0).append(",");
      if (fileNameString.lastIndexOf("*") > 1) {
        fileNameString =
            new StringBuilder(fileNameString.substring(0, fileNameString.length() - 1));
      }
      valuesStr0.append("'").append(fileNameString).append("',");
    }
    // 去掉列名最后逗号
    fieldStr0 = new StringBuilder(fieldStr0.substring(0, fieldStr0.length() - 1));
    valuesStr0 = new StringBuilder(valuesStr0.substring(0, valuesStr0.length() - 1));
    sqlString = sqlString + fieldStr0 + ") values(" + valuesStr0 + ")";
    return sqlString;
  }
  
  /**将数据映射对象，其中爱好，日期，地址，文件名类型使用StringBuilder类型进行拼接，创建相应对象类时使用StringBuilder类型*/
  public  static void requestToModelHaveFile(String tbName, Object object, HttpServletRequest request)throws ServletException, IOException{
    request.setCharacterEncoding("UTF-8");
    Collection<Part> parts = request.getParts();
    System.out.println(parts.size());
    /*文件路径*/
    
    /*用于存放一个或多个文件在数据库中的文件名字*/
    StringBuilder fileNameString = new StringBuilder();
    /*存放到数据库文件列名*/
    StringBuilder fieldFilename0 = new StringBuilder();
    /*默认无文件上传*/
    boolean file0 = false;
    /*在循环之前将元素取出，避免重复多次遍历*/
    Enumeration<String> parameterNames = request.getParameterNames();
    for (Part part : parts){
      if (part.getContentType() == null) {
        /*处理普通数据*/
        while (parameterNames.hasMoreElements()) {
          StringBuilder str0 = new StringBuilder(parameterNames.nextElement());
          if (request.getParameterValues(String.valueOf(str0)).length > 1) {
            // 表示同一个name有多个选项值，使用数组接收，例如多种爱好,日期，地址
            StringBuilder valueString = new StringBuilder();
            for (int i = 0; i < request.getParameterValues(String.valueOf(str0)).length; i++) {
              valueString.append(request.getParameterValues(String.valueOf(str0))[i]).append("-");
            }
            // 去掉最后多余的-
            valueString = new StringBuilder(valueString.substring(0, valueString.length() - 1));
            Field field = null;
            try {
              field = object.getClass().getDeclaredField(String.valueOf(str0));
            } catch (NoSuchFieldException e) {
              e.printStackTrace();
            }
            assert field != null;
            field.setAccessible(true);
            try {
              field.set(object, valueString);
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            }
          }else{
            /*单个数据*/
            Field field = null;
            try {
              field = object.getClass().getDeclaredField(String.valueOf(str0));
            } catch (NoSuchFieldException e) {
              e.printStackTrace();
            }
            assert field != null;
            field.setAccessible(true);
            try {
              field.set(object, request.getParameter(String.valueOf(str0)));
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            }
          }
        }
      }else{
        /*处理文件类*/
        /*取得提交的文件名*/
        String fileName = part.getSubmittedFileName();
        String header = part.getHeader("Content-Disposition");
        /*获取filename=后面的内容*/
        int start = header.lastIndexOf("=");
        fileName = header.substring(start + 1);
        /*说明至少有一个文件上传了*/
        file0 = true;
        /*判断是否是相对路径名*/
        if (fileName.lastIndexOf(":") == -1) {
          // 说明是相对路径
          fileName = fileName.substring(1, fileName.length() - 1);
        } else {
          /*绝对路径文件名*/
          /*从后往前找\\来取真正的文件名*/
          fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
          /*去掉最后的“*/
          fileName = fileName.substring(0, fileName.length() - 1);
        }
        /*添加文件名前的临时名*/
        String temp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        fileName =tbName +"/"+ temp + "_" + fileName;
        String path = savePath + "/" + fileName;
        System.out.println(path);
        part.write(path);
        /*插入数据库文件名串*/
        fileNameString.append(fileName).append("*");
        /*取出文件名的name，注意，在HTML要标准化把多个文件的name统一，否则取出与数据库列名不一致*/
        fieldFilename0 = new StringBuilder(part.getName());
      }
    }
    if(file0){
      if(fileNameString.lastIndexOf("*")>1){
        fileNameString = new StringBuilder(fileNameString.substring(0,fileNameString.length()-1));
      }
      Field field = null;
      try {
        field = object.getClass().getDeclaredField(String.valueOf(fieldFilename0));
      } catch (NoSuchFieldException e) {
        e.printStackTrace();
      }
      assert field != null;
      field.setAccessible(true);
      try {
        field.set(object, fileNameString);
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }
  
  /**数据映射对象，不带文件*/
  public  static void requestToModel(Object object, HttpServletRequest request) throws UnsupportedEncodingException {
    request.setCharacterEncoding("UTF-8");
    /*在循环之前将元素取出，避免重复多次遍历*/
    Enumeration<String> parameterNames = request.getParameterNames();
        /*处理普通数据*/
        while (parameterNames.hasMoreElements()) {
          StringBuilder str0 = new StringBuilder(parameterNames.nextElement());
          if (request.getParameterValues(String.valueOf(str0)).length > 1) {
            // 表示同一个name有多个选项值，使用数组接收，例如多种爱好,日期，地址
            StringBuilder valueString = new StringBuilder();
            for (int i = 0; i < request.getParameterValues(String.valueOf(str0)).length; i++) {
              valueString.append(request.getParameterValues(String.valueOf(str0))[i]).append("-");
            }
            // 去掉最后多余的-
            valueString = new StringBuilder(valueString.substring(0, valueString.length() - 1));
            Field field = null;
            try {
              field = object.getClass().getDeclaredField(String.valueOf(str0));
            } catch (NoSuchFieldException e) {
              e.printStackTrace();
            }
            assert field != null;
            field.setAccessible(true);
            try {
              field.set(object, valueString);
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            }
          } else {
            /*单个数据*/
            Field field = null;
            try {
              field = object.getClass().getDeclaredField(String.valueOf(str0));
            } catch (NoSuchFieldException e) {
              e.printStackTrace();
            }
            assert field != null;
            field.setAccessible(true);
            try {
              field.set(object, request.getParameter(String.valueOf(str0)));
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            }
          }
        }
  }
  /**遍历*/
  public static void traversal(Object object){
    Field[] fields = object.getClass().getDeclaredFields();
    for (Field f0 : fields) {
      f0.setAccessible(true);
      String value0 = "";
      try {
        value0 = f0.get(object).toString();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
      System.out.println(f0.getName() + ":" + value0);
    }
  }
  
  /**通过MVC模式拼接数据库插入语句*/
  public static String insertDb(String dbName, Object object){
    String sqlString = "insert into " + dbName + "(";
    Field[] fields = object.getClass().getDeclaredFields();
    StringBuilder fieldstr = new StringBuilder();
    StringBuilder valStr = new StringBuilder();
    for (Field field : fields){
      field.setAccessible(true);
      String name = field.getName();
      StringBuilder value0 = new StringBuilder();
      try {
        value0 = new StringBuilder(field.get(object).toString());
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
      fieldstr.append(name).append(",");
      valStr.append("'").append(value0).append("',");
    }
    /*去掉字符串最后的逗号*/
    fieldstr = new StringBuilder(fieldstr.substring(0, fieldstr.length() - 1));
    valStr = new StringBuilder(valStr.substring(0, valStr.length() - 1));
    fieldstr = new StringBuilder(fieldstr + ") values(");
    sqlString = sqlString + fieldstr + valStr + ")";
    return sqlString;
  }
  /**单个对象映射成json数组*/
  public static void modelToJsonArray(Object c0, JSONArray jsArray0) {
    JSONObject js0 = new JSONObject(true);
    Field[] fields = c0.getClass().getDeclaredFields();
    for (Field f0 : fields) {
      f0.setAccessible(true);
      String name0 = f0.getName();
      // String value0 = "";
      Object value0 = "";
      try {
        value0 = f0.get(c0);
      } catch (IllegalArgumentException | IllegalAccessException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      js0.put(name0, value0);
      
    }
    jsArray0.add(js0);
    
  }
  /**对象数组映射为json数组*/
  public static void modelToJsonArray(Object []objects, JSONArray jsonArray){
    for (Object c0:objects){
      JSONObject jsonObject = new JSONObject(true);
      Field[] fields = c0.getClass().getDeclaredFields();
      for (Field f0 : fields) {
        f0.setAccessible(true);
        String name0 = f0.getName();
        Object value0 = "";
        try {
          value0 = f0.get(c0);
        } catch (IllegalArgumentException | IllegalAccessException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        jsonObject.put(name0, value0);
      }
      jsonArray.add(jsonObject);
    }
  }
  
  public static void setCode(HttpServletRequest request, HttpServletResponse response) {
    try {
      request.setCharacterEncoding("utf-8");
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    response.setCharacterEncoding("utf-8");
    response.setContentType("text/html;charset=utf-8");
  }
}
