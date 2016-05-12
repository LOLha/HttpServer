package XmlConfig;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ServletConfig {
	
	private Map<String,String> servletConfig = new LinkedHashMap<String,String>();
	
	private Map<String,String> mappingConfig = new LinkedHashMap<String,String>();
	
	
	public ServletConfig(){
		    String config = System.getProperty("user.dir")+File.separator+"webRoot"+File.separator+"web.xml";
			SAXReader reader = new SAXReader();
			try {
				Document doc = reader.read(new File(config));
				Element webApp = doc.getRootElement();
				readServlet(webApp);
				readServletMapping(webApp);
			} catch (DocumentException e) {
			
				e.printStackTrace();
			}
		
		
		
	}


	private void readServletMapping(Element webApp) {
		List<Element> mapping = webApp.elements("servlet-mapping");
		for(Element e:mapping){
            String servletName = e.elementTextTrim("servlet-name");
            String urlPattern = e.elementTextTrim("url-pattern");
            mappingConfig.put(servletName, urlPattern);
	}
		
	}


	private void readServlet(Element webApp) {
		List<Element> servlet = webApp.elements("servlet");
		for(Element e:servlet){
	               String servletName = e.elementTextTrim("servlet-name");
	               String servletClass = e.elementTextTrim("servlet-class");
	               servletConfig.put(servletName, servletClass);
		}
	}


	public Map<String, String> getServletConfig() {
		return servletConfig;
	}


	public Map<String, String> getMappingConfig() {
		return mappingConfig;
	}
	
}
