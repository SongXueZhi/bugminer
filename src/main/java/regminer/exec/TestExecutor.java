package regminer.exec;

import org.apache.commons.io.IOUtils;
import regminer.model.MigrateItem.MigrateFailureType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TestExecutor extends Executor {

    // 请注意以最小的单元运行任务
    public boolean execBuildWithResult(String cmd, boolean record) {
        Process process = null;
        InputStreamReader inputStr = null;
        BufferedReader bufferReader = null;
        try {
            if (OS.contains(OS_WINDOWS)) {
                pb.command("cmd.exe", "/c", cmd);
            } else {
                pb.command("bash", "-c", cmd);
            }
            process = pb.start();
            inputStr = new InputStreamReader(process.getInputStream(), "gbk");
            bufferReader = new BufferedReader(inputStr);
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = bufferReader.readLine()) != null) {
                line = line.toLowerCase();
                sb.append(line + "\n");
                // FileUtils.writeStringToFile(new File("build_log.txt"), line, true);
                if (line.contains("success")) {
                    return true;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (process != null) {
                    process.destroy();
                }
                if (inputStr != null) {
                    IOUtils.close(inputStr);
                }
                if (bufferReader != null) {
                    IOUtils.close(bufferReader);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return false;
    }


    // 请注意以最小的单元运行任务
    public MigrateFailureType execTestWithResult(String cmd) {
        Process process = null;
        Timer t = null;
        InputStreamReader inputStr = null;
        BufferedReader bufferReader = null;
        try {
            if (OS.contains(OS_WINDOWS)) {
                pb.command("cmd.exe", "/c", cmd);
            } else {
                pb.command("bash", "-c", cmd);
            }
            process = pb.start();
            t = new Timer();
            Process finalProcess = process;
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    finalProcess.destroy();
                }
            }, 60000);
            inputStr = new InputStreamReader(process.getInputStream());
            bufferReader = new BufferedReader(inputStr);
            String line;
            boolean testCE = false;
            while ((line = bufferReader.readLine()) != null) {
                line = line.toLowerCase();
                if (line.contains("build success")) {
                    return MigrateFailureType.TESTSUCCESS;
                } else if (line.contains("compilation error") || line.contains("compilation failure")) {
                    testCE = true;
                } else if (line.contains("no test")) {
                    return MigrateFailureType.NoTests;
                }
            }
            if (testCE) {
                return MigrateFailureType.CompilationFailed;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {

                if (t != null) {
                    t.cancel();
                }
                if (process != null) {
                    process.destroy();
                }
                if (inputStr != null) {
                    IOUtils.close(inputStr);
                }
                if (bufferReader != null) {
                    IOUtils.close(bufferReader);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return MigrateFailureType.NONE;
    }

    public List<String> runCommand(String cmd) {
        Process process = null;
        InputStreamReader inputStr = null;
        BufferedReader bufferReader = null;
        List<String> result = new ArrayList<String>();
        try {
            if (OS.contains(OS_WINDOWS)) {
                pb.command("cmd.exe", "/c", cmd);
            } else {
                pb.command("bash", "-c", cmd);
            }
            process = pb.start();
            inputStr = new InputStreamReader(process.getInputStream());
            bufferReader = new BufferedReader(inputStr);
            String line;
            while ((line = bufferReader.readLine()) != null) {
                result.add(line);
            }
            int a = process.waitFor();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            try{
                if (process != null) {
                    process.destroy();
                }
                if (inputStr != null) {
                    IOUtils.close(inputStr);
                }
                if (bufferReader != null) {
                    IOUtils.close(bufferReader);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
