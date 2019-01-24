package clientBounderiesLibrarian;

import com.jfoenix.controls.JFXTextField;

import Common.IGUIController;
import Common.ObjectMessage;
import Common.Report;
import clientCommonBounderies.StartPanelController;
import clientConrollers.OBLClient;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

public class ReportsController implements IGUIController
{

	OBLClient client;
	
    @FXML
    private TabPane TabPaneSelect;
    
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField activeReaderAccounts;

    @FXML
    private TextField freezedReaderAccounts;

    @FXML
    private TextField lockedReaderAccounts;

    @FXML
    private TextField totalNumOfCopies;

    @FXML
    private TextField numOfdelayedReaderAccounts;

    @FXML
    private ComboBox<?> monthComboBox;

    @FXML
    private ComboBox<?> yearComboBox;

    @FXML
    private ComboBox<?> chooseFromPreviousComboBox;

    @FXML
    private TextField avgForRegular;

    @FXML
    private TextField medianForRegular;

    @FXML
    private BarChart<?, ?> diagramForRegular;

    @FXML
    private TextField avgForDesired;

    @FXML
    private TextField medianForDesired;

    @FXML
    private BarChart<?, ?> diagramForDesired;

    @FXML
    private TextField avgForAll;

    @FXML
    private TextField medianForAll;

    @FXML
    private BarChart<?, ?> diagramForAll;

    @FXML
    private TextField avgForNumLateReturns;

    @FXML
    private TextField medianForNumLateReturns;

    @FXML
    private BarChart<?, ?> diagramForNumLateReturns;

    @FXML
    private TextField avgForDurationLateReturns;

    @FXML
    private TextField medianForDurationLateReturns;

    @FXML
    private JFXTextField BookIDReport3;

    @FXML
    void initialize() 
    {
    	client=StartPanelController.connToClientController;
    	client.setClientUI(this);
    	ObjectMessage sendToServer=new ObjectMessage("Ask for report2","History");
		client.handleMessageFromClient(sendToServer); 
    	
    	

    }

	@Override
	public void display(ObjectMessage msg) 
	{
		if(msg.getMessage().equals("succssful reporting"))
		{
			Report report=(Report)msg.getObjectArray().get(arg0)
			avgForAll.setText(String.valueOf(report.getAverage()));
			//medianForAll.setText();
		}
		else if(msg.getMessage().equals("average of desired book"))
		{
			
			
		}
		else
		{
			
			
		}
		

		
		
		

	}
}
