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

import org.apache.beam.sdk.extensions.sql.impl.schema.BaseBeamTable;
import org.apache.beam.sdk.io.solace.SolaceIO;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.POutput;
import org.apache.beam.sdk.values.Row;

import java.util.List;
import java.util.Map;

/**
 * {@code BeamKafkaTable} represent a Kafka topic, as source or target. Need to extend to convert
 * between {@code BeamSqlRow} and {@code KV<byte[], byte[]>}.
 */
public abstract class BeamSolaceTable extends BaseBeamTable {
    private List<String> queues;
    private String cIp;
    private String cu;
    private String cp;
    private String vpn;
    private boolean autoAck;
    private int timeout;

    private Map<String, Object> configUpdates;

    protected BeamSolaceTable(Schema beamSchema) {
        super(beamSchema);
    }

    public BeamSolaceTable(
            Schema beamSchema,
            String cIp,
            List<String> queues,
            String vpn,
            String cu,
            String cp,
            boolean autoAck,
            int timeout) {
        super(beamSchema);
        this.cIp = cIp;
        this.vpn = vpn;
        this.queues = queues;
        this.cu = cu;
        this.cp = cp;
        this.autoAck = autoAck;
        this.timeout = timeout;
    }

    @Override
    public PCollection.IsBounded isBounded() {
        return PCollection.IsBounded.UNBOUNDED;
    }

    public abstract PTransform<PCollection<String>, PCollection<Row>> getPTransformForInput();

    @Override
    public PCollection<Row> buildIOReader(PBegin begin) {

        return begin
                .apply(SolaceIO.readAsString()
                        .withConnectionConfiguration(
                                SolaceIO.ConnectionConfiguration.create(this.cIp, this.queues)
                                        .withVpn(this.vpn)
                                        .withUsername(this.cu)
                                        .withPassword(this.cp)))
                .apply("in_format", getPTransformForInput())
                .setRowSchema(getSchema())
                ;
    }

    @Override
    public POutput buildIOWriter(PCollection<Row> input) {
        return null;
    }
}
