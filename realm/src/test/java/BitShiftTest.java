public class BitShiftTest {


    public static void main(String[] args) {

        /*float dur = 1.5f;
        long time = (long) dur;
        System.out.println(time);
        System.out.println(((int) Math.pow(2, 32) / 2 ) << 4);
        System.out.println(((int) Math.pow(2, 32)));*/


        int id = 5;
        int type = 0;

        int val = (id << 1) | type;

        System.out.println(val >> 1);


       /* int val = (id << 4) | type;
        int extracted = val & 0xf;

        System.out.println(extracted);//type
        System.out.println(val >> 4);//id*/
    }
}
