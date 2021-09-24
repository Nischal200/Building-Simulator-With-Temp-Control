package building;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;

import java.io.*;
import java.time.LocalDateTime;


public class Building_Tool_Bar {

    /**
     *
     * @return the file object denoting the destination to be used for saving a room.
     */
    private static File getSaveLocation(){
        // file chooser
        FileChooser fileChooser = new FileChooser();

        //  title
        fileChooser.setTitle("Save building.Building");

        // set initial current directory
        fileChooser.setInitialDirectory(new File("."));

        // create the extension filter
        FileChooser.ExtensionFilter extensionFilter =
                new FileChooser.ExtensionFilter("building.Building Configuration", "*.cfg", "*.dat", "*.bin");

        // set the initial name  save as .dat
        fileChooser.setInitialFileName(String.valueOf(LocalDateTime.now().hashCode()).concat(".dat"));

        // add the extension filter.
        fileChooser.getExtensionFilters().add(extensionFilter);

        // return the file chooser.
        return fileChooser.showSaveDialog(null);
    }

    /**
     *
     * @return the target source file for the building configuration data.
     */
    private static File getLoadLocation(){
        // create file chooser
        FileChooser fileChooser = new FileChooser();

        // set title
        fileChooser.setTitle("Load Saved building.Building");

        // set initial directory as the current directory
        fileChooser.setInitialDirectory(new File("."));

        // create the extension filter
        FileChooser.ExtensionFilter extensionFilter =
                new FileChooser.ExtensionFilter("building.Building Configuration", "*.cfg", "*.dat", "*.bin");
        fileChooser.getExtensionFilters().add(extensionFilter);

        // return the file chooser.
        return fileChooser.showOpenDialog(null);
    }

    /**
     * Method to save the building to file.
     * @param building the building to be saved.
     */
    public static void saveBuilding(Building building){
        // get the save location.
        File saveLocation = getSaveLocation();

        if(saveLocation != null) { // ensure it is not null
            try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveLocation))){
                // open object output stream and write the building to it.
                oos.writeObject(building);
                oos.flush();

                // show success message.
                Building_GUI.showMessage("Success", "Your building design has been saved: " + saveLocation.getName(), Alert.AlertType.INFORMATION);
            } catch (Exception e){
                // show error message if the save failed.
                Building_GUI.showMessage("Could not save file.", e.getLocalizedMessage(), Alert.AlertType.ERROR);
            }
        }
    }


    /**
     * Retrieved a building that was saved to file.
     * @return the retreived building.
     */
    public static Building retreiveBuilding(){
        // load the source location.
        File loadLocation = getLoadLocation();

        if(loadLocation != null){ // ensure it is non-null
            try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(loadLocation))){
                // read the building from the object input stream obtained from the source location.
                return (Building) ois.readObject();
            } catch (Exception e){
                // show error message if the building could not be loaded.
                Building_GUI.showMessage("Could not load file.", e.getLocalizedMessage(), Alert.AlertType.ERROR);
            }
        }

        return null;
    }

}
