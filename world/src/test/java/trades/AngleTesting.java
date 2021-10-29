package trades;

import com.shattered.game.actor.object.component.transform.Vector3;

public class AngleTesting {

    static Vector3 Left = new Vector3(-451, -1244, 99);//Top
    static Vector3 Right = new Vector3(-447, -1110, 99);//Bottom
    static float rotY = -160;
    //100 = 1 silver

    public static void main(String[] args) {

        float directionTo = Right.directionTo(Left);

        System.out.println(Right.directionTo(Left));
    }

}
