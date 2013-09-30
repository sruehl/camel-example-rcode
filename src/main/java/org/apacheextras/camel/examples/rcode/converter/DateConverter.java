/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apacheextras.camel.examples.rcode.converter;

import com.google.gson.internal.StringMap;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import org.apache.camel.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cemmersb
 */
@Converter
public class DateConverter {
  
  private final static Logger LOGGER = LoggerFactory.getLogger(DateConverter.class);
  
  @Converter
  public Date toDate(LinkedHashMap dateMap) {
    final StringMap dsm = (StringMap) dateMap.get("date");
    
    final String dateString = String.valueOf(dsm.get("year")) + '-' + ((Double) dsm.get("month")).intValue() + '-' + ((Double) dsm.get("day")).intValue();
    
    try {
      LOGGER.debug("Converting '{}' to Date object.", dateString);
      return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
    } catch (ParseException ex) {
      LOGGER.error("Could not parse the String to retrieve the Date object: {}", ex);
      return null;
    }
  }
}