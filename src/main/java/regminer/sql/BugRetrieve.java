package regminer.sql;

import regminer.constant.Conf;

import java.util.Set;

public class BugRetrieve {
    public Set<String> getRegressionsFromDB() {
        String sql = "select bfc from bugs where project_name ='" + Conf.PROJRCT_NAME + "'";
        Set<String> result = MysqlManager.executeSql(sql);
        return  result;
    }
}
