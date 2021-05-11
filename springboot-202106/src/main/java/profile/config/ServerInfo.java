package profile.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by Choen-hee Park
 * User : chpark
 * Date : 2021/05/11
 * Time : 11:13 AM
 */

@Component
@ConfigurationProperties(value = "server")
public class ServerInfo {
    private String addr;

    private Integer port;

    protected ServerInfo() { }

    public ServerInfo(String addr, Integer port) {
        this.addr = addr;
        this.port = port;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
       StringBuilder sb = new StringBuilder();
       sb.append("[ServerInfo] addr:<").append(addr).append(">, port:<").append(port).append(">");
       return sb.toString();
    }
}
