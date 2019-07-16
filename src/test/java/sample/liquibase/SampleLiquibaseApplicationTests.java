/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.liquibase;

import java.net.ConnectException;

import org.junit.Rule;
import org.junit.Test;

import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.core.NestedCheckedException;

import static org.assertj.core.api.Assertions.assertThat;

public class SampleLiquibaseApplicationTests {

    @Rule
    public OutputCapture outputCapture = new OutputCapture();

    @Test
    public void testDefaultSettings() {
        try {
            SampleLiquibaseApplication.main(new String[]
                    {
                            "--server.port=0",
                            "--spring.datasource.driver-class-name=org.h2.Driver",
                            "--spring.datasource.url=jdbc:h2:mem:test;",
                            "--spring.datasource.username=sa",
                            "--spring.datasource.password= "
                    });
        } catch (IllegalStateException ex) {
            if (serverNotRunning(ex)) {
                return;
            }
        }
        String output = this.outputCapture.toString();
        assertThat(output).contains("CREATE TABLE PUBLIC.DATABASECHANGELOGLOCK")
                .contains("Successfully acquired change log lock")
                .contains("Creating database history table with name: PUBLIC.DATABASECHANGELOG")
                .contains("Table person created")
                .contains("ChangeSet db/changelog/changes/create-person-table-changelog-1.xml::1::ft ran successfully")
                .contains("New row inserted into person")
                .contains("ChangeSet db/changelog/changes/insert-person-table-changelog-2.xml::2::ft ran successfully")
                .contains("Data updated in person")
                .contains("ChangeSet db/changelog/changes/update-person-table-precondition-3.xml::3::ft ran successfully")
                .contains("Successfully released change log lock");
    }

    @SuppressWarnings("serial")
    private boolean serverNotRunning(IllegalStateException ex) {
        NestedCheckedException nested = new NestedCheckedException("failed", ex) {
        };
        if (nested.contains(ConnectException.class)) {
            Throwable root = nested.getRootCause();
            return root.getMessage().contains("Connection refused");
        }
        return false;
    }

}
