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

import com.google.gson.internal.StringMap;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
  private List<Date> holidays = new ArrayList<Date>();

  @Override
  public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
    
    if(null != oldExchange) {
      holidays = oldExchange.getIn().getBody(List.class);
    }
    
    LinkedHashMap dateMap = newExchange.getIn().getBody(LinkedHashMap.class);
    try {
      if(!holidays.contains(toDate(dateMap))) {
        holidays.add(toDate(dateMap));
      }
    } catch (ParseException ex) {
      LOGGER.error("Could not cast the given date: {}", ex.getMessage());
    }
    
    newExchange.getIn().setBody(holidays);
    return newExchange;
  }
  
  private Date toDate(LinkedHashMap dateMap) throws ParseException {
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    final StringMap dsm = (StringMap) dateMap.get("date");
    
    final StringBuilder sb = new StringBuilder()
        .append(dsm.get("year"))
        .append('-')
        .append(((Double)dsm.get("month")).intValue())
        .append('-')
        .append(((Double)dsm.get("day")).intValue());
    
    return sdf.parse(sb.toString());
  }
  
}
