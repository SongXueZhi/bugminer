/*
 *
 *  * Copyright 2021 SongXueZhi
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package regminer.sql;

import regminer.constant.Conf;
import regminer.model.Bugx;

public class BugStorage {
    public  void saveBug(Bugx bugx) {
        String sql = "INSERT IGNORE INTO bugs (id,project_name,bfc,buggy,testcases,test_cmd,compile_cmd,bfc_dir,buggy_dir) " +
                "VALUES "+
                "('"+bugx.getId()+"','"+bugx.getProjectName()+"','"+bugx.getBfc()+
                "','"+bugx.getBuggy()+"','"+bugx.getTestCases()+"','"+bugx.getTestCMD()+"','"+bugx.getCompileCMD()+
                "','"+bugx.getBfcDir()+"','"+bugx.getBuggyDir()+"')";
        MysqlManager.executeUpdate(sql);
    }
}
