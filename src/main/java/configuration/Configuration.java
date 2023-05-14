package configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class Configuration {
    private final String configFile;
    private String type;
    private int historySize;
    private int timeout;
    public Configuration(String _configFile) throws IOException {
        configFile = _configFile;
        readConfig();
    }
    private void readConfig() throws IOException {
        InputStream input = Configuration.class.getClassLoader().getResourceAsStream(configFile);
        Properties properties = new Properties();
        properties.load(input);

        type = properties.getProperty("type");
        historySize = Integer.parseInt(properties.getProperty("history_size"));
        timeout = Integer.parseInt(properties.getProperty("timeout"));
    }
    public String getType() { return type; }
    public int getHistorySize() { return historySize; }
    public int getTimeout() { return timeout; }
}