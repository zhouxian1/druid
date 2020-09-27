package com.hjj.util;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DataBaseUtil {

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public static final String SQL = "SELECT  * FROM ";

    /**
     * 获取所有的数据库
     *
     * @return
     */
    public List<String> getDataBase() {
        List<String> list = new ArrayList<>();
        Connection con = null;
        ResultSet rs = null;
        try {
            con = jdbcTemplate.getDataSource().getConnection();
            DatabaseMetaData md = con.getMetaData();
            rs = md.getCatalogs();
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 获取某数据库下所有的表名和注释
     *
     * @param dataBaseName
     * @return
     */
    public List<Map<String, String>> getTableName(String dataBaseName) {
        List<Map<String, String>> list = new ArrayList<>();
        Connection con = null;
        ResultSet resultSet = null;
        try {
            con = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement stemt = null;
            String tableSql = "select TABLE_NAME,TABLE_COMMENT from INFORMATION_SCHEMA.Tables where table_schema='" + dataBaseName + "' AND  RIGHT(TABLE_NAME,2)!='ls'";
            stemt = con.prepareStatement(tableSql);
            resultSet = stemt.executeQuery();
            while (resultSet.next()) {
                Map map = new HashMap();
                map.put("tableName", resultSet.getString(1));
                map.put("remarks", resultSet.getString(2));
                list.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return list;
    }

    /**
     * 获取某一个表的所有字段
     *
     * @param tableName
     * @return
     */
    public List<String> getColumns(String tableName) {
        List<String> columnTypes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pStemt = null;
        String tableSql = SQL + tableName;
        ResultSet rs = null;
        try {
            conn = jdbcTemplate.getDataSource().getConnection();
            pStemt = conn.prepareStatement(tableSql);
            rs = pStemt.executeQuery("show full columns from " + tableName);
            while (rs.next()) {
                columnTypes.add(rs.getString("Field"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return columnTypes;
    }

    /**
     * 返回某一个表所有字段的注释
     *
     * @param tableName
     * @return
     */
    public List<String> getComments(String tableName) {
        List<String> columnTypes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pStemt = null;
        String tableSql = SQL + tableName;
        ResultSet rs = null;
        try {
            conn = jdbcTemplate.getDataSource().getConnection();
            pStemt = conn.prepareStatement(tableSql);
            rs = pStemt.executeQuery("show full columns from " + tableName);
            while (rs.next()) {
                columnTypes.add(rs.getString("Comment"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return columnTypes;
    }


    /**
     * 获取字段和注释
     *
     * @param tableName
     * @return
     */
    public List<Map<String, String>> getColumnAndComments(String tableName) {
        List<Map<String, String>> columnTypes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pStemt = null;
        String tableSql = SQL + tableName;
        ResultSet rs = null;
        try {
            conn = jdbcTemplate.getDataSource().getConnection();
            pStemt = conn.prepareStatement(tableSql);
            rs = pStemt.executeQuery("show full columns from " + tableName);
            while (rs.next()) {
                Map map = new HashMap();
                map.put("column", rs.getString("Field"));
                map.put("columns", rs.getString("Comment"));
                columnTypes.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return columnTypes;
    }


    /**
     * 单表查询数据
     *
     * @param tableName
     * @param inParamKey
     * @param inParamValue
     * @param outParamKey
     * @return
     */

    public List<Map<String, String>> getOneTableData(String tableName, String[] inParamKey, String[] inParamValue, String[] outParamKey) {
        List<Map<String, String>> columnTypes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pStemt = null;
        String tableSql = SQL + tableName;
        ResultSet rs = null;
        try {
            conn = jdbcTemplate.getDataSource().getConnection();
            pStemt = conn.prepareStatement(tableSql);
            rs = pStemt.executeQuery();
            while (rs.next()) {
                Map map = new HashMap();
                map.put("column", rs.getString("Field"));
                map.put("columns", rs.getString("Comment"));
                columnTypes.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return columnTypes;
    }

    /**
     * 单表统计数据
     *
     * @param tableName
     * @param inParamKey
     * @param inParamValue
     * @param outParamKey
     * @return
     */

    public List<Map> getOneTableCountData(String tableName, String[] inParamKey, String[] inParamValue, String[] outParamKey) {

        return null;
    }

    /**
     * 多表查询数据
     *
     * @param tableName
     * @param inParamKey
     * @param inParamValue
     * @param outParamKey
     * @return
     */

    public List<Map> getMultiTableData(String[] tableName, String[] inParamKey, String[] inParamValue, String[] outParamKey) {

        return null;
    }

    /**
     * 多表表统计数据
     *
     * @param tableName
     * @param inParamKey
     * @param inParamValue
     * @param outParamKey
     * @return
     */

    public List<Map> getMultiTableCountData(String[] tableName, String[] inParamKey, String[] inParamValue, String[] outParamKey) {


        return null;
    }

    /**
     * 单（多）表sql拼接
     *@param  tableName
     * @param  inParamKey
     * @param  inParamValue
     * @param  outParamKey
     * */
    public  String getOneTableSql(String [] tableName, String[] inParamKey, String[] inParamValue, String[] outParamKey){

        return  "";
    }

    /**
     * 验证多表是否可以关联查询
     *@param  tableName
     * */

    public  boolean isOrNo(String tableName){
        return  true;
    }

}
