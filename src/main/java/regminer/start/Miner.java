package regminer.start;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import regminer.constant.Conf;
import regminer.git.provider.Provider;
import regminer.miner.PotentialBFCDetector;
import regminer.miner.migrate.BFCEvaluator;
import regminer.model.Bugx;
import regminer.model.PotentialRFC;
import regminer.model.RelatedTestCase;
import regminer.model.TestFile;
import regminer.monitor.ProgressMonitor;
import regminer.sql.BugStorage;
import regminer.sql.MysqlManager;
import regminer.utils.FileUtilx;

import java.util.*;

/**
 * @author sxz
 * 方法入口
 */
public class Miner {
    public static Repository repo = null;
    public static Git git = null;
    public static LinkedList<PotentialRFC> pRFCs;
    public static Set<String> setResult = new HashSet<>();
    static BugStorage bugStorage = new BugStorage();

    public static void main(String[] args) throws Exception {
        long s1 = System.currentTimeMillis();
        ConfigLoader.refresh();//加载配置
        ProgressMonitor.load(); // 加载断点

        repo = new Provider().create(Provider.EXISITING).get(Conf.LOCAL_PROJECT_GIT);
        git = new Git(repo);
        try {
            PotentialBFCDetector pBFCDetector = new PotentialBFCDetector(repo, git);
            pRFCs = (LinkedList<PotentialRFC>) pBFCDetector.detectPotentialBFC();
            ProgressMonitor.rePlan(pRFCs);
            singleThreadHandle();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        long s2 = System.currentTimeMillis();
        System.out.println(s2 - s1);
    }

    public static void singleThreadHandle() throws Exception {
        // 工具类准备,1)测试方法查找 2)测试用例确定 3)BIC查找
        BFCEvaluator tm = new BFCEvaluator(repo);
        // 声明一些辅助变量
        float i = 0;
        float j = (float) pRFCs.size();
        System.out.println("origin bfc number " + j);
        Iterator<PotentialRFC> iterator = pRFCs.iterator();
        FileUtilx.log("########################Start################################");
        Bugx bugx;
        while (iterator.hasNext()) {
            PotentialRFC pRfc = iterator.next();
            tm.evolute(pRfc);
            i++;
            FileUtilx.log(i / j + "%");
            // TODO 此处的方法和类之间的affix按照mvn的习惯用"#"连接,没有配置子项目
            if (pRfc.getTestCaseFiles().size() == 0) { // 找不到测试用例直接跳过
                iterator.remove();
            } else {
                bugx = new Bugx(UUID.randomUUID().toString());
                bugx.setProjectName(Conf.PROJRCT_NAME);
                bugx.setBfc(pRfc.getCommit().getName());
                bugx.setBuggy(pRfc.getBuggyCommitId());
                bugx.setTestCases(testListToSting(pRfc.getTestCaseFiles()));
                bugx.setCompileCMD(Conf.compileLine);
                bugx.setTestCMD(Conf.testLine);
                bugx.setBfcDir(pRfc.fileMap.get(bugx.getBfc()).getAbsolutePath());
                bugx.setBuggyDir(pRfc.fileMap.get(bugx.getBuggy()).getAbsolutePath());
                bugStorage.saveBug(bugx);
            }
            ProgressMonitor.addDone(pRfc.getCommit().getName());
        }
        FileUtilx.log("########################END SEARCH################################");
    }

    static String testListToSting(List<TestFile> testFileList) {
        StringJoiner stringJoiner = new StringJoiner(";");
        Map<String, RelatedTestCase> testCaseMap;
        for (TestFile testSuite : testFileList) {
            testCaseMap = testSuite.getTestMethodMap();
            for (Iterator<Map.Entry<String, RelatedTestCase>> it = testCaseMap.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, RelatedTestCase> entry = it.next();
               stringJoiner.add(testSuite.getQualityClassName() + Conf.methodClassLinkSymbolForTest
                       + entry.getKey().split("[(]")[0]);
            }
        }
        return stringJoiner.toString();
    }

}
