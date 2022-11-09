package com.gracerun.log.util;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * 获取本地ip
 *
 * @author Tom
 * @version 1.0.0
 * @date 12/26/21
 */
@Slf4j
public class IpUtil {
    private static volatile InetAddress LOCAL_ADDRESS = null;

    /**
     * get first valid addredd
     *
     * @return
     */
    private static InetAddress getFirstValidAddress() {
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            if (!localAddress.isLoopbackAddress() && !localAddress.isLinkLocalAddress()
                    && localAddress.isSiteLocalAddress()) {
                return localAddress;
            }
        } catch (Throwable e) {
            log.error("Failed to retriving ip address, " + e.getMessage(), e);
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = interfaces.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        if (addresses != null) {
                            while (addresses.hasMoreElements()) {
                                try {
                                    InetAddress address = addresses.nextElement();
                                    if (!address.isLoopbackAddress()
                                            && !address.isLinkLocalAddress()
                                            && address.isSiteLocalAddress()) {
                                        return address;
                                    }
                                } catch (Throwable e) {
                                    log.error(
                                            "Failed to retriving ip address, " + e.getMessage(), e);
                                }
                            }
                        }
                    } catch (Throwable e) {
                        log.error("Failed to retriving ip address, " + e.getMessage(), e);
                    }
                }
            }
        } catch (Throwable e) {
            log.error("Failed to retriving ip address, " + e.getMessage(), e);
        }
        log.error("Could not get local host ip address, will use 127.0.0.1 instead.");
        return localAddress;
    }

    /**
     * get address
     *
     * @return
     */
    private static InetAddress getAddress() {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        }
        InetAddress localAddress = getFirstValidAddress();
        LOCAL_ADDRESS = localAddress;
        return localAddress;
    }

    /**
     * get ip
     *
     * @return
     */
    public static String getIp() {
        InetAddress address = getAddress();
        if (address == null) {
            return null;
        }
        return address.getHostAddress();
    }

    /**
     * get ip:port
     *
     * @param port
     * @return
     */
    public static String getIpPort(int port) {
        String ip = getIp();
        if (ip == null) {
            return null;
        }
        return ip.concat(":").concat(String.valueOf(port));
    }

    public static void main(String[] args) throws Exception {
        System.out.println(getIp());
        System.out.println(getIpPort(8080));
        StringBuilder sb = new StringBuilder();
        Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
        while (en.hasMoreElements()) {
            NetworkInterface intf = (NetworkInterface) en.nextElement();
            Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
            while (enumIpAddr.hasMoreElements()) {
                InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()
                        && inetAddress.isSiteLocalAddress()) {
                    sb.append(inetAddress.getHostAddress().toString() + "\n");
                }
            }
        }
        System.out.println(sb);
    }
}