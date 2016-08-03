package visual;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import opdb.DBHandler;
import opdb.Service;

/**
 * FXML Controller class
 *
 * @author Joao
 */
public class AddPanelController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML private TextField plateField;
    @FXML private TextField dateField;
    @FXML private TextField qntField;
    @FXML private TextField descField;
    @FXML private TextField priceField;
    @FXML private TextField kmField;
    
    @FXML private Button saveButton;
    @FXML private Button addButton;
    @FXML private Button deleteButton;
    
    @FXML private CheckBox beltBox;
    
    @FXML private TableView<Service> serviceTable;
    @FXML private TableColumn<Service, Integer> qntColumn;
    @FXML private TableColumn<Service, String> descColumn;
    @FXML private TableColumn<Service, Double> priceColumn;
    
    private ObservableList<Service> servicesList,selectedServicesList;
    private DBHandler dbHandler;
    
    @FXML
    private void handleAddButton(ActionEvent event){
       
       //create service
       Service service = new Service(Double.parseDouble(qntField.getText()),descField.getText(),Double.parseDouble(priceField.getText()));
       
       //add it to service table
       serviceTable.getItems().add(service);
       
       //clear fields
       qntField.clear();
       descField.clear();
       priceField.clear();
      
    }
    
    @FXML
    private void handleDeleteButton(ActionEvent event){
       //get all items
       servicesList = serviceTable.getItems();
       
       //get only selected items
       selectedServicesList = serviceTable.getSelectionModel().getSelectedItems();
       
       //check if item from servicelist is selected and remove it
       selectedServicesList.forEach(servicesList::remove);
    }
    
    @FXML
    private void saveButton(ActionEvent event){
        //retrieve input info to the variables
        servicesList = serviceTable.getItems();
        String plate = plateField.getText();
        String date = dateField.getText();
        String km = kmField.getText();
        
        //add input to the database
        dbHandler.addtoDB(plate, date, Double.parseDouble(km), servicesList);
        
        //add to distbelt table if belt box is selected
        if(beltBox.isSelected()){
            dbHandler.addDistBelt(plate, date);
        }
        
        //clear everything after save
        serviceTable.getItems().clear();
        beltBox.setSelected(false);
        plateField.clear();
        dateField.clear();
        kmField.clear();

    }
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
     
     dbHandler = new DBHandler();
     qntColumn.setCellValueFactory(new PropertyValueFactory<>("qnt"));
     descColumn.setCellValueFactory(new PropertyValueFactory<>("desc"));
     priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
     
     plateField.setPromptText("XX-XX-XX");
     dateField.setPromptText("DD/MM/AAAA");
     qntField.setPromptText("Quantidade");
     descField.setPromptText("Descricão");
     priceField.setPromptText("Preco");
     
    }    
    
}
