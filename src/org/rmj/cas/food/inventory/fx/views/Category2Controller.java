package org.rmj.cas.food.inventory.fx.views;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.json.simple.JSONObject;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.cas.food.inventory.fx.modules.qsParameter;
import org.rmj.lp.parameter.agent.XMCategoryLevel2;

public class Category2Controller implements Initializable {

    @FXML private VBox VBoxForm;
    @FXML private Button btnExit;
    @FXML private AnchorPane anchorField;
    @FXML private TextField txtField01;
    @FXML private TextField txtField02;
    @FXML private TextField txtField03;
    @FXML private CheckBox Check05;
    @FXML private TextField txtField04;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;
    @FXML private Button btnCancel;
    @FXML private Button btnClose;
    @FXML private Button btnSearch;
    @FXML private Button btnBrowse;
    @FXML private Button btnNew;
    @FXML private Button btnActivate;
    @FXML private FontAwesomeIconView glyphExit;
    @FXML private Label lblHeader;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        /*Initialize class*/
        poRecord = new XMCategoryLevel2(poGRider, poGRider.getBranchCode(), false);
                
        /*Set action event handler for the buttons*/
        btnBrowse.setOnAction(this::cmdButton_Click);
        btnCancel.setOnAction(this::cmdButton_Click);
        btnClose.setOnAction(this::cmdButton_Click);
        btnExit.setOnAction(this::cmdButton_Click);
        btnNew.setOnAction(this::cmdButton_Click);
        btnSave.setOnAction(this::cmdButton_Click);
        btnSearch.setOnAction(this::cmdButton_Click);
        btnUpdate.setOnAction(this::cmdButton_Click);
        btnActivate.setOnAction(this::cmdButton_Click);
                
        /*Add listener to text fields*/
        txtField02.focusedProperty().addListener(txtField_Focus);
        txtField03.focusedProperty().addListener(txtField_Focus);
        txtField04.focusedProperty().addListener(txtField_Focus);
        
        txtField01.setOnKeyPressed(this::txtField_KeyPressed);
        txtField02.setOnKeyPressed(this::txtField_KeyPressed);
        txtField03.setOnKeyPressed(this::txtField_KeyPressed);
        txtField04.setOnKeyPressed(this::txtField_KeyPressed);
        
        pnEditMode = EditMode.UNKNOWN;
        
        clearFields();
        initButton(pnEditMode);
        
