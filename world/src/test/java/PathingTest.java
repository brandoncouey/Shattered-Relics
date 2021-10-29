import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.game.actor.object.component.transform.Vector3;
import jotunheim.vmap.NavRequestPath;

public class PathingTest {


    public static void main(String[] args) {
        NavRequestPath.findPath("zalar", new Vector3(-83741, 116088, -26019), new Vector3(-84387, 119384, -25626)).stream().forEach(n -> {
            System.out.println(n);
        });
    }
}
