package org.apacheextras.camel.examples.rcode;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.spi.AggregationRepository;

/**
 * Takes the bodies and concatenate them comma separated or by whatever separator is supplied.
 *
 * @author Sebastian Rühl
 */
public class ConcatenateAggregationStrategy implements AggregationStrategy {

  // Determines the separation between the bodies.
  private String separator = ", ";

  /**
   * Generates a a strategy using the default separator ", ".
   */
  public ConcatenateAggregationStrategy() {
  }

  /**
   * Generates a a strategy using the supplied separator.
   */
  public ConcatenateAggregationStrategy(String separator) {
    this.separator = separator;
  }

  /**
   * Generates a new body by appending the new {@link Exchange}s body with the {@link ConcatenateAggregationStrategy#separator}.
   *
   * @param oldExchange the old {@link Exchange}.
   * @param newExchange the new {@link Exchange}.
   * @return the aggregated {@link Exchange}.
   */
  @Override
  public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
    if (oldExchange == null) {
      return newExchange;
    }

    String oldBody = oldExchange.getIn().getBody(String.class);
    String newBody = newExchange.getIn().getBody(String.class);
    oldExchange.getIn().setBody(oldBody + separator + newBody);
    return oldExchange;
  }
}
