import java.util.*;
import java.util.List;
import java.awt.*;

public class Rescuer extends Student {
  private Entity frozenTarget = null; // frozen student 
  private int unfreezeCounter = 0; // keeps track of time standing next to target to unfreeze 
  private boolean isUnfreezing = false; 

  public Rescuer(GamePanel gp) {
    super.gp();
    speed = 2;
  }

  @Override 
  public void update() {
    
}
