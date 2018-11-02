package game.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import game.request.parameters.PlayerParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author Manish Shrestha
 */
@Component
public class StringToPlayerParamsListConverter implements Converter<String, List<PlayerParams>> {
    private static Logger log = LoggerFactory.getLogger(StringToPlayerParamsListConverter.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<PlayerParams> convert(String s) {

        try {
            return objectMapper.readValue(s, new TypeReference<List<PlayerParams>>() {});
        } catch (IOException e) {
            log.error("error converting players parameter to list of playersparams", e);
            throw new RuntimeException(e);
        }
    }
}
