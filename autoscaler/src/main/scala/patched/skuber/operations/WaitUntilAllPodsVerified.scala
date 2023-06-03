package patched.skuber.operations

import cats.effect.{IO, Temporal}
import skuber.Pod.Phase
import skuber.PodList
import skuber.api.client.KubernetesClient
import skuber.apps.v1.{Deployment, ReplicaSet, StatefulSet}

import scala.concurrent.TimeoutException
import scala.concurrent.duration.FiniteDuration
import concurrent.duration.DurationInt
import cats.implicits.*
import skuber.json.format.*

def waitUntilAllPodsVerified(statefulSets: List[StatefulSet],
                             deployments: List[Deployment],
                             replicaSets: List[ReplicaSet],
                             pollInterval: FiniteDuration = 1.seconds,
                             timeout: FiniteDuration = 5.minutes,
                             checkRunning: Boolean = true
                            )(implicit k8s: KubernetesClient, temporal: Temporal[IO]): IO[Unit] = {

  val deadline = temporal.realTime.map(_ + timeout)

  println(s"Waiting for pods to be running. Deployments: ${deployments.length}, StatefulSets: ${statefulSets.length}, ReplicaSets: ${replicaSets.length}")

  def checkPods(): IO[Boolean] = {
    IO.fromFuture(IO {
      k8s.list[PodList]()
    }).map { podList =>
      val allResourceNames = statefulSets.map(_.metadata.name) ++
        deployments.map(_.metadata.name) ++
        replicaSets.map(_.metadata.name)

      val relevantPods = podList.items.filter(pod =>
        allResourceNames.contains(pod.metadata.labels("app"))
      )

      val expectedPodCount = statefulSets.map(_.spec.map(_.replicas.getOrElse(0)).getOrElse(0)).sum +
        deployments.map(_.spec.map(_.replicas.getOrElse(0)).getOrElse(0)).sum +
        replicaSets.map(_.spec.map(_.replicas.getOrElse(0)).getOrElse(0)).sum

      println(s"Relevant pod count: ${relevantPods.length}, Expected pod count: $expectedPodCount")

      val correctPodCount = relevantPods.length == expectedPodCount
      if (!checkRunning) {
        correctPodCount
      } else {
        val allPodsRunning = relevantPods.forall(_.status.exists(_.phase.contains(Phase.Running)))

        correctPodCount && allPodsRunning
      }
    }
  }

  def poll(deadline: FiniteDuration): IO[Unit] = {
    checkPods().flatMap { allRunning =>
      if (allRunning) {
        IO.unit
      } else {
        temporal.realTime.flatMap { now =>
          if (now >= deadline) {
            IO.raiseError(new TimeoutException(s"Timed out waiting for all pods to be running after $timeout"))
          } else {
            temporal.sleep(pollInterval) *> poll(deadline)
          }
        }
      }
    }
  }

  deadline.flatMap(poll)
}