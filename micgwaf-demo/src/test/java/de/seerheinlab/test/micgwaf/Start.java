package de.seerheinlab.test.micgwaf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Runs the micgwaf demo web application within an embedded jetty servlet container,
 * using the port 8080.
 * The application is available at http://localhost:8080
 */
public class Start {
  
  private static Server server;
  
    public static void main(String[] args) throws Exception {
        int timeout = 60*60*1000;

        stop();
        
        server = new Server();
        SocketConnector connector = new SocketConnector();

        // Set some timeout options to make debugging easier.
        connector.setMaxIdleTime(timeout);
        connector.setSoLingerTime(-1);
        connector.setPort(8080);
        server.addConnector(connector);

        WebAppContext webappContext = new WebAppContext();
        webappContext.setServer(server);
        webappContext.setContextPath("/");
        webappContext.setWar("src/main/webapp");
        server.setHandler(webappContext);
        
 
        try {
            System.out.println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP");
            Thread monitor = new MonitorThread();
            monitor.start();
            server.start();
            System.in.read();
            System.out.println(">>> STOPPING EMBEDDED JETTY SERVER");
            server.stop();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public static void stop() throws IOException {
      try
      {
        Socket s = new Socket(InetAddress.getByName("127.0.0.1"), 8079);
        OutputStream out = s.getOutputStream();
        System.out.println("*** sending jetty stop request");
        out.write(("\r\n").getBytes());
        out.flush();
        s.close();
      }
      catch (ConnectException e)
      {
        System.out.println("*** jetty is not running");
      }
    }

    
    private static class MonitorThread extends Thread {
      
      private ServerSocket socket;

      public MonitorThread() {
          setDaemon(true);
          setName("StopMonitor");
          try {
              socket = new ServerSocket(8079, 1, InetAddress.getByName("127.0.0.1"));
          } catch(Exception e) {
              throw new RuntimeException(e);
          }
      }

      @Override
      public void run() {
          System.out.println("*** running jetty 'stop' thread");
          Socket accept;
          try {
              accept = socket.accept();
              BufferedReader reader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
              reader.readLine();
              System.out.println("*** stopping jetty embedded server");
              server.stop();
              accept.close();
              socket.close();
          } catch(Exception e) {
              throw new RuntimeException(e);
          }
      }
  }

}
