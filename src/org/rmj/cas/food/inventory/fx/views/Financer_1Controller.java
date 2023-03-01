/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rmj.cas.food.inventory.fx.views;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author jovanalic
 * since 2021-07-12
 * to accept entry for financer
 */
public class Financer_1Controller implements Initializable {

    @FXML
    private VBox VBoxForm;
    @FXML
    private Button btnAdd;
    @FXML
    private TextField txtField00;
    @FXML
    private TextField txtField01;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    /**
     * 
     * @param fsTransNox - transaction number to delete
     * for deletion of entry
     */
    public void deleteEntry(String fsTransNox){
    
    }
    
}
