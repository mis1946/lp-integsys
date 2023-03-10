
package org.rmj.cas.food.inventory.fx.views;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.appdriver.constants.TransactionStatus;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.MiscUtil;
import org.rmj.appdriver.SQLUtil;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.cas.inventory.base.InvTransfer;
import org.rmj.cas.parameter.agent.XMBranch;
import org.rmj.appdriver.agentfx.callback.IMasterDetail;

public class InvTransferController implements Initializable {

    @FXML private Button btnExit;
    @FXML private FontAwesomeIconView glyphExit;
    @FXML private AnchorPane anchorField;
    @FXML private Label lblHeader;
    @FXML private TextField txtField01;
    @FXML private TextField txtField03;
    @FXML private TextField txtField04;
    @FXML private TextField txtField18;
    @FXML private TextField txtField07;
    @FXML private TextField txtField13;
    @FXML private TextArea txtField05;
    @FXML private Label Label12;
    @FXML private TextField txtDetail05;
    @FXML private TextField txtDetail03;
    @FXML private TextField txtDetail80;
    @FXML private TextField txtDetail04;
    @FXML private TextField txtDetail07;
    @FXML private TextField txtDetail06;
    @FXML private TableView table; 
    @FXML private TextField txtField06;
    @FXML private Button btnNew;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;
    @FXML private Button btnClose;
    @FXML private Button btnSearch;
    @FXML private Button btnDel;
    @FXML private Button btnBrowse;
    @FXML private Button btnConfirm;
    @FXML private ImageView imgTranStat;
    @FXML private TextArea txtDetail10;
    @FXML private TextField txtField50;
    @FXML private TextField txtField51;
    @FXML private Button btnPrint;
    @FXML private TextField txtOther02;
    @FXML private TextField txtDetail08;
    @FXML private TableView tableDetail;
    @FXML private AnchorPane dataPane;
    
    TableColumn index01 = new TableColumn("No.");
    TableColumn index02 = new TableColumn("Expiration");
    TableColumn index03 = new TableColumn("OnHnd");
    TableColumn index04 = new TableColumn("Out");
    TableColumn index05 = new TableColumn("Rem");
    
    private final String pxeModuleName = "InvTransferController";
    protected Date pdExpiryDt = null;
    protected Boolean pbEdited = false;
    private static GRider poGRider;
    private InvTransfer poTrans;
    private int pnEditMode = -1;
    private boolean pbLoaded = false;
    private boolean pbBrowse = false;
    private final String pxeDateFormat = "yyyy-MM-dd";
    private final String pxeDateDefault = java.time.LocalDate.now().toString();
    
    private TableModel model;
    private ObservableList<TableModel> data = FXCollections.observableArrayList();
    
    private int pnIndex = -1;
    private int pnRow = -1;
    private int pnOldRow = -1;
    
    private String psDestina = "";
    private String psTrukNme = "";
    private String psOrderNm = "";
    private String psTransNox = "";
    private String psOldRec = "";
    private String psOrderNox = "";
    private boolean pbFound;
    private int pnlRow=0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        poTrans = new InvTransfer(poGRider, poGRider.getBranchCode(), false);
        poTrans.setCallBack(poCallBack);
        
        btnCancel.setOnAction(this::cmdButton_Click);
        btnSearch.setOnAction(this::cmdButton_Click);
        btnSave.setOnAction(this::cmdButton_Click);
        btnDel.setOnAction(this::cmdButton_Click);
        btnNew.setOnAction(this::cmdButton_Click);
        btnPrint.setOnAction(this::cmdButton_Click);
        btnClose.setOnAction(this::cmdButton_Click);
        btnExit.setOnAction(this::cmdButton_Click);
        btnBrowse.setOnAction(this::cmdButton_Click);
        btnConfirm.setOnAction(this::cmdButton_Click);
        
        txtField01.focusedProperty().addListener(txtField_Focus);
        txtField03.focusedProperty().addListener(txtField_Focus);
        txtField04.focusedProperty().addListener(txtField_Focus);
        txtField05.focusedProperty().addListener(txtArea_Focus);
        txtField06.focusedProperty().addListener(txtField_Focus);
        txtField07.focusedProperty().addListener(txtField_Focus);
        txtField13.focusedProperty().addListener(txtField_Focus);
        txtField18.focusedProperty().addListener(txtField_Focus);
        txtField50.focusedProperty().addListener(txtField_Focus);
        txtField51.focusedProperty().addListener(txtField_Focus);
        
        txtDetail03.focusedProperty().addListener(txtDetail_Focus);
        txtDetail04.focusedProperty().addListener(txtDetail_Focus);
        txtDetail05.focusedProperty().addListener(txtDetail_Focus);
        txtDetail06.focusedProperty().addListener(txtDetail_Focus);
        txtDetail07.focusedProperty().addListener(txtDetail_Focus);
        txtDetail08.focusedProperty().addListener(txtDetail_Focus);
        txtDetail10.focusedProperty().addListener(txtDetailArea_Focus);
        txtDetail80.focusedProperty().addListener(txtDetail_Focus);
                
        /*Add keypress event for field with search*/
        txtField01.setOnKeyPressed(this::txtField_KeyPressed);
        txtField03.setOnKeyPressed(this::txtField_KeyPressed);
        txtField04.setOnKeyPressed(this::txtField_KeyPressed);
        txtField06.setOnKeyPressed(this::txtField_KeyPressed);
        txtField07.setOnKeyPressed(this::txtField_KeyPressed);
        txtField13.setOnKeyPressed(this::txtField_KeyPressed);
        txtField18.setOnKeyPressed(this::txtField_KeyPressed);
        txtField05.setOnKeyPressed(this::txtFieldArea_KeyPressed);
        txtField50.setOnKeyPressed(this::txtField_KeyPressed);
        txtField51.setOnKeyPressed(this::txtField_KeyPressed);
        
