/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.extensions.sql.meta.provider.solace;

import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.Row;
import org.apache.commons.csv.CSVFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.apache.beam.sdk.extensions.sql.impl.schema.BeamTableUtils.beamRow2CsvLine;
import static org.apache.beam.sdk.extensions.sql.impl.schema.BeamTableUtils.csvLines2BeamRows;

/** A Kafka topic that saves records as CSV format. */
public class BeamSolaceCSVTable extends BeamSolaceTable {
  private CSVFormat csvFormat;

  private static final Logger LOG = LoggerFactory.getLogger(BeamSolaceCSVTable.class);


  public BeamSolaceCSVTable(
      Schema beamSchema,
      String cIp,
      List<String> queues,
      String vpn,
      String cu,
      String cp,
      boolean autoAck,
      int timeout) {
    this(beamSchema, cIp, queues, vpn, cu, cp, autoAck, timeout, CSVFormat.DEFAULT);
  }

  public BeamSolaceCSVTable(
      Schema beamSchema,
      String cIp,
      List<String> queues,
      String vpn,
      String cu,
      String cp,
      boolean autoAck,
      int timeout,
      CSVFormat format) {
    super(beamSchema, cIp, queues, vpn, cu, cp, autoAck, timeout);
    this.csvFormat = format;
  }

  @Override
  public PTransform<PCollection<String>, PCollection<Row>> getPTransformForInput() {
    return new CsvRecorderDecoder(schema, csvFormat);
  }

  //    @Override
  public PTransform<PCollection<Row>, PCollection<String>> getPTransformForOutput() {
    return new CsvRecorderEncoder(schema, csvFormat);
  }

  /** A PTransform to convert {@code KV<byte[], byte[]>} to {@link Row}. */
  public static class CsvRecorderDecoder extends PTransform<PCollection<String>, PCollection<Row>> {
    private Schema schema;
    private CSVFormat format;

    public CsvRecorderDecoder(Schema schema, CSVFormat format) {
      this.schema = schema;
      this.format = format;
    }

    @Override
    public PCollection<Row> expand(PCollection<String> input) {
      return input
          .apply(
              "decodeRecord",
              ParDo.of(
                  new DoFn<String, Row>() {
                    @ProcessElement
                    public void processElement(ProcessContext c) {
                      String rowInString = c.element();
                      for (Row row : csvLines2BeamRows(format, rowInString, schema)) {
                        c.output(row);
                      }
                    }
                  }))
          .setRowSchema(schema);
    }
  }

  /** A PTransform to convert {@link Row} to {@code KV<byte[], byte[]>}. */
  public static class CsvRecorderEncoder extends PTransform<PCollection<Row>, PCollection<String>> {
    private Schema schema;
    private CSVFormat format;

    public CsvRecorderEncoder(Schema schema, CSVFormat format) {
      this.schema = schema;
      this.format = format;
    }

    @Override
    public PCollection<String> expand(PCollection<Row> input) {
      return input.apply(
          "encodeRecord",
          ParDo.of(
              new DoFn<Row, String>() {
                @ProcessElement
                public void processElement(ProcessContext c) {
                  Row in = c.element();
                  c.output(beamRow2CsvLine(in, format));
                }
              }));
    }
  }
}
