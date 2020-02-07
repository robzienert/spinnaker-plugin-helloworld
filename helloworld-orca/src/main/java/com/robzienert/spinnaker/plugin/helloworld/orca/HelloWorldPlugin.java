/*
 * Copyright 2019 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.robzienert.spinnaker.plugin.helloworld.orca;

import com.netflix.spinnaker.kork.plugins.api.ConfigurableExtension;
import com.netflix.spinnaker.kork.plugins.api.SpinnakerExtension;
import com.netflix.spinnaker.orca.api.SimpleStage;
import com.netflix.spinnaker.orca.api.SimpleStageInput;
import com.netflix.spinnaker.orca.api.SimpleStageOutput;
import com.netflix.spinnaker.orca.api.SimpleStageStatus;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.util.Collections;
import java.util.Optional;
import java.util.Random;

import static java.lang.String.format;

/**
 * Wraps the hello world extension into a plugin so it is loaded into its own class loader!
 */
public class HelloWorldPlugin extends Plugin {
  /**
   * Constructor to be used by plugin manager for plugin instantiation.
   * Your plugins have to provide constructor with this exact signature to
   * be successfully loaded by manager.
   *
   * @param wrapper
   */
  public HelloWorldPlugin(PluginWrapper wrapper) {
    super(wrapper);
  }

  /**
   * It says hello.
   */
  @Extension
  @SpinnakerExtension(id = "com.robzienert.helloworld")
  public static class HelloWorldStage implements
    SimpleStage<HelloWorldStage.Input>,
    ConfigurableExtension<HelloWorldStageConfigProperties> {

    private HelloWorldStageConfigProperties properties;

    @Override public SimpleStageOutput execute(
      SimpleStageInput<HelloWorldStage.Input> simpleStageInput) {
      HelloWorldStage.Output output = new HelloWorldStage.Output();
      output.setOutput(new HelloWorldStage.HelloWorldMessage(
        format("%s, %s!",
          properties.greetings.get(new Random().nextInt(properties.greetings.size())),
          Optional.ofNullable(simpleStageInput.getValue().recipient).orElse(properties.defaultRecipient)
        )
      ));
      output.setContext(Collections.emptyMap());
      output.setStatus(SimpleStageStatus.SUCCEEDED);
      return output;
    }

    @Override public String getName() {
      return "helloWorld";
    }

    @Override public void setConfiguration(HelloWorldStageConfigProperties configuration) {
      properties = configuration;
    }

    public static class Input {
      public String recipient;
    }

    public static class Output extends SimpleStageOutput<HelloWorldStage.HelloWorldMessage, Object> {
    }

    public static class HelloWorldMessage {
      public String message;

      public HelloWorldMessage(String message) {
        this.message = message;
      }
    }
  }
}
