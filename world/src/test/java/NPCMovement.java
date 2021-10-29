import com.shattered.game.actor.object.component.transform.Vector3;

public class NPCMovement {


    static float speed = 1000;
    static Vector3 myLoc = new Vector3(-9070.f, 18744.f, -222.f);

    static Vector3 origin = new Vector3(-9467.246094f, 18827.0f, -145.54184f);
    static Vector3 target = new Vector3(-9655.246094f, 19014.0f, -143.54184f);


    public static void main(String[] args) throws InterruptedException {

        System.out.println(origin.directionTo(target));


    }

}
