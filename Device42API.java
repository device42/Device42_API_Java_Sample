/*
 * Copyright (c) 2015, Device42, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import org.json.simple.*;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author user
 */
public class Device42API {

    private static JSONObject Device42Get(String apiUrl, String authorization) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        try {
           url = new URL(apiUrl);
           conn = (HttpURLConnection) url.openConnection();
           conn.setRequestMethod("GET");
           conn.setRequestProperty("Authorization", "Basic " + authorization);
           conn.setRequestProperty("Content-length", "0");
           conn.setUseCaches(false);
           conn.setAllowUserInteraction(false);
           conn.setRequestProperty("Content-Type", "application/json");
           conn.setRequestProperty("Accept", "application/json");
           conn.connect();
           int status = conn.getResponseCode(); 
           
           if ( status == 200 ) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = rd.readLine()) != null) {
                    result += line;
                }
                rd.close();

                return (JSONObject) JSONValue.parse(result);
           } else {
               throw new Exception("Device42 returned a status of " + status);
           }
            
        } catch (IOException e) {
           e.printStackTrace();
        } catch (Exception e) {
           e.printStackTrace();
        }
        
        return null;
    }
    
    private static JSONObject Device42Post(String apiUrl, String authorization, String data) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        try {
           url = new URL(apiUrl);
           byte[] postData = data.getBytes( Charset.forName( "UTF-8" ));
           int postDataLength = postData.length;
           
           conn = (HttpURLConnection) url.openConnection();
           conn.setRequestMethod("POST");
           conn.setRequestProperty("Authorization", "Basic " + authorization);
           conn.setUseCaches(false);
           conn.setAllowUserInteraction(false);
           conn.setDoOutput(true);
           conn.setDoInput ( true );
           conn.setInstanceFollowRedirects( false );
           conn.setRequestProperty("Accept", "application/json");
           conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
           conn.setRequestProperty( "charset", "utf-8");
           conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
           conn.setUseCaches( false );
           try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
               wr.write( postData );
           }           

           conn.connect();
           int status = conn.getResponseCode(); 
           
           if ( status == 200 ) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = rd.readLine()) != null) {
                    result += line;
                }
                rd.close();

                return (JSONObject) JSONValue.parse(result);
           } else {
               throw new Exception("Device42 returned a status of " + status);
           }
            
        } catch (IOException e) {
           e.printStackTrace();
        } catch (Exception e) {
           e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if ( args.length == 3 ) {
            String device42Url = args[0];
            String userName = args[1];
            String password = args[2];

            String userPassword = userName + ":" + password;
            String authorization = DatatypeConverter.printBase64Binary(userPassword.getBytes());

            JSONObject device = Device42Get(device42Url + "api/1.0/devices/id/10482/", authorization );
            if ( device != null ) {
                long deviceId = (long )device.get("device_id");
                String deviceName = (String )device.get("name");
                String uuid = (String )device.get("uuid");
                String notes = (String )device.get("notes");
                System.out.println("DeviceId = " + deviceId + " Device Name = " + deviceName + " UUID = " + uuid + " Notes = " + notes);

                String data = "uuid=" + uuid + "&notes=" + "Updated from Java";
                JSONObject results = Device42Post(device42Url + "api/1.0/devices/", authorization, data );
                JSONArray msg = (JSONArray )results.get("msg");
                System.out.print(" Message = " );
                for(int i=0;i<msg.size();i++) {
                    System.out.print( msg.get(i) + " " );    
                }
                long code = (long )results.get("code");
                System.out.println(" Code = " + code);
            } else {
                System.out.println("Failure accessing Device42API");    
            }
        } else {
            System.out.println("Usage: Device42API device_42_url device42_user device42_password");
        }
    }
}
