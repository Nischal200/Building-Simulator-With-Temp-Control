package building;

import javafx.scene.paint.Color;

/**
 * Class to track the temperature rise and falls of the room.
 */
public class Thermometer extends Updatable_Input_Item {
    // the target building
    private Building building;

    // total temperature rise to which the thermometer should gravitate to.
    private int totalTempRise;

    // the temperature rise per person.
    private static final int tempRisePerPerson = 3;

    // the current temperature.
    private double currentTemperature = 0;

    // whether the thermometer should use the occupants to determine the temperature change.
    private boolean shouldUpdate = true;

    /**
     * @param building The building whose temperature we are monitoring.
     */
    public Thermometer(Building building) {
        this.building = building;

        // get the total temp rise.
        this.totalTempRise = this.building.getOccupants().size() * tempRisePerPerson;
    }

    /**
     * Update the measure of the thermometer.
     */
    @Override
    public void update() {
        if (shouldUpdate) {
            //Change the number of temp from the number of people
            totalTempRise = this.building.getOccupants().size() * tempRisePerPerson + 10;
        }

        
        double step;
        if ((int) currentTemperature < totalTempRise) {
            //The temp should raise using this step
            step = 0.08;
        } else if ((int) currentTemperature > totalTempRise) {
            // the temp should drop using this temp
            step = -0.08;
        } else {
            // the temperature should remain the same.
            step = 0;
        }

        // update the current temperature.
        currentTemperature += step;

    }


    /**
     * Method to draw the thermometer.
     *
     * @param buildingGUI the GUI interface to use for drawing.
     */
    @Override
    public void draw(Building_GUI buildingGUI) {
        // create the thermometer room
        Room room = new Room("470 50 480 370 500 200");
        // draw the room with black as colour and wall size of 2
        room.draw(buildingGUI, Color.BLACK, 2);

      
      
    
        // draw the temperature bar.
        drawTempBar(buildingGUI);
    }

 

	/**
     * Draw the temperature bar.
     * @param buildingGUI the GUI object to be used.
     */
    private void drawTempBar(Building_GUI buildingGUI) {
        // magnify the current temperature to draw a much larger bar
        double newY = 368 - currentTemperature * 9;

        // ensure the Y position of the temp bar does not go below 50
        newY = newY >= 50 ? newY : 50;

        // draw the temp bar.
        buildingGUI.showWall(475, 368, 475, newY, Color.RED, 6);
    }

    /**
     *
     * @param shouldUpdate whether the thermometer source temperature changes from
     *                     the room occupants.
     */
    public void setShouldUpdate(boolean shouldUpdate) {
        this.shouldUpdate = shouldUpdate;
    }

    /**
     *
     * @return the current temperature.
     */
    public double getCurrentTemperature() {
        return currentTemperature;
    }

    /**
     *
     * @return the temp rise per person.
     */
    public int getTempRisePerPerson() {
        return tempRisePerPerson;
    }

    /**
     *
     * @return whether the thermometer source temperature changes from
     *                        the room occupants.
     */
    public boolean shouldUpdate() {
        return shouldUpdate;
    }

    /**
     *
     * @param totalTempRise get the total temperature rise
     */
    public void setTotalTempRise(int totalTempRise) {
        this.totalTempRise = totalTempRise;
    }
}
