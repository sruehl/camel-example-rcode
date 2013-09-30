/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apacheextras.camel.examples.rcode.converter;

import org.apache.camel.Converter;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author cemmersb
 */
@Converter
public class StringConverter {
  
  @Converter
  public String[] convertToJasonStringArray(String string) {
    String[] stringArray = StringUtils.substringsBetween(string, "{\"date\":{\"", "\"},");
    for (int i = 0; i < stringArray.length; i++) {
      stringArray[i] = "{\"date\":{\"" + stringArray[i]+ "\"}";
    }
    return stringArray;
  }
  
}
