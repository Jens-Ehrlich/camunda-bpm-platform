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
package org.camunda.bpm.engine.test.variables;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.impl.core.variable.type.FileValueTypeImpl;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.FileValue;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Ronny Bräunlich
 *
 */
public class FileValueTypeImplTest {

  private FileValueTypeImpl type;

  @Before
  public void setUp() {
    type = new FileValueTypeImpl();
  }

  @Test
  public void nameShouldBeFile() {
    assertThat(type.getName(), is("file"));
  }

  @Test
  public void shouldNotHaveParent() {
    assertThat(type.getParent(), is(nullValue()));
  }

  @Test
  public void isPrimitiveValue() {
    assertThat(type.isPrimitiveValueType(), is(true));
  }

  @Test
  public void isNotAnAbstractType() {
    assertThat(type.isAbstract(), is(false));
  }

  @Test
  public void canNotConvertFromAnyValue() {
    // we just use null to make sure false is always returned
    assertThat(type.canConvertFromTypedValue(null), is(false));
  }

  @Test(expected = ProcessEngineException.class)
  public void convertingThrowsException() {
    type.convertFromTypedValue(Variables.untypedNullValue());
  }

  @Test
  public void createValueFromFile() throws URISyntaxException {
    File file = new File(this.getClass().getClassLoader().getResource("org/camunda/bpm/engine/test/variables/simpleFile.txt").toURI());
    TypedValue value = type.createValue(file, Collections.<String, Object> singletonMap(FileValueTypeImpl.VALUE_INFO_FILE_NAME, "simpleFile.txt"));
    assertThat(value, is(instanceOf(FileValue.class)));
    assertThat(value.getType(), is(instanceOf(FileValueTypeImpl.class)));
    checkStreamFromValue(value, "text");
  }

  @Test
  public void createValueFromStream() {
    InputStream file = this.getClass().getClassLoader().getResourceAsStream("org/camunda/bpm/engine/test/variables/simpleFile.txt");
    TypedValue value = type.createValue(file, Collections.<String, Object> singletonMap(FileValueTypeImpl.VALUE_INFO_FILE_NAME, "simpleFile.txt"));
    assertThat(value, is(instanceOf(FileValue.class)));
    assertThat(value.getType(), is(instanceOf(FileValueTypeImpl.class)));
    checkStreamFromValue(value, "text");
  }

  @Test
  public void createValueFromBytes() throws IOException, URISyntaxException {
    File file = new File(this.getClass().getClassLoader().getResource("org/camunda/bpm/engine/test/variables/simpleFile.txt").toURI());
    TypedValue value = type.createValue(file, Collections.<String, Object> singletonMap(FileValueTypeImpl.VALUE_INFO_FILE_NAME, "simpleFile.txt"));
    assertThat(value, is(instanceOf(FileValue.class)));
    assertThat(value.getType(), is(instanceOf(FileValueTypeImpl.class)));
    checkStreamFromValue(value, "text");
  }

  @Test(expected = IllegalArgumentException.class)
  public void cannotCreateFileWithoutName() {
    InputStream file = this.getClass().getClassLoader().getResourceAsStream("org/camunda/bpm/engine/test/variables/simpleFile.txt");
    type.createValue(file, Collections.<String, Object> emptyMap());
  }

  @Test
  public void valueInfoContainsFileTypeNameAndCharsetEncoding() {
    InputStream file = this.getClass().getClassLoader().getResourceAsStream("org/camunda/bpm/engine/test/variables/simpleFile.txt");
    String fileName = "simpleFile.txt";
    String fileType = "text/plain";
    Charset encoding = Charset.forName("UTF-8");
    FileValue fileValue = Variables.fileValue(fileName).file(file).mimeType(fileType).encoding(encoding).create();
    Map<String, Object> info = type.getValueInfo(fileValue);

    assertThat(info, hasEntry(FileValueTypeImpl.VALUE_INFO_FILE_NAME, (Object) fileName));
    assertThat(info, hasEntry(FileValueTypeImpl.VALUE_INFO_FILE_MIME_TYPE, (Object) fileType));
    assertThat(info, hasEntry(FileValueTypeImpl.VALUE_INFO_FILE_ENCODING, (Object) encoding.name()));
  }

  @Test
  public void valueInfoContainsFileTypeNameAndStringEncoding() {
    InputStream file = this.getClass().getClassLoader().getResourceAsStream("org/camunda/bpm/engine/test/variables/simpleFile.txt");
    String fileName = "simpleFile.txt";
    String fileType = "text/plain";
    String encoding = "UTF-8";
    FileValue fileValue = Variables.fileValue(fileName).file(file).mimeType(fileType).encoding(encoding).create();
    Map<String, Object> info = type.getValueInfo(fileValue);

    assertThat(info, hasEntry(FileValueTypeImpl.VALUE_INFO_FILE_NAME, (Object) fileName));
    assertThat(info, hasEntry(FileValueTypeImpl.VALUE_INFO_FILE_MIME_TYPE, (Object) fileType));
    assertThat(info, hasEntry(FileValueTypeImpl.VALUE_INFO_FILE_ENCODING, (Object) encoding));
  }

  @Test
  public void doesNotHaveParent(){
    assertThat(type.getParent(), is(nullValue()));
  }

  private void checkStreamFromValue(TypedValue value, String expected) {
    InputStream stream = (InputStream) value.getValue();
    Scanner scanner = new Scanner(stream);
    assertThat(scanner.nextLine(), is(expected));
  }
}
