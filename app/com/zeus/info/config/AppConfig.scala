package com.zeus.info.config

import java.net.URL
import java.util.UUID

import com.typesafe.config.{Config, ConfigFactory, ConfigObject}
import play.api.Logging

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Try
import scala.util.control.NonFatal

object AppConfig extends Logging {

  private val config = ConfigFactory.load()

  private def getBoolean(path: String, config: Config, defaultValue: Boolean): Boolean =
    Try(config.getBoolean(path)).getOrElse {
      Try(config.getString(path).toBoolean).getOrElse {
        logger.warn(s"Unspecified value for property: $path. Defaulting to: '$defaultValue'")
        defaultValue
      }
    }

  private def getString(path: String, defaultValue: String): String =
    Try(config.getString(path)).getOrElse {
      logger.warn(s"Unspecified value for property: $path. Defaulting to: '$defaultValue'")
      defaultValue
    }

  private def getInt(path: String, defaultValue: Int): Int =
    Try(config.getInt(path)).getOrElse {
      logger.warn(s"Unspecified value for property: $path. Defaulting to: '$defaultValue'")
      defaultValue
    }

  private def getBoolean(path: String, defaultValue: Boolean): Boolean =
    Try(config.getBoolean(path)).getOrElse {
      logger.warn(s"Unspecified value for property: $path. Defaulting to: '$defaultValue'")
      defaultValue
    }

  private def getConfigObject(path: String): ConfigObject =
    Try(config.getObject(path)).getOrElse {
      throw new RuntimeException(s"Invalid path to LMS common-ui settings config: $path")
    }

  private def getList(path: String): Option[Seq[Any]] = {
    try {
      Some(config.getAnyRef(path).asInstanceOf[java.util.List[Any]].asScala.toList)
    } catch {
      case NonFatal(e) => logger.warn(s"Unspecified value for property: $path.")
        None
    }
  }

  private def getStringList(path: String, defaultValue: List[String], verbose: Boolean = true): List[String] =
    Try(config.getStringList(path).asScala.toList).getOrElse {
      if (verbose) {
        logger.warn(s"Unspecified value for property: $path. Defaulting to: '$defaultValue'")
      }
      defaultValue
    }

  lazy val consulHosts = getStringList("consul.hosts", List.empty)
  lazy val serverName = getString("play.server.http.name", "some_server_name")
  lazy val serverIdPrefix = getString("play.server.http.id_prefix", "some_server_name")
  lazy val serverHost = getString("play.server.http.node_ip", "some_server_host")
  lazy val serverPort = getInt("play.server.http.port", 0)
  lazy val serverInfo = ServerInfo(s"$serverIdPrefix-$serverName", serverName, serverHost, serverPort)
}

case class ServerInfo(id: String, name: String, host: String, port: Int) {
  val healthUrl = new URL(s"http://$host:$port/health")
  val healthInterval = 2 seconds
}
