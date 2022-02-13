# How to build your first Apache KafkaConsumer application

***Question:***

*How do you get started building your first Kafka Consumer application?*

Steps are from: **[How to build your first Apache KafkaConsumer application using Confluent](https://developer.confluent.io/tutorials/creating-first-apache-kafka-consumer-application/confluent.html#create-the-kafka-consumer-application)**

The numbers in black circles (❶, ❷, etc) refer to the step number in that Kafka tutorial at Confluent Developer.

---

## Provision the cluster
❶
1. Sign in to Confluent Cloud.
2. Create a cluster. Name it …

> ⚠ **Warning:** GET THE LINK AND PROMO CODE FROM JOE SHORE!

## Download and set up the Confluent CLI
❹
Install confluent CLI from here: [Install Confluent CLI](https://docs.confluent.io/confluent-cli/current/install.html#scripted-installation)
Install to `~/bin`. Then use the follosing 

`$ confluent login`

`$ confluent environment list`

`$ confluent environment use <env-id>`

`$ confluent api-key store --resource <cluster id>`

`$ confluent api-key store <key> <secret> --resource <cluster id>`

`$ confluent kafka cluster list`

`$ confluent kafka cluster use <cluster id>`

## Create a topic
 ❺ Create a topic in the Kafka cluster:
```
confluent kafka topic create input-topic
```
## Initialise the project
❷

Make the local directory for the project
```
mkdir kafka-consumer-application && cd kafka-consumer-application
```
Make a directory for configuration data:
```
mkdir configuration
```
❸ Write config info in files
`configuration/ccloud.properties` :

> ⚠ **Warning:** DO NOT SHOW KEY & SECRET CREDENTIALS ON VIDEO !

```

# Required connection configs for Kafka producer, consumer, and admin
bootstrap.servers={{ BOOTSTRAP_SERVERS }}
security.protocol=SASL_SSL
sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username='{{ CLUSTER_API_KEY }}' password='{{ CLUSTER_API_SECRET }}';
sasl.mechanism=PLAIN
# Required for correctness in Apache Kafka clients prior to 2.6
client.dns.lookup=use_all_dns_ips

# Best practice for Kafka producer to prevent data loss
acks=all

# Required connection configs for Confluent Cloud Schema Registry
schema.registry.url={{ SR_URL }}
basic.auth.credentials.source=USER_INFO
basic.auth.user.info={{ SR_API_KEY }}:{{ SR_API_SECRET }}

```
> ⚠ **Warning:** Don't directly copy-paste the above configuration. Copy it from the Confluent Cloud Console so that it includes your credentials.
>
> Keep one blank line before and after the block of text above, so that we can concatenate it to other text if we want (as we will be doing later).

## Add application & producer properties
❼ `configuration/dev.properties :`

```
key.serializer=org.apache.kafka.common.serialization.StringSerializer
value.serializer=org.apache.kafka.common.serialization.StringSerializer
acks=all

#Properties below this line are specific to code in this application
input.topic.name=input-topic
output.topic.name=output-topic

```

> ℹ Meaning of the dev.properties file:
> - `key.serializer` and `value.serializer` 
> - `max.poll.interval.ms`
> - `enable.auto.commit`
> - `auto.offset.reset`
> - `group.id`
> - `file.path`
> - `input.topic.name`

❽ Combine the above two configs:
```
cat configuration/ccloud.properties >> configuration/dev.properties
``` 

## Create the Project
❻ Use the Gradle build file, named `build.gradle` :

Obtain the gradle wrapper:
```
gradle wrapper
```
❾ Use the 3 `.java` files containing the following:

`KafkaConsumerApplication` class

`ConsumerRecordsHandler` interface

`FileWritingRecordsHandler` class that implements the above interface.


## Compile and Run
⓫
Compile:

```
./gradlew shadowJar
```

Run:

```
java -jar build/libs/kafka-producer-application-standalone-0.0.1.jar configuration/dev.properties input.txt
```

## Produce sample data to the input topic
⓬

```
confluent kafka topic produce input-topic
```

```
the quick brown fox
jumped over
the lazy dog
Go to Kafka Summit
All streams lead
to Kafka
```
Use `Ctrl + C` to exit.

## Inspect the consumed records
⓭
```
cat consumer-records.out
```

The output should look exactly like the input above.