package net.jidget;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Launch {

    public static final String RMI = "net.jidget.rmi.server";
    public static final int PORT = 3997;
    
    public static void main(String[] args) throws Exception {
        if (!sendToRemote(args)) {
            startApplication(args);
        }
    }

    protected static boolean sendToRemote(String[] args) throws Exception {
        JidgetRmiServer j = findRMIServer();
        if (j == null) return false;
        j.perform(args);
        return true;
    }
    
    protected static void startApplication(String[] args) throws Exception {
        Class jfxMain = Class.forName("com.javafx.main.Main");
        Method main = jfxMain.getMethod("main", String[].class);
        main.invoke(null, (Object) args);
    }
    
    protected static JidgetRmiServer findRMIServer() throws Exception {
        try {
            Registry reg = LocateRegistry.getRegistry(PORT);
            Remote r = reg.lookup(RMI);
            return (JidgetRmiServer) r;
        } catch (RemoteException e) {
            return null;
        }
    }
    
    public static void startRMIServer(Object app, String[] args) {
        try {
            Registry reg = LocateRegistry.createRegistry(PORT);
            JidgetRmiServerImpl j = new JidgetRmiServerImpl();
            JidgetRmiServer stub = (JidgetRmiServer) UnicastRemoteObject.exportObject(j, PORT);
            try {
                reg.bind(RMI, stub);
            } catch (AlreadyBoundException e2) {
                JidgetRmiServer r = (JidgetRmiServer) reg.lookup(RMI);
                r.perform(args);
                System.exit(0);
            }
            j.setApp(app);
            rmiServer = j;
        } catch(RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static JidgetRmiServer rmiServer = null;
    
    public static interface JidgetRmiServer extends Remote {
        
        void perform(String[] args) throws RemoteException;
        
    }
    
    public static class JidgetRmiServerImpl implements JidgetRmiServer {

        private transient Object app = null;
        
        private synchronized void setApp(Object app) {
            this.app = app;
            notifyAll();
        }
        
        @Override
        public synchronized void perform(String[] args) throws RemoteException {
            try {
                while (app == null) wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            ((App) app).perform(args);
        }
        
    }
    
}
