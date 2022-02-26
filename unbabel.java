
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
    
        String string0="";
        String string1="";
        String string2="";
        String string3="";
 
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
        cal.parseJson(filename);
        cal.getDate(windowsize);
    }
}


 class Events {
 
 
     static List<TransactionLog> list = new ArrayList<>();
     static double sum = 0;
     	
     	
     	// calculate average of durations larger than current_time-window_size
    public void getDate(int window_size){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");

        while(true)
        {
           sum=0;
           Date date = new Date();
     	   Calendar c = Calendar.getInstance();
     	   c.setTime(date);
     	   Date currentDate=c.getTime();
     	   
     	   c.add(Calendar.MINUTE, -window_size);
     	   Date currentDateMinusWindowSize = c.getTime();
     	   Date eventDate=null;
     	   int count=0;
     	   
     	   try {
     	   	for(int i = list.size()-1 ; i>=0;i--){
     	   		
     	   		eventDate = dateFormat.parse(list.get(i).timestamp);
     	   		//System.out.println(eventDate);
     	   		int result = eventDate.compareTo(currentDateMinusWindowSize);
     	   		
     	   		if(result == 1){
     	   		    //System.out.println(list.get(i).duration);
     	   			sum = sum + list.get(i).duration;
     	   			count++;
     	   		}
     	   		else 
     	   		break;
     	   	}
     	        
     	   } catch (Exception e) { 
     	   
     	   }

           //if (count>0)
           
     	   sum=sum/count;
     	   SimpleDateFormat dateFormatout = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
     	   String datefor=dateFormatout.format(currentDate);
     	   writeTojson(sum,datefor);
     	   
     	   
     	   try{
     	   	 TimeUnit.SECONDS.sleep(60);
     	   } catch (Exception e) { 
     	   }

         }

        
        }
        
        
    public void writeTojson(double avg, String eventDate){
        JSONObject jsonObject = new JSONObject();
      	//Inserting key-value pairs into the json object
      	
      	try {
      	 jsonObject.put("date", eventDate);
      	 jsonObject.put("average_delivery_time", avg);
         FileWriter file = new FileWriter("/Users/fahimakhtar/Downloads/unbabel/output.json",true);
         file.write(jsonObject.toJSONString());
         file.write("\n");
         file.close();
      	} catch (Exception e) {         
        
        }
        }
        
        
        // Parse events.json and populate list 
     public void parseJson(String json){
        
        	
        	 BufferedReader br = null;
        	JSONParser parser = new JSONParser();
            
            String sCurrentLine;
			 try {
            br = new BufferedReader(new FileReader(json));
            
            
            while ((sCurrentLine = br.readLine()) != null) {
                //System.out.println("Record:\t" + sCurrentLine);

                Object obj;
               
                    obj = parser.parse(sCurrentLine);
                    JSONObject jsonObject = (JSONObject) obj;

                    String timestamp = (String) jsonObject.get("timestamp");
                    int count=0,i=0;
                    for(i=0; i<timestamp.length();i++)
                    {
                    	if (timestamp.charAt(i)=='.')
                    	{
                    		count=i;
                    	}
                    }
                    if (count!=0)
                    	timestamp=timestamp.substring(0, Math.min(count+4,i));
                    //System.out.println(timestamp);

                    Long duration =  (Long) jsonObject.get("duration");
                    //System.out.println(duration);

        			TransactionLog tran = new TransactionLog(timestamp,duration);
        			list.add(tran);
                    

                } 
                
                
            }
            catch (Exception e) {
                    // TODO Auto-generated catch block
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






