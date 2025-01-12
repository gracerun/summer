package com.gracerun.repository.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.incrementer.AbstractColumnMaxValueIncrementer;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * 自增编号
 *
 * @author ce.sun
 */
public class AutoIncrementService implements InitializingBean {

    @Resource
    private DataSource dataSource;

    /**
     * The name of the sequence/table containing the sequence
     */
    @Value("${incrementer.table.name:increment_sequence}")
    private String incrementerTableName;

    /**
     * The name of the sequence/key containing the sequence
     */
    @Value("${incrementer.column.name:code}")
    private String incrementerColumnName;

    /**
     * 默认缓存大小
     */
    private int cacheSize = 100;

    private boolean useNewConnection = true;

    private final Map<String, SQLMaxValueIncrementer> incrementers = new HashMap<String, SQLMaxValueIncrementer>();

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(this.incrementerTableName, "Property 'incrementerTableName' is required");
        Assert.hasText(this.incrementerColumnName, "Property 'incrementerColumnName' is required");
    }

    public SQLMaxValueIncrementer init(DataSource dataSource, String incrementerName,
                                       String columnName, String keyName, int cacheSize, boolean useNewConnection) {
        synchronized (keyName.intern()) {
            SQLMaxValueIncrementer inc = incrementers.get(keyName);
            if (inc != null) {
                return inc;
            }
            inc = new SQLMaxValueIncrementer();
            inc.setDataSource(dataSource);
            inc.setIncrementerName(incrementerName);
            inc.setColumnName(columnName);
            inc.setColumnValue(keyName);
            inc.setCacheSize(cacheSize);
            inc.setUseNewConnection(useNewConnection);
            incrementers.put(keyName, inc);
            return inc;
        }
    }

    public long nextLongValue(String keyName) {
        SQLMaxValueIncrementer inc = incrementers.get(keyName);
        if (inc == null) {
            inc = init(dataSource, incrementerTableName, incrementerColumnName, keyName, cacheSize,
                    useNewConnection);
        }
        return inc.nextLongValue();
    }

    public long getNextContinuousCount(String keyName, long count) {
        Assert.isTrue(count > 0, "序列数量必须大于0");
        SQLMaxValueIncrementer inc = incrementers.get(keyName);
        if (inc == null) {
            inc = init(dataSource, incrementerTableName, incrementerColumnName, keyName, cacheSize,
                    useNewConnection);
        }
        return inc.getNextContinuousCount(count);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getIncrementerTableName() {
        return incrementerTableName;
    }

    public void setIncrementerTableName(String incrementerTableName) {
        this.incrementerTableName = incrementerTableName;
    }

    public String getIncrementerColumnName() {
        return incrementerColumnName;
    }

    public void setIncrementerColumnName(String incrementerColumnName) {
        this.incrementerColumnName = incrementerColumnName;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public Map<String, SQLMaxValueIncrementer> getIncrementers() {
        return incrementers;
    }

    class SQLMaxValueIncrementer extends AbstractColumnMaxValueIncrementer {

        protected final Logger logger = LoggerFactory.getLogger(getClass());

        private static final String SELECT_SQL = "select optimistic, value from %s where %s='%s'";
        private static final String UPDATE_SQL = "update %s set optimistic=optimistic+1, value=%s where %s='%s' and optimistic=%s";

        /**
         * The value of the column for this sequence name
         */
        private String columnValue;

        /**
         * Whether or not to use a new connection for the incrementer
         */
        private boolean useNewConnection = true;

        /**
         * The next id to serve
         */
        private long nextId = 0;

        /**
         * The max id to serve
         */
        private long maxId = 0;

        @Override
        public void afterPropertiesSet() {
            super.afterPropertiesSet();
            if (this.columnValue == null) {
                throw new IllegalArgumentException("Property 'columnValue' is required");
            }
        }

        public SQLMaxValueIncrementer() {
        }

        public SQLMaxValueIncrementer(DataSource dataSource, String incrementerName,
                                      String columnName) {
            super(dataSource, incrementerName, columnName);
        }

        @Override
        protected synchronized long getNextKey() throws DataAccessException {
            if (this.maxId == this.nextId) {
                /*
                 * If useNewConnection is true, then we obtain a non-managed
                 * connection so our modifications are handled in a separate
                 * transaction. If it is false, then we use the current
                 * transaction's connection relying on the use of a
                 * non-transactional storage engine like MYISAM for the
                 * incrementer table. We also use straight JDBC code because we
                 * need to make sure that the insert and select are performed on
                 * the same connection (otherwise we can't be sure that
                 * last_insert_id() returned the correct value).
                 */
                Connection con = null;
                Statement stmt = null;
                boolean mustRestoreAutoCommit = false;
                try {
                    if (this.useNewConnection) {
                        con = getDataSource().getConnection();
                        if (con.getAutoCommit()) {
                            mustRestoreAutoCommit = true;
                            con.setAutoCommit(false);
                        }
                    } else {
                        con = DataSourceUtils.getConnection(getDataSource());
                    }
                    stmt = con.createStatement();

                    if (!this.useNewConnection) {
                        DataSourceUtils.applyTransactionTimeout(stmt, getDataSource());
                    }
                    // Increment the sequence column...
                    String columnName = getColumnName();

                    ResultSet qrs = stmt.executeQuery(String.format(SELECT_SQL,
                            getIncrementerName(), getColumnName(), this.columnValue));
                    long optimistic;
                    long max;
                    try {
                        if (!qrs.next()) {
                            throw new DataAccessResourceFailureException(
                                    "select optimistic, value failed after executing an update");
                        }
                        optimistic = qrs.getLong(1);
                        max = qrs.getLong(2);
                    } finally {
                        JdbcUtils.closeResultSet(qrs);
                    }

                    try {
                        int executeUpdate = stmt.executeUpdate(String.format(UPDATE_SQL,
                                getIncrementerName(), (max + getCacheSize()), getColumnName(),
                                this.columnValue, optimistic));

                        if (executeUpdate != 1) {
                            logger.error("increment {} executeUpdate size={}",
                                    getIncrementerName(), executeUpdate);
                            closeConnection(con, stmt, mustRestoreAutoCommit);
                            return getNextKey();
                        }
                    } catch (SQLException ex) {
                        throw new DataAccessResourceFailureException("Could not increment "
                                + columnName + " for " + getIncrementerName() + " sequence table",
                                ex);
                    }
                    this.maxId = max + getCacheSize();
                    this.nextId = this.maxId - getCacheSize() + 1;
                } catch (SQLException ex) {
                    throw new DataAccessResourceFailureException("Could not increment", ex);
                } finally {
                    closeConnection(con, stmt, mustRestoreAutoCommit);
                }
            } else {
                this.nextId++;
            }
            return this.nextId;
        }

        private void closeConnection(Connection con, Statement stmt, boolean mustRestoreAutoCommit) {
            JdbcUtils.closeStatement(stmt);
            if (con != null) {
                if (this.useNewConnection) {
                    try {
                        con.commit();
                        if (mustRestoreAutoCommit) {
                            con.setAutoCommit(true);
                        }
                    } catch (SQLException ignore) {
                        logger.error("Could not commit JDBC Connection", ignore);
                    }
                    JdbcUtils.closeConnection(con);
                } else {
                    DataSourceUtils.releaseConnection(con, getDataSource());
                }
            }
        }

        /**
         * 获取N个连续的序号值 返回结尾的序号值~起始值(返回值减count)
         *
         * @param count
         * @return
         * @throws DataAccessException
         */
        protected synchronized long getNextContinuousCount(long count) throws DataAccessException {
            Connection con = null;
            Statement stmt = null;
            try {
                con = DataSourceUtils.getConnection(getDataSource());
                stmt = con.createStatement();

                DataSourceUtils.applyTransactionTimeout(stmt, getDataSource());
                String columnName = getColumnName();

                ResultSet qrs = stmt.executeQuery(String.format(SELECT_SQL, getIncrementerName(),
                        getColumnName(), this.columnValue));
                long optimistic;
                long max;
                try {
                    if (!qrs.next()) {
                        throw new DataAccessResourceFailureException(
                                "select optimistic, value failed after executing an update");
                    }
                    optimistic = qrs.getLong(1);
                    max = qrs.getLong(2);
                } finally {
                    JdbcUtils.closeResultSet(qrs);
                }

                try {
                    int executeUpdate = stmt.executeUpdate(String.format(UPDATE_SQL,
                            getIncrementerName(), (max + count), getColumnName(), this.columnValue,
                            optimistic));

                    if (executeUpdate != 1) {
                        throw new DataAccessResourceFailureException("Could not increment "
                                + columnName + " for " + getIncrementerName()
                                + " sequence table, reason: executeUpdate=" + executeUpdate);
                    }
                } catch (SQLException ex) {
                    throw new DataAccessResourceFailureException("Could not increment "
                            + columnName + " for " + getIncrementerName() + " sequence table", ex);
                }
                return max + count;
            } catch (SQLException ex) {
                throw new DataAccessResourceFailureException("Could not increment", ex);
            } finally {
                JdbcUtils.closeStatement(stmt);
                if (con != null) {
                    DataSourceUtils.releaseConnection(con, getDataSource());
                }
            }
        }

        public String getColumnValue() {
            return columnValue;
        }

        public void setColumnValue(String columnValue) {
            this.columnValue = columnValue;
        }

        public boolean isUseNewConnection() {
            return useNewConnection;
        }

        public void setUseNewConnection(boolean useNewConnection) {
            this.useNewConnection = useNewConnection;
        }

    }
}
