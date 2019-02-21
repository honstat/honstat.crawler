package com.honstat.crawler.service.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.utils
 * @Description: TODO
 * @date 2018/12/27 10:38
 */
public class CommonUtil {
    /**
     * 获取服务器IP地址
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String  getServerIp(){
        String SERVER_IP = null;
        try {
            SERVER_IP = InetAddress.getLocalHost().getHostAddress();
        }catch (UnknownHostException e){
            e.printStackTrace();
        }

//        try {
//            Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
//            InetAddress ip = null;
//            while (netInterfaces.hasMoreElements()) {
//                NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
//                ip = (InetAddress) ni.getInetAddresses().nextElement();
//                SERVER_IP = ip.getHostAddress();
//                if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
//                        && ip.getHostAddress().indexOf(":") == -1) {
//                    SERVER_IP = ip.getHostAddress();
//                    break;
//                } else {
//                    ip = null;
//                }
//            }
//        } catch (SocketException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

        return SERVER_IP;
    }
}

