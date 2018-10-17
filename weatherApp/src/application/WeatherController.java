package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class WeatherController implements Initializable{

	@FXML
	TextField cityTextField;
	@FXML
	Label tempLabel, weatherLabel,sunriseLabel,sunsetLabel;
	@FXML
	ImageView imageView;
	@FXML
	Button celciusButton,farenheitButton,showWeatherButton;

	private String celcius, fahrenheit;
	private String latitude, longitude;

	private StringBuilder getJson(String urlStr) throws IOException{
		StringBuilder sb=new StringBuilder();
		Thread t=new Thread(new Runnable(){

			@Override
			public void run() {
				try{
					
					URL url=new URL(urlStr);
					URLConnection connection=url.openConnection();
					BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String line;
					while((line=reader.readLine())!=null){
						sb.append(line);
					}
					reader.close();
				}

				catch(Exception e){
					
				}
			}
			
		});
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return sb;
	}

	public void showWeather(){
		String city=cityTextField.getText();
		if(!city.equals(null)){
			String apiKeyWeather="244ec3fd30394c4d6fd8e325abc9d838";
			String apiKeyTime="4V8CXGV5VPDI";
			String urlStrWeather="http://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+apiKeyWeather+"&units=metric";
			try{
				StringBuilder sb=this.getJson(urlStrWeather);
				Map<String,Object> respMap=jsonToMap(sb.toString());
				Map<String,Object> mainMap=jsonToMap(respMap.get("main").toString());
				Map<String,Object> coordMap=jsonToMap(respMap.get("coord").toString());
				Map<String,Object> sysMap=jsonToMap(respMap.get("sys").toString());

				//getting latitude and longitude
				this.latitude=coordMap.get("lat").toString();
				this.longitude=coordMap.get("lon").toString();
				//System.out.println("latitude "+latitude+" longitude "+longitude);
				this.printHashMap(respMap);
				int c=(int)Double.parseDouble(mainMap.get("temp").toString());
				this.celcius=Integer.toString(c);
				this.fahrenheit=this.celciusToFarenheit(this.celcius);
				this.tempLabel.setText( this.celcius + "°C");

				//getting weather
				String str=respMap.get("weather").toString();
				str=str.substring(2, str.length()-2);
				String [] arr=str.split(", ");
				HashMap<String,String> weatherMap=new HashMap<>();
				for(String temp:arr){
					String [] arr2=temp.split("=");
					weatherMap.put(arr2[0], arr2[1]);
				}
				String description=weatherMap.get("description");
				String icon=weatherMap.get("icon");
				this.weatherLabel.setText(description);
				Thread t1=new Thread(new Runnable(){

					@Override
					public void run() {
						//loading image on imageView
						Image image=new Image("http://openweathermap.org/img/w/"+icon+".png");
						imageView.setImage(image);
					}
					
				});
				t1.start();
				//getting the timezone abbreviation
				String urlStrTime="http://api.timezonedb.com/v2/get-time-zone?key="+apiKeyTime+"&format=json&by=position&lat="+this.latitude+"&lng="+this.longitude;
				sb=this.getJson(urlStrTime);
				Map<String,Object> tzoneMap=jsonToMap(sb.toString());
				this.printHashMap(tzoneMap);
				String zoneName=tzoneMap.get("zoneName").toString();
				//setting the sunrise/sunset time
				String sunriseTimestamp=sysMap.get("sunrise").toString();
				String sunsetTimestamp=sysMap.get("sunset").toString();
				this.sunriseLabel.setText(this.unixToDate(sunriseTimestamp,zoneName));
				this.sunsetLabel.setText(this.unixToDate(sunsetTimestamp,zoneName));
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
		else if(city.equals(null)){
			System.out.println("enter city name");
		}
	}

	private String unixToDate(String unixStamp, String zoneName){
		 double dd=Double.parseDouble(unixStamp);
		 long unixSeconds =(long)dd;
		 // convert seconds to milliseconds
		 Date date = new java.util.Date(unixSeconds*1000L);
		 // the format of your date

		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		 // give a timezone reference for formatting
		  // stATIC java.util.TimeZone.getTimeZone("GMT")
		 //GMT+2
		 sdf.setTimeZone(java.util.TimeZone.getTimeZone(zoneName));
		 String formattedDate = sdf.format(date);
		return formattedDate;
	}

	private void printHashMap(Map<String,Object> map){
		for (String name: map.keySet()){
            String key =name;
            String value = map.get(name).toString();
            System.out.println(key + "  " + value);
		}
	}

	private String celciusToFarenheit(String celcius){
		double c=Double.parseDouble(celcius);
		double fahrenheit=((9*c)/5)+32;
		return Integer.toString((int)fahrenheit);
	}

//	private String fahrenheitToCelcius(String fahrenheit){
//		double f=Double.parseDouble(fahrenheit);
//		double celcius=(5*(f-32))/9;
//		return Integer.toString((int)celcius);
//	}

	public void setFahrenheit(){
		this.tempLabel.setText(this.fahrenheit+"°F");
	}
	public void setCelcius(){
		this.tempLabel.setText(this.celcius+"°C");
	}

	public static Map<String, Object> jsonToMap(String str){
		Map<String, Object> map=new Gson().fromJson(str, new TypeToken<HashMap<String,Object>>(){}.getType());
		return map;

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}

}
