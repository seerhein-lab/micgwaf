package com.seitenbau.test.micgwaf;

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
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

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

        Resource keystore = Resource.newClassPathResource("/keystore");
        if (keystore != null && keystore.exists()) {
            // if a keystore for a SSL certificate is available, start a SSL
            // connector on port 8443.
            // By default, the quickstart comes with a Apache Wicket Quickstart
            // Certificate that expires about half way september 2021. Do not
            // use this certificate anywhere important as the passwords are
            // available in the source.

//            connector.setConfidentialPort(8443);

            SslContextFactory factory = new SslContextFactory();
            factory.setKeyStoreResource(keystore);
            factory.setKeyStorePassword("wicket");
            factory.setTrustStoreResource(keystore);
            factory.setKeyManagerPassword("wicket");
            SslSocketConnector sslConnector = new SslSocketConnector(factory);
            sslConnector.setMaxIdleTime(timeout);
            sslConnector.setPort(8443);
            sslConnector.setAcceptors(4);
            server.addConnector(sslConnector);

            System.out.println("SSL access to the quickstart has been enabled on port 8443");
            System.out.println("You can access the application using SSL on https://localhost:8443");
            System.out.println();
        }

        WebAppContext bb = new WebAppContext();
        bb.setServer(server);
        bb.setContextPath("/");
        bb.setWar("src/main/webapp");

        // START JMX SERVER
        // MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        // MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
        // server.getContainer().addEventListener(mBeanContainer);
        // mBeanContainer.start();

        server.setHandler(bb);
        
 
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
