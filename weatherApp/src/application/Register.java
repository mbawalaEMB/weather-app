package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

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

public class Register {
	@FXML
	TextField usernameTextField;
	@FXML
	PasswordField passTextField, repeatPassTextField;
	@FXML
	Label noMatchLabel;

	 final int threshold=100;

	private String modulo(int a, int n){
		int remainder=a%n;
		while(remainder<0){
			remainder+=n;
		}
		return Integer.toString(remainder);
	}

	private int getAscii(char c){
		int ascii=(int) c;
		return ascii;
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

	private boolean passwordMatch() {
		if(passTextField.getText()!=null && repeatPassTextField.getText()!=null){
			if(passTextField.getText().equals(repeatPassTextField.getText())){
				return true;
			}
		}
		noMatchLabel.setText("Passwords do not match");
		return false;
	}

		//-1 username does not have an alphabet
		//0 username already exists
		//1 username is available
		private int checkUsername() throws Exception{
			//if username contains atleast one a to z
			String username=usernameTextField.getText();
			if(containsAlphabet(username) && username.matches("[a-zA-Z0-9]+") ){//Alphanumeric values only
				try(FileInputStream inStream=new FileInputStream("userAuthentication.txt");
					BufferedReader reader=new BufferedReader(new InputStreamReader(inStream))){
					String line;
					while((line=reader.readLine())!=null){
						String arr[]=line.split(",");
						String usernameOut=arr[0];
						if(usernameTextField.getText().equals(usernameOut)){
							System.out.println("Username already exists");
							return 0;
						}

					}
					return 1;
				}
			}
			else{
				System.out.println("username does not have an alphabet");
				return -1;
			}
		}

	private boolean containsAlphabet(String str) {
			for(int i=0;i<str.length();i++){
				char c=str.charAt(i);
				if(Character.isLetter(c)){
					return true;
				}
			}
			return false;
		}

	private boolean usernameFree() throws Exception {
		if(this.checkUsername()==1){
			return true;
		}
		return false;
	}

	public boolean registerSuccessful() throws Exception{
		String userCredentials=this.encryption();
		if(userCredentials!=null){
			try(FileWriter fw=new FileWriter("userAuthentication.txt",true);
					BufferedWriter writer=new BufferedWriter(fw) ) {
					fw.write(userCredentials);
					fw.write("\n");
			 }
			return true;
		}
		return false;
	}

	public void back(ActionEvent event) throws Exception{
		FXMLLoader loginLoader=new FXMLLoader(getClass().getResource("Login.fxml"));
		Parent loginRoot=(Parent)loginLoader.load();
		Scene loginScene = new Scene(loginRoot);
		loginScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Stage loginStage=(Stage) ((Node) event.getSource()).getScene().getWindow();
		loginStage.setScene(loginScene);
		loginStage.show();
	}

	public void registerAction(ActionEvent event) throws Exception{
		if(registerSuccessful()){
			FXMLLoader loginLoader=new FXMLLoader(getClass().getResource("Login.fxml"));
			Parent loginRoot=(Parent)loginLoader.load();
			Scene loginScene = new Scene(loginRoot);
			loginScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Stage loginStage=(Stage) ((Node) event.getSource()).getScene().getWindow();
			loginStage.setScene(loginScene);
			loginStage.show();
		}
	}
	private String encryption() throws Exception{
		if(usernameFree() && passwordMatch()){
			Random random=new Random();
			int salt=random.nextInt(threshold);
			String userName=usernameTextField.getText();
			String password=passTextField.getText();
			StringBuilder sb=new StringBuilder();
			for(int i=0;i<password.length();i++){
				int m=this.getAscii(password.charAt(i));
				sb.append(this.powerModulo(m, salt, threshold));
			}
			return userName+","+Integer.toString(salt)+","+sb.toString();
		}
		return null;
	}

}
