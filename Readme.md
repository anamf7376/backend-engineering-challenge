#unbabel.java
This is a JAVA code that parses a stream of events and writes an aggregated output every minute to output.json

##Execution
java unbabel.java --input_file events.json --window_size 1000


##SampleOutput

Output is generated to output.json in the following format every minute --

{"date":"2022-02-25 23:34:34","average_delivery_time":35.0}
{"date":"2022-02-25 23:35:34","average_delivery_time":35.0}
{"date":"2022-02-25 23:36:34","average_delivery_time":35.0}
{"date":"2022-02-25 23:37:34","average_delivery_time":35.0}
{"date":"2022-02-25 23:38:34","average_delivery_time":35.0}


##Prerequisites
Download json-simple-1.1.1.jar
http://www.java2s.com/Code/Jar/j/Downloadjsonsimple111jar.htm

Set the following environment variables -- 

export JSON_JAVA=/home/unbabel
export CLASSPATH=$JSON_JAVA/json-simple-1.1.1.jar 



