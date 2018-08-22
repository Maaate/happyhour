import com.google.inject.AbstractModule
import services.{BaseFireBaseService, MockFireBackService, RealFireBaseService}
import play.api.{Configuration, Environment}

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.

 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module(environment: Environment,
             configuration: Configuration) extends AbstractModule {

  override def configure() = {
    val envConfig: Configuration = configuration.getOptional[Configuration]("variables").getOrElse(Configuration.empty)

    if (envConfig.get[String]("env").equals("local")) {
      bind(classOf[BaseFireBaseService]).to(classOf[MockFireBackService])
    } else {
      bind(classOf[BaseFireBaseService]).to(classOf[RealFireBaseService])
    }
  }

}