        pbLoaded = true;
    }    

    private void txtField_KeyPressed(KeyEvent event) {
        TextField txtField = (TextField)event.getSource();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        
        switch (event.getCode()){
            case F3:
                JSONObject lsResult;
                if (lnIndex == 3){
                    lsResult = qsParameter.getInvType(poGRider, txtField03.getText(), 1);
                    loadMaster(3, lsResult);
                }else{
                    lsResult = qsParameter.getCategory(poGRider, txtField04.getText(), 1);
                    loadMaster(4, lsResult);
                }
                break;
            case ENTER:
            case DOWN:
                CommonUtils.SetNextFocus(txtField);
                break;
            case UP:
                CommonUtils.SetPreviousFocus(txtField);
        }
    }
    
    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button)event.getSource()).getId();
        
        switch (lsButton){
            case "btnBrowse":
                JSONObject lsResult = qsParameter.getCategory2(poGRider, "", 1);
                
                if (lsResult != null){
                    openRecord((String) lsResult.get("sCategrCd"));
                }
                break;
            case "btnCancel":
                if(ShowMessageFX.OkayCancel(null, pxeModuleName, "Do you want to disregard changes?")== true){
                    clearFields();
                    pnEditMode = EditMode.UNKNOWN;
                    break;
                }else return;
                
            case "btnClose":
            case "btnExit":
                unloadForm();
                return;
            case "btnNew":
                if (poRecord.newRecord()) loadRecord(); pnEditMode = poRecord.getEditMode();
                break;
            case "btnSave":
                if (poRecord.saveRecord()){
                    openRecord(psOldRec);
                    ShowMessageFX.Information(null, pxeModuleName, "Record Save Successfully.");
                }
                break;
            case "btnSearch":
                break;
            case "btnUpdate":
                if (poRecord.getMaster(1) != null && !poRecord.getMaster(1).toString().equals("")){
                    if (poRecord.updateRecord()){
                        pnEditMode = poRecord.getEditMode();
                    }
                }
                break;
            case "btnActivate":
                if (poRecord.getMaster(1) != null && !poRecord.getMaster(1).toString().equals("")){
                    if (btnActivate.getText().equals("Activate")){
                        if (poRecord.activateRecord(poRecord.getMaster(1).toString())){
                            openRecord(psOldRec);
                            ShowMessageFX.Information(null, pxeModuleName, "Record Activated Successfully.");
                        }
                    } else{
                        if (poRecord.deactivateRecord(poRecord.getMaster(1).toString())){
                            openRecord(psOldRec);
                            ShowMessageFX.Information(null, pxeModuleName, "Record Deactivated Successfully.");
                        }
                            
                    }
                }
                break;
            default:
                ShowMessageFX.Warning(null, pxeModuleName, "Button with name " + lsButton + " not registered.");
                return;
        }
        
        initButton(pnEditMode);
    } 
    
    private void unloadForm(){
        /*This code is working*/
        //VBoxForm.getChildren().clear();
    
        /*But i think this is a better approach*/
        VBox myBox = (VBox) VBoxForm.getParent();
        myBox.getChildren().clear();
    }
    
    private void openRecord(String fsRecordID){
        if (poRecord.openRecord(fsRecordID)){
            loadRecord();
            pnEditMode = poRecord.getEditMode();
        }
    }
    
    private void initButton(int fnValue){
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
        
        btnCancel.setVisible(lbShow);
        btnSearch.setVisible(lbShow);
        btnSave.setVisible(lbShow);
        lblHeader.setVisible(lbShow);
                
        btnClose.setVisible(!lbShow);
        btnBrowse.setVisible(!lbShow);
        btnActivate.setVisible(!lbShow);
        btnUpdate.setVisible(!lbShow);
        btnNew.setVisible(!lbShow);
        
        txtField01.setDisable(!lbShow);
        txtField02.setDisable(!lbShow);
        txtField03.setDisable(!lbShow);
        txtField04.setDisable(!lbShow);
        Check05.setDisable(true);
        
        if (lbShow)
            txtField02.requestFocus();
        else
            btnNew.requestFocus();
    }
    
    private void clearFields(){
        txtField01.setText("");
        txtField02.setText("");
        txtField03.setText("");
        txtField04.setText("");
        
        Check05.selectedProperty().setValue(false);
        btnActivate.setText("Activate");
        
        psOldRec = "";
        psInvType = "";
    }
    
    private void loadRecord(){
        txtField01.setText((String) poRecord.getMaster(1));
        txtField02.setText((String) poRecord.getMaster(2));
        
        JSONObject lsResult;
        /*Load the inventory type*/
        if (poRecord.getMaster(3) != null && !poRecord.getMaster(3).toString().isEmpty()){
            lsResult = qsParameter.getInvType(poGRider, (String) poRecord.getMaster(3), 0);
            loadMaster(3, lsResult);
        } else
            txtField03.setText("");
        
        /*Load the main category*/
        if (poRecord.getMaster(4) != null && !poRecord.getMaster(4).toString().isEmpty()){
            lsResult = qsParameter.getCategory(poGRider, (String) poRecord.getMaster(4), 0);
            loadMaster(4, lsResult);
        } else
            txtField04.setText("");
        
        boolean lbCheck = poRecord.getMaster("cRecdStat").toString().equals("1");
        Check05.selectedProperty().setValue(lbCheck);
        
        if (poRecord.getMaster("cRecdStat").toString().equals("1"))
            btnActivate.setText("Deactivate");
        else
            btnActivate.setText("Activate");
        
        psOldRec = txtField01.getText();
    }
            
    public void setGRider(GRider foGRider){this.poGRider = foGRider;}
    
    private final String pxeModuleName = "Category2Controller";
    private static GRider poGRider;
    private XMCategoryLevel2 poRecord;
    
    private int pnEditMode;
    private boolean pbLoaded = false;
    private String psOldRec;
    
     /*search description container*/
    private String psInvType = "";
    private String psMainCat = "";
   
    final ChangeListener<? super Boolean> txtField_Focus = (o,ov,nv)->{
        if (!pbLoaded) return;
        
        TextField txtField = (TextField)((ReadOnlyBooleanPropertyBase)o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        String lsValue = txtField.getText();
        
        if (lsValue == null) return;
            
        if(!nv){ /*Lost Focus*/
            switch (lnIndex){
                case 3:
                    if (lsValue.equals("")){
                        psInvType = "";
                        poRecord.setMaster(3, "");
                    }else
                        txtField.setText(psInvType);
                    break;
                case 4:
                    if (lsValue.equals("")){ 
                        psMainCat = "";
                        poRecord.setMaster(4, "");
                    }else
                        txtField.setText(psMainCat);
                    break;
                case 2:
                    if (lsValue.length() > 32) lsValue = lsValue.substring(0, 32);
                    
                    poRecord.setMaster(lnIndex, lsValue);
                    txtField.setText((String)poRecord.getMaster(lnIndex));
                    break;
                default:
                    ShowMessageFX.Warning(null, pxeModuleName, "Text field with name " + txtField.getId() + " not registered.");
            }
        } else
            txtField.selectAll();
    };
    
    private void loadMaster(int fnIndex, JSONObject fsResult){
        switch (fnIndex){
            case 3:
                if (fsResult != null && !fsResult.isEmpty()){
                    psInvType = (String) fsResult.get("sDescript");
                    poRecord.setMaster(3, (String) fsResult.get("sInvTypCd"));
                    txtField03.setText(psInvType);
                } else {
                    psInvType = "";
                    poRecord.setMaster(3, "");
                    txtField03.setText("");
                }
                break;
            case 4:
                if (fsResult != null && !fsResult.isEmpty()){
                     psMainCat = (String) fsResult.get("sDescript");
                    poRecord.setMaster(4, (String) fsResult.get("sCategrCd"));
                    txtField04.setText(psMainCat);
                } else {
                    psMainCat = "";
                    poRecord.setMaster(4, "");
                    txtField04.setText("");
                }
                break;
        }
        
    }
}
