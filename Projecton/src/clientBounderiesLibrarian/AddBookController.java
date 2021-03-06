package clientBounderiesLibrarian;

import com.jfoenix.controls.JFXButton;
import java.net.*;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;

import Common.Book;
import Common.IGUIController;
import Common.ObjectMessage;
import clientCommonBounderies.StartPanelController;
import clientConrollers.AClientCommonUtilities;
import clientConrollers.AValidationInput;
import clientConrollers.OBLClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * This Class is the controller for fxml file: AddBook.fxml
 */

public class AddBookController implements IGUIController
{
	OBLClient client;
	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML 
	private JFXTextField TopicTextField; 
	@FXML
	private JFXTextField BookTitleTextField;

	@FXML 
	private JFXTextField numberOfCopies; 

	@FXML
	private JFXTextField BookAuthorTextField;

	@FXML
	private JFXTextField PublishedYearTextField;

	@FXML
	private JFXTextField EditionTextField;


	@FXML
	private JFXCheckBox DesiredCheckBox;

	@FXML
	private DatePicker PurchaseDateDatePicker;

	@FXML
	private Button UploadBtn;

	@FXML
	private JFXButton CancelBtn;

	@FXML
	private JFXButton SaveBtn;

	@FXML
	private Label fileLabel;

	@FXML
	private Button cancelUploadBtn;

	@FXML
	private ComboBox<String> bookLocationLetter;

	@FXML
	private ComboBox<String> BookLocationNumber;

	ObservableList<String> list1;
	ObservableList<String> list2;

	private boolean isUploaded =false;
	private static File f;

	private static Book book;

	//function for cancel button. if librarian doesn't want  continue to add a book
	@FXML
	void cancelBtnClicked(ActionEvent event) 
	{
		AClientCommonUtilities.backToStartPanel();
	}


	@FXML
	/**
	 * the function activated when user press on cancel upload file and it's clean file path
	 * @param event
	 */
	void cancelUpload(ActionEvent event)
	{
		isUploaded=false;
		fileLabel.setText(" " );
		cancelUploadBtn.setVisible(false);
	}

	/**
	 * the function set letters and numbers to the comboBox
	 */
	public void combo() 
	{
		ArrayList <String> s=new ArrayList<String>();

		for(int i=1; i<=6; i++)
		{
			s.add(Integer.toString(i));
		}

		list1 = FXCollections.observableArrayList(s);
		BookLocationNumber.setItems( list1);

		ArrayList <String> b=new ArrayList<String>();
		char ch = 'A';

		for(int i= 0; i<= ('Z'- 'A'); i++)
		{
			b.add(String.valueOf(ch));
			ch++;
		}
		list2 = FXCollections.observableArrayList(b);
		bookLocationLetter.setItems((ObservableList) list2);
	}


	//add new book or only copy
	@FXML
	/**
	 * the function activated when user press on save and it's sand all data about new book to server
	 * @param event
	 */
	void saveAddNewBook(ActionEvent event) 
	{ 
		String checkResult = validationFields();

		if(checkResult.equals("correct"))//if all fields correctly
		{
			String bookLocation;

			if( ( bookLocationLetter.getValue() == null  || (bookLocationLetter.getValue().toString()).equals(null) ) 
					|| ( BookLocationNumber.getValue() == null  || (BookLocationNumber.getValue().toString()).equals(null) ) )
			{
				bookLocation = "";
			}
			else
			{
				bookLocation =  bookLocationLetter.getValue() + "-" + BookLocationNumber.getValue();
			}
			
			//if file was upload check that it is a '.pdf' file
			if(isUploaded)
			{
				String fileName = f.getAbsolutePath();
				//int fileLength = fileName.length();
				
				if(fileName.substring(fileName.length()-4,fileName.length()).equals(".pdf"))
				{
					boolean isDesired= DesiredCheckBox.isSelected();
					book=new Book(BookTitleTextField.getText(), BookAuthorTextField.getText(),PublishedYearTextField.getText(),TopicTextField.getText(),String.valueOf(isDesired),EditionTextField.getText(),numberOfCopies.getText(), bookLocation);
					ObjectMessage msg= new ObjectMessage(book,"addBook","Book");
					book.setFileIsLoaded(isUploaded);
					client.setClientUI(this);
					client.handleMessageFromClient(msg);
				}
				else
				{
					AClientCommonUtilities.alertErrorWithOption("The file must be '.pdf' file","Wrong","Back");
				}
				
			}
			
			else
			{
				boolean isDesired= DesiredCheckBox.isSelected();
				book=new Book(BookTitleTextField.getText(), BookAuthorTextField.getText(),PublishedYearTextField.getText(),TopicTextField.getText(),String.valueOf(isDesired),EditionTextField.getText(),numberOfCopies.getText(), bookLocation);
				ObjectMessage msg= new ObjectMessage(book,"addBook","Book");
				book.setFileIsLoaded(isUploaded);
				client.setClientUI(this);
				client.handleMessageFromClient(msg);
			}
			
		}
		else
		{
			AClientCommonUtilities.alertErrorWithOption(checkResult,"Wrong","Back");		
		}



	}


