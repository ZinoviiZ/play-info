package com.zeus.info.task

import akka.http.scaladsl.model.Uri
import com.google.common.net.HostAndPort
import com.orbitz.consul.Consul
import com.typesafe.config.ConfigFactory
import com.zeus.info.config.ServerInfo
import com.zeus.info.task.ConsulRegistration.{CONSUL_SERVER_IP_KEY, blackListTime}

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Try}

case class ConsulRegistration(nodes: List[Uri]) {

  val consulNodes = nodes.map(_.authority).map(url => HostAndPort.fromParts(url.host.toString(), url.port))

  def register(serverInfo: ServerInfo): Try[Unit] = Try {
    val consulClient = Consul.builder().withMultipleHostAndPort(consulNodes.asJava, blackListTime.toMillis).build()
    val agentClient = consulClient.agentClient()

    agentClient.register(
      serverInfo.port,
      serverInfo.healthUrl,
      serverInfo.healthInterval.toSeconds,
      serverInfo.name, serverInfo.id,
      List.empty[String].asJava,
      Map(CONSUL_SERVER_IP_KEY -> serverInfo.host).asJava
    )

    println("done")
  }
}

object ConsulRegistration {

  private final val CONSUL_SERVER_IP_KEY = "server_ip"
  private final val CONSUL_CONFIG_PATH = "consul.hosts"
  private final val blackListTime = 5 seconds
  private val config = ConfigFactory.load()

  def apply(consulHosts: List[String], serverInfo: ServerInfo): Try[Unit] = Try {

    if(consulHosts.size >= 2 && serverInfo.port != 0) {
      ConsulRegistration(consulHosts.map(Uri(_))).register(serverInfo)
    } else {
      Failure(new RuntimeException("Consul hosts is not setup or setup incorrect"))
    }
  }
}
