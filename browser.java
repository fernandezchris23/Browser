import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.scene.image.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.*;
import java.util.*;
import java.lang.*;
import java.awt.event.*;
import javafx.concurrent.Worker;


public class Browser extends Application{

	//Objects
	Stage window;
	TextField addressBar;
	Button home, back, refresh, addFav;
	MenuButton favorites;
	WebView webpage;
	ToolBar toolbar;
	
	//Variables
	String input = "https://www.google.com",
		   homePage = "";
	String [] backHistory;
	int counter = 0; //Counter for array for back button
	boolean isBack = false; //Necessary for dealing with history
	
	public static void main(String[] args) {
		launch(args);

	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
	
		window = primaryStage;
		window.setTitle("Creators");
		
		//Creating images for Buttons
		ImageView backImage = new ImageView(new Image("back.png"));
		backImage.setFitHeight(30);
		backImage.setFitWidth(30);
		
		ImageView homeImage = new ImageView(new Image("home.png"));
		homeImage.setFitHeight(30);
		homeImage.setFitWidth(30);
		
		ImageView refreshImage = new ImageView(new Image("refresh.png"));
		refreshImage.setFitHeight(30);
		refreshImage.setFitWidth(30);
		
		//Creating Objects for GUI
		addressBar = new TextField();
		addressBar.setPrefSize(300,40);
		home = new Button("", homeImage);
		home.setPrefSize(40,40);
		back = new Button("", backImage);
		back.setPrefSize(40,40);
		refresh = new Button("", refreshImage);
		refresh.setPrefSize(40,40);
		webpage = new WebView();
		favorites = new MenuButton("Favorites");
		addFav = new Button("+ Favorite");
		
		//Toolbar Setup
		toolbar = new ToolBar(
				
				addFav,
				favorites
								
				);
		
		//URL After Update
		webpage.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (Worker.State.SUCCEEDED.equals(newValue)) {
                addressBar.setText(webpage.getEngine().getLocation());
                input = addressBar.getText();
            }
        });
		
		//Layouts and Scene
		BorderPane layoutMain = new BorderPane();
		GridPane layoutTop = new GridPane();
		Scene scene = new Scene(layoutMain, 800, 600);
		
		//Loading up Page and setting Address Bar
		webpage.getEngine().load(input);
		webpage.setPrefSize(800, 560);
		addressBar.setText(input);
		
		//Dealing with Entering Web Address
		addressBar.setOnKeyReleased(e -> {
						if(e.getCode() == KeyCode.ENTER){
							if(addressBar.getText().indexOf("www.") != -1){
								
								if(addressBar.getText().indexOf("https://") == -1) {	
									input = "https://";
									input += addressBar.getText();
								}
								
								else {
									input = addressBar.getText();
								}
								
								System.out.print(input);
								webpage.getEngine().load(input);
								addressBar.setText(input);
							}
							else {
								input = "https://symfony.com/doc/current/_images/errors-in-prod-environment.png";
								webpage.getEngine().load(input);
								addressBar.setText("ERROR: Not a web address");
							}
							
						}
					});
		
		//History
		webpage.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
		    if (Worker.State.SUCCEEDED.equals(newValue)) {
		            if(isBack == false) {
		    			addressBar.setText(webpage.getEngine().getLocation());
		    			createHistory();
		    			//counter += 1; //Used in back function
		            }
		    }
		    });
			
		//Back Button (WORK IN PROGRESS)
		/*back.setOnAction(e -> {
			if(counter > 1) {
				backHistory = new String[counter];
				isBack = true;
				
				backFunc();
				System.out.println(counter);
				counter -= 1;
				recreateHistory();
				isBack = false;
			}
		});*/ (WORK IN PROGRESS)
		
		//Permanant History not necessary once I figure out how to create a proper log system
    File tempHistory = new File("history.txt");
		tempHistory.deleteOnExit();
		
		//Refresh Button
		refresh.setOnAction(e ->{
			webpage.getEngine().load(input);
			addressBar.setText(input);
		});
		
		//Home Button
		readHomeFile();
		home.setOnAction(e -> {
				if(homePage == "") {
					setHomePage();
				}
				else {				
					input = homePage;
					webpage.getEngine().load(input);
					addressBar.setText(input);
				}
			});
		
    //Shortcut for Setting Home after initial setup
		scene.getAccelerators().put(new KeyCodeCombination(KeyCode.H,
				KeyCombination.CONTROL_DOWN), () ->{
					setHomePage();
				});
		home.setTooltip(new Tooltip("Ctrl + H to set a new Homepage"));
		
		//Add Favorite Button
		addFav.setOnAction(e-> addFavoriteFunc());
		
		//Favorites Stored
		readFavs();
				
		//Layout Setup
		layoutTop.setConstraints(back, 0,0);
		layoutTop.setConstraints(home, 1,0);
		layoutTop.setConstraints(refresh, 2,0);
		layoutTop.setConstraints(addressBar, 3,0);
		layoutTop.setConstraints(toolbar, 0, 1, 20, 1);
		layoutTop.setHgrow(addressBar, Priority.ALWAYS);
		
		layoutTop.getChildren().addAll(back, home,
				refresh, addressBar, toolbar);
		layoutMain.setTop(layoutTop);
		layoutMain.setCenter(webpage);
				
		scene.getStylesheets().add("webBrowser.css");
		primaryStage.setScene(scene);
				
		primaryStage.show();		
	
	}
	
	public void readHomeFile() {
		
		try (Scanner scanFile = new Scanner (new File("homePage.txt"))) {
			homePage = scanFile.useDelimiter("\\A").next();
		}
		catch (IOException e1) {
			System.out.println("ERROR!");
		}
		
	}
	
	private void setHomePage(){
		Stage homeWindow = new Stage();
		
		TextField inputHome = new TextField();
		Button setHome = new Button("Set Home");
		Label label = new Label("Enter a home page:");
		
		BorderPane layoutHome = new BorderPane();
		GridPane grid = new GridPane();
		
		layoutHome.setCenter(grid);
		
		grid.setConstraints(label, 0,0);
		grid.setConstraints(inputHome, 0,1);
		grid.setConstraints(setHome, 0,2);
		
		grid.getChildren().addAll(label, inputHome, setHome);
		
		layoutHome.setPadding(new Insets(5,5,5,5));
		
		setHome.setOnAction(e -> {
			
			if(inputHome.getText().indexOf("https://") == -1) {
				homePage = "https://";
				homePage += inputHome.getText();
				try (BufferedWriter format1 = new BufferedWriter(new FileWriter("homePage.txt"))) {
					format1.write(homePage);
				} catch (IOException e1) {
					System.out.println("ERROR!");
				}
			}
			else {
				homePage = inputHome.getText();
				try (BufferedWriter format1 = new BufferedWriter(new FileWriter("homePage.txt"))) {
					format1.write(homePage);
				} catch (IOException e1) {
					System.out.println("ERROR!");
				}
			}
			
		});
				
		Scene scene2 = new Scene(layoutHome, 200, 100);
		homeWindow.setScene(scene2);
		homeWindow.show();
	}
	
	private void addFavoriteFunc() {
		
		try (BufferedReader format1 = new BufferedReader(new FileReader("favorites.txt"))) {
			
			    String line; //Used to look through file
			    String found = ""; //Stores the address that was the same
			    			    
			    while((line = format1.readLine()) != null) {
			    	//System.out.println(line);
			    	if(line.equals(input)) {
			    		found = line;
			    		break;
			    	}
			    }
			    
			   	format1.close();
			   	
			    if(!(found.equals(input))) {
		        	BufferedWriter format2 = new BufferedWriter(new FileWriter("favorites.txt", true));
					format2.write(input);
					format2.newLine();
					
					format2.close();				
		        }			
		} catch (IOException e1) {
			System.out.println("ERROR! Favorite not Added.");
		}
	}
	
	public void readFavs() {
		
		String link;
				
		try (BufferedReader format1 = new BufferedReader(new FileReader("favorites.txt"))) {
			
			while((link = format1.readLine()) != null) {
		    	//System.out.println(link);
		    	MenuItem links = new MenuItem(link);
		    	
		    	links.setOnAction(e -> {
		    		input = links.getText();
		    		webpage.getEngine().load(input);
					addressBar.setText(input);
		    	});
		    	
		    	favorites.getItems().add(links);
		    }
		}
		catch (IOException e1) {
			System.out.println("ERROR!");
		}
	}
	
	private void createHistory() {
		
		try (BufferedWriter format1 = new BufferedWriter(new FileWriter("history.txt", true))) {
			
			format1.write(input);
			format1.newLine();
								    
		   	format1.close();
		   				
	        			
		} catch (IOException e1) {
			System.out.println("ERROR! Creating History Failed");
		}
	}
	
	//backFunc() and recreateHistory() functions are a work in progress at the moment
	private void backFunc() {
		
		ArrayList<String> holder = new ArrayList<String>(); //Just a placeholder
		try (BufferedReader format1 = new BufferedReader(new FileReader("history.txt"))) {
			
			String lineHolder = ""; //Another placeholder
			
			while((lineHolder = format1.readLine()) != null) {
		    	
				holder.add(lineHolder);
		    	System.out.println(lineHolder);
		    }
			format1.close();
		}
		catch (IOException e1) {
			System.out.println("ERROR!");
		}
		backHistory = holder.toArray(new String[counter - 1]);
		
		input = backHistory[counter - 2];
		webpage.getEngine().load(input);
		addressBar.setText(input);
		
	}
	
	private void recreateHistory() {
		
		try (BufferedWriter format1 = new BufferedWriter(new FileWriter("history.txt"))) {
			
			String placeHolder;
			
			for(int i = 0; i < counter; i++) {
				
				placeHolder = backHistory[i];
				
				format1.write(placeHolder);
				format1.newLine();
				
				format1.close();
			}			
	        			
		} catch (IOException e1) {
			System.out.println("ERROR! Recreating History Failed");
		}		
	}
}
