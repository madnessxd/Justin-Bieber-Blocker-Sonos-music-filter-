/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonosfilter;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author Grim Reaper
 */
public class SonosFilter {
    /**
     * @param args the command line arguments
     */
    List<String> ips = new ArrayList<>();
    int blockedCount = 0;
    int loudCount = 0;
    Tray tray;
    
    String lastError = "";
    String lastInfo = "";
    String lastWarning = "";
    
    Date lastErrorTime = new Date(0);
    
    public static void main(String[] args) {
        new SonosFilter();
    }
    
    private void showError(String error){
        if(lastError != error && lastErrorTime.compareTo(new Date()) < 0 ){
            System.out.println(lastErrorTime.compareTo(new Date()));
            tray.showError(error);
            lastError = error;
            lastErrorTime.setTime(new Date().getTime() + 30000);
        }
    }
    
    private void showInfo(String error){
        if(lastInfo != error){
            tray.showInfo(error);
            lastInfo = error;
        }
    }
    
    private void showWarning(String error){
        if(lastWarning != error){
            tray.showWarning(error);
            lastWarning = error;
        }
    }
    
    SonosFilter(){    
        tray = new Tray(checkPopUps());   
        getIp();
        
        Thread thread = (new Thread(){
            @Override
            public void run(){
                while(true){
                    try {    
                        checkSonos();
                        Thread.sleep(5000);
                        tray.setPopUps(checkPopUps());
                    } catch (Exception ex) {
                        showError("SonosFilter Crashed. Please restart.");
                    }
                }
            }
        });
        thread.start();
    }
    
    private void pauseSonos(){
        try {
            for(String ip: ips){
                URLConnection connection = new URL("http://" + ip + ":1400" + "/MediaRenderer/AVTransport/Control").openConnection();
                connection.setRequestProperty("Connection", "close");
                connection.setRequestProperty("SOAPACTION", "\"urn:schemas-upnp-org:service:AVTransport:1#Pause\"");

                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setConnectTimeout( 2000 );  // long timeout, but not infinite
                connection.setReadTimeout( 2000 );
                connection.setUseCaches (false);
                connection.setDefaultUseCaches (false);

                connection.setRequestProperty ( "Content-Type", "text/xml" );

                OutputStreamWriter writer = new OutputStreamWriter( connection.getOutputStream() );

                writer.write( "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
                                "	<s:Body>\n" +
                                "		<u:Pause xmlns:u=\"urn:schemas-upnp-org:service:AVTransport:1\">\n" +
                                "			<InstanceID>0</InstanceID>\n" +
                                "			<Speed>1</Speed>\n" +
                                "		</u:Pause>\n" +
                                "	</s:Body>\n" +
                                "</s:Envelope>" );

                writer.flush();
                writer.close();

                InputStreamReader reader = new InputStreamReader( connection.getInputStream() );
                StringBuilder buf = new StringBuilder();
                char[] cbuf = new char[ 2048 ];
                int num;
                while ( -1 != (num=reader.read( cbuf )))
                {
                    buf.append( cbuf, 0, num );
                }

                String result = buf.toString();
            }
        } catch (Exception e) {
            
        }
    }
    
