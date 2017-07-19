/**
 * Copyright © 2016-2017 The Thingsboard Authors
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
package org.thingsboard.server.extensions.core.filter;

import java.util.ArrayList;
import java.util.List;

import org.josql.Query;
import org.josql.QueryExecutionException;
import org.josql.QueryParseException;
import org.josql.QueryResults;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Andrew Shvayka
 */
@RunWith(MockitoJUnitRunner.class)
public class JosqlSampleTest {

    @Test
    public void josqlsample1Test() throws QueryParseException, QueryExecutionException {
      Employee emp1 = new Employee();
      emp1.setEmployeeId(1);
      emp1.setEmployeeName("Rockey");
      emp1.setSalary(1000);
      Query q = new Query();
      q.parse("SELECT * FROM org.thingsboard.server.extensions.core.filter.Employee WHERE employeeName=:employeeName and abs(salary)>200");
      q.setVariable("employeeName", "Rockey");
      List<Employee> empList = new ArrayList<Employee>();
      empList.add(emp1);

      QueryResults execute = q.execute(empList);
      List<?> results = execute.getResults();
      System.out.println(results);
    }

    @Test
    public void josqlsample2Test() throws QueryParseException, QueryExecutionException {
      Employee emp1 = new Employee();
      emp1.setEmployeeId(1);
      emp1.setEmployeeName("Rockey");
      emp1.setSalary(1000);
      Query q = new Query();
      q.parse("SELECT * FROM org.thingsboard.server.extensions.core.filter.Employee WHERE employeeName like \"%:employeeName\"");
      q.setVariable("employeeName", "Roc");
      List<Employee> empList = new ArrayList<Employee>();
      empList.add(emp1);

      QueryResults execute = q.execute(empList);
      List<?> results = execute.getResults();
      System.out.println(results);
    } 
    
    @Test
    public void josqlsample3Test() throws QueryParseException, QueryExecutionException {
      Employee emp1 = new Employee();
      emp1.setEmployeeId(1);
      emp1.setEmployeeName("Rockey");
      emp1.setSalary(1000);
      Query q = new Query();
      q.parse("SELECT * FROM org.thingsboard.server.extensions.core.filter.Employee WHERE employeeName LIKE \"Roc%\"");
    
      List<Employee> empList = new ArrayList<Employee>();
      empList.add(emp1);

      QueryResults execute = q.execute(empList);
      List<?> results = execute.getResults();
      System.out.println(results);
    }

    @Test
    public void josqlsample4Test() throws QueryParseException, QueryExecutionException {
      Employee emp1 = new Employee();
      emp1.setEmployeeId(1);
      emp1.setEmployeeName("Rockey");
      emp1.setSalary(1000);
      Query q = new Query();
      q.parse("SELECT * FROM org.thingsboard.server.extensions.core.filter.Employee WHERE regexp(employeeName,'Rock.*')");
    
      List<Employee> empList = new ArrayList<Employee>();
      empList.add(emp1);
    
      QueryResults execute = q.execute(empList);
      List<?> results = execute.getResults();
      System.out.println(results);
    }

    @Test
    public void josqlsample5Test() throws QueryParseException, QueryExecutionException {
      Employee emp1 = new Employee();
      emp1.setEmployeeId(1);
      emp1.setEmployeeName("Rockey");
      emp1.setSalary(1000);
      Query q = new Query();
      q.parse("SELECT * FROM org.thingsboard.server.extensions.core.filter.Employee WHERE regexp(employeeName,'Rock2.*')");
    
      List<Employee> empList = new ArrayList<Employee>();
      empList.add(emp1);
    
      QueryResults execute = q.execute(empList);
      List<?> results = execute.getResults();
      System.out.println(results);
    } 
}
