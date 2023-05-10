package com.wanda.epc.util;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author LianYanFei
 * @version 1.0
 * @project iot_epc
 * @description ping工具类多线程
 * @date 2023/1/15 16:32:51
 */
public class PingUtil {

    private Queue<String> allIp; // 需验证的IP
    private int threadNum = 5; // 线程数
    private static   String ipsOK = ""; // 可以ping通的IP
    private  static String ipsNO = ""; // 不能ping通的IP


    public  String getIpsNO() {
        return ipsNO;
    }


    public  void setIpsNO(String ipsNO) {
        PingUtil.ipsNO = ipsNO;
    }


    public  String getIpsOK() {
        return ipsOK;
    }


    public  void setIpsOK(String ipsOK) {
        PingUtil.ipsOK = ipsOK;
    }


    public PingUtil(Queue<String> allIp) {
        this.allIp = allIp;
    }


    public void startPing() {
        // 创建一个线程池，多个线程同步执行
        ExecutorService executor = Executors.newFixedThreadPool(threadNum);
        for (int i = 0; i < threadNum; i++) {
            executor.execute(new PingRunner());
        }
        executor.shutdown();
        try {
            while (!executor.isTerminated()) {
                Thread.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class PingRunner implements Runnable {
        private String taskIp = null;


        @Override
        public void run() {
            try {
                while ((taskIp = getIp()) != null) {
                    InetAddress addr = InetAddress.getByName(taskIp);
                    if (addr.isReachable(3000)) {
                        ipsOK += taskIp + ",";
                    } else {
                        ipsNO += taskIp + ",";
                    }
                }
            } catch (SocketException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        public String getIp() {
            String ip = null;
            synchronized (allIp) {
                ip = allIp.poll();
            }
            return ip;
        }
    }
}
