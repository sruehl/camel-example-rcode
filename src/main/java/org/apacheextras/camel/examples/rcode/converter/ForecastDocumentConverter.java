/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apacheextras.camel.examples.rcode.converter;

import org.apache.camel.Converter;
import org.apacheextras.camel.examples.rcode.types.ForecastDocument;

/**
 *
 * @author cemmersb
 */
@Converter
public class ForecastDocumentConverter {
  
  @Converter
  public ForecastDocument convertByteArrayToForecastDocument(byte[] jpegDate) {
    return new ForecastDocument(jpegDate);
  }
  
}
