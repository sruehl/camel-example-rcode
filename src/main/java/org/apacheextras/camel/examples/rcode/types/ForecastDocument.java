/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apacheextras.camel.examples.rcode.types;

import java.util.Date;

/**
 *
 * @author cemmersb
 */
public class ForecastDocument {
  
  private String title = "";
  private String path = "";
  private Date date = null;
  private byte[] jpegGraph = null;
  
  /**
   * 
   */
  public ForecastDocument() {
  }
  
  /**
   * 
   */
  public ForecastDocument(byte[] jpegGraph) {
    this.jpegGraph = jpegGraph;
  }
  
  /**
   * 
   */
  public ForecastDocument(String title, String path, Date date, byte[] jpegGraph) {
    this.title = title;
    this.path = path;
    if(null != date) {
      this.date = date;
    } else {
      this.date = new Date();
    }
    this.jpegGraph = jpegGraph;
  }
  
  /**
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * @param title the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @return the path
   */
  public String getPath() {
    return path;
  }

  /**
   * @param path the path to set
   */
  public void setPath(String path) {
    this.path = path;
  }

  /**
   * @return the date
   */
  public Date getDate() {
    return date;
  }

  /**
   * @param date the date to set
   */
  public void setDate(Date date) {
    this.date = date;
  }

  /**
   * @return the jpegGraph
   */
  public byte[] getJpegGraph() {
    return jpegGraph;
  }

  /**
   * @param jpegGraph the jpegGraph to set
   */
  public void setJpegGraph(byte[] jpegGraph) {
    this.jpegGraph = jpegGraph;
  }
}
