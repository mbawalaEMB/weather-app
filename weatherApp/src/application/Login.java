package application;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Login {
	@FXML
	TextField usernameTextField;
	@FXML
	PasswordField passTextField;
	@FXML
	Label errorLabel;

	private String modulo(int a, int n){
		int remainder=a%n;
		while(remainder<0){
			remainder+=n;
		}
		return Integer.toString(remainder);
	}


	private int powerModulo(int x,int e,int n){
		int count=0;
		int result=1;
		while(count<e){
			result*=x;
			result=Integer.parseInt(this.modulo(result, n));
			count++;
		}
		return result;
	}

	private int getAscii(char c){
		int ascii=(int) c;
		return ascii;
	}

	private boolean loginSuccessful() throws Exception{
		String username=usernameTextField.getText();
		String password=passTextField.getText();
		try(FileInputStream inStream=new FileInputStream("userAuthentication.txt");
			BufferedReader reader=new BufferedReader(new InputStreamReader(inStream))){
			String line;
			while((line=reader.readLine())!=null){
				String [] arr=line.split(",");
				if(arr[0].equals(username)){
					int salt=Integer.parseInt(arr[1]);
					StringBuilder sb=new StringBuilder();
					for(int i=0;i<password.length();i++){
						int m=this.getAscii(password.charAt(i));
						sb.append(this.powerModulo(m, salt, new Register().threshold));
					}
					if(arr[2].equals(sb.toString())){
						//passwords matched
						return true;
					}
				}
			}
		}
		return false;
	}

	public void loginAction(ActionEvent event) throws IOException, Exception{
		if(loginSuccessful()){
			FXMLLoader loginLoader=new FXMLLoader(getClass().getResource("CityWeather.fxml"));
			Parent loginRoot=(Parent)loginLoader.load();
			Scene loginScene = new Scene(loginRoot);
			loginScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Stage loginStage=(Stage) ((Node) event.getSource()).getScene().getWindow();
			loginStage.setScene(loginScene);
			loginStage.show();
		}
		else{
			errorLabel.setText("Username and Password Incorrect");
		}
	}
	public void registerAction(ActionEvent event) throws IOException{
		FXMLLoader registerLoader=new FXMLLoader(getClass().getResource("Register.fxml"));
		Parent registerRoot=(Parent)registerLoader.load();
		Scene registerScene = new Scene(registerRoot);
		registerScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Stage registerStage=(Stage) ((Node) event.getSource()).getScene().getWindow();
		registerStage.setScene(registerScene);
		registerStage.show();
	}
}
