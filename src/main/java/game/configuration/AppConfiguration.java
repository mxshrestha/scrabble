package game.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Manish Shrestha
 */
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfiguration {

    private List<String> dictionaries = new ArrayList<>();

    private List<String> files = new ArrayList<>();

    public List<String> getDictionaries() {
        return dictionaries;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setDictionaries(List<String> dictionaries) {
        this.dictionaries = dictionaries;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}