    private void playSonos(){
        for(String ip: ips){
            try {
                URLConnection connection = new URL("http://" + ip +":1400" + "/MediaRenderer/AVTransport/Control").openConnection();
                connection.setRequestProperty("Connection", "close");
                connection.setRequestProperty("SOAPACTION", "\"urn:schemas-upnp-org:service:AVTransport:1#Play\"");

                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setConnectTimeout( 2000 );  // long timeout, but not infinite
                connection.setReadTimeout( 2000 );
                connection.setUseCaches (false);
                connection.setDefaultUseCaches (false);

                connection.setRequestProperty ( "Content-Type", "text/xml" );

                OutputStreamWriter writer = new OutputStreamWriter( connection.getOutputStream() );

                writer.write( "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
                                "	<s:Body>\n" +
                                "		<u:Play xmlns:u=\"urn:schemas-upnp-org:service:AVTransport:1\">\n" +
                                "			<InstanceID>0</InstanceID>\n" +
                                "			<Speed>1</Speed>\n" +
                                "		</u:Play>\n" +
                                "	</s:Body>\n" +
                                "</s:Envelope>" );

                writer.flush();
                writer.close();

                InputStreamReader reader = new InputStreamReader( connection.getInputStream() );
                StringBuilder buf = new StringBuilder();
                char[] cbuf = new char[ 2048 ];
                int num;
                while ( -1 != (num=reader.read( cbuf )))
                {
                    buf.append( cbuf, 0, num );
                }

                String result = buf.toString();
                //System.out.println(result);
            } catch (Exception e) {

            }
        }
    }
    
    private void setVolume(int volume){
        for(String ip: ips){
            try {
                URLConnection connection = new URL("http://" + ip + ":1400" + "/MediaRenderer/RenderingControl/Control").openConnection();
                connection.setRequestProperty("Connection", "close");
                connection.setRequestProperty("SOAPACTION", "urn:schemas-upnp-org:service:RenderingControl:1#SetVolume");

                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setConnectTimeout( 2000 );  // long timeout, but not infinite
                connection.setReadTimeout( 2000 );
                connection.setUseCaches (false);
                connection.setDefaultUseCaches (false);

                connection.setRequestProperty ( "Content-Type", "text/xml" );

                try (OutputStreamWriter writer = new OutputStreamWriter( connection.getOutputStream() )) {
                    writer.write( "<s:Envelope \n" +
                                    "	xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                                    "	s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"\n" +
                                    "	>\n" +
                                    "  <s:Body>\n" +
                                    "    <u:SetVolume xmlns:u=\"urn:schemas-upnp-org:service:RenderingControl:1\">\n" +
                                    "      <InstanceID>0</InstanceID>\n" +
                                    "      <Channel>Master</Channel>\n" +
                                    "      <DesiredVolume>" + volume + "</DesiredVolume>\n" +
                                    "    </u:SetVolume>\n" +
                                    "  </s:Body>\n" +
                                    "</s:Envelope>" );

                    writer.flush();
                }

                InputStreamReader reader = new InputStreamReader( connection.getInputStream() );
                StringBuilder buf = new StringBuilder();
                char[] cbuf = new char[ 2048 ];
                int num;
                while ( -1 != (num=reader.read( cbuf )))
                {
                    buf.append( cbuf, 0, num );
                }

                String result = buf.toString();
                //System.out.println(result);
            } catch (Exception e) {

            }
        }
    }
    