        txtDetail03.setOnKeyPressed(this::txtDetail_KeyPressed);
        txtDetail04.setOnKeyPressed(this::txtDetail_KeyPressed);
        txtDetail05.setOnKeyPressed(this::txtDetail_KeyPressed);
        txtDetail06.setOnKeyPressed(this::txtDetail_KeyPressed);
        txtDetail07.setOnKeyPressed(this::txtDetail_KeyPressed);
        txtDetail08.setOnKeyPressed(this::txtDetail_KeyPressed);
        txtDetail10.setOnKeyPressed(this::txtDetailArea_KeyPressed);
        txtDetail80.setOnKeyPressed(this::txtDetail_KeyPressed);
        
        pnEditMode = EditMode.UNKNOWN;
        clearFields();
        initGrid();
        initButton(pnEditMode);
        initLisView();
        
        pbLoaded = true;
    }
    
    public void setGRider(GRider foGRider){
        this.poGRider = foGRider;
    }
    
    private void initButton(int fnValue){
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
        
        btnCancel.setVisible(lbShow);
        btnSearch.setVisible(lbShow);
        btnSave.setVisible(lbShow);
        btnDel.setVisible(lbShow);
        lblHeader.setVisible(lbShow);
        
        txtField50.setDisable(lbShow);
        txtField51.setDisable(lbShow);
                
        btnBrowse.setVisible(!lbShow);
        btnNew.setVisible(!lbShow);
        btnPrint.setVisible(!lbShow);
        btnClose.setVisible(!lbShow);
        btnConfirm.setVisible(!lbShow);
        
        txtField01.setDisable(!lbShow);
        txtField03.setDisable(!lbShow);
        txtField04.setDisable(!lbShow);
        txtField05.setDisable(!lbShow);
        //txtField06.setDisable(!lbShow);
        txtField07.setDisable(!lbShow);
        txtField13.setDisable(!lbShow);
        //txtField18.setDisable(!lbShow);
        
        txtDetail03.setDisable(!lbShow);
        txtDetail04.setDisable(!lbShow);
        txtDetail05.setDisable(!lbShow);
        txtDetail06.setDisable(!lbShow);
        txtDetail10.setDisable(!lbShow);
        txtDetail80.setDisable(!lbShow);
        
        if (lbShow)
            txtField03.requestFocus();
        else
            txtField51.requestFocus();
    }
    
    private void clearFields(){
        txtField01.setText("");
        txtField03.setText("");
        txtField04.setText("");
        txtField05.setText("");
        txtField06.setText("");
        txtField07.setText("0.00");
        txtField13.setText("0.00");
        txtField50.setText("");
        txtField51.setText("");
        txtOther02.setText("0");        
        
        pbFound = false;
        txtDetail03.setText("");
        txtDetail04.setText("");
        txtDetail05.setText("");
        txtDetail07.setText("0.00");
        txtDetail06.setText("0");
        txtDetail80.setText("");
        Label12.setText("0.00");
        
        pnlRow = 0;
        pnRow = -1;
        pnOldRow = -1;
        pnIndex = 51;
        setTranStat("-1");
        
        psOldRec = "";
        psDestina = "";
        psTrukNme = "";
        psOrderNm = "";
        psOrderNox = "";
        psTransNox = "";
        pbEdited = false;
        pbBrowse =false;
        
        tableDetail.setItems(loadEmptyData());
        data.clear();
    }
    
    private void initGrid(){
        TableColumn index01 = new TableColumn("No.");
        TableColumn index02 = new TableColumn("Order No.");
        TableColumn index03 = new TableColumn("Barcode");
        TableColumn index04 = new TableColumn("Description");
        TableColumn index05 = new TableColumn("Unit Price");
        TableColumn index06 = new TableColumn("Unit");
        TableColumn index07 = new TableColumn("Qty");
        
        index01.setPrefWidth(30); index01.setStyle("-fx-alignment: CENTER;");
        index02.setPrefWidth(70);
        index03.setPrefWidth(90);
        index04.setPrefWidth(140);
        index05.setPrefWidth(65); index05.setStyle("-fx-alignment: CENTER;");
        index06.setPrefWidth(65); index06.setStyle("-fx-alignment: CENTER;");
        index07.setPrefWidth(40); index06.setStyle("-fx-alignment: CENTER;");
        
        index01.setSortable(false); index01.setResizable(false);
        index02.setSortable(false); index02.setResizable(false);
        index03.setSortable(false); index03.setResizable(false);
        index04.setSortable(false); index04.setResizable(false);
        index05.setSortable(false); index05.setResizable(false);
        index06.setSortable(false); index06.setResizable(false);
        index07.setSortable(false); index07.setResizable(false);

        table.getColumns().clear();        
        table.getColumns().add(index01);
        table.getColumns().add(index02);
        table.getColumns().add(index03);
        table.getColumns().add(index04);
        table.getColumns().add(index05);
        table.getColumns().add(index06);
        table.getColumns().add(index07);
        
        index01.setCellValueFactory(new PropertyValueFactory<org.rmj.cas.food.inventory.fx.views.TableModel,String>("index01"));
        index02.setCellValueFactory(new PropertyValueFactory<org.rmj.cas.food.inventory.fx.views.TableModel,String>("index02"));
        index03.setCellValueFactory(new PropertyValueFactory<org.rmj.cas.food.inventory.fx.views.TableModel,String>("index03"));
        index04.setCellValueFactory(new PropertyValueFactory<org.rmj.cas.food.inventory.fx.views.TableModel,String>("index04"));
        index05.setCellValueFactory(new PropertyValueFactory<org.rmj.cas.food.inventory.fx.views.TableModel,String>("index05"));
        index06.setCellValueFactory(new PropertyValueFactory<org.rmj.cas.food.inventory.fx.views.TableModel,String>("index06"));
        index07.setCellValueFactory(new PropertyValueFactory<org.rmj.cas.food.inventory.fx.views.TableModel,String>("index07"));
        
        /*making column's position uninterchangebale*/
        table.widthProperty().addListener(new ChangeListener<Number>() {  
            public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth)
            {
                TableHeaderRow header = (TableHeaderRow) table.lookup("TableHeaderRow");
                header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        header.setReordering(false);
                            }
                        });
                    }
                });
        
        /*Set data source to table*/
        table.setItems(data);
    }
    
    
    private void initLisView(){
        
        index01.setPrefWidth(30); index01.setStyle("-fx-alignment: CENTER;");
        index02.setPrefWidth(90); index02.setStyle("-fx-alignment: CENTER;");
        index03.setPrefWidth(65); index03.setStyle("-fx-alignment: CENTER;");
        index04.setPrefWidth(65); index04.setStyle("-fx-alignment: CENTER;");
        index05.setPrefWidth(65); index05.setStyle("-fx-alignment: CENTER;");
        
        index01.setSortable(false); index01.setResizable(false);
        index02.setSortable(true); index02.setResizable(false);
        index03.setSortable(false); index03.setResizable(false);
        index04.setSortable(false); index04.setResizable(false);
        index05.setSortable(false); index05.setResizable(false);

        tableDetail.getColumns().clear();
        tableDetail.getColumns().add(index01);
        tableDetail.getColumns().add(index02);
        tableDetail.getColumns().add(index03);
        tableDetail.getColumns().add(index04);
        tableDetail.getColumns().add(index05);
        
        index01.setCellValueFactory(new PropertyValueFactory<org.rmj.cas.food.inventory.fx.views.TableModel,String>("index01"));
        index02.setCellValueFactory(new PropertyValueFactory<org.rmj.cas.food.inventory.fx.views.TableModel,String>("index02"));
        index03.setCellValueFactory(new PropertyValueFactory<org.rmj.cas.food.inventory.fx.views.TableModel,String>("index03"));
        index04.setCellValueFactory(new PropertyValueFactory<org.rmj.cas.food.inventory.fx.views.TableModel,String>("index04"));
        index05.setCellValueFactory(new PropertyValueFactory<org.rmj.cas.food.inventory.fx.views.TableModel,String>("index05"));
       
    }
     
    private void unloadForm(){
//        VBox myBox = (VBox) VBoxForm.getParent();
//        myBox.getChildren().clear();
        dataPane.getChildren().clear();
        dataPane.setStyle("-fx-border-color: transparent");
    }
    
    private void txtFieldArea_KeyPressed(KeyEvent event){
        if (event.getCode() == ENTER || event.getCode() == DOWN){ 
            event.consume();
            CommonUtils.SetNextFocus((TextArea)event.getSource());
        }else if (event.getCode() ==KeyCode.UP){
        event.consume();
            CommonUtils.SetPreviousFocus((TextArea)event.getSource());
        }
    }
    
     private void txtDetailArea_KeyPressed(KeyEvent event){
        if (event.getCode() == ENTER || event.getCode() == KeyCode.DOWN){
            event.consume();
            CommonUtils.SetNextFocus((TextArea)event.getSource());
        }else if (event.getCode() ==KeyCode.UP){
        event.consume();
            CommonUtils.SetPreviousFocus((TextArea)event.getSource());
        }
    }
    
     private void txtDetail_KeyPressed(KeyEvent event){
        TextField txtDetail = (TextField) event.getSource();
        int lnIndex = Integer.parseInt(txtDetail.getId().substring(9, 11));
        String lsValue = txtDetail.getText() + "%";
        
        if (event.getCode() == F3){
            switch (lnIndex){
                case 3: /*Barcode Search*/                   
                    if (poTrans.SearchDetail(pnRow, 3, lsValue, false, false)){
                        txtDetail03.setText(poTrans.getDetailOthers(pnRow, "sBarCodex").toString());
                        txtDetail80.setText(poTrans.getDetailOthers(pnRow, "sDescript").toString());
                        txtDetail06.setText(poTrans.getDetail(pnRow, "nQuantity").toString());
                        txtDetail07.setText(poTrans.getDetail(pnRow, "nInvCostx").toString());
                        txtDetail08.setText(CommonUtils.xsDateMedium((Date) poTrans.getDetail(pnRow, "dExpiryDt")));
                        txtOther02.setText(poTrans.getDetailOthers(pnRow, "nQtyOnHnd").toString());
                    } else {
                        txtDetail03.setText("");
                        txtDetail80.setText("");
                        txtDetail06.setText("");
                        txtDetail07.setText("");
                        txtDetail08.setText("");
                        txtOther02.setText("0");
                    }
                    break;
                case 80: /*Description Search*/
                    if (poTrans.SearchDetail(pnRow, 3, lsValue, true, false)){
                        txtDetail03.setText(poTrans.getDetailOthers(pnRow, "sBarCodex").toString());
                        txtDetail80.setText(poTrans.getDetailOthers(pnRow, "sDescript").toString());
                        txtDetail06.setText(poTrans.getDetail(pnRow, "nQuantity").toString());
                        txtDetail07.setText(poTrans.getDetail(pnRow, "nInvCostx").toString());
                        txtOther02.setText(poTrans.getDetailOthers(pnRow, "nQtyOnHnd").toString());
                        txtDetail08.setText(CommonUtils.xsDateMedium((Date) poTrans.getDetail(pnRow, "dExpiryDt")));
                    } else {
                        txtDetail03.setText("");
                        txtDetail80.setText("");
                        txtDetail06.setText("");
                        txtDetail07.setText("");
                        txtOther02.setText("0");
                        txtDetail08.setText("");
                    }
                    
                    break;
                case 4:
                    if (poTrans.SearchDetail(pnRow, 4, lsValue, false, false)){
                        txtDetail.setText(poTrans.getDetailOthers(pnRow, "sOrigCode").toString());
                        loadDetail();
                    }
                    else 
                        txtDetail.setText("");
                    
                    break;
                case 5:
                    break;
            }
        }
        
        switch (event.getCode()){
        case ENTER:
        case DOWN:
            CommonUtils.SetNextFocus(txtDetail);
            break;
        case UP:
            CommonUtils.SetPreviousFocus(txtDetail);
        }
    }
    
    private void txtField_KeyPressed(KeyEvent event){
        TextField txtField = (TextField)event.getSource();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        String lsValue = txtField.getText() + "%";
            if (event.getCode() == ENTER || event.getCode() == F3){
                switch (lnIndex){
                    case 4: /*sDestinat*/
                        if (poTrans.SearchMaster(lnIndex, txtField.getText(), false)){
                            CommonUtils.SetNextFocus(txtField); 
                        }else txtField.setText("");
                        break;
                    case 6: /*sTruckIDx*/
                        if (poTrans.SearchMaster(lnIndex, txtField.getText(), false)){
                            CommonUtils.SetNextFocus(txtField); 
                        }else txtField.setText("");
                        break;
                    case 18: /*sOrderNox */
                        if (poTrans.SearchMaster(lnIndex, txtField.getText(), false)){
                            CommonUtils.SetNextFocus(txtField); 
                        }else txtField.setText("");
                        break;
                    case 50: /*sTransNox*/
                        if(poTrans.BrowseRecord(lsValue, true)==true){
                            loadRecord();
                            pbBrowse = true;
                            pnEditMode = poTrans.getEditMode();
                        } else {
                            clearFields();
                            pnEditMode = EditMode.UNKNOWN;
                        }
                        return;
                    case 51: /*psDestina*/
                        if(poTrans.BrowseRecord(lsValue, false)== true){
                            loadRecord(); 
                            pbBrowse = true;
                            pnEditMode = poTrans.getEditMode();
                        } else {
                            clearFields();
                            pnEditMode = EditMode.UNKNOWN;
                        }
                        return;
                    }
                }

        switch (event.getCode()){
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
            case "btnNew":
                if (poTrans.newTransaction()){  
                    clearFields();
                    loadRecord();
                    txtField50.setText("");
                    pnEditMode = poTrans.getEditMode();
                }  
                break;
            case "btnPrint": 
                if (!psOldRec.equals("")){
                    if(!poTrans.getMaster("cTranStat").equals(TransactionStatus.STATE_OPEN)){
                    ShowMessageFX.Warning("Trasaction may be CANCELLED/POSTED.", pxeModuleName, "Can't update processed transactions!!!");
                    return;
                    }
                    
                    if( ShowMessageFX.YesNo(null, pxeModuleName, "Do you want to print this transasction?")== true){
                        if (!printTransfer()) return;
                        
                        if (poTrans.closeTransaction(psOldRec)){
                            //ShowMessageFX.Information(null, pxeModuleName, "Transaction PRINTED successfully.");
                            clearFields();
                            initGrid();
                            pnEditMode = EditMode.UNKNOWN;
                            initButton(pnEditMode);
                        }
                    }
                    
                } else ShowMessageFX.Warning(null, pxeModuleName, "Please select a record to print!");
                break;
             
            case "btnClose":
            case "btnExit":
                unloadForm();
                return;
                
            case "btnCancel": 
                if(ShowMessageFX.OkayCancel(null, pxeModuleName, "Do you want to disregard changes?") == true){
                    clearFields();
                    pnEditMode = EditMode.UNKNOWN;
                    break;
                } else
                    return;
                
            case "btnSearch":return;
            case "btnSave": 
                if (poTrans.saveTransaction()){
                    ShowMessageFX.Information(null, pxeModuleName, "Transaction saved successfuly.");
                    clearFields();
                    initGrid();
                    pnEditMode = EditMode.UNKNOWN;
                    initButton(pnEditMode);
                    break;
                } else{
                    if (!poTrans.getErrMsg().equals(""))
                        ShowMessageFX.Error(poTrans.getErrMsg(), pxeModuleName, "Please inform MIS Department.");
                    else
                        ShowMessageFX.Warning(poTrans.getMessage(), pxeModuleName, "Please verify your entry.");
                    return;
                } 
            
            case "btnConfirm":
                if (!psOldRec.equals("")){
                    if(poTrans.getMaster("cTranStat").equals(TransactionStatus.STATE_CANCELLED) ||
                        poTrans.getMaster("cTranStat").equals(TransactionStatus.STATE_POSTED) ||
                        poTrans.getMaster("cTranStat").equals(TransactionStatus.STATE_VOID)){
                        ShowMessageFX.Warning("Trasaction may be CANCELLED/POSTED.", pxeModuleName, "Can't update processed transactions!!!");
                        return;
                    }
                    if( ShowMessageFX.YesNo(null, pxeModuleName, "Do you want to confirm this transasction?")== true){
                        if (poTrans.closeTransaction(psOldRec)){
                        ShowMessageFX.Information(null, pxeModuleName, "Transaction CONFIRMED successfully.");
                        clearFields();
                        initGrid();
                        pnEditMode = EditMode.UNKNOWN;
                        initButton(pnEditMode);
                        }
                    }
                } else ShowMessageFX.Warning(null, pxeModuleName, "Please select a record to confirm!");
                
                break;
            case "btnBrowse":
                switch(pnIndex){
                    case 50: /*sTransNox*/
                        if(poTrans.BrowseRecord(txtField50.getText(), true)==true){
                            loadRecord();
                            pbBrowse = true;
                            pnEditMode = poTrans.getEditMode();
                            break;
                        }else{
                            clearFields();
                            pnEditMode = EditMode.UNKNOWN;
                        }
                        return;    
                    case 51: /*sDestination*/
                        if(poTrans.BrowseRecord(txtField51.getText() + "%", false)== true){
                            loadRecord();
                            pbBrowse = true;
                            pnEditMode = poTrans.getEditMode();
                        }
                    default:
                        txtField51.requestFocus();
                }
                return;
            case "btnDel":  
               int lnIndex = table.getSelectionModel().getFocusedIndex();    
                if(table.getSelectionModel().getSelectedItem() == null){
                     ShowMessageFX.Warning(null, pxeModuleName, "Please select item to remove!");
                     break;
                }
                if(ShowMessageFX.OkayCancel(null, pxeModuleName, "Do you want to remove this item?") == true){
                    poTrans.deleteDetail(lnIndex);
                    loadDetail();
                }     
                break;
            default:
                ShowMessageFX.Warning(null, pxeModuleName, "Button with name " + lsButton + " not registered.");
                return;
        }
        
        initButton(pnEditMode);
    }
    
    private void loadRecord(){
        txtField01.setText((String) poTrans.getMaster("sTransNox"));
        txtField50.setText((String) poTrans.getMaster("sTransNox"));
        psTransNox = txtField50.getText();
        
        XMBranch loBranch = poTrans.GetBranch((String)poTrans.getMaster(4), true);
        if (loBranch != null) {
            txtField04.setText((String) loBranch.getMaster("sBranchNm"));
            txtField51.setText((String) loBranch.getMaster("sBranchNm"));
        }
        
        //TODO:
        // Order No. and Truck
        txtField18.setText("");
        txtField06.setText("");
        
        txtField03.setText(CommonUtils.xsDateMedium((Date) poTrans.getMaster("dTransact")));
        psDestina = txtField51.getText();
        txtField05.setText((String) poTrans.getMaster("sRemarksx"));
        
        txtField07.setText(CommonUtils.NumberFormat(Double.valueOf(poTrans.getMaster("nFreightx").toString()), "0.00"));
        txtField13.setText(CommonUtils.NumberFormat(Double.valueOf(poTrans.getMaster("nDiscount").toString()), "0.00"));
        
        Label12.setText(CommonUtils.NumberFormat(Double.valueOf(poTrans.getMaster("nTranTotl").toString()), "#,##0.00"));
        
        pnRow = 0;
        pnOldRow = 0;
        loadDetail();
        setTranStat((String) poTrans.getMaster("cTranStat"));
        tableDetail.setItems(loadEmptyData());
        psOldRec = txtField01.getText();
    }
    
    private void setTranStat(String fsValue){
        switch (fsValue){
            case "0":
                imgTranStat.setImage(new Image("org/rmj/cas/food/inventory/fx/images/open.png")); break;
            case "1":
                imgTranStat.setImage(new Image("org/rmj/cas/food/inventory/fx/images/closed.png")); break;
            case "2":
                imgTranStat.setImage(new Image("org/rmj/cas/food/inventory/fx/images/posted.png")); break;
            case "3":
                imgTranStat.setImage(new Image("org/rmj/cas/food/inventory/fx/images/cancelled.png")); break;
            case "4":
                imgTranStat.setImage(new Image("org/rmj/cas/food/inventory/fx/images/void.png")); break;
            default:
                imgTranStat.setImage(new Image("org/rmj/cas/food/inventory/fx/images/unknown.png"));
        }    
    }
    
    private ObservableList loadInitData(int fnRow){
        ObservableList dataDetail = FXCollections.observableArrayList();
        ResultSet loRS = null;
        
        //mac 2023.03.24
        //error ito nung binigay, magpass nalang ako ng empty string para sa 2nd parameter
        //loRS = poTrans.getExpiration((String)poTrans.getDetail(fnRow, "sStockIDx"));
        loRS = poTrans.getExpiration((String)poTrans.getDetail(fnRow, "sStockIDx"), "");
        boolean lbGetExpiry =false;
        int rowCount = 0;
        pbEdited = true;
        try {
                dataDetail.clear();
                if(MiscUtil.RecordCount(loRS)==0){
                    dataDetail.add(new TableModel(String.valueOf(rowCount +1),
                        String.valueOf(CommonUtils.xsDateMedium(loRS.getDate("dExpiryDt"))),
                        String.valueOf(loRS.getInt("nQtyOnHnd")),
                        String.valueOf((Integer) poTrans.getDetail(fnRow, "nQuantity")),
                        String.valueOf(loRS.getInt("nQtyOnHnd") -(Integer) poTrans.getDetail(pnRow, "nQuantity")),
                        "",
                        "",
                        "",
                        "",
                        ""     
                    ));
                    poTrans.setDetail(fnRow, "dExpiryDt", loRS.getDate("dExpiryDt"));
                    pdExpiryDt = (Date) poTrans.getDetail(fnRow, "dExpiryDt");
                }else{
                    int lnQtyOut = (Integer) poTrans.getDetail(fnRow, "nQuantity");
                    loRS.first();
                    for (int lnRow = 0; lnRow <= MiscUtil.RecordCount(loRS) - 1; lnRow ++){
                        if(!lbGetExpiry){
                            poTrans.setDetail(fnRow, "dExpiryDt", loRS.getDate("dExpiryDt"));
                            pdExpiryDt = (Date) poTrans.getDetail(fnRow, "dExpiryDt");
                            lbGetExpiry = true;
                        }
                        if(lnQtyOut<=loRS.getInt("nQtyOnHnd")){
                           dataDetail.add(new TableModel(String.valueOf(rowCount +1),
                                        String.valueOf(CommonUtils.xsDateMedium(loRS.getDate("dExpiryDt"))),
                                        String.valueOf(loRS.getInt("nQtyOnHnd")),
                                        String.valueOf(lnQtyOut),
                                        String.valueOf(loRS.getInt("nQtyOnHnd") -lnQtyOut),
                                        "",
                                        "",
                                        "",
                                        "",
                                        ""     
                                    ));
                            lnQtyOut =  0;
                        }else{
                            dataDetail.add(new TableModel(String.valueOf(rowCount +1),
                                        String.valueOf(CommonUtils.xsDateMedium(loRS.getDate("dExpiryDt"))),
                                        String.valueOf(loRS.getInt("nQtyOnHnd")),
                                        String.valueOf(loRS.getInt("nQtyOnHnd")),
                                        String.valueOf(loRS.getInt("nQtyOnHnd")-loRS.getInt("nQtyOnHnd")),
                                        "",
                                        "",
                                        "",
                                        "",
                                        ""     
                                    ));

                            lnQtyOut =  lnQtyOut - loRS.getInt("nQtyOnHnd");
                        }
                        rowCount++;
                        loRS.next();
                    }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return dataDetail;
    }
    
    private ObservableList loadDetailData(int fnRow){
        ObservableList dataDetail = FXCollections.observableArrayList();
        ResultSet loRS = null;
        //mac 2023.03.24
        //error ito nung binigay, magpass nalang ako ng empty string para sa 2nd parameter
        //loRS = poTrans.getExpiration((String)poTrans.getDetail(fnRow, "sStockIDx"));
        loRS = poTrans.getExpiration((String)poTrans.getDetail(fnRow, "sStockIDx"), "");
        int lnQuantity = 0;
        pnlRow = 0;
        pbFound = false;
        
        try {
                dataDetail.clear();
                loRS.first();
                    for( int rowCount = 0; rowCount <= MiscUtil.RecordCount(loRS) -1; rowCount++){
                        if (CommonUtils.xsDateShort(loRS.getDate("dExpiryDt")).equals(CommonUtils.xsDateShort((Date) poTrans.getDetail(fnRow, "dExpiryDt")))){
                            if(!pbFound) pbFound = true;
                            lnQuantity = (int)poTrans.getDetail(fnRow, "nQuantity");
                        }else{
                            lnQuantity = 0;
                        }
                    dataDetail.add(new TableModel(String.valueOf(rowCount +1),
                        String.valueOf(CommonUtils.xsDateMedium(loRS.getDate("dExpiryDt"))),
                        String.valueOf(loRS.getInt("nQtyOnHnd")),
                        String.valueOf(lnQuantity),
                        String.valueOf((int)loRS.getInt("nQtyOnHnd") - (int) lnQuantity),
                        "",
                        "",
                        "",
                        "",
                        ""     
                    ));
                    pnlRow++;
                    loRS.next();
                }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return dataDetail;
    }
    
    private void loadDetail(){
        int lnCtr;
        int lnRow = poTrans.ItemCount();
        
        data.clear();
        /*ADD THE DETAIL*/
        for(lnCtr = 0; lnCtr <= lnRow -1; lnCtr++){
            data.add(new TableModel(String.valueOf(lnCtr + 1), 
                        (String) poTrans.getDetail(lnCtr, "sOrderNox"),
                        (String) poTrans.getDetailOthers(lnCtr, "sBarCodex"), 
                        (String) poTrans.getDetailOthers(lnCtr, "sDescript"),
                        CommonUtils.NumberFormat(Double.valueOf(poTrans.getDetail(lnCtr, "nInvCostx").toString()), "0.00"),
                        String.valueOf(poTrans.getDetailOthers(lnCtr, "sMeasurNm")),
                        String.valueOf(poTrans.getDetail(lnCtr, "nQuantity")),
                        "",
                        "",
                        ""));
        }
    
        /*FOCUS ON FIRST ROW*/
        if (!data.isEmpty()){
            table.getSelectionModel().select(lnRow -1);
            table.getFocusModel().focus(lnRow -1);
            
            pnRow = table.getSelectionModel().getSelectedIndex();           
            
            setDetailInfo(pnRow);
        }
        Label12.setText(CommonUtils.NumberFormat(Double.valueOf(poTrans.getMaster("nTranTotl").toString()), "#,##0.00"));
    }
    
    private void setDetailInfo(int fnRow){
        if (fnRow >= 0){
            txtDetail05.setText(String.valueOf(poTrans.getDetail(fnRow, "sOrderNox")));
            txtDetail03.setText((String) poTrans.getDetailOthers(fnRow, "sBarCodex"));
            txtDetail80.setText((String) poTrans.getDetailOthers(fnRow, "sDescript"));
            txtDetail04.setText((String) poTrans.getDetailOthers(fnRow, "sOrigCode"));
            txtDetail07.setText(CommonUtils.NumberFormat(Double.valueOf(poTrans.getDetail(fnRow, "nInvCostx").toString()), "0.00"));
            txtDetail08.setText(CommonUtils.xsDateMedium((Date) poTrans.getDetail(fnRow, "dExpiryDt")));
            txtDetail06.setText(String.valueOf(poTrans.getDetail(fnRow, "nQuantity")));
            txtDetail10.setText(String.valueOf(poTrans.getDetail(fnRow, "sNotesxxx")));
            txtOther02.setText(String.valueOf(poTrans.getDetailOthers(fnRow, "nQtyOnHnd")));
            if (pbBrowse == true) pdExpiryDt = poGRider.getServerDate();
        } else{
            txtDetail03.setText("");
            txtDetail04.setText("");
            txtDetail05.setText("");
            txtDetail06.setText("0");
            txtDetail07.setText("0.00");
            txtDetail08.setText("");
            txtDetail10.setText("");
            txtDetail80.setText("");
            txtOther02.setText("0");
        }
    }
    
    final ChangeListener<? super Boolean> txtField_Focus = (o,ov,nv)->{
        if (!pbLoaded) return;
        
        TextField txtField = (TextField)((ReadOnlyBooleanPropertyBase)o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        String lsValue = txtField.getText();
        
        if (lsValue == null) return;
            
        if(!nv){ /*Lost Focus*/      
            switch (lnIndex){
                case 1: /*sTransNox*/
                    break;
                case 3: /*dTransact*/
                  if (CommonUtils.isDate(txtField.getText(), pxeDateFormat)){
                        poTrans.setMaster("dTransact", CommonUtils.toDate(txtField.getText()));
                    } else{
                        ShowMessageFX.Warning("Invalid date entry.", pxeModuleName, "Date format must be yyyy-MM-dd (e.g. 1991-07-07)");
                        poTrans.setMaster(lnIndex, CommonUtils.toDate(pxeDateDefault));
                    }
                    return;
                case 4:/*sDestinat*/
                case 6:/*sTruckIDx*/
                case 18:/*sOrderNox*/
                    break;
                case 7:/*nFreightx*/
                     double x = 0.00;
                    try {
                        /*this must be numeric*/
                        x = Double.parseDouble(lsValue);
                    } catch (Exception e) {
                        x = 0.00;
                        txtField.setText("0.00");
                    }
                    poTrans.setMaster("nFreightx",  Double.parseDouble(txtField.getText()));
                    break;
                case 13:/*nDiscount*/
                    double y = 0.00;
                    try {
                        /*this must be numeric*/
                        y = Double.parseDouble(lsValue);
                    } catch (Exception e) {
                        y = 0.00;
                        txtField.setText("0.00");
                    }
                    if (y > 1 ){
                        poTrans.setMaster("nDiscount", 0);
                    } else{
                        poTrans.setMaster("nDiscount", (y > 0 ? y : 0));
                    }
                    break;                   
                case 50:
                    if(lsValue.equals("") || lsValue.equals("%"))
                        txtField.setText("");
                    break; 
                case 51: 
                    if(lsValue.equals("") || lsValue.equals("%"))
                        txtField.setText("");
                    break;
                default:
                    ShowMessageFX.Warning(null, pxeModuleName, "Text field with name " + txtField.getId() + " not registered.");
                    return;
            }
            pnIndex = lnIndex;
        } else{
            switch (lnIndex){
                case 3: /*dTransact*/
                    try{
                        txtField.setText(CommonUtils.xsDateShort(lsValue));
                    }catch(ParseException e){
                        ShowMessageFX.Error(e.getMessage(), pxeModuleName, null);
                    }
                    txtField.selectAll();
                    break;
                default:
            }
            pnIndex = lnIndex;
            txtField.selectAll();
        }
    };
    
    final ChangeListener<? super Boolean> txtDetail_Focus = (o,ov,nv)->{
        if (!pbLoaded) return;
        
        TextField txtDetail = (TextField)((ReadOnlyBooleanPropertyBase)o).getBean();
        int lnIndex = Integer.parseInt(txtDetail.getId().substring(9, 11));
        String lsValue = txtDetail.getText();
        
        if (pnRow < 0) return;
//        if (lsValue == null) return;
        
        if(!nv){ /*Lost Focus*/     
            switch (lnIndex){
                case 3: /*Barcode Search*/
                case 80:/*sDescript Search*/
                case 4:/*sOrigIDxx*/
                case 5:/*sOrderNox Search*/
                    break;
                case 6:/*nQuantity*/
                    int x = 0;
                    try {
                        /*this must be numeric*/
                        x = Integer.parseInt(lsValue);
                    } catch (NumberFormatException e) {
                        x = 0;
                        txtDetail.setText("0");
                    }
                    poTrans.setDetail(pnRow, "nQuantity",  x);
                    break;
                case 7:/*nInvCostx*/
                    double y = 0.00;
                    try {
                        /*this must be numeric*/
                        y = Double.parseDouble(lsValue);
                    } catch (NumberFormatException e) {
                        y = 0.00;
                    }
                    poTrans.setDetail(pnRow,"nInvCostx", y);
                    break;
                case 8: /*dExpiryDt*/
                    if (CommonUtils.isDate(txtDetail.getText(), pxeDateFormat)){
                        poTrans.setDetail(pnRow, "dExpiryDt", CommonUtils.toDate(txtDetail.getText()));
                    }else{
                        ShowMessageFX.Warning("Invalid date entry.", pxeModuleName, "Date format must be yyyy-MM-dd (e.g. 1991-07-07)");
                        poTrans.setDetail(pnRow, "dExpiryDt",CommonUtils.toDate(pxeDateDefault));
                    }
                    txtDetail.setText(CommonUtils.xsDateLong((Date)poTrans.getDetail(pnRow, "dExpiryDt")));
                    return;
            }
            pnOldRow = table.getSelectionModel().getSelectedIndex();
            pnIndex= lnIndex;
        } else{
            switch (lnIndex){
                case 8: /*dExpiryDt*/
                    try{
                        txtDetail.setText(CommonUtils.xsDateShort(lsValue));
                    }catch(ParseException e){
                        ShowMessageFX.Error(e.getMessage(), pxeModuleName, null);
                    }
                    txtDetail.selectAll();
                    break;
                default:
            }
            pnIndex = -1;
            txtDetail.selectAll();
        }
    };
    
    final ChangeListener<? super Boolean> txtDetailArea_Focus = (o,ov,nv)->{
        if (!pbLoaded) return;
        
        TextArea txtDetail = (TextArea)((ReadOnlyBooleanPropertyBase)o).getBean();
        int lnIndex = Integer.parseInt(txtDetail.getId().substring(9, 11));
        String lsValue = txtDetail.getText();
        
        if (lsValue == null) return;
        
        if(!nv){ /*Lost Focus*/            
            switch (lnIndex){
                case 10: /*sNotesxxx*/
                    if (lsValue.length() > 256) lsValue = lsValue.substring(0, 256);
                    
                    poTrans.setDetail(pnRow, "sNotesxxx", CommonUtils.TitleCase(lsValue));
            }
        }else{ 
            pnIndex = -1;
            txtDetail.selectAll();
        }
    };
    
    final ChangeListener<? super Boolean> txtArea_Focus = (o,ov,nv)->{
        if (!pbLoaded) return;
        
        TextArea txtField = (TextArea)((ReadOnlyBooleanPropertyBase)o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        String lsValue = txtField.getText();
        
        if (lsValue == null) return;
        
        if(!nv){ /*Lost Focus*/            
            switch (lnIndex){
                case 5: /*sRemarksx*/
                    if (lsValue.length() > 256) lsValue = lsValue.substring(0, 256);
                    
                    poTrans.setMaster("sRemarksx", CommonUtils.TitleCase(lsValue));
                    txtField.setText((String)poTrans.getMaster("sRemarksx"));
                    break;
            }
        }else{ 
            pnIndex = -1;
            txtField.selectAll();
        }
    };

    @FXML
    private void table_Clicked(MouseEvent event) {
        pnRow = table.getSelectionModel().getSelectedIndex();
        if (pnRow < 0) return;
        
        setDetailInfo(pnRow);
        if (poTrans.getDetail(pnRow, "sStockIDx").equals("")){
            tableDetail.setItems(loadEmptyData());
            return;
        }
        
        if(pbEdited ==false){
            tableDetail.setItems(loadInitData(pnRow));
        }else{
            if (!pdExpiryDt.equals((Date) poTrans.getDetail(pnRow, "dExpiryDt"))){
                tableDetail.setItems(loadDetailData(pnRow));
                if(!pbFound){
                    addDetailData(pnlRow);
                }
            }else{
                tableDetail.setItems(loadInitData(pnRow));
            }
        }
        

        txtDetail03.requestFocus();
        txtDetail03.selectAll();
        
    }
    /**
     * author -jovan
     * will accept entry that is not equal to expiration date of inventory
     * @param fnRow -passing the detail
     */
    
    private void addDetailData(int fnRow){
        if (poTrans.getDetail(pnRow, "sStockIDx").equals("")) return;
        
        TableModel newData = new TableModel();
        newData.setIndex01(String.valueOf(fnRow + 1));
        newData.setIndex02(CommonUtils.xsDateMedium((Date) poTrans.getDetail(pnRow, "dExpiryDt")));
        newData.setIndex03("0");
        newData.setIndex04(String.valueOf(poTrans.getDetail(pnRow, "nQuantity")));
        newData.setIndex05("");
        newData.setIndex06("");
        newData.setIndex07("");
        newData.setIndex08("");
        newData.setIndex09("");
        newData.setIndex10("");
        tableDetail.getItems().add(newData);
        
        index02.setSortType( TableColumn.SortType.ASCENDING);
        tableDetail.getSortOrder().add(index02);
        tableDetail.sort();
    }
    
    /**
     * author jovan
     * since 2021-06-23
     * added this function to empty record from the tableDetail table
     */
     private ObservableList loadEmptyData(){
        ObservableList dataDetail = FXCollections.observableArrayList();
        if (pbBrowse == true) pbEdited = true;
            dataDetail.clear();
            dataDetail.add(new TableModel(String.valueOf(1),
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                ""     
            ));        
        return dataDetail;
    }
    
    private boolean printTransfer(){        
        JSONArray json_arr = new JSONArray();
        json_arr.clear();
        
        for(int lnCtr = 0; lnCtr <= poTrans.ItemCount()-1; lnCtr ++){
            JSONObject json_obj = new JSONObject();
            json_obj.put("sField01", (String) poTrans.getDetailOthers(lnCtr, "sBarCodex"));
            json_obj.put("sField02", (String) poTrans.getDetailOthers(lnCtr, "sDescript"));
            json_obj.put("nField01", (double) poTrans.getDetail(lnCtr, "nQuantity"));
            json_obj.put("lField01", Double.valueOf(poTrans.getDetail(lnCtr, "nInvCostx").toString()));
            json_obj.put("sField05", (String) poTrans.getDetailOthers(lnCtr, "sMeasurNm"));
            json_arr.add(json_obj);
        }
        
        String lsSQL = "SELECT sBranchNm FROM Branch WHERE sBranchCD = " + SQLUtil.toSQL((String) poTrans.getMaster("sDestinat"));
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        try {
            if (loRS.next())
                lsSQL = loRS.getString("sBranchNm");
            else
                lsSQL = (String) poTrans.getMaster("sDestinat");
        } catch (SQLException ex) {
            ShowMessageFX.Error(ex.getMessage(), pxeModuleName, "Unable to print...");
            System.exit(1);
        }
        
        //Create the parameter
        Map<String, Object> params = new HashMap<>();
        params.put("sCompnyNm", "Los Pedritos");  
        params.put("sBranchNm", poGRider.getBranchName());
        params.put("sDestinat", lsSQL);
        
        params.put("sTransNox", poTrans.getMaster("sTransNox").toString().substring(1));
        params.put("sReportDt", CommonUtils.xsDateMedium((Date)poTrans.getMaster("dTransact")));
        params.put("sPrintdBy", System.getProperty("user.name"));
                
        try {
            InputStream stream = new ByteArrayInputStream(json_arr.toJSONString().getBytes("UTF-8"));
            JsonDataSource jrjson = new JsonDataSource(stream); 
            
            JasperPrint _jrprint = JasperFillManager.fillReport("d:/GGC_Java_Systems/reports/InvTransferPrint.jasper", params, jrjson);
            JasperViewer jv = new JasperViewer(_jrprint, false);     
            jv.setVisible(true);
        } catch (JRException | UnsupportedEncodingException  ex) {
            Logger.getLogger(InvTransferController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return true;
    }
    
    IMasterDetail poCallBack = new IMasterDetail() {
        @Override
        public void MasterRetreive(int fnIndex) {
            getMaster(fnIndex);
        }

        @Override
        public void DetailRetreive(int fnIndex) {
            switch(fnIndex){
                
                case 10:
                    txtDetail10.setText((String)poTrans.getDetail(pnRow,"sNotesxxx"));
//                    loadDetail();
                    break;
                case 6:
                    txtDetail06.setText(String.valueOf(poTrans.getDetail(pnRow,"nQuantity")));
                    loadDetail();
                    
                    if (!poTrans.getDetail(poTrans.ItemCount()- 1, "sStockIDx").toString().isEmpty() && 
                            (int) poTrans.getDetail(poTrans.ItemCount()- 1, fnIndex) > 0){
                        poTrans.addDetail();
                        pnRow = poTrans.ItemCount()- 1;

                        //set the previous order numeber to the new ones.
                        poTrans.setDetail(pnRow, "sOrderNox", psOrderNox);
                    }                           
                    loadDetail();
                    if (!txtDetail03.getText().isEmpty()){
                        txtDetail07.requestFocus();
                        txtDetail07.selectAll();
                    } else{
                        txtDetail05.requestFocus();
                        txtDetail05.selectAll();
                    }
                    break;
                case 7:
                    txtDetail07.setText(CommonUtils.NumberFormat((Double)poTrans.getDetail(pnRow,"nInvCostx"), "0.00")); 
                    break;
                case 8:
                    txtDetail08.setText(CommonUtils.xsDateLong((Date)poTrans.getDetail(pnRow,"dExpiryDt")));
                    break;
                        
            }
        }
    };
    
    private void getMaster(int fnIndex){
        switch(fnIndex){
            case 3:
                txtField03.setText(CommonUtils.xsDateLong((Date)poTrans.getMaster("dTransact")));
                break;
            case 4:
                XMBranch loBranch = poTrans.GetBranch((String)poTrans.getMaster(fnIndex), true);
                if (loBranch != null) txtField04.setText((String) loBranch.getMaster("sBranchNm"));
                break;    
            case 7:
                txtField07.setText(CommonUtils.NumberFormat((Double)poTrans.getMaster("nFreightx"), "0.00"));
                break;
            case 13:
                txtField13.setText(CommonUtils.NumberFormat(Double.valueOf(poTrans.getMaster("nDiscount").toString()), "0.00"));
                
        }
    }
}
