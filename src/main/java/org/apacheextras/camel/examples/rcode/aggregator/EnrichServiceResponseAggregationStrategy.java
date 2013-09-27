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

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cemmersb
 */
public class EnrichServiceResponseAggregationStrategy implements AggregationStrategy {
  
  public static final String CALENDAR_SERVICE_RESPONSE = "CalendarServiceResponse";
  
  private static final Logger LOGGER = LoggerFactory.getLogger(EnrichServiceResponseAggregationStrategy.class);
  
  @Override
  public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
    final String oldBody = oldExchange.getIn().getBody(String.class);
    final Object newBody = newExchange.getIn().getBody();
    
    LOGGER.debug("Mapping new exchange to oldExchange in Header");
    
    oldExchange.getIn().setHeader(CALENDAR_SERVICE_RESPONSE, newBody);
    oldExchange.getIn().setBody(oldBody);
    
    return oldExchange;
  }  
}