    private void checkSonos(){
        for(String ip: ips){
            try {
                URLConnection connection = new URL("http://" + ip + ":1400" + "/MediaRenderer/AVTransport/Control").openConnection();
                connection.setRequestProperty("Connection", "close");
                connection.setRequestProperty("SOAPACTION", "urn:schemas-upnp-org:service:AVTransport:1#GetPositionInfo");

                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setConnectTimeout(2000);  // long timeout, but not infinite
                connection.setReadTimeout(2000);
                connection.setUseCaches (false);
                connection.setDefaultUseCaches (false);

                connection.setRequestProperty ( "Content-Type", "text/xml" );

                try (OutputStreamWriter writer = new OutputStreamWriter( connection.getOutputStream() )) {
                    writer.write( "<s:Envelope \n" +
                            "	xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                            "	s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"\n" +
                            "	>\n" +
                            "  <s:Body>\n" +
                            "    <u:GetPositionInfo xmlns:u=\"urn:schemas-upnp-org:service:AVTransport:1\">\n" +
                            "      <InstanceID>0</InstanceID>\n" +
                            "    </u:GetPositionInfo>\n" +
                            "  </s:Body>\n" +
                            "</s:Envelope>\n" +
                            "<!--MediaRenderer/AVTransport/Control-->" );

                    writer.flush();
                }

                InputStreamReader reader = new InputStreamReader( connection.getInputStream() );
                StringBuilder buf = new StringBuilder();
                char[] cbuf = new char[ 2048 ];
                int num;
                while ( -1 != (num=reader.read( cbuf )))
                {
                    buf.append( cbuf, 0, num );
                }

                String result = buf.toString();
                //System.out.println(result);
                try{
                    Document document = loadXMLFromString(result);
                    Element rootElement = document.getDocumentElement();

                    //System.out.println(getString("TrackMetaData", rootElement));
                    
                    if(!equals(getString("TrackMetaData", rootElement).equals("NOT_IMPLEMENTED"))){
                        Document document2 = loadXMLFromString(getString("TrackMetaData", rootElement));
                        Element rootElement2 = document2.getDocumentElement();

                        System.out.println("---");
                        System.out.println(getString("r:streamContent", rootElement2));
                        System.out.println(getString("res", rootElement2));
                        System.out.println(getString("title", rootElement2));
                        System.out.println("---");
                    }
                } catch(Exception e){
                    
                }
                
                int volume = getVolume();
                Document document = loadXMLFromString(result);
                Element rootElement = document.getDocumentElement();
                    
                if(checkFilter(result.toLowerCase())){
                    if(!equals(getString("TrackMetaData", rootElement).equals("NOT_IMPLEMENTED"))){
                        Document document2 = loadXMLFromString(getString("TrackMetaData", rootElement));
                        Element rootElement2 = document2.getDocumentElement();
                        showInfo("Song muted: " + getString("r:streamContent", rootElement2));
                    }
                    volume = 0;
                }
                if(checkLouder(result.toLowerCase())){
                    if(!equals(getString("TrackMetaData", rootElement).equals("NOT_IMPLEMENTED"))){
                        Document document2 = loadXMLFromString(getString("TrackMetaData", rootElement));
                        Element rootElement2 = document2.getDocumentElement();
                        showInfo("Song made louder: " + getString("r:streamContent", rootElement2));
                    }
                    
                    volume = getLoudVolume();
                }
                setVolume(volume);
                if(volume != getVolume()){
                    break;
                }
            } catch (Exception e) {
                showError("Can't connect to " + ip);
            }
        }
    }
    
    protected String getString(String tagName, Element element) {
        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            NodeList subList = list.item(0).getChildNodes();

            if (subList != null && subList.getLength() > 0) {
                return subList.item(0).getNodeValue();
            }
        }

