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
package org.apache.beam.examples;

import org.apache.beam.examples.common.ExampleBigQueryTableOptions;
import org.apache.beam.examples.common.ExampleOptions;
import org.apache.beam.examples.common.WriteOneFilePerWindow;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.beam.sdk.io.solace.*;
import java.util.Arrays;
import java.util.List;

/**
 * An example that counts words in text, and can run over either unbounded or bounded input
 * collections.
 *
 * <p>This class, {@link WindowedWordCount}, is the last in a series of four successively more
 * detailed 'word count' examples. First take a look at {@link MinimalWordCount}, {@link WordCount},
 * and {@link DebuggingWordCount}.
 *
 * <p>Basic concepts, also in the MinimalWordCount, WordCount, and DebuggingWordCount examples:
 * Reading text files; counting a PCollection; writing to GCS; executing a Pipeline both locally and
 * using a selected runner; defining DoFns; user-defined PTransforms; defining PipelineOptions.
 *
 * <p>New Concepts:
 *
 * <pre>
 *   1. Unbounded and bounded pipeline input modes
 *   2. Adding timestamps to data
 *   3. Windowing
 *   4. Re-using PTransforms over windowed PCollections
 *   5. Accessing the window of an element
 *   6. Writing data to per-window text files
 * </pre>
 *
 * <p>By default, the examples will run with the {@code DirectRunner}. To change the runner,
 * specify:
 *
 * <pre>{@code
 * --runner=YOUR_SELECTED_RUNNER
 * }</pre>
 *
 * See examples/java/README.md for instructions about how to configure different runners.
 *
 * <p>To execute this pipeline locally, specify a local output file (if using the {@code
 * DirectRunner}) or output prefix on a supported distributed file system.
 *
 * <pre>{@code
 * --output=[YOUR_LOCAL_FILE | YOUR_OUTPUT_PREFIX]
 * }</pre>
 *
 * <p>The input file defaults to a public data set containing the text of of King Lear, by William
 * Shakespeare. You can override it and choose your own input with {@code --inputFile}.
 *
 * <p>By default, the pipeline will do fixed windowing, on 10-minute windows. You can change this
 * interval by setting the {@code --windowSize} parameter, e.g. {@code --windowSize=15} for
 * 15-minute windows.
 *
 * <p>The example will try to cancel the pipeline on the signal to terminate the process (CTRL-C).
 */
public class WindowedWordCountSolace {
  private static final Logger LOG = LoggerFactory.getLogger(WindowedWordCountSolace.class);

    public interface Options
            extends WordCount.WordCountOptions, ExampleOptions, ExampleBigQueryTableOptions {
              @Description("IP and port of the client appliance. (e.g. -cip=192.168.160.101)")
              String getCip();
              void setCip(String value);
          
              @Description("Client username and optionally VPN name.")
              String getCu();
              void setCu(String value);

              @Description("Client password (default '')")
              @Default.String("")
              String getCp();
              void setCp(String value);

              @Description("List of queues for subscribing")
              String getSql();
              void setSql(String value);

              @Description("Enable auto ack for all GD msgs. (default **client** ack)")
              @Default.Boolean(false)
              boolean getAuto();
              void setAuto(boolean value);

              @Description("The timeout in milliseconds while try to receive a messages from Solace broker")
              @Default.Integer(100)
              int getTimeout();
              void setTimeout(int timeoutInMillis);;
            }

  static void runWindowedWordCount(Options options) throws Exception {

    List<String> queues =Arrays.asList(options.getSql().split(","));

    Pipeline pipeline = Pipeline.create(options);

    /*
     * Concept #1: the Beam SDK lets us run the same pipeline with either a bounded or
     * unbounded input source.
     */
    PCollection<String> input =
        pipeline
            /* Read from the Solace JMS Server. */
            .apply(SolaceIO.readAsString()
              .withConnectionConfiguration(SolaceIO.ConnectionConfiguration.create(
                  options.getCip(), queues)
              .withUsername(options.getCu())
              .withPassword(options.getCp())
              .withAutoAck(options.getAuto())
              .withTimeout(options.getTimeout()))
          );

    /*
     * Concept #3: Window into fixed windows. The fixed window size for this example defaults to 1
     * minute (you can change this with a command-line option). See the documentation for more
     * information on how fixed windows work, and for information on the other types of windowing
     * available (e.g., sliding windows).
     */
    PCollection<String> windowedWords =
        input.apply(Window.<String>into(FixedWindows.of(Duration.standardSeconds(10))));

    /*
     * Concept #4: Re-use our existing CountWords transform that does not have knowledge of
     * windows over a PCollection containing windowed values.
     */
    PCollection<KV<String, Long>> wordCounts = windowedWords.apply(new WordCount.CountWords());

    /*
     * Concept #5: Format the results and write to a sharded file partitioned by window, using a
     * simple ParDo operation. Because there may be failures followed by retries, the
     * writes must be idempotent, but the details of writing to files is elided here.
     */
      final String output = options.getOutput();
      wordCounts
        .apply(MapElements.via(new WordCount.FormatAsTextFn()))
        .apply(new WriteOneFilePerWindow(output, 1));

    PipelineResult result = pipeline.run();
    try {
      result.waitUntilFinish();
    } catch (Exception exc) {
      result.cancel();
    }
  }

  public static void main(String[] args) throws Exception {
//    PipelineOptions options = PipelineOptionsFactory.fromArgs(args).create();
      Options options = PipelineOptionsFactory.fromArgs(args).withValidation().as(Options.class);

    try {
      runWindowedWordCount(options);
    } catch (Exception e){
      e.printStackTrace();
    }
  }
}
