package modules

import com.google.inject.{Provides, AbstractModule}
import modules.ConfigurationModule.ApplicationConfiguration
import play.api.{Configuration, Environment}

class ConfigurationModule(environment: Environment, rawConfig: Configuration) extends AbstractModule {

  override def configure(): Unit = {}

  @Provides
  def providesApplicationConfiguration(): ApplicationConfiguration = new ApplicationConfiguration(rawConfig.underlying)

}

object ConfigurationModule {
  class ApplicationConfiguration(typesafeConfig: com.typesafe.config.Config) {

  }
}