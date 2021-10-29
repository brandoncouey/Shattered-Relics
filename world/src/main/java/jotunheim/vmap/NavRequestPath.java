package jotunheim.vmap;

import com.google.common.util.concurrent.ListenableFuture;
import com.shattered.game.actor.object.component.transform.Vector3;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.List;

public class NavRequestPath {

    /**
     * Connects to the Navserver
     */
    private static ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress("127.0.0.1", 80); // host.docker.internal

    /**
     * Creates a Managed Server
     */
    private static ManagedChannel channel = channelBuilder.usePlaintext().build();

    /**
     * Creates a new GRPC Client Stub
     */
    private static VmapServiceGrpc.VmapServiceFutureStub client = VmapServiceGrpc.newFutureStub(channel);

    /*suspend fun testLOS(map: String, start: Vector3, end: Vector3): HitResult {
        val request = InternalVmap.CheckLOSRequest.newBuilder().setChannel(InternalVmap.CollisionChannel.ECC_Visibility)
        request.map = map
        request.startBuilder.setX(start.x.toFloat()).setY(start.y.toFloat()).z = start.z.toFloat()
        request.endBuilder.setX(end.x.toFloat()).setY(end.y.toFloat()).z = end.z.toFloat()
        val response = client.checkLOS(request.build()).await()

        return HitResult(response.hit, response.position?.run { Vector3(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())}, response.distance)
    }*/

    /**
     * Finds a Path with the given map, start location, and destination location
     *
     * Returns null if a route cannot be found.
     * @param map
     * @param start
     * @param destination
     */
    public static List<Vector3> findPath(String map, Vector3 start, Vector3 destination) {
        InternalVmap.GetPathRequest.Builder request = InternalVmap.GetPathRequest.newBuilder();
        request.setMap(map);
        request.setOrigin(request.getOriginBuilder().setX(start.getX()).setY(start.getY()).setZ(start.getZ()));
        request.setDestination(request.getDestinationBuilder().setX(destination.getX()).setY(destination.getY()).setZ(destination.getZ()));
        request.setStraightPath(false);

        ListenableFuture<InternalVmap.GetPathResponse> path = client.getPath(request.build());
        List<InternalVmap.Vector3> response = await(path);

        if (response != null) {
            List<Vector3> points = new ArrayList<>();
            //We skip the first one considering it's the same as the start.

            for (int index = 1; index < response.size(); index++) {
                points.add(new Vector3(response.get(index).getX(), response.get(index).getY(), response.get(index).getZ() + 96));
            }
            return points;
        }

        return null;
    }

    /**
     * Awaits for the response
     * @param future
     * @return the response
     */
    private static List<InternalVmap.Vector3> await(ListenableFuture<InternalVmap.GetPathResponse> future) {
        if (future == null) {
            return null;
        }
        new Thread(() -> {
            while (!future.isDone()) ;
        }).start();

       try {
           return future.get().getPointsList();
       } catch (Exception e) {
           e.printStackTrace();
           return null;
       }
    }

}
