package com.zeus.info.config

import com.zeus.info.config.AppConfig._
import com.zeus.info.task.ConsulRegistration
import javax.inject._
import play.api.Logging
import play.api.inject.ApplicationLifecycle

// This creates an `ApplicationStart` object once at start-up and registers hook for shut-down.
@Singleton
class ApplicationLife @Inject() (lifecycle: ApplicationLifecycle) extends Logging {

  ConsulRegistration(consulHosts, serverInfo).recover {
    case th =>
      logger.error("Unable to register server in Consul", th)
      th.printStackTrace()
  }.get
}