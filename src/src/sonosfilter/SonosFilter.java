/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonosfilter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
    //String ip = "localhost";
    List<String> ips = new ArrayList<>();
    int blockedCount = 0;
    int loudCount = 0;
    
    public static void main(String[] args) {
        SonosFilter sf = new SonosFilter();
    }
    
    SonosFilter(){
        getIp();
        
        Thread thread = (new Thread(){
            @Override
            public void run(){
                while(true){
                    try {    
                        checkSonos();
                        Thread.sleep(5000);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, new JLabel("Warning: " + "SonosFilter Crashed. Please restart."));
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
                //System.out.println(result);
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
                
                if(checkLouder(result.toLowerCase())){
                    setVolume(getLoudVolume());
                    loudCount = 4;
                } else {
                    loudCount--;
                    if(loudCount <= 0){
                        loudCount = 0;
                        setVolume(getVolume());
                        
                        if(checkFilter(result.toLowerCase())){
                            setVolume(0);
                            blockedCount = 4;
                        } else {
                            blockedCount--;
                            if(blockedCount <= 0){
                                blockedCount = 0;
                                setVolume(getVolume());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Can't connect to " + ip);
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
            JOptionPane.showMessageDialog(null, new JLabel("Warning: " + "filter.json is missing \"filters\"."));
        }
        return false;
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
            JOptionPane.showMessageDialog(null, new JLabel("Warning: " + "filter.json is missing \"filters\"."));
        }
        return false;
    }
    
    private void getIp(){
        try {
            JSONObject filters = getFilters();
            JSONArray j = filters.getJSONArray("ips");
            
            //System.out.println(j.toString());
            //System.out.println(j.length());
            for(int i = 0; i < j.length();i++){
                ips.add(j.getString(i));
            }
            System.out.println("ip: " + j);
            JOptionPane.showMessageDialog(null, new JLabel("ips: " + j));   
            //return j;
        } catch (JSONException ex) {
            JOptionPane.showMessageDialog(null, new JLabel("Warning: " + "filter.json is missing \"ips\"."));
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
            JOptionPane.showMessageDialog(null, new JLabel("Warning: " + "filter.json is missing \"volume\"."));
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
            JOptionPane.showMessageDialog(null, new JLabel("Warning: " + "filter.json is missing \"volume\"."));
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
            JOptionPane.showMessageDialog(null, new JLabel("Warning: " + "filter.json is broken for some reason."));
            Logger.getLogger(SonosFilter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, new JLabel("Warning: " + "filter.json is missing."));
            Logger.getLogger(SonosFilter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, new JLabel("Warning: " + "filter.json is missing/corrupted."));
            Logger.getLogger(SonosFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
