

package org.apache.beam.sdk.io.solace;

import javax.annotation.Nullable;
import java.util.List;

// Generated by com.google.auto.value.processor.AutoValueProcessor
final class AutoValue_SolaceIO_ConnectionConfiguration extends SolaceIO.ConnectionConfiguration {

  private final String host;

  private final List<String> queues;

  private final String clientName;

  private final String vpn;

  private final String username;

  private final String password;

  private final boolean autoAck;

  private final int timeoutInMillis;

  private AutoValue_SolaceIO_ConnectionConfiguration(
      @Nullable String host,
      @Nullable List<String> queues,
      @Nullable String clientName,
      @Nullable String vpn,
      @Nullable String username,
      @Nullable String password,
      boolean autoAck,
      int timeoutInMillis) {
    this.host = host;
    this.queues = queues;
    this.clientName = clientName;
    this.vpn = vpn;
    this.username = username;
    this.password = password;
    this.autoAck = autoAck;
    this.timeoutInMillis = timeoutInMillis;
  }

  @Nullable
  @Override
  String getHost() {
    return host;
  }

  @Nullable
  @Override
  List<String> getQueues() {
    return queues;
  }

  @Nullable
  @Override
  String getClientName() {
    return clientName;
  }

  @Nullable
  @Override
  String getVpn() {
    return vpn;
  }

  @Nullable
  @Override
  String getUsername() {
    return username;
  }

  @Nullable
  @Override
  String getPassword() {
    return password;
  }

  @Override
  boolean isAutoAck() {
    return autoAck;
  }

  @Override
  int getTimeoutInMillis() {
    return timeoutInMillis;
  }

  @Override
  public String toString() {
    return "ConnectionConfiguration{"
         + "host=" + host + ", "
         + "queues=" + queues + ", "
         + "clientName=" + clientName + ", "
         + "vpn=" + vpn + ", "
         + "username=" + username + ", "
         + "password=" + password + ", "
         + "autoAck=" + autoAck + ", "
         + "timeoutInMillis=" + timeoutInMillis
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof SolaceIO.ConnectionConfiguration) {
      SolaceIO.ConnectionConfiguration that = (SolaceIO.ConnectionConfiguration) o;
      return ((this.host == null) ? (that.getHost() == null) : this.host.equals(that.getHost()))
           && ((this.queues == null) ? (that.getQueues() == null) : this.queues.equals(that.getQueues()))
           && ((this.clientName == null) ? (that.getClientName() == null) : this.clientName.equals(that.getClientName()))
           && ((this.vpn == null) ? (that.getVpn() == null) : this.vpn.equals(that.getVpn()))
           && ((this.username == null) ? (that.getUsername() == null) : this.username.equals(that.getUsername()))
           && ((this.password == null) ? (that.getPassword() == null) : this.password.equals(that.getPassword()))
           && (this.autoAck == that.isAutoAck())
           && (this.timeoutInMillis == that.getTimeoutInMillis());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= (host == null) ? 0 : host.hashCode();
    h$ *= 1000003;
    h$ ^= (queues == null) ? 0 : queues.hashCode();
    h$ *= 1000003;
    h$ ^= (clientName == null) ? 0 : clientName.hashCode();
    h$ *= 1000003;
    h$ ^= (vpn == null) ? 0 : vpn.hashCode();
    h$ *= 1000003;
    h$ ^= (username == null) ? 0 : username.hashCode();
    h$ *= 1000003;
    h$ ^= (password == null) ? 0 : password.hashCode();
    h$ *= 1000003;
    h$ ^= autoAck ? 1231 : 1237;
    h$ *= 1000003;
    h$ ^= timeoutInMillis;
    return h$;
  }

  private static final long serialVersionUID = 42L;

  @Override
  SolaceIO.ConnectionConfiguration.Builder builder() {
    return new Builder(this);
  }

  static final class Builder extends SolaceIO.ConnectionConfiguration.Builder {
    private String host;
    private List<String> queues;
    private String clientName;
    private String vpn;
    private String username;
    private String password;
    private Boolean autoAck;
    private Integer timeoutInMillis;
    Builder() {
    }
    private Builder(SolaceIO.ConnectionConfiguration source) {
      this.host = source.getHost();
      this.queues = source.getQueues();
      this.clientName = source.getClientName();
      this.vpn = source.getVpn();
      this.username = source.getUsername();
      this.password = source.getPassword();
      this.autoAck = source.isAutoAck();
      this.timeoutInMillis = source.getTimeoutInMillis();
    }
    @Override
    SolaceIO.ConnectionConfiguration.Builder setHost(String host) {
      this.host = host;
      return this;
    }
    @Override
    SolaceIO.ConnectionConfiguration.Builder setQueues(List<String> queues) {
      this.queues = queues;
      return this;
    }
    @Override
    SolaceIO.ConnectionConfiguration.Builder setClientName(String clientName) {
      this.clientName = clientName;
      return this;
    }
    @Override
    SolaceIO.ConnectionConfiguration.Builder setVpn(String vpn) {
      this.vpn = vpn;
      return this;
    }
    @Override
    SolaceIO.ConnectionConfiguration.Builder setUsername(String username) {
      this.username = username;
      return this;
    }
    @Override
    SolaceIO.ConnectionConfiguration.Builder setPassword(String password) {
      this.password = password;
      return this;
    }
    @Override
    SolaceIO.ConnectionConfiguration.Builder setAutoAck(boolean autoAck) {
      this.autoAck = autoAck;
      return this;
    }
    @Override
    SolaceIO.ConnectionConfiguration.Builder setTimeoutInMillis(int timeoutInMillis) {
      this.timeoutInMillis = timeoutInMillis;
      return this;
    }
    @Override
    SolaceIO.ConnectionConfiguration build() {
      String missing = "";
      if (this.autoAck == null) {
        missing += " autoAck";
      }
      if (this.timeoutInMillis == null) {
        missing += " timeoutInMillis";
      }
      if (!missing.isEmpty()) {
        throw new IllegalStateException("Missing required properties:" + missing);
      }
      return new AutoValue_SolaceIO_ConnectionConfiguration(
          this.host,
          this.queues,
          this.clientName,
          this.vpn,
          this.username,
          this.password,
          this.autoAck,
          this.timeoutInMillis);
    }
  }

}
