package ro.mariantirlea.sockettrafficproxy.stpcore;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ProxyHelper extends Thread {

    public ProxyHelper(int localPort, String destinationIP, int remotePort){
        this.localPort = localPort;
        this.destinationIP = destinationIP;
        this.remotePort = remotePort;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(localPort);


            while (true){
                new EchoClientHandler(serverSocket.accept(), destinationIP, remotePort).run();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ServerSocket serverSocket;
    private int localPort;
    private String destinationIP;
    private int remotePort;


//    public void stop() throws IOException {
//        serverSocket.close();
//    }

}

class GreetClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedInputStream in;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
    }

}

class EchoClientHandler{
    private Socket fromSocket;
    private String destinationIP;
    private int remotePort;


    public EchoClientHandler(Socket socket, String destinationIP, int remotePort) {
        this.fromSocket = socket;
        this.destinationIP = destinationIP;
        this.remotePort = remotePort;
    }

    public void run() throws InterruptedException {
        System.out.println("New client was connected..." +  Thread.currentThread().getName() + Thread.currentThread().getId());

        try {
            BufferedInputStream fromSocketInput = new BufferedInputStream(fromSocket.getInputStream());
            BufferedOutputStream fromSocketOutput = new BufferedOutputStream(fromSocket.getOutputStream());

            Socket toSocket = new Socket(destinationIP, remotePort);
            BufferedInputStream toSocketInput = new BufferedInputStream(toSocket.getInputStream());
            BufferedOutputStream toSocketOutput = new BufferedOutputStream(toSocket.getOutputStream());

            fromSocket.setSoTimeout(100);
            toSocket.setSoTimeout(100);

            Thread t1 = new Thread(() -> {
                Thread.currentThread().setName("fromSocket" + Thread.currentThread().getId());

                byte[] buffer = new byte[4096];

                try {
                    do {
                        if(fromSocketInput.available() <=0 ){
                            System.out.println("Nothing to read from: " + Thread.currentThread().getId());
                            break;
                        }
                        int size = fromSocketInput.read(buffer);

                        if(size > 0){
//                            System.out.println("Read input from: " + Thread.currentThread().getId());

                            toSocketOutput.write(buffer, 0, size);
                            toSocketOutput.flush();
//                            System.err.println(size);
//                            System.err.println(new String(buffer));
                        }

                    }while(fromSocketInput.available() > 0);

                }catch (IOException e){
                    e.printStackTrace();
                }
                System.out.println("Try to close FROM thread: " + Thread.currentThread().getId());

            });
            t1.start();

            Thread t2 = new Thread(() -> {
                Thread.currentThread().setName("toSocket" + Thread.currentThread().getId());

                byte[] buffer = new byte[4096];

                try {
                    do {
                        int size = 0;

                        try {
                            size = toSocketInput.read(buffer);

                            if(size > 0){
//                                System.out.println("Write input to: " + Thread.currentThread().getId());

                                fromSocketOutput.write(buffer, 0, size);
                                fromSocketOutput.flush();
//                                System.err.println(size);
//                            System.err.println(new String(buffer));
                            }else{
                                break;
                            }

                        }catch (IOException e){
                            System.out.println("Timeout -> stop an close");
                            break;

                        }


                    }while(true);


                }finally {
                    try {
                        System.out.println("Try to close TO thread: " + Thread.currentThread().getId());

                        fromSocketInput.close();
                        toSocketInput.close();
                        fromSocketOutput.close();
                        toSocketOutput.close();
                        toSocket.close();
                        fromSocket.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });
            t2.start();

            t1.join();
            t2.join();

        } catch (IOException  e) {
            e.printStackTrace();
        }

    }


}