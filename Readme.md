#unbabel.java
This is a JAVA code that parses a stream of events and writes an aggregated output every minute to output.json

##Execution
Input requires input file name and window size.
Example:
java unbabel.java --input_file events.json --window_size 1000


##SampleOutput

Output is generated to output.json in the following format for every minute --

{"date":"2018-12-26 18:11:00","average_delivery_time":0.0}
{"date":"2018-12-26 18:12:00","average_delivery_time":20.0}
{"date":"2018-12-26 18:13:00","average_delivery_time":20.0}
{"date":"2018-12-26 18:14:00","average_delivery_time":20.0}
{"date":"2018-12-26 18:15:00","average_delivery_time":20.0}
{"date":"2018-12-26 18:16:00","average_delivery_time":25.5}
{"date":"2018-12-26 18:17:00","average_delivery_time":25.5}
{"date":"2018-12-26 18:18:00","average_delivery_time":25.5}
{"date":"2018-12-26 18:19:00","average_delivery_time":25.5}
{"date":"2018-12-26 18:20:00","average_delivery_time":25.5}
{"date":"2018-12-26 18:21:00","average_delivery_time":25.5}
{"date":"2018-12-26 18:22:00","average_delivery_time":31.0}
{"date":"2018-12-26 18:23:00","average_delivery_time":31.0}
{"date":"2018-12-26 18:24:00","average_delivery_time":42.5}

##Prerequisites
Download json-simple-1.1.1.jar
http://www.java2s.com/Code/Jar/j/Downloadjsonsimple111jar.htm

Set the following environment variables -- 

export JSON_JAVA=/home/unbabel
export CLASSPATH=$JSON_JAVA/json-simple-1.1.1.jar 



