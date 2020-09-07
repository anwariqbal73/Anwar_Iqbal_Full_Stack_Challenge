package challenge;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;


import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.opencsv.CSVWriter;
public class Scrapper {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    	/*************************************************
    	 * Module 1  
    	 * Scrapping questions from url response
    	 *************************************************/
        String baseUrl = "https://www.cheatsheet.com/gear-style/20-questions-to-ask-siri-for-a-hilarious-response.html/";

        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);

        try {

            HtmlPage page = client.getPage(baseUrl);

            List < HtmlElement > items = (List < HtmlElement > ) page.getByXPath("//h2");

            /*************************************************
        	 * Module 2
        	 * Export questions to csv file
        	 *************************************************/
            
            if (items.isEmpty()) {
                System.out.println("No Questions found !");
            } else {
            	
            	
                File file = new File("output.csv");
                try {
                    // create FileWriter object with file as parameter 
                    FileWriter outputfile = new FileWriter(file);

                    // create CSVWriter object filewriter object as parameter 
                    CSVWriter writer = new CSVWriter(outputfile);

                    // adding header to csv 
                    String[] header = {
                        "Question"
                    };
                    writer.writeNext(header);

                    // add data to csv 
                    for (int i = 0; i < items.size(); i++) {


                        String[] row = {
                            items.get(i).asText()
                        };

                        writer.writeNext(row);

                    }

                    // closing writer connection 
                    writer.close();
                    System.out.println("File exported successfully!");
                } catch (IOException e) {
                    // TODO Auto-generated catch block 
                    e.printStackTrace();
                }
                
                /*************************************************
            	 * Module 3
            	 * Getting random question and use IFTTT webhook to send email
            	 *************************************************/
                HttpClient httpclient = HttpClients.createDefault();
                HttpPost httppost = new HttpPost("https://maker.ifttt.com/trigger/siri_question/with/key/nMXxVDzmexValChY21HQ4DLcd7I-opIpCNpjED2mvLn");

                // Request parameters and other properties.
                List < NameValuePair > params = new ArrayList < NameValuePair > (2);

                Random randomGenerator = new Random();

                int index = randomGenerator.nextInt(items.size());

                params.add(new BasicNameValuePair("value1", items.get(index).asText()));

                httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

                //Execute and get the response.
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    System.out.print("done! Email Sent");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}