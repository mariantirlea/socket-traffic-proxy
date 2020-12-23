package ro.mariantirlea.sockettrafficproxy.stpcore;

import ro.mariantirlea.sockettrafficproxy.stpcore.utils.SocketUtility;

import java.io.IOException;

public class Runner {

    public static void main(String[] args) throws IOException, InterruptedException {

        new ProxyHelper(7090, "192.168.1.67", 7090).start();
        new ProxyHelper(8508, "192.168.1.67", 8508).start();
        new ProxyHelper(8509, "192.168.1.67", 8509).start();

    }

}
