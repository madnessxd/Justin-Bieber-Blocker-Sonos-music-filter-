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
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Grim Reaper
 */
public class SonosFilter {
    /**
     * @param args the command line arguments
     */
    String ip = "localhost";
    
    public static void main(String[] args) {
        SonosFilter sf = new SonosFilter();
    }
    SonosFilter(){
        ip = getIp();
        
        Thread thread = (new Thread(){
            @Override
            public void run(){
                while(true){
                    try {    
                        playSonos();
                        checkSonos();
                        Thread.sleep(5000);
                    } catch (Exception ex) {
                        
                    }
                }
            }
        });
        thread.start();
        //pauseSonos();
        //playSonos();
    }
    
    private void pauseSonos(){
        try {
            URLConnection connection = new URL("http://" + ip +":1400" + "/MediaRenderer/AVTransport/Control").openConnection();
            connection.setRequestProperty("Connection", "close");
            connection.setRequestProperty("SOAPACTION", "\"urn:schemas-upnp-org:service:AVTransport:1#Pause\"");

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout( 20000 );  // long timeout, but not infinite
            connection.setReadTimeout( 20000 );
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
            System.out.println(result);
        } catch (Exception e) {

        }
    }
    
    private void playSonos(){
        try {
            URLConnection connection = new URL("http://" + ip +":1400" + "/MediaRenderer/AVTransport/Control").openConnection();
            connection.setRequestProperty("Connection", "close");
            connection.setRequestProperty("SOAPACTION", "\"urn:schemas-upnp-org:service:AVTransport:1#Play\"");

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout( 20000 );  // long timeout, but not infinite
            connection.setReadTimeout( 20000 );
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
            System.out.println(result);
        } catch (Exception e) {

        }
    }
    
    private void checkSonos(){
    try {
            URLConnection connection = new URL("http://" + ip + ":1400" + "/MediaRenderer/AVTransport/Control").openConnection();
            connection.setRequestProperty("Connection", "close");
            connection.setRequestProperty("SOAPACTION", "urn:schemas-upnp-org:service:AVTransport:1#GetPositionInfo");

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout( 20000 );  // long timeout, but not infinite
            connection.setReadTimeout( 20000 );
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
            System.out.println(result);
            
            if(checkFilter(result.toLowerCase())){
                pauseSonos();
            }
        } catch (Exception e) {

        }
    }
    private Boolean checkFilter(String input){
        try {
            JSONObject filters = getFilters();
            JSONArray j = filters.getJSONArray("filters");
            for(int i = 0; i < j.length(); i++){
                String filterString = j.getJSONObject(i).getString("Artist");
                if(input.contains(filterString.toLowerCase())){
                    return true; 
                }
            }
        } catch (JSONException ex) {
            Logger.getLogger(SonosFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private String getIp(){
        try {
            JSONObject filters = getFilters();
            String j = filters.getString("ip");
            System.out.println("ip: " + j);
            JOptionPane.showMessageDialog(null, new JLabel("ip: " + j));
            return j;
        } catch (JSONException ex) {
            JOptionPane.showMessageDialog(null, new JLabel("Warning: " + "filter.json is empty/corrupted."));
            Logger.getLogger(SonosFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    private JSONObject getFilters(){
        try {
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
