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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author dtaieb
 *
 */
public class DataStream {
	
	private static DataStream stream = null;
	private static final String[] EVENT_TYPES = {"browsing", "logout_without_purchase", "logout_with_purchase", "login", "checkout", "add_to_cart"};
	private JsonArray events = null;
	private int eventPtr = 0;
	private int eventPerSeconds = 2;
	
	protected Map<String,Object> stats = new HashMap<>();
	
	private DataStream(){
		System.out.println("Loading local cart data stream...");
		
		try {
			loadSampleData();
			MessageHubConfig.getInstance().createTopicsIfNecessary( "clickStream" );
			for (String type: EVENT_TYPES){
				MessageHubConfig.getInstance().createTopicsIfNecessary( type );
			}
			startProducerThread();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void loadSampleData() throws Exception{
		URL url = new URL("https://github.com/ibm-cds-labs/advo-beta/raw/master/data/dataStream.json");
	    
	    JsonParser parser = new JsonParser();
	    try (InputStream is = url.openConnection().getInputStream()){
		    JsonElement root = parser.parse( new InputStreamReader(is));
		    events = root.getAsJsonArray();
	    }	
	}

	public static final DataStream getInstance(){
		if ( stream == null ){
			stream = new DataStream();
			setEventsPerSeconds( stream.eventPerSeconds );
		}
		return stream;
	}
	
	private synchronized JsonObject getNextEvent(){
		if ( events == null ){
			return null;
		}
		
		if (events.size() <= eventPtr ){
			eventPtr = 0;
		}
		
		try{
			JsonObject event = events.get(eventPtr).getAsJsonObject();
			event.remove("timestamp");
			event.addProperty("event_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").format(new Date()) );
			return event;
		}finally{
			eventPtr++;
		}
	}
	
	public static void setEventsPerSeconds(int eps){
		if (eps < 0 ){
			eps = 0;
		}else if (eps > 20){
			eps = 20;
		}
		getInstance().eventPerSeconds = eps;
		getInstance().stats.put("eps", eps);
	}
	
	private void startProducerThread() {
		new Thread( new Runnable() {
			public void run(){
				try(KafkaProducer<String, Object> kafkaProducer = new KafkaProducer<String,Object>( MessageHubConfig.getInstance().getConfig() )){
					while( true ){
						try{
							for (int i = 0; i < eventPerSeconds; i++ ){
								JsonObject event = getNextEvent();
								String eventStr = event.toString();
								ProducerRecord<String, Object> producerRecord = 
									new ProducerRecord<String,Object>("clickStream", "event", eventStr);
								Future<RecordMetadata> future = kafkaProducer.send(producerRecord);
								RecordMetadata metadata = future.get(5000, TimeUnit.MILLISECONDS);
								incrementStats("totalEvents");
								
								//Also send record to its own topic
								String type = event.get("click_event_type").getAsString();
								producerRecord = new ProducerRecord<String,Object>(type, "subevent", eventStr );
								kafkaProducer.send(producerRecord).get(5000, TimeUnit.MILLISECONDS);
								incrementStats(type);
							}
							Thread.sleep( 1000L );
						}catch (Throwable t){
							t.printStackTrace();
							Thread.sleep(5000);
						}
					}
				}catch (Throwable t){
					t.printStackTrace();
				}
			}
		},"Producer").start();
	}

	private void incrementStats(String key) {
		if (!stats.containsKey( key )){
			stats.put(key, 1);
		}else{
			stats.put(key, (int)stats.get(key) + 1);
		}
	}
	
}
