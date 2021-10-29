import com.shattered.game.actor.object.component.transform.Vector3;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;


public class DotProductTest {


    public static void main(String[] args) {
        Vector3 forwardVector = new Vector3(-0.230f, -0.973f, 0f);
        Vector3 velocity = new Vector3(91.960f, 389.288f, 0f);

        double direction =  Vector3D.dotProduct(forwardVector.toVector3D(), velocity.toVector3D().normalize());

        System.out.println(direction);


    }
}