	@FXML
	/**
	 * function upload file and send it to server
	 * @param event
	 */
	void uploadTableOfContents(ActionEvent event) 
	{
		FileChooser fc=new FileChooser();
		fc.getExtensionFilters().add(new ExtensionFilter("PDF Files","*.pdf"));
		fc.setInitialDirectory(new File("c:\\"));
		f=fc.showOpenDialog(null);
		
		if(f!=null)
		{
			isUploaded=true;
			fileLabel.setText("Selected File::" + f.getAbsolutePath());
			cancelUploadBtn.setVisible(true);
		}


	}


	@FXML
	void initialize() 
	{
		client=StartPanelController.connToClientController;
		client.setClientUI(this);
		combo();
	}



	/**
	 * The function checks validation of input
	 * @return
	 */
	private String validationFields()
	{
		String result,finalResult="";

		result=AValidationInput.checkValidationBook("bookName",BookTitleTextField.getText());
		if(!result.equals("correct"))
		{
			finalResult+=result+'\n';
		}


		result=AValidationInput.checkValidationBook("authorName",BookAuthorTextField.getText());
		if(!result.equals("correct"))
		{
			finalResult+=result+'\n';
		}

		result=AValidationInput.checkValidationBook("dateOfBook",PublishedYearTextField.getText());
		if(!result.equals("correct"))
		{
			finalResult+=result+'\n';
		}

		result=AValidationInput.checkValidationBook("topic",TopicTextField.getText());
		if(!result.equals("correct"))
		{
			finalResult+=result+'\n';
		}

		if(EditionTextField.getText().equals("") || null == EditionTextField.getText())
		{
			finalResult+="Insert number of edition\n";

		}
		else
		{
			for(int i=0;i<EditionTextField.getText().length();i++)//check validation for edition text field
			{

				if(EditionTextField.getText().charAt(i)<'0' ||EditionTextField.getText().charAt(i)>'9')        
				{
					finalResult+="Edition must contain only numbers\n";
					break;
				}

			}
		}

		if(numberOfCopies.getText().equals("") || null == numberOfCopies.getText())
		{
			finalResult+="Insert number of copies\n";

		}
		else if(numberOfCopies.getText().equals("0"))
		{
			finalResult+="Number of copies must be bigger then 0.\n";
		}
		else
		{
			for(int i=0;i<numberOfCopies.getText().length();i++)//check validation for number of copies text field
			{

				if(numberOfCopies.getText().charAt(i)<'0' ||numberOfCopies.getText().charAt(i)>'9')        
				{
					finalResult+="Edition must contain only numbers\n";
					break;
				}

			}
		}


		if(finalResult.equals(""))
		{
			return "correct";
		}

		else
		{
			return finalResult;
		}

	}
	Book bookForSend;

	
	@FXML
	/**
	 * This function activate every time that user input text in key fields and create 'book' object and send it to server to check if this book is exist in db 
	 * @param event
	 * 
	 */
	void checkLocation(KeyEvent event) 
	{
		Platform.runLater(()->
		{ 
			String bookName, authorName; 
			int year, edition;	

			if(BookTitleTextField.getText().equals("") || null == BookTitleTextField.getText())
			{
				bookName= " ";
			}
			else
			{
				bookName=BookTitleTextField.getText();
			}

			if(BookAuthorTextField.getText().equals("") || null == BookAuthorTextField.getText())
			{
				authorName= " ";
			}
			else
			{
				authorName=BookAuthorTextField.getText();
			}

			if(PublishedYearTextField.getText().equals("") || null == PublishedYearTextField.getText())
			{
				year=0;
			}
			else
			{
				boolean onlyDigits = true;
				
				for(int i=0; i<PublishedYearTextField.getText().length(); i++) 
				{
					if(PublishedYearTextField.getText().charAt(i)<'0' ||PublishedYearTextField.getText().charAt(i)>'9')
					{
						onlyDigits = false;
						break;
					}
				}
				
				if(onlyDigits)
				{
					year=Integer.parseInt(PublishedYearTextField.getText());
				}
				else
				{
					year= 0;
				}
				
			}

			if(EditionTextField.getText().equals("") || null == EditionTextField.getText())
			{
				edition=0;
			}
			else
			{
				boolean onlyDigits2 = true;
				
				for(int i=0; i<EditionTextField.getText().length(); i++) 
				{
					if(EditionTextField.getText().charAt(i)<'0' ||EditionTextField.getText().charAt(i)>'9')
					{
						onlyDigits2 = false;
						break;
					}
				}
				
				if(onlyDigits2)
				{
					edition=Integer.parseInt(EditionTextField.getText());
				}
				else
				{
					edition= 0;
				}
				
			}
			
			bookForSend = new Book(bookName, authorName, year,edition );
			ObjectMessage msg= new ObjectMessage(bookForSend,"setLocation","Book");
			client.setClientUI(this);
			client.handleMessageFromClient(msg);
		});

	}

