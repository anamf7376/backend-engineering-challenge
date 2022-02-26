
import java.io.*;
import java.util.concurrent.*;

import java.util.*;
import java.time.*;
import java.text.*;
import java.sql.Timestamp;
 
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;




public class Main
{
    public static void main(String[] args) {
    
        String string0="", string1="", string2="", string3="";
 		// we need 4 inputs including --input_file and --window_size
   		if (args.length < 4)
   		{
      		System.out.println(" INCORRECT INPUT ");
      		return;
   		}
        else
        {
             string0 = args[0];
             string1 = args[1];
             string2 = args[2];
             string3 = args[3];
            
        }
        // we check the input format
        String filename="";
        int windowsize=0;
		if(string0.equals("--input_file"))
		{
			filename=string1;
			if(string2.equals("--window_size"))
			{
				windowsize=Integer.parseInt(string3);
			}
			else
			{
				System.out.println(" INCORRECT INPUT ");
				return;
			}
			
		}
        else
		if(string2.equals("--input_file"))
		{
			filename=string3;
			if(string0.equals("--window_size"))
			{
				windowsize=Integer.parseInt(string1);
			}
			else
			{
				System.out.println(" INCORRECT INPUT ");
				return;
			}
			
		}
		else
			{
				System.out.println(" INCORRECT INPUT ");
				return;
			}
			


        Events cal = new Events();
        
        // parse the Json file and populate list of objects with timestamp and duration
        cal.parseJson(filename);
        // calculate average time and dump into output.json
        cal.getDate(windowsize);
    }
}


 class Events {
 
     // global list to hold transaction logs
     static List<TransactionLog> list = new ArrayList<>();
     static double sum = 0;
     	
     	
     	// calculate average of durations larger than current_time-window_size
    public void getDate(int window_size){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        SimpleDateFormat dateFormatOut = new SimpleDateFormat("yyyy-MM-dd HH:mm");//dd/MM/yyyy
		Date eventDatestart=null;
		Date eventDateend=null;
     	Date eventDate=null;

        try {
        // remove seconds
        	eventDatestart = dateFormatOut.parse(list.get(0).timestamp);
        	eventDateend   = dateFormatOut.parse(list.get(list.size()-1).timestamp);
        	eventDate= dateFormat.parse(list.get(list.size()-1).timestamp);
     	       
        int one_min = 1;
        Calendar c = Calendar.getInstance();
        // add 1 min to end if we are higher than the endtime after truncate
        if(eventDateend.compareTo(eventDate)!=0)
        {
 		  c.setTime(eventDateend);
     	  c.add(Calendar.MINUTE, one_min);
     	  eventDateend = c.getTime();
        }
        


        // infinite loop to dump every minute to output.json
        while(eventDateend.compareTo(eventDatestart)!=-1)
        {
           // re-init sum, count to 0.
           // get current time
           sum=0;
 		   c.setTime(eventDatestart);
     	   // current time - window_size in minutes
     	   c.add(Calendar.MINUTE, -window_size);
     	   Date eventDatestartMinusWindowSize = c.getTime();

     	   int count=0;
     	   // traverse the list backwards to find timestamps larger than the one we need
     	   	for(int i = 0 ; i<list.size();i++){
     	   		
     	   		eventDate = dateFormat.parse(list.get(i).timestamp);
     	   		int resultst = eventDate.compareTo(eventDatestartMinusWindowSize); //comparing with start of window
     	   		int resultend = eventDate.compareTo(eventDatestart);                 //comparing with end of window
				// time from list lies between start and end (both inclusive)
     	  		if(resultst == 1 && resultend !=1 ){
     	   			sum = sum + list.get(i).duration;
     	   			count++;
     	   		}
     	   	}
     	   
           // calculate average
           if(count==0)count++;
     	   sum=sum/count;
     	   SimpleDateFormat dateFormatout = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
     	   String datefor=dateFormatout.format(eventDatestart);
     	   // write output to json file
     	   writeTojson(sum,datefor);
     	   // increment start date by 1 minute
     	   c.setTime(eventDatestart);
     	   c.add(Calendar.MINUTE, one_min);
     	   eventDatestart = c.getTime();
     	   

         }
         }   catch(java.text.ParseException  e) {
     	   		e.printStackTrace();
     	} 
     	

        }
        
        
    public void writeTojson(double avg, String eventDate){
        JSONObject jsonObject = new JSONObject();
        FileWriter file = null;
      	//Inserting key-value pairs into the json object
      	try {
      	 jsonObject.put("date", eventDate);
      	 jsonObject.put("average_delivery_time", avg);
         file = new FileWriter("output.json",true);
         file.write(jsonObject.toJSONString());
         file.write("\n");
         file.close();
         
      	} catch (IOException e) {         
        	e.printStackTrace();
        }
     }
        
        
        // Parse events.json and populate list 
     public void parseJson(String json){
        
        	
        	BufferedReader br = null;
        	JSONParser parser = new JSONParser();
            
            String sCurrentLine;
			 try {
           			 br = new BufferedReader(new FileReader(json));
            
            // keep looping till we get to the end of the file
           		 	while ((sCurrentLine = br.readLine()) != null) {
               
                	Object obj;
               
                    obj = parser.parse(sCurrentLine);
                    JSONObject jsonObject = (JSONObject) obj;
					// extract timestamp
                    String timestamp = (String) jsonObject.get("timestamp");
                    int count=0,i=0;
                    for(i=0; i<timestamp.length();i++)
                    {
                    	if (timestamp.charAt(i)=='.')
                    	{
                    		count=i;
                    	}
                    }
                    // we only take 3 decimal places in millisecs
                    if (count!=0)
                    	timestamp=timestamp.substring(0, Math.min(count+4,i));
                    
                    Long duration =  (Long) jsonObject.get("duration");
                    
        			TransactionLog tran = new TransactionLog(timestamp,duration);
        			// add timestamp and duration to list
        			list.add(tran);
                } 
                
            }
            catch (IOException e) {
            	e.printStackTrace();
            }
            // there are 2 Parse Exception from org.json.simple.parser and java.text so adding full name here to avoid ambiguity
            catch (org.json.simple.parser.ParseException e) {
            	e.printStackTrace();
            }
            
       }     
}
            



