package clientConrollers;

import java.io.IOException;
import java.util.Optional;

import Common.IGUIController;
import Common.IGUIStartPanel;
import clientBounderiesLibrarian.StartPanelLibrarianController;
import clientBounderiesReaderAccount.StartPanelReaderAccountController;
import clientCommonBounderies.StartPanelController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 
 * This Class contains the common action for the GUIs controllers
 */

public abstract class AClientCommonUtilities 
{
	/**
	 * static attributes for keeping the "way" to the "windows" 
	 * there can be maximum 2 windows open at one time
	 */
	private static IGUIStartPanel startPanelUser = AStartClient.startPanelController; //keeping the StartPannel that currently in use
	public static Stage stage; //if there is a "small" window- this is it's stage 

	/**
	 * This method opens a new GUI "small" window 
	 * @param classThatAsk - in the original Class just type: getClass() 
	 * @param loc - is the full location of the fxml file. for example: "/clientBounderiesLibrarian/AddBook.fxml"
	 * @param title - title is the title of the new window
	 */
	public static void loadWindow(Object classThatAsk, String loc, String title)
	{
		try 
		{
			if(startPanelUser.getActivateWindows()==0)
			{
				
				Parent parent = FXMLLoader.load(((Class<?>)classThatAsk).getResource(loc));
				stage = new Stage(StageStyle.DECORATED);
				stage.setTitle(title);
				stage.setScene(new Scene(parent));
				stage.show();
				stage.setOnCloseRequest(e->
				{
					backToStartPanel();
				});
				int x=startPanelUser.getActivateWindows();
				startPanelUser.setActivateWindows(++x);
			}
			else
			{
				infoAlert("You have already active window ,close him first","Already activate task");
			}

		} 
		catch (IOException e) 
		{
			alertError("An error has accourd. window can't load","Error"); // open error message
			e.printStackTrace();
		}

	}
	
	/**
	 * This function is closing the current "small" window and sets the ClientUI in OBLClient to be the current StartPanel
	 */
	public static void backToStartPanel()
	{
		try 
		{
			stage.close();
		} catch (Exception e) { 
			Platform.runLater(()->
			{
				stage.close();
			});
		}
		int x=startPanelUser.getActivateWindows();
		startPanelUser.setActivateWindows(--x);
		StartPanelController.connToClientController.setClientUI((IGUIController)startPanelUser);
	}
	
	
	/**
	 * This method opens a GUI "StartPanel" window 
	 * @param classThatAsk - in the original Class just type: getClass() 
	 * @param loc - is the full location of the fxml file. 
	 * @param title - title is the title of the new window
	 */
	public static void loadStartPanelWindow(Object classThatAsk, String loc, String title)
	{
		Platform.runLater(()->
		{ 
			try 
			{
				startPanelUser.setActivateWindows(0);
				stage.close();
				FXMLLoader loader=new FXMLLoader(((Class<?>)classThatAsk).getResource(loc)); // load the FXML file
		        Parent parent = (Parent) loader.load();
		        startPanelUser = loader.getController();//get the controller of fxml
		        if(startPanelUser instanceof StartPanelController)
		        {
		        	((StartPanelController)startPanelUser).initialize(new String[2]);
		        }
		        else if(startPanelUser instanceof StartPanelLibrarianController)
		        {
		        	((StartPanelLibrarianController)startPanelUser).initialize();
		        }
		        else if(startPanelUser instanceof StartPanelReaderAccountController)
		        {
		        	((StartPanelReaderAccountController)startPanelUser).initialize();
		        }
		        AStartClient.primaryStagePanel.close();
				//Parent parent = FXMLLoader.load(((Class<?>)classThatAsk).getResource(loc));
				AStartClient.primaryStagePanel.setTitle(title);
				AStartClient.primaryStagePanel.setScene(new Scene(parent));
				AStartClient.primaryStagePanel.show();
			} 
			catch (IOException e) 
			{
				alertError("An error has accourd. window can't load","Error"); // open error message
				e.printStackTrace();
			}
		});

	}
	
	/**
	 * This method prompts an alert Error Message 
	 * @param headerText - the text message to the user
	 * @param title - the title for the window
	 */
	public static void alertError(String headerText,String title) 
    { 
		Platform.runLater(()->
		{  
			Alert alert = new Alert(Alert.AlertType.ERROR);
			ButtonType bttexit = new ButtonType("exit", ButtonBar.ButtonData.CANCEL_CLOSE);
			alert.getButtonTypes().clear();
			alert.setHeaderText(headerText);
			alert.setTitle(title);
			alert.getButtonTypes().addAll(bttexit);
			Optional<ButtonType> result = alert.showAndWait();
		});
    }
	
	/**
	 * This method prompts an alert Error Message and exiting from the program 
	 * @param headerText - the text message to the user
	 * @param title - the title for the window
	 */
	public static void alertErrorWithExit(String headerText,String title) 
    { 
		Platform.runLater(()->
		{  
			Alert alert = new Alert(Alert.AlertType.ERROR);
			ButtonType bttexit = new ButtonType("exit", ButtonBar.ButtonData.CANCEL_CLOSE);
			alert.getButtonTypes().clear();
			alert.setHeaderText(headerText);
			alert.setTitle(title);
			alert.getButtonTypes().addAll(bttexit);
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) 
			{
				System.exit(0);
			}
		});
    }
	
	/**
	 * This method prompts an alert Error Message and exiting from the program 
	 * @param headerText - the text message to the user
	 * @param title - the title for the window
	 * @param btnName - the text that will be on the button
	 */
	public static void alertErrorWithOption(String headerText,String title,String btnName) 
    { 
		Platform.runLater(()->
		{ 
			Alert alert = new Alert(Alert.AlertType.ERROR);
			ButtonType bttexit = new ButtonType(btnName, ButtonBar.ButtonData.CANCEL_CLOSE);
			alert.getButtonTypes().clear();
			alert.setHeaderText(headerText);
			alert.setTitle(title);
			alert.getButtonTypes().addAll(bttexit);
			Optional<ButtonType> result = alert.showAndWait();
		}); 
    }
	
	/**
	 * This function prompts an alert message to the user (not the Error one) 
	 * @param headerText
	 * @param title
	 */
	public static void infoAlert(String headerText,String title)  
	{
		Platform.runLater(()->
		{   
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			ButtonType bttCountiue = new ButtonType("continue", ButtonBar.ButtonData.FINISH);
			alert.getButtonTypes().clear();
			alert.setHeaderText(headerText);
			alert.setTitle(title);
			alert.getButtonTypes().addAll(bttCountiue);
			Optional<ButtonType> result = alert.showAndWait();
		});
	}


}
