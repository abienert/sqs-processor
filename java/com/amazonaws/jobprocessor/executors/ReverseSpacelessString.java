/*
 * Copyright 2008 Amazon Technologies, Inc.  Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 * http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.amazonaws.jobprocessor.executors;

import org.apache.log4j.Logger;

import com.amazonaws.jobprocessor.JobExecutionException;
import com.amazonaws.jobprocessor.JobExecutor;

/**
 * 
 * This is a simple JobExecutor that reverses a string but throws an exception
 * if the string has a space in it.
 * 
 * @author walters
 * 
 */
public class ReverseSpacelessString implements JobExecutor {

    private static Logger log = Logger.getLogger(ReverseSpacelessString.class);

    public String execute(String jobDefinition) throws JobExecutionException {
        log.info("Executing job = '" + jobDefinition + "'");
        if (jobDefinition == null) {
            throw new JobExecutionException("Job definition is null");
        }
        if (jobDefinition.indexOf(' ') != -1) {
            throw new JobExecutionException("String has a space in it");
        }
        return new StringBuffer(jobDefinition).reverse().toString();
    }

}