	@Override
	public void display(ObjectMessage msg)
	{

		if (msg.getNote().equals("Successfull"))
		{
			//if file was uploaded and the "BookAdd" was successful add the file
			if(isUploaded)
			{
				sendFile();
			}
 
			AClientCommonUtilities.infoAlert(msg.getMessage(), msg.getNote());
			AClientCommonUtilities.backToStartPanel();

		}

		else if (msg.getNote().equals("Unsuccessfull"))
		{
			AClientCommonUtilities.alertErrorWithExit(msg.getMessage(), msg.getNote());
		}

		else if (msg.getNote().equals("Wrong"))
		{
			AClientCommonUtilities.alertErrorWithOption(msg.getMessage(), msg.getNote(),"Back");
		}

		else if(msg.getNote().equals("LocationFound")) 
		{
			setLocation(msg);//set location  for exist book in the gui window

		}

		else if(msg.getNote().equals("LocationNotFound")) 
		{
			Platform.runLater(()->
			{ 
				bookLocationLetter.setDisable(false);
				BookLocationNumber.setDisable(false);
			});
		}

	}
 
	
	/**
	 * the function set location of exists book in gui window for client
	 * @param msg
	 */
	private void setLocation(ObjectMessage msg)
	{
		Platform.runLater(()->
		{ 
			bookLocationLetter.setValue(String.valueOf(msg.getMessage().charAt(0)));
			BookLocationNumber.setValue(String.valueOf(msg.getMessage().charAt(2)));
			bookLocationLetter.setDisable(true);
			BookLocationNumber.setDisable(true);
		});
	}

	
	/**
	 * the function send uploaded file from client to server
	 */
	public void sendFile()
	{
		File myFile = new File(f.getAbsolutePath());
		Socket sock;
		ServerSocket servsock;
		BufferedInputStream bis;

		String fileName= bookForSend.getBookName() + " " + bookForSend.getAuthorName() + " " + bookForSend.getDateOfBook() + " " + bookForSend.getEdition();

		try
		{
			ObjectMessage m = new ObjectMessage();
			m.setNote("AddPDF");
			m.setMessage(Integer.toString( (int) myFile.length()) );
			m.setExtra(fileName);
			servsock = new ServerSocket(5643);
			client.setClientUI(this);
			client.handleMessageFromClient(m);
			sock = servsock.accept();
			bis = new BufferedInputStream(new FileInputStream(myFile));
			OutputStream os = sock.getOutputStream();
			
			byte[] bytes = new byte[16*1024];
		    int count;
		    while ((count = bis.read(bytes)) > 0) 
		    {
		        os.write(bytes, 0, count);
		    }
			os.flush();

			servsock.close();
			os.close();
			bis.close();
			sock.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
