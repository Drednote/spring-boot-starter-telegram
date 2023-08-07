package io.github.drednote.telegram.datasource;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("drednote.telegram-bot.datasource")
@Getter
@Setter
public class DataSourceProperties {

  /**
   * By default, all tables for jpa datasource are generating with hibernate (if it exists on
   * classpath). If you don't want to generate them, set this parameter to true.
   *
   * @apiNote <b>WARNING</b> If you set this to true, be aware to create tables manually for needed
   * entities
   * @see DataSourceAutoConfiguration
   */
  private boolean disableAutoGenerateTables = false;
  /**
   * Disable all datasource configuration. {@link DataSourceAdapter} bean will not create. The
   * application will work like there is no datasource
   *
   * @see DataSourceAutoConfiguration
   */
  private boolean disableDataSourceAutoConfiguration = false;
}
