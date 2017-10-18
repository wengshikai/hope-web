package models.util;

import models.dbmanager.GlobalTool;
import play.db.DB;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import java.sql.*;

/**
 * Created by shanmao on 15-10-25.
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


    public static void doSql(String db, String sql){
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
            GlobalTool.logger.error("something error", e);
        }finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e1) {
                    GlobalTool.logger.error("something error",e1);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    GlobalTool.logger.error("something error",e);
                }
            }
        }

    }


    public static void dropTable(String dbname, String tablename) {
        DatabaseTool.doSql(dbname,"drop table if exists "+tablename);
    }

}