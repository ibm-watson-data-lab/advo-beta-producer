/*******************************************************************************
 * Copyright (c) 2017 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/ 
package com.ibm.localcart;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;

/**
 * @author dtaieb
 *
 */
public class MessageHubConfig {
	//Hold configuration key/value pairs
	private Map<String, String> config = new HashMap<>();
	
	private static final String KAFKA_TOPIC_TWEETS = "kafka.topic.tweet";    //Key for name of the kafka topic holding used for publishing the tweets
	private static final String KAFKA_USER_NAME = "kafka.user.name";
	private static final String KAFKA_USER_PASSWORD = "kafka.user.password";

	private static final String MESSAGEHUB_API_KEY = "api_key";
	private static final String MESSAGEHUB_REST_URL = "kafka_rest_url";
	
	private static MessageHubConfig instance = null;
	
	public static final MessageHubConfig getInstance() throws Throwable{
		if ( instance == null ){
			instance = new MessageHubConfig();
			instance.validateConfiguration();
		}
		return instance;
	}
	
	public Map<String, Object> getConfig(){
		return new HashMap<String, Object>(config);
	}

	private boolean validateConfiguration() throws Throwable {
	    boolean ret = true;
	    Iterator<Entry<String,String>> it = config.entrySet().iterator();
	    while (it.hasNext()) {
	        Entry<String,String> pair = it.next();
	        String key = pair.getKey();
	        String value = pair.getValue();
	    	if (value == null ){
	    		System.err.println(key + " configuration not set. Use setConfig(\"" + key + "\",<your Value>)"); 
	    		ret = false;
	    	}
	    }
	    
	    return ret;		
	}

	private MessageHubConfig(){
		registerConfigKey(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG);
		registerConfigKey(CommonClientConfigs.CLIENT_ID_CONFIG, "demo.watson.twitter.messagehub");
		registerConfigKey("auto.offset.reset", "latest");
		registerConfigKey("acks", "-1");
		registerConfigKey("retries", "0");
		registerConfigKey("batch.size", "16384");
		registerConfigKey("linger.ms", "1");
		registerConfigKey("buffer.memory", "33554432");
		registerConfigKey("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		registerConfigKey("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		registerConfigKey(SslConfigs.SSL_PROTOCOL_CONFIG, "TLSv1.2");
		registerConfigKey(SslConfigs.SSL_ENABLED_PROTOCOLS_CONFIG, "TLSv1.2");
		registerConfigKey(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, "JKS");
		registerConfigKey(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, getDefaultSSLTrustStoreLocation());
		registerConfigKey(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "changeit");
		registerConfigKey(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "HTTPS");
		registerConfigKey(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL" );
		registerConfigKey(MessageHubConfig.KAFKA_TOPIC_TWEETS, "demo.tweets.watson.topic");
		registerConfigKey(MessageHubConfig.KAFKA_USER_NAME);
		registerConfigKey(MessageHubConfig.KAFKA_USER_PASSWORD);
		registerConfigKey(MessageHubConfig.MESSAGEHUB_API_KEY);
		registerConfigKey(MessageHubConfig.MESSAGEHUB_REST_URL);
		
		setConfig("value.serializer", StringSerializer.class.getName());
	      
		//Load config from property file if specified
		String configPath = (System.getProperty("DEMO_CONFIG_PATH") );
		if (configPath == null){
			configPath = System.getenv("DEMO_CONFIG_PATH");
		}

		if ( configPath == null ){
			configPath = System.getProperty("spark.service.user.DEMO_CONFIG_PATH");
		}

		if (configPath == null){
			configPath = System.getenv("spark.service.user.DEMO_CONFIG_PATH");
		}

		if (configPath != null ){
			System.out.println("ConfigPath is: " + configPath );
			System.out.println("Loading config from DEMO_CONFIG_PATH env variable: " + configPath);
			Properties props = new java.util.Properties();

			try( InputStream fis = new FileInputStream(configPath)){
				props.load(fis);
				for( String key : props.keySet().toArray(new String[0]) ){
					setConfig( key, props.getProperty(key));
				}
			}catch (Throwable t){
				t.printStackTrace();
			}
		}
	}

	private void registerConfigKey( String key ){
		
		registerConfigKey(key, null);
	}

	private void registerConfigKey( String key, String defaultValue ) {
		String value = System.getProperty( key );
		if ( value == null ){
			value = defaultValue;
		}
		config.put(key, value);
	}

	private void setConfig(String key, String value){
		config.put( key, value );
	}

	private String getConfig(String key){
		String retValue = config.get(key);
		if ( retValue == null){
			return "";
		}
		return retValue;
	}
	
	private String getDefaultSSLTrustStoreLocation(){
		String javaHome = System.getProperty("java.home") + File.separator + "lib" + File.separator + "security" + File.separator + "cacerts";
		System.out.println("default location of ssl Trust store is: " + javaHome);
		return javaHome;
	}
	
	public void createTopicsIfNecessary( String... topics ) throws Exception{
		SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
		sslContext.init(null, null, null);
		for( String topic : topics ){
			URL messageHubUrl = new URL( getConfig(MessageHubConfig.MESSAGEHUB_REST_URL) + "/admin/topics" );
			HttpsURLConnection con = (HttpsURLConnection) messageHubUrl.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("X-Auth-Token", getConfig(MessageHubConfig.MESSAGEHUB_API_KEY));
			con.setRequestProperty("Accept", "application/json");
			con.setRequestMethod("POST");

			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			wr.write( "{\"name\":\"" + topic + "\"}" );
			wr.flush();

			int res = con.getResponseCode();
			switch (res){
			case 200:
			case 202:
				System.out.println("Successfully created topic: " + topic);
				break;            	   
			case 422:
			case 403: 
				System.out.println("Topic already exists in the server: " + topic);
				break;
			default:
				throw new IllegalStateException("Error when trying to create topic: " + res + " Reason: " + con.getResponseMessage());
			}
		}
	}
}
