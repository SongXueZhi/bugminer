package regminer.model;

/**
 * @Author: sxz
 * @Date: 2022/08/08/18:15
 * @Description:
 */
public class Bugx {
    String id;
    String projectName;
    String bfc;
    String buggy;
    String testCases;
    String compileCMD;
    String testCMD;
    String bfcDir;
    String buggyDir;

    public Bugx(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getBfc() {
        return bfc;
    }

    public void setBfc(String bfc) {
        this.bfc = bfc;
    }

    public String getBuggy() {
        return buggy;
    }

    public void setBuggy(String buggy) {
        this.buggy = buggy;
    }

    public String getTestCases() {
        return testCases;
    }

    public void setTestCases(String testCases) {
        this.testCases = testCases;
    }

    public String getCompileCMD() {
        return compileCMD;
    }

    public void setCompileCMD(String compileCMD) {
        this.compileCMD = compileCMD;
    }

    public String getTestCMD() {
        return testCMD;
    }

    public void setTestCMD(String testCMD) {
        this.testCMD = testCMD;
    }

    public String getBfcDir() {
        return bfcDir;
    }

    public void setBfcDir(String bfcDir) {
        this.bfcDir = bfcDir;
    }

    public String getBuggyDir() {
        return buggyDir;
    }

    public void setBuggyDir(String buggyDir) {
        this.buggyDir = buggyDir;
    }
}
