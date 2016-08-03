package visual;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode; 
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import opdb.DBHandler;
import opdb.Service;

/**
 *
 * @author joao
 */
public class OPDBController implements Initializable {
    
    DBHandler dbHandler;
    String lastmat;
    Parent add_page;
    Scene addscene;
    Stage add_stage;
    
    @FXML private Label label;
    @FXML private Label kmLabel;
    @FXML private Label beltDates;
    @FXML private Label totalLabel;
    @FXML private TextField searchBar;
    @FXML private Button searchButton;
    @FXML private Button addButton;
    @FXML private ListView dateList;
    
    
    @FXML private TableView<Service> serviceTable;
    @FXML private TableColumn<Service, Double> qntColumn;
    @FXML private TableColumn<Service, String> descColumn;
    @FXML private TableColumn<Service, Double> priceColumn;
    
    @FXML
    private void handleSearchButton(ActionEvent event) {
        //do stuff when searchButton get clicked
        lastmat = DBHandler.convertToDBForm(searchBar.getText());
        Set<String> matlist = dbHandler.getDatesFromPlate(lastmat);
        fillListWith(matlist);
        beltDates.setText((dbHandler.getBeltDates(lastmat)));
    }
    
    @FXML
    private void handleAddButton(ActionEvent event){
        // do stuff when addButton get clicked
        add_stage.show();
    }
    
    
    @FXML
    private void textFieldKeyHandler(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){         
          lastmat = DBHandler.convertToDBForm(searchBar.getText());
          Set<String> matlist = dbHandler.getDatesFromPlate(lastmat);
          fillListWith(matlist);
          beltDates.setText((dbHandler.getBeltDates(lastmat)));
        }
    }

    private void fillListWith(Set<String> list){
        //adds string set at param to dateList
        // set because list cant have duplicate dates
        dateList.setItems(FXCollections.observableArrayList(list));
        
    }
    
    
    private String calculateTotal(List<Service> lista){
        double num = 0;
        
        for(int i = 0; i<lista.size();i++){
            num += lista.get(i).getPrice();
        }
        
        return Double.toString(num);
    }
   
    @FXML
   private void listMouseSelectHandler(MouseEvent event){
      // handle when something from list gets clicked
      if(event.getClickCount() == 2){
          String date = (String) dateList.getSelectionModel().getSelectedItem();
          
          List<Service> lista1 = dbHandler.getServiceFromDateNPlate(date, lastmat);
          
          kmLabel.setText( dbHandler.getKmfromDateNPlate(date, lastmat) );
          
          totalLabel.setText(calculateTotal(lista1));
          
          ObservableList<Service> lista = FXCollections.observableArrayList(lista1);
          serviceTable.setItems(lista);

      } 
   }
   
   @FXML
   
   private void deleteKeySelectedHandler(KeyEvent event){
       
       if(event.getCode() == KeyCode.DELETE){
          
           //delete from DB tables
           String date = (String) dateList.getSelectionModel().getSelectedItem();
           dbHandler.deleteFromTable(lastmat, date);
           
           //refresh lists
           Set<String> matlist = dbHandler.getDatesFromPlate(lastmat);
           fillListWith(matlist);
           beltDates.setText((dbHandler.getBeltDates(lastmat)));

       } 
   
   }
   
  
    @Override
    public void initialize(URL url, ResourceBundle rb) {
     
     //associate table to the class "Service" parameters
     qntColumn.setCellValueFactory(new PropertyValueFactory<>("qnt"));
     descColumn.setCellValueFactory(new PropertyValueFactory<>("desc"));
     priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
     
     //creates the database handler
     dbHandler = new DBHandler();
     
    //load visuals
        try {
            add_page = FXMLLoader.load(getClass().getResource("/visual/AddPanel.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(OPDBController.class.getName()).log(Level.SEVERE, null, ex);
        }
     
     
     addscene = new Scene(add_page);
     add_stage = new Stage();
     
     add_stage.setScene(addscene);
     add_stage.setResizable(false);
     add_stage.setTitle("Adicionar Servico");
     searchBar.setPromptText("Insira uma matricula...");
    }    
    
}
