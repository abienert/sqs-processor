Copyright 2008 Amazon Technologies, Inc.  Licensed under the Apache License,
Version 2.0 (the "License"); you may not use this file except in compliance
with the License. You may obtain a copy of the License at:

http://aws.amazon.com/apache2.0

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.


Background
----------
This package contains the back-end of the SQS-EC2 Job Processor, intended to
demonstrate idiomatic use of SQS as a mechanism for co-ordinating distributed
job processing.

The application takes the following parameters:

 - AWS Access Key ID
 - AWS Secret Access Key
 - Job backlog queue name (queue to pull jobs from)
 - Job results queue name (queue where job results go)
 - Dead letter queue name (queue where failed jobs go)
 - Poll rate per minute (number of times per minute to poll the backlog)
 - Expected job execution time in seconds
 - Job failure retries (number of retries before a failed job is put into the
                        dead letter queue)
 - Job executor (the fully-qualified class that will actually process each job)

How It Works
------------
The basic work-flow is:

 - Receive a single message from the jobs backlog queue whose body is expected
     to contain a job definition
 - Process the job by handing the job definition to a JobExecutor
 - If the job succeeds, delete the job from the jobs backlog and place the
     result in the results queue
 - Otherwise, keep track of the number of times a job has failed, moving it
     to a dead letter queue if it fails a given number of times

For execution details, see java/com/amazonaws/jobprocessor/QueueListener.java.

Building the Sample Code
------------------------
To build the sample code, cd into the base directory that contains build.xml and
run "ant dist". This will compile the source files and put everything needed
into a directory called dist/.

Integrating with EC2
--------------------
To get an AMI to automatically start the Job Processor, the following commands
must be present at the end of /etc/rc.local:

  export JAVA_HOME=/usr/java/latest
  export JOB_PROCESSOR_HOME=/job_processor
  $JOB_PROCESSOR_HOME/bin/parse-user-data-and-start-job-processing $JOB_PROCESSOR_HOME &> /tmp/job_processor.log 

which assumes that /job_processor is the location of the pre-built application.

Parameterizing Launches with EC2
--------------------------------
In order to build a general-purpose AMI that can support any user, queue names,
poll rate, etc., it takes that information from the instance configuration that
is specified at launch.

The parsing of these parameters is handled in the
  /job_processor/bin/parse-user-data-and-start-job-processing
Ruby script. See that file for more details.

To launch with your own parameters using the EC2 commandline tools, provide them
using the -d argument to ec2-run-instances. The parameters are specified as a
comma-delimited string of key-value pairs:

 AWS Access Key ID                       = access_key_id
 AWS Secret Access Key                   = secret_access_key
 Job backlog queue name                  = job_backlog_queue_name
 Job results queue name                  = results_queue_name
 Dead letter queue name                  = dead_letter_queue_name
 Poll rate per minute                    = poll_rate_per_min
 Expected job execution time in seconds  = expected_job_execution_time_in_secs
 Job failure retries                     = job_failure_retries
 Job executor                            = job_executor

All parameters have defaults, as specified in the launch script, except Access
Key ID and Secret Access Key, which you must provide.

For example, to launch with a poll rate of once per second:

  ec2-run-instances <AMI ID> -k gsg-keypair -d "access_key_id=<Access Key ID>,secret_access_key=<Secret Access Key>,poll_rate_per_min=60"

Using Your Own Job Processing Logic
-----------------------------------
You can use your own job processing logic by implementing the
com.amazonaws.jobprocessor.JobExecutor interface and writing your own logic in
the execute method. Then compile your class into a jar, place it in
/job_processor/lib, and launch with the job_executor parameter set to your
class's fully-qualified name.
