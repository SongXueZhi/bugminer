package regminer.miner.migrate;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import regminer.constant.Conf;
import regminer.constant.Constant;
import regminer.exec.TestExecutor;
import regminer.miner.migrate.model.MergeTask;
import regminer.model.ChangedFile;
import regminer.model.PotentialRFC;
import regminer.model.SourceFile;
import regminer.model.TestFile;
import regminer.utils.FileUtilx;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Migrator {
    TestExecutor exec = new TestExecutor();

    public File checkout(String bfcId, String commitId, String version) throws IOException {
        String cacheFile = Conf.CACHE_PATH + File.separator + bfcId + File.separator + commitId + File.separator
                + version + "_" + UUID.randomUUID();
        File file = new File(cacheFile);
        if (!file.exists()) {
            file.mkdirs();
        }
        exec.setDirectory(file);
        FileUtils.copyDirectoryToDirectory(new File(Conf.META_PATH), new File(cacheFile));
        File result = new File(cacheFile + File.separator + "meta");
        exec.setDirectory(result);
        exec.execPrintln("git checkout -f " + commitId);
        return result;
    }

    public String findJavaFile(@NotNull String className, @NotNull String[] projectJavaFiles) {
        String path = className.replace(".", File.separator) + ".java";
        for (String file : projectJavaFiles) {
            if (file.contains(path)) {
                return file;
            }
        }
        return null;
    }

    public String findClassFile(@NotNull String className, @NotNull String[] projectJavaFiles) {
        String path = className.replace(".", File.separator) + ".class";
        for (String file : projectJavaFiles) {
            if (file.contains(path)) {
                return file;
            }
        }
        return null;
    }

    /**
     * @param pRFC
     * @param tDir
     */
    public void mergeTwoVersion_BaseLine(PotentialRFC pRFC, File tDir) {
        /**
         *
         * ?????????bfc???patch??????????????? ??????java?????????????????????????????????????????????????????????????????????????????????java????????????????????????(???????????????????????????)
         * ???base_line?????????????????? ????????????????????????????????????????????????????????????????????????merge???
         */
        // ??????????????????
        List<TestFile> testSuite = pRFC.getTestCaseFiles();
        // ???????????????????????????????????????????????????

        //###XXX:TestDenpendency BlocK
        List<TestFile> underTestDirJavaFiles = pRFC.getTestRelates();
        List<SourceFile> sourceFiles = pRFC.getSourceFiles();
        //##block end

        // merge????????????
        // ????????????
        MergeTask mergeJavaFileTask = new MergeTask();
        mergeJavaFileTask.addAll(testSuite).addAll(underTestDirJavaFiles).addAll(sourceFiles).compute();//XXX:TestDenpendency BlocK
        File bfcDir = pRFC.fileMap.get(pRFC.getCommit().getName());
        for (Map.Entry<String, ChangedFile> entry : mergeJavaFileTask.getMap().entrySet()) {
            String newPathInBfc = entry.getKey();
            if (newPathInBfc.contains(Constant.NONE_PATH)) {
                continue;
            }
            File bfcFile = new File(bfcDir, newPathInBfc);
            File tFile = new File(tDir, newPathInBfc);
            if (tFile.exists()) {
                tFile.deleteOnExit();
            }
            // ??????copy??????
            try {
                FileUtils.forceMkdirParent(tFile);
                FileUtils.copyFileToDirectory(bfcFile, tFile.getParentFile());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

    }

    /**
     *
     * @param pRFC
     * @param targetProjectDirectory
     * @throws IOException
     */
    public void copyToTarget(@NotNull PotentialRFC pRFC, File targetProjectDirectory) throws IOException {
        // copy
        String targetPath = null;
        File bfcFile = pRFC.fileMap.get(pRFC.getCommit().getName());
        List<ChangedFile> taskFiles = new ArrayList<>();
        //now none test file be remove
        //test Related file is removed after test bfc
        taskFiles.addAll(pRFC.getTestCaseFiles());

        //###XXX:TestDenpendency BlocK
        taskFiles.addAll(pRFC.getTestRelates());
        taskFiles.addAll(pRFC.getSourceFiles());
        //####Block end#####

        for (ChangedFile cFile : taskFiles) {
            File file = new File(bfcFile, cFile.getNewPath());
            // ?????????????????????????????????????????????
            if (cFile.getNewPath().contains(Constant.NONE_PATH)) {
                continue;
            }
            targetPath = cFile.getNewPath();
            // ??????????????????????????????copy
            targetPath = FileUtilx.getDirectoryFromPath(targetPath);
            File file1 = new File(targetProjectDirectory, targetPath);
            if (!file1.exists()) {
                file1.mkdirs();
            }
            FileUtils.copyFileToDirectory(file, file1);
        }
    }

    //TODO SongXueZhi
    public void detectCompileWay(File bfcDir) {
    	File[] files = bfcDir.listFiles();
    	for (File file : files){
    		if (file.getName().equals("pom.xml")){

			}
		}
    }
}
