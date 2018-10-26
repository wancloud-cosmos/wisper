package io.wancloud.wisper;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

public class TextPlainMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

	public TextPlainMappingJackson2HttpMessageConverter() {
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
		setSupportedMediaTypes(mediaTypes);
    }

}