package HttpServer.connector.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import XmlConfig.ServletConfig;



public class HttpProcessor {

	private HttpConnector connector = null;
	
	HttpRequestLine requestLine = new HttpRequestLine();
	
	ServletConfig servletConfig = new ServletConfig();
	
	HttpRequest request;
	HttpResponse response;
	protected String method = null;
	protected String queryString = null;

	public HttpProcessor(HttpConnector connector) {
		this.connector = connector;
	}

	public void process(Socket socket) {
		SocketInputStream input = null;
		OutputStream output = null;
		try{
			input = new SocketInputStream(socket.getInputStream(),2048);
			output = socket.getOutputStream();
			request = new HttpRequest(input);
			response =  new HttpResponse(output);
			response.setRequest(request);
			response.setHeader("Server", "hbc/1.1");
			parseRequest(input,output);
			parseHeaders(input);
			System.out.println(request.getRequestURI());
			if(pattern(request,servletConfig.getMappingConfig())){
				ServletProcessor processor = new ServletProcessor();
				processor.process(request, response);
				
			}else{
				StaticResourceProcessor processor = new StaticResourceProcessor();
				processor.process(request, response);				
			}
			socket.close();
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
   
	}

	private boolean pattern(HttpRequest request, Map<String, String> mappingConfig) {
		Set<Entry<String,String>> mappings = mappingConfig.entrySet();
		Iterator<Entry<String,String>> it = mappings.iterator();
		while(it.hasNext()){
			Entry<String,String> e = it.next();
			String key = e.getKey();
			String value = e.getValue();
			if(value.contains("*")){
				value = value.replaceAll("\\*","(\\.\\*)");
			}
			if(Pattern.compile(value).matcher(request.getRequestURI()).find()){
				request.setServletName(key);
				return true;
			}
			
		}
		return false;
	}

	private void parseHeaders(SocketInputStream input) throws IOException,ServletException{
            HttpHeader httpHeader = new HttpHeader();
            input.readHeader(httpHeader);
            if(httpHeader.nameEnd==0){
            	if(httpHeader.valueEnd==0){
            		return;
            	}
            }
            String name = new String(httpHeader.name,0,httpHeader.nameEnd);
            String value = new String(httpHeader.value,0,httpHeader.valueEnd);
            
	}
	

	private void parseRequest(SocketInputStream input, OutputStream output) throws Exception{
		input.readRequestLine(requestLine);
		String method = new String(requestLine.method,0,requestLine.methodEnd);
		String uri = null;
		String protocol = new String(requestLine.protocol,0,requestLine.protocolEnd);
		
		int question = requestLine.indexOf("?");
		if(question>=0){
			System.out.println(new String(requestLine.uri,question+1,requestLine.uriEnd-question-1));
	        request.setQueryString(new String(requestLine.uri,question+1,requestLine.uriEnd-question-1));
	     
			uri = new String(requestLine.uri,0,question);
		
			
		}else{
			request.setQueryString(null);
			uri = new String(requestLine.uri,0,requestLine.uriEnd);
		}
		if(!uri.startsWith("/")){
			int pos = uri.indexOf("://");
			if(pos!=-1){
				pos = uri.indexOf("/",pos+3);
			    if(pos==-1){
			    	uri = "";
			    }else{
			    	uri = uri.substring(pos);
			    }
			}
		}
		String match = ";jessionid=";
		int semicolon = uri.indexOf(match);
		if(semicolon>=0){
			String rest = uri.substring(semicolon+match.length());
			int semicolon2 = rest.indexOf(";");
		    if(semicolon2>=0){
		    	request.setRequestedSessionId(rest.substring(0,semicolon2));
		    	rest = rest.substring(semicolon2);
		    }else{
		    	request.setRequestedSessionId(rest);
		    	rest = "";
		    }
		    request.setRequestSessionURL(true);
			uri = uri.substring(0,semicolon)+rest;
		}else{
			request.setRequestedSessionId(null);
			request.setRequestSessionURL(false);
		}
		request.setMethod(method);
		request.setProtocol(protocol);
		request.setRequestURI(uri);
		
		
	}

}
