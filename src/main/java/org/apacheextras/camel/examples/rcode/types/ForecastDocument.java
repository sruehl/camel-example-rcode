/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apacheextras.camel.examples.rcode.types;

import lombok.Data;

import java.util.Date;

/**
 *
 * @author cemmersb
 */
@Data
public class ForecastDocument {
  private final byte[] jpegGraph;
  private String title;
  private String path;
  private Date date;
}
