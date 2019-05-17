import java.time.Clock

import com.google.inject.AbstractModule
import com.zeus.info.config.{AppConfig, ApplicationLife}

class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
    bind(classOf[ApplicationLife]).asEagerSingleton()
  }

}