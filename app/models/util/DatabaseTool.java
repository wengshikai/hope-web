package models.util;

import models.GlobalTool;
import play.db.DB;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by fengya on 15-10-25.
 */
public class DatabaseTool {
    public static EntityManagerFactory defaultFactory;
    public static EntityManager defaultEm ;
    public static EntityManagerFactory userFactory;
    public static EntityManager userEm ;

    static {
        defaultFactory = Persistence.createEntityManagerFactory("defaultPersistenceUnit");
        defaultEm = defaultFactory.createEntityManager();
        userFactory = Persistence.createEntityManagerFactory("userPersistenceUnit");
        userEm = userFactory.createEntityManager();
    }


    public static EntityManager getDefaultEntityManager(){
        return defaultFactory.createEntityManager();
    }



    public static void dosql(String db,String sql){
        DataSource ds = DB.getDataSource(db);
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ds.getConnection();
            StringBuffer ddl = new StringBuffer();
            ddl.append(sql);
            stmt = conn.prepareStatement(ddl.toString());
            stmt.execute();
        } catch (SQLException e) {
            GlobalTool.loger.error("something error", e);
        }finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e1) {
                    GlobalTool.loger.error("something error",e1);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    GlobalTool.loger.error("something error",e);
                }
            }
        }

    }

    private static void statementSetHelper(PreparedStatement stmt,int index,Object value) throws SQLException {
        stmt.setObject(index,value);
    }

    public static void insertTable(String dbname, String tablename,ArrayList<Object> values) {
        DataSource ds = DB.getDataSource(dbname);
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ds.getConnection();
            StringBuffer ddl = new StringBuffer();
            ddl.append("insert into " + tablename + " values(?");
            for (int i = 0; i < values.size() - 1; i++) {
                ddl.append(",?");
            }
            ddl.append(");");
            stmt = conn.prepareStatement(ddl.toString());
            for (int i = 0; i < values.size(); i++) {
                statementSetHelper(stmt, (i + 1), values.get(i));
            }
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e1) {
                    GlobalTool.loger.error("something error", e1);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e1) {
                    GlobalTool.loger.error("something error", e1);
                }
            }
        }
    }


    public static String selectTable(String dbname, String sql) {
        DataSource ds = DB.getDataSource(dbname);
        Connection conn = null;
        Statement st = null;
        String resstr = "";

        try {
            conn = ds.getConnection();
            ResultSet result = null;
            st = conn.createStatement();
            result = st.executeQuery(sql);
            int columnCount = result.getMetaData().getColumnCount();
            while (result.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    resstr = resstr+result.getString(i)+" ";
                }
                resstr = resstr+"\n";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e1) {
                    GlobalTool.loger.error("something error", e1);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e1) {
                    GlobalTool.loger.error("something error", e1);
                }
            }
        }
        return resstr;
    }

    public static String getAllTable(String dbname, String tablename) {
        String sql = "select * from "+tablename;
        return selectTable(dbname,sql);
    }

    public static void dropTable(String dbname, String tablename) {
        DatabaseTool.dosql(dbname,"drop table if exists "+tablename);
    }

}