package HttpServer.connector.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Stack;
import java.util.Vector;

public class HttpConnector implements Runnable{

	boolean stopped;
	
    private Stack processors = new Stack();
    
    private int maxProcessors = 20;
    
    private int minProcessors = 5;
    
    private Vector created = new Vector();
    
    private int curProcessors = 0;
	private String scheme = "http";
	public String getScheme(){
		return this.scheme;
	}
	
	
	
	public void run() {
		ServerSocket serverSocket = null;
		int port = 8080;
		try{
			serverSocket = new ServerSocket(port,1,InetAddress.getByName("127.0.0.1"));
			
		}catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		}
		while(!stopped){
			Socket socket = null;
			try{
				socket = serverSocket.accept();
			}catch(IOException e){
				continue;
			}
			if(socket==null) continue;
			HttpProcessor processor = createProcessor();
			processor.process(socket);
			
		}
		
		
		
		
		
	}
	
	private HttpProcessor createProcessor() {

        synchronized (processors) {
            if (processors.size() > 0) {
                // if (debug >= 2)
                // log("createProcessor: Reusing existing processor");
                return ((HttpProcessor) processors.pop());
            }
            if ((maxProcessors > 0) && (curProcessors < maxProcessors)) {
                // if (debug >= 2)
                // log("createProcessor: Creating new processor");
                return (newProcessor());
            } else {
                if (maxProcessors < 0) {
                    // if (debug >= 2)
                    // log("createProcessor: Creating new processor");
                    return (newProcessor());
                } else {
                    // if (debug >= 2)
                    // log("createProcessor: Cannot create new processor");
                    return (null);
                }
            }
        }

    }
	private HttpProcessor newProcessor() {

        //        if (debug >= 2)
        //            log("newProcessor: Creating new processor");
        HttpProcessor processor = new HttpProcessor(this);
        
        created.addElement(processor);
        return (processor);

    }
	public void start()  {
        // Start our background thread
        threadStart();

        // Create the specified minimum number of processors
        while (curProcessors < minProcessors) {
            if ((maxProcessors > 0) && (curProcessors >= maxProcessors))
                break;
            HttpProcessor processor = newProcessor();
            recycle(processor);
        }

    }



	private void recycle(HttpProcessor processor) {
		processors.push(processor);
	}



	private void threadStart() {
		Thread t = new Thread(this);
		t.start();
	}

}
