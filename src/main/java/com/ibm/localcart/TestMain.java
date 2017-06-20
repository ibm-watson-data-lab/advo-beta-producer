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

public class TestMain {

	public static void main(String[] args) throws InterruptedException {
		DataStream.getInstance();
		
		Thread.sleep( 5000 );		
		System.out.println("Setting EPS to 10");
		DataStream.setEventsPerSeconds( 10 );
		
		Thread.sleep( 5000 );		
		System.out.println("Setting EPS to 1");
		DataStream.setEventsPerSeconds( 1 );
		
		Thread.sleep( 5000 );		
		System.out.println("Setting EPS to 5");
		DataStream.setEventsPerSeconds( 5 );
		
	}

}
