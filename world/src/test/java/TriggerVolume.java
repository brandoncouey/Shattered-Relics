import com.shattered.game.actor.object.component.transform.Vector3;
import com.shattered.game.volume.Volume;

public class TriggerVolume {

    public static final Vector3 location = new Vector3(-5770, 16892, 239);

    public static final Vector3 origin = new Vector3(-45, -87, -228);

    public static final Vector3 extent = new Vector3(12700, 12700, 12700);


    public static void main(String[] args) {
        if (location.isInsideVolume(new Volume(origin, extent))) {
            System.out.println("Inside Volume.");
        } else {
            System.out.println("Not Inside Volume.");
        }
    }


}
