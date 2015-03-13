/*
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.demos.peeper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ludovic Orban
 */
public class DataStore {

  private Connection connection;
  protected Logger logger = LoggerFactory.getLogger(getClass());


    public void init() throws Exception {
        Class.forName("org.h2.Driver");
        connection = DriverManager.getConnection("jdbc:h2:~/ehcache-demo-peeper", "sa", "");

        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS PEEPS (" +
            "id bigint auto_increment primary key," +
            "PEEP_TEXT VARCHAR(142) NOT NULL" +
            ")");
        connection.commit();
        statement.close();
  }



  public synchronized void addPeep(String peepText) throws Exception {
    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO PEEPS (PEEP_TEXT) VALUES (?)");
    preparedStatement.setString(1, peepText);
    preparedStatement.execute();
    connection.commit();
    preparedStatement.close();
  }

  public synchronized List<String> findAllPeeps() throws Exception {
    List<String> result = new ArrayList<String>();

    Statement statement = connection.createStatement();
    try {
      ResultSet resultSet = statement.executeQuery("SELECT * FROM PEEPS");

      while (resultSet.next()) {
        String peepText = resultSet.getString("PEEP_TEXT");
        result.add(peepText);
      }
    } finally {
      statement.close();
    }
    logger.info("Adding into the cache");
    return result;
  }

  public void close() throws Exception {
      connection.close();
  }

}