class TransactionLog{
    String timestamp;
    Long duration;
    TransactionLog(String timestamp, Long duration){
        this.timestamp = timestamp;
        this.duration = duration;
    }
}

/**
 * Your MovingAverage object will be instantiated and called as such:
 * MovingAverage obj = new MovingAverage(size);
 * double param_1 = obj.next(val);
 
 
 unbabel_cli --input_file events.json --window_size 10
The input file format would be something like:

{"timestamp": "2018-12-26 18:11:08.509654","translation_id": "5aa5b2f39f7254a75aa5","source_language": "en","target_language": "fr","client_name": "easyjet","event_name": "translation_delivered","nr_words": 30, "duration": 20}
{"timestamp": "2018-12-26 18:15:19.903159","translation_id": "5aa5b2f39f7254a75aa4","source_language": "en","target_language": "fr","client_name": "easyjet","event_name": "translation_delivered","nr_words": 30, "duration": 31}
{"timestamp": "2018-12-26 18:23:19.903159","translation_id": "5aa5b2f39f7254a75bb33","source_language": "en","target_language": "fr","client_name": "booking","event_name": "translation_delivered","nr_words": 100, "duration": 54}
The output file would be something in the following format.

{"date": "2018-12-26 18:11:00", "average_delivery_time": 0}
{"date": "2018-12-26 18:12:00", "average_delivery_time": 20}
{"date": "2018-12-26 18:13:00", "average_delivery_time": 20}
{"date": "2018-12-26 18:14:00", "average_delivery_time": 20}
{"date": "2018-12-26 18:15:00", "average_delivery_time": 20}
{"date": "2018-12-26 18:16:00", "average_delivery_time": 25.5}
{"date": "2018-12-26 18:17:00", "average_delivery_time": 25.5}
{"date": "2018-12-26 18:18:00", "average_delivery_time": 25.5}
{"date": "2018-12-26 18:19:00", "average_delivery_time": 25.5}
{"date": "2018-12-26 18:20:00", "average_delivery_time": 25.5}
{"date": "2018-12-26 18:21:00", "average_delivery_time": 25.5}
{"date": "2018-12-26 18:22:00", "average_delivery_time": 31}
{"date": "2018-12-26 18:23:00", "average_delivery_time": 31}
{"date": "2018-12-26 18:24:00", "average_delivery_time": 42.5}
 
 
 
 
 */






