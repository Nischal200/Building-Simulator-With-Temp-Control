package building;

/**
 * 
 * The AirConditiner class simulates the air conditioner

 */
public class Air_Con  {

    private Thermometer thermometer;


    private static final int maxThreshold = 42;


    private static final int minThreshold = 20;



    public Air_Con(Thermometer thermometer) {
        this.thermometer = thermometer;
    }


    /**
     * Updates the air conditioner of the current temperature status.
     */
    public void update(){
       if(thermometer.getCurrentTemperature() > maxThreshold){ // temperature has exceeded the maximum threshold.
           if(thermometer.shouldUpdate()){
               // forbid the thermometer from receiving temperature 
               this.thermometer.setShouldUpdate(false);
           }

      
           thermometer.setTotalTempRise(minThreshold);

       }
    }
}
