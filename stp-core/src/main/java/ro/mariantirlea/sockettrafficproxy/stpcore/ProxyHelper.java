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

    public static String readInputStream(BufferedInputStream _in)
            throws IOException {
        String data = "";
        int s = _in.read();
        if(s==-1)
            return null;
        data += ""+(char)s;
        int len = _in.available();
        System.out.println("Len got : "+len);
        if(len > 0) {
            byte[] byteData = new byte[len];
            _in.read(byteData);
            data += new String(byteData);
        }

//        System.err.println(data);
        return data;
    }
}

class GreetClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedInputStream in;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedInputStream(clientSocket.getInputStream());
    }

    public String sendMessage(String msg) throws IOException {
//        System.err.println(msg);
        out.println(msg);
        return ProxyHelper.readInputStream(in);
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
}

class EchoClientHandler{
    private Socket clientSocket;
    private String destinationIP;
    private int remotePort;
    private PrintWriter out;
    private BufferedInputStream in;

    public EchoClientHandler(Socket socket, String destinationIP, int remotePort) {
        this.clientSocket = socket;
        this.destinationIP = destinationIP;
        this.remotePort = remotePort;
    }

    public synchronized void run() throws InterruptedException {
        System.out.println("New client was connected..." +  Thread.currentThread().getName() + Thread.currentThread().getId());

        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedInputStream(clientSocket.getInputStream());


            Socket clientSocket = new Socket(destinationIP, remotePort);
            PrintWriter out2 = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedInputStream in2 = new BufferedInputStream(clientSocket.getInputStream());
            clientSocket.setSoTimeout(10000);
            this.clientSocket.setSoTimeout(10000);

            byte[] buffer = new byte[4096];
            int read;

            if(in.available() > 0){
                do {


                    read = in.read(buffer);
                    if (read > 0) {
                        clientSocket.getOutputStream().write(buffer, 0, read);
//                        System.err.println(new String(buffer));
                        if (in.available() < 1) {
                            clientSocket.getOutputStream().flush();
                        }
                    }else{
                        clientSocket.getOutputStream().flush();
                    }
                } while (in.available() > 0);

                byte[] buffer2 = new byte[4096];
                int read2;
                boolean first = true;
                do {
                    read2 = in2.read(buffer2);
                    if (read2 > 0) {
//                        System.err.println(new String(buffer2));
                        Thread.sleep(10);
                        this.clientSocket.getOutputStream().write(buffer2, 0, read2);
                        if (in2.available() < 1) {

                            if(first){
                                Thread.sleep(50);
                                first = false;
                                continue;
                            }

                            this.clientSocket.getOutputStream().flush();

                        }
                    }else{
                        this.clientSocket.getOutputStream().flush();
                    }
                } while (in2.available() > 0);
            }

            this.in.close();
            this.out.close();
            this.clientSocket.close();

            in2.close();
            out2.close();
            clientSocket.close();


//                in.close();
//                clientSocket.close();



//                String rec=null;
//                while(true) {
//                    rec=null;
//                    try	{
//                        rec = readInputStream(in);//in.readLine();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        break;
//                    }
//
//                    if (rec != null) {
//                        System.err.println(rec);
//
//                        //Send body to destination and return response from there
//                        GreetClient client = new GreetClient();
//                        client.startConnection("127.0.0.1", 8080);
//                        Thread.sleep(2000);
//                        out.write(client.sendMessage(rec));
//
////                        out.write("HTTP/1.1 200 \n" +
////                                "Content-Type: text/html;charset=utf-8\n" +
////                                "Content-Language: en\n" +
////                                "Content-Length: 10\n" +
////                                "Date: Tue, 22 Dec 2020 14:03:56 GMT\n" +
////                                "Connection: close\n" +
////                                "\n" +
////                                "<!doctype");
//                        out.write("");
//                        out.flush();
//                        clientSocket.close();
//                        client.stopConnection();
//                        //rec = rec.replaceAll("\n","<LF>");
//                        //rec = rec.replaceAll("\r","<CR>");
//                        //parent.append("R: "+rec);
//                    } else {
//
//                        break;
//                    }
//                } //end of while

//                in.close();
//                out.close();
//                clientSocket.close();

        } catch (IOException  e) {
            e.printStackTrace();
        }

    }


}