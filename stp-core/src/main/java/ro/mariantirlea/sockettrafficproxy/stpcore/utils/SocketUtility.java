package ro.mariantirlea.sockettrafficproxy.stpcore.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class SocketUtility {

    public static String ipFromHost(String host) throws UnknownHostException {
        return InetAddress.getByName(host).getHostAddress();
    }

    public static void main(String[] args) throws UnknownHostException {

        String ip = SocketUtility.ipFromHost("www.github.com");
        System.err.println(ip);

    }

}
