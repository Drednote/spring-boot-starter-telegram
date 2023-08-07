package io.github.drednote.telegram.filter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("drednote.telegram-bot.filters")
@Getter
@Setter
public class FilterProperties {


}
