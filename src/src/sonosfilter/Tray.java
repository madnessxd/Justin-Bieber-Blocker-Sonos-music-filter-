package sonosfilter;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Desktop;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import javax.imageio.ImageIO;

public final class Tray implements ActionListener, ItemListener{
    TrayIcon trayIcon;
    Boolean popUps = true;
    CheckboxMenuItem cb1;
    
    public void setPopUps(Boolean b){
        popUps = b;
        cb1.setState(popUps);
    }
    
    Tray(Boolean popUps){
        this.popUps = popUps;
        
        try {
            Image image = ImageIO.read(new URL("http://www.fancyicons.com/free-icons/176/paradise-fruit/png/256/apple_256.png"));
            PopupMenu popup = new PopupMenu();
            trayIcon = new TrayIcon(image, "Title", popup);
            trayIcon.setImageAutoSize(true);
            trayIcon.displayMessage("Title", "Sonos Blocker", TrayIcon.MessageType.ERROR);
            
            //Create menu
            MenuItem githubItem = new MenuItem("Github");
            cb1 = new CheckboxMenuItem("Enable Pop-ups");
            CheckboxMenuItem cb2 = new CheckboxMenuItem("Set tooltip");
            Menu displayMenu = new Menu("Display");
            MenuItem errorItem = new MenuItem("Error");
            MenuItem warningItem = new MenuItem("Warning");
            MenuItem infoItem = new MenuItem("Info");
            MenuItem noneItem = new MenuItem("None");
            MenuItem exitItem = new MenuItem("Exit");

            exitItem.addActionListener(this);
            githubItem.addActionListener(this);
            cb1.addItemListener(this);
            cb1.setState(popUps);
        
            //Add components to pop-up menu
            popup.add(githubItem);
            popup.addSeparator();
            popup.add(cb1);
            popup.add(cb2);
            popup.addSeparator();
            popup.add(displayMenu);
            displayMenu.add(errorItem);
            displayMenu.add(warningItem);
            displayMenu.add(infoItem);
            displayMenu.add(noneItem);
            popup.add(exitItem);
            
            final SystemTray tray = SystemTray.getSystemTray();

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.out.println("TrayIcon could not be added.");
            }
        } catch (IOException | HeadlessException ex) {
            showError(ex.toString());
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        try{
            switch(e.getActionCommand()){
                case "Exit": System.exit(1); break;
                case "Github": openWebpage(new URI("https://github.com/madnessxd/Justin-Bieber-Blocker-Sonos-music-filter-")); break;
                case "Enable Pop-ups": togglePopups(); break;
            }
        } catch(Exception ex){
            showError(ex.toString());
        }
    }

    private void togglePopups(){
        popUps = !popUps;
    }
    
    public void showError(String error){
        if(popUps)
        trayIcon.displayMessage("Error", error, TrayIcon.MessageType.ERROR);
    }
    
    public void showWarning(String error){
        if(popUps)
        trayIcon.displayMessage("Warning", error, TrayIcon.MessageType.WARNING);
    }
    
    public void showInfo(String error){
        if(popUps)
        trayIcon.displayMessage("Info", error, TrayIcon.MessageType.INFO);
    }
    
    public void openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
                showError(e.toString());
            }
        }
    }

    @Override
    public void itemStateChanged(ItemEvent ie) {
        switch(ie.getStateChange()){
            case 1: popUps = true; break;
            case 2: popUps = false; break;
        }
    }
}