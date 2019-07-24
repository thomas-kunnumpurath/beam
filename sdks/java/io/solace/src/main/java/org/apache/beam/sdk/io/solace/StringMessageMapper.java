package org.apache.beam.sdk.io.solace;

import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class StringMessageMapper implements SolaceIO.MessageMapper<String> {
  private final Logger LOG = LoggerFactory.getLogger(StringMessageMapper.class);
  private static final long serialVersionUID = 42L;

  @Override
  public String mapMessage(BytesXMLMessage message) throws Exception {
    return ((TextMessage)message).getText();
  }
}
