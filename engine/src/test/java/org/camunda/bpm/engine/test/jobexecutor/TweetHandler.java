/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.engine.test.jobexecutor;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.jobexecutor.JobHandler;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.junit.Assert;

public class TweetHandler implements JobHandler {

  List<String> messages = new ArrayList<String>();

  public String getType() {
    return "tweet";
  }

  public void execute(String configuration, ExecutionEntity execution, CommandContext commandContext) {
    messages.add(configuration);
    Assert.assertNotNull(commandContext);
  }
  
  public List<String> getMessages() {
    return messages;
  }
}