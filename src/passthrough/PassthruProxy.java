package passthrough;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class PassthruProxy {
    private static final int BUFFER_SIZE = 1000;
    
    private ServerSocket serverSock;
    private int port = 2025;
    private String destHost;
    private int destPort = 23;
    
    public PassthruProxy(String[] args) throws IOException {
        if (args.length < 1 || args.length > 3) {
            usage();
        }
        
        destHost = args[0];
        if (args.length >= 2)
            destPort = Integer.parseInt(args[1]);
        
        if (args.length >= 3) {
            port = Integer.parseInt(args[2]);
        }
        
        serverSock = new ServerSocket(port);
        
        System.out.println("Proxy started on port " + port + ", destHost: " + destHost + ", destPort: " + destPort);
    }
    
    public void usage() {
        System.out.println("\nUsage: java " + this.getClass().getName() + " <dest_host> <dest_port> <client_port>");
        System.exit(1);
    }
    
    public void process() {
        Socket incoming;
        while (true) {
            try {
                incoming = serverSock.accept();
            } catch (IOException e) {
                e.printStackTrace(System.out);
                continue;
            }
            (new MyHandler(incoming)).start();
        }
    }
    
    private void forward(DataInputStream ins, DataOutputStream outs, String prompt) throws IOException {
        byte[] buff = new byte[BUFFER_SIZE];
        int len;
        while (true) {
            // Read the data
            len = ins.read(buff);
            if (len < 0)  // End of stream
                break;
            
            prtBuff(buff, len, prompt);
            
            // Forward the data
            outs.write(buff, 0, len);
            outs.flush();
        }
    }
    
    private void prtBuff(byte[] buff, int len, String prompt) {
        if (len <= 0)
            return;
        
        PrintStream pout = System.out;
        
        String msg = new String(buff, 0, len);
        pout.println(">> " + prompt + ": " + msg);
        
        pout.print("[");
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                pout.print(' ');
            }
            pout.print(buff[i] & 0xff);
        }
        pout.println("]");
    }
    
    class MyHandler extends Thread {
        private Socket clientSock;
        private Socket destSock;
        private DataInputStream clientIns;
        private DataOutputStream clientOuts;
        private DataInputStream destIns;
        private DataOutputStream destOuts;
        
        MyHandler(Socket sock) {
            clientSock = sock;
        }
        
        public void run() {
            try {
                // Get the streams from the client socket
                clientIns = new DataInputStream(clientSock.getInputStream());
                clientOuts = new DataOutputStream(clientSock.getOutputStream());

                // Open a connection to the destination server
                destSock = new Socket(destHost, destPort);
                destIns = new DataInputStream(destSock.getInputStream());
                destOuts = new DataOutputStream(destSock.getOutputStream());
                
                // Start the thread to forward data from destination to client
                (new ForwardHandler(this, destIns, clientOuts)).start();
                
                // Forward data from client to destination
                forward(clientIns, destOuts, "From client");
            } catch (IOException e) {
                e.printStackTrace(System.out);
            } finally {
                cleanup();
            }
            System.out.println("MyHandler exits");
        }
        
        public synchronized void cleanup() {
            System.out.println("Closing the sockets ...");
                
            if (clientSock != null) {
                try {
                    clientSock.close();
                } catch (IOException e) {
                    e.printStackTrace(System.out);
                }
                clientSock = null;
            }

            if (destSock != null) {
                try {
                    destSock.close();
                } catch (IOException e) {
                    e.printStackTrace(System.out);
                }
                destSock = null;
            }
        }
    }
    
    class ForwardHandler extends Thread {
        private MyHandler parent;
        private DataInputStream ins;
        private DataOutputStream outs;
        
        ForwardHandler(MyHandler parent, DataInputStream ins, DataOutputStream outs) {
            this.parent = parent;
            this.ins = ins;
            this.outs = outs;
        }
        
        public void run() {
            try {
                forward(ins, outs, "From server");
            } catch (IOException e) {
                e.printStackTrace(System.out);
            } finally {
                parent.cleanup();
            }
            System.out.println("ForwardHandler exits");
        }
    }
    
    public static void main(String[] args) throws IOException {
        PassthruProxy pp = new PassthruProxy(args);
        pp.process();
    }
}
