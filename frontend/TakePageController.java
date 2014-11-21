/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frontend;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author User
 */
public class TakePageController extends AnchorPane implements Initializable
{
    private View instance;
    
    public void setApp(View application){
        this.instance = application;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {    
        
    }  
    
}
