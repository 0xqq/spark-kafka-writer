/**
 * Copyright (c) 2016-2016, Benjamin Fradet, and other contributors.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.github.benfradet.spark.kafka.writer

import java.util.Properties

import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.DStream

import scala.reflect.ClassTag

object KafkaWriter {
  implicit def dStreamToKafkaWriter[T: ClassTag, K, V](dStream: DStream[T]): KafkaWriter[T] =
    new DStreamKafkaWriter[T](dStream)
  implicit def rddToKafkaWriter[T: ClassTag, K, V](rdd: RDD[T]): KafkaWriter[T] =
    new RDDKafkaWriter[T](rdd)
}

abstract class KafkaWriter[T: ClassTag] extends Serializable {
  def writeToKafka[K, V](
    producerConfig: Properties,
    transformFunc: T => ProducerRecord[K, V]
  ): Unit
}
