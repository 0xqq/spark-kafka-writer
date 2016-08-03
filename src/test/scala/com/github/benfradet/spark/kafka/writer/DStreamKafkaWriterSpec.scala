package com.github.benfradet.spark.kafka.writer

import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.DStream

import scala.collection.mutable
import scala.concurrent.duration._

class DStreamKafkaWriterSpec extends SKRSpec {

  "a DStreamKafkaWriter" when {
    "given a dstream" should {
      "write it to kafka" in {
        val localTopic = topic
        val msgs = (1 to 10).map(_.toString)
        val stream = createDStream(msgs)
        import KafkaWriter._
        stream.writeToKafka(
          producerConfig,
          s => new ProducerRecord[String, String](localTopic, s)
        )

        val results = collect(ssc, localTopic)

        ssc.start()
        eventually(timeout(30.seconds), interval(1.second)) {
          results shouldBe msgs
        }
      }
    }
  }

  private def createDStream(seq: Seq[String]): DStream[String] = {
    val q = mutable.Queue.empty[RDD[String]]
    q.enqueue(ssc.sparkContext.makeRDD(seq))
    ssc.queueStream(q)
  }
}
