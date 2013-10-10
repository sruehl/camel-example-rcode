/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apacheextras.camel.examples.rcode.processor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cemmersb
 */
public class MonthlySalesFigureCalcProcessor implements Processor {

  /**
   * Provides some level of logging information.
   */
  private final static Logger LOGGER = LoggerFactory.getLogger(MonthlySalesFigureCalcProcessor.class);
  /**
   * Hash Map containing all daily sales within a calendar layout.
   */
  public final Map<String, Map<String, String>> dailySalesCalendar =
          new LinkedHashMap<String, Map<String, String>>();

  /**
   * {@inheritDoc}
   * <p>Executes the calculation of daily sales data into monthly values to run
   * the forecast</p>
   */
  @Override
  public void process(Exchange exchng) throws Exception {
    // Retrieve the sales days from the exchange
    final List<List> salesDay = exchng.getIn().getBody(ArrayList.class);
    
    // Get the sales data and convert it into an object where we can run the summary
    for (List salesDate : salesDay) {
      LOGGER.debug("Sales date: {} and value: {}", salesDate.get(0), salesDate.get(1));
      setSalesValue(getMonthAndYearOfDate(salesDate.get(0).toString()), 
              salesDate.get(0).toString(), salesDate.get(1).toString());
    }
    // Create an R vector from the monthly sales values
    final String rVector = toRVector(summarizeMonthlyValues());
    // Set the r vector to the exchange for further processing
    exchng.getIn().setBody(rVector);
  }
  
  /**
   * Creates an R vector by a given array of monthly sales
   */
  private String toRVector(int[] monthlySales) {
    // Create the R vector as string
    final StringBuilder rVector = new StringBuilder();
    // Iterate via the monthly sales and buildup the string
    for (int i = 0; i < monthlySales.length; i++) {
      rVector.append(monthlySales[i]);
      if ((i + 1) != monthlySales.length) {
        rVector.append(',');
      }
    }
    // Return the vector as string
    return rVector.toString();
  }
  
  /**
   * Summarize all daily values by month
   */
  private int[] summarizeMonthlyValues() {
    final Set<String> monthAndYears = dailySalesCalendar.keySet();
    final int[] sum = new int[monthAndYears.size()];
    int i = 0;
    for (String monthAndYear : monthAndYears) {
      for (String value : dailySalesCalendar.get(monthAndYear).values()) {
        sum[i] = sum[i] + Integer.parseInt(value);
      }
      i++;
    }
    return sum;
  }
  
  /**
   * Set's the sales value to the calendar layout.
   */
  private void setSalesValue(String monthAndYear, String date, String value) throws Exception {
    if (null == dailySalesCalendar.get(monthAndYear)) {
      // Initialize the month if not available
      dailySalesCalendar.put(monthAndYear, new LinkedHashMap<String, String>());
    } else {
      if (null != dailySalesCalendar.get(monthAndYear).get(date)) {
        // Remove the existing value to replace with the lates information
        dailySalesCalendar.get(monthAndYear).remove(date);
      }
    }
    // Set the date to the calendar map
    dailySalesCalendar.get(monthAndYear).put(date, value);
  }
  
  /**
   * Converting the date object into a year and month string to be able running
   * a sum calculation on every month per year
   */
  private String getMonthAndYearOfDate(String strDate) {
    LOGGER.trace("Mapping date '{}' to the apropriate month.", strDate);
    // Format the dates
    final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    // Get calendar instance
    final Calendar cal = Calendar.getInstance();
    // Intermediate date to convert the string into a calendar like object
    Date date;
    // Month value
    int month = -1;
    // Year value
    int year = -1;
    // Parse the date and retrieve month and day values
    try {
      date = sdf.parse(strDate);
      cal.setTime(date);
      month = cal.get(Calendar.MONTH);
      year = cal.get(Calendar.YEAR);
    } catch (ParseException ex) {
      LOGGER.error("Could not parse the given date: {}", ex);
    }
    // Return month and day as string objects
    return String.valueOf(year) + '-' + month;
  }
}