        return null;
    }
    
    public static Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }
    
    private Boolean checkPopUps(){
        try {
            JSONObject filters = getFilters();
            Boolean j = filters.getBoolean("pop-ups");
            return j; 
        } catch (JSONException ex) {
            showError("filter.json is missing \"pop-ups\".");
        } catch(NullPointerException n){
            return true;
        }
        return false;
    }
    
    private void createJson(){
        try {
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("filter.json"), "utf-8"))) {
                writer.write("{\n" +
                        "    \"filters\": [\n" +
                        "        {\"Artist\": \"Justin Bieber\"},\n" +
                        "        {\"Artist\": \"Adele\"},\n" +
                        "        {\"Artist\": \"Rihanna\"},\n" +
                        "        {\"Artist\": \"Shawn Mendes\"},\n" +
                        "        {\"Artist\": \"Bruno Mars\"},\n" +
                        "        {\"Artist\": \"Drake\"}\n" +
                        "    ],\n" +
                        "    \"louder\": [\n" +
                        "		{\"Artist\": \"Simple Minds\"},\n" +
                        "		{\"Artist\": \"Stone Roses\"},\n" +
                        "		{\"Artist\": \"Joy Division\"},\n" +
                        "		{\"Artist\": \"Cure\"},\n" +
                        "		{\"Artist\": \"Smiths\"}\n" +
                        "    ],\n" +
                        "    \"ips\" : [\"192.168.1.2\"],\n" +
                        "    \"volume\": 20,\n" +
                        "	\"loudVolume\": 30,\n" +
                        "	\"pop-ups\": true\n" +
                        "}");
            } catch (IOException ex) {
                Logger.getLogger(SonosFilter.class.getName()).log(Level.SEVERE, null, ex);
                showError("Can't create filters.json");
            }
            showInfo("filters.json created. Please restart the program.");
            Desktop.getDesktop().open(new File("filter.json"));
            Thread.sleep(15000);
            System.exit(1);
        } catch (IOException ex) {
            Logger.getLogger(SonosFilter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(SonosFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private Boolean checkFilter(String input){
        try {
            JSONObject filters = getFilters();
            JSONArray j = filters.getJSONArray("filters");
            for(int i = 0; i < j.length(); i++){
                String filterString = j.getJSONObject(i).getString("Artist");
                if(input.contains(filterString.toLowerCase())){
                    System.out.println("Found " + filterString);
                    return true; 
                }
            }
        } catch (JSONException ex) {
            showError("filter.json is missing \"filters\".");
        }
        return false;
    }
    
    private int getFilterLength(){
        try {
            JSONObject filters = getFilters();
            JSONArray j = filters.getJSONArray("filters");
            return j.length(); 
            
        } catch (JSONException ex) {
            showError("filter.json is missing \"filters\".");
        }
        return 0;
    }
  
    private int getLouderLength(){
        try {
            JSONObject filters = getFilters();
            JSONArray j = filters.getJSONArray("louder");
            return j.length(); 
        } catch (JSONException ex) {
            showError("filter.json is missing \"louder\".");
        }
        return 0;
    }
        
    private Boolean checkLouder(String input){
        try {
            JSONObject filters = getFilters();
            JSONArray j = filters.getJSONArray("louder");
            for(int i = 0; i < j.length(); i++){
                String filterString = j.getJSONObject(i).getString("Artist");
                if(input.contains(filterString.toLowerCase())){
                    System.out.println("Found " + filterString);
                    return true; 
                }
            }
        } catch (JSONException ex) {
            showError("filter.json is missing \"filters\".");
        }
        return false;
    }
    
    private void getIp(){
        try {
            JSONObject filters = getFilters();
            JSONArray j = filters.getJSONArray("ips");
            
            for(int i = 0; i < j.length();i++){
                ips.add(j.getString(i));
            }
            System.out.println("ip: " + j);
            showInfo("ips: " + j);   
        } catch (JSONException ex) {
            showError("filter.json is missing \"ips\".");
            Logger.getLogger(SonosFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    private int getVolume(){
        try {
            JSONObject filters = getFilters();
            int j = filters.getInt("volume");
            System.out.println("volume: " + j);
            return j;
        } catch (JSONException ex) {
            showError("filter.json is missing \"volume\".");
            Logger.getLogger(SonosFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 15;
    }
    
    private int getLoudVolume(){
        try {
            JSONObject filters = getFilters();
            int j = filters.getInt("loudVolume");
            System.out.println("loudVolume: " + j);
            return j;
        } catch (JSONException ex) {
            showError("filter.json is missing \"volume\".");
            Logger.getLogger(SonosFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 15;
    }
    
    private JSONObject getFilters(){
        try {
            //BufferedReader br = new BufferedReader(new FileReader("C:\\filter.json"));
            BufferedReader br = new BufferedReader(new FileReader("filter.json"));
            
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            String result = sb.toString();
            
            JSONObject filters = new JSONObject(result);
            return filters;
        } catch (JSONException ex) {
            showError("filter.json is broken. It might be empty.");
            Logger.getLogger(SonosFilter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            showError("filter.json is missing.");
            Logger.getLogger(SonosFilter.class.getName()).log(Level.SEVERE, null, ex);
            createJson();
        } catch (IOException ex) {
            showError("filter.json is missing/corrupted.");
            Logger.getLogger(SonosFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
