/*
 * Copyright 2013 Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apacheextras.camel.examples.rcode.aggregator;

import java.util.ArrayList;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cemmersb
 */
public class CalendarAgregationStrategy implements AggregationStrategy {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(CalendarAgregationStrategy.class);
  private List<String> holidays = new ArrayList<String>();

  @Override
  public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
    LOGGER.debug("Aggrgating the dates to generate a List of holidays.");
    
    final String date = newExchange.getIn().getBody(String.class);
    
    if(null != oldExchange) {
      holidays = oldExchange.getIn().getBody(List.class);
    }
    
    if(!holidays.contains(date)) {
      holidays.add(date);
    }
    
    newExchange.getIn().setBody(holidays);
    return newExchange;
  } 
}
