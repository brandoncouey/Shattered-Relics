public class GridZoneId {


    public static int X = -1767;

    public static int Y = 34851;

    public static void main(String[] args) {
        System.out.println("Grid Cell Id = " + getZoneId());
    }


    /**
     * Gets the Zone Id
     * @return
     */
    public static int getZoneId() {
        int calculate_x = (int) (X / 49.8);
        int calculate_y = (int) (Y / 49.8);
        return ((calculate_x / 253) << 8) | (calculate_y / 253);
    }

    /**
     * Calculates the Chunk X
     * @return
     */
    public static int calculateZoneX() {
        return getZoneId() >> 8;
    }

    /**
     * Calculates the Chunk Y
     * @return
     */
    public static int calculateZoneY() {
        return getZoneId() & 0xff;
    }
}
