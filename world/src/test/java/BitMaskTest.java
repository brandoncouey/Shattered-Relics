public class BitMaskTest {


    public static void main(String[] args) {

        //================ TEST 1 ==================

       /* int itemId = 120000;

        int output = (itemId << 4 | 16);*/

        //0b1 = 1 bit
        //0b11 = 2bit
        //0b111 = 3bit
        //...etc

        /*System.out.println(output);//prints out the item id.
        System.out.println(output >> 4);//prints out the item id.
        System.out.println(output & 0xf);*///prints out the item id.


        //================ TEST 2 ==================


        int id = 511;
        int mask = (((((id << 3 | 5) << 3 | 6) << 2 | 3) << 4 | 14) << 2 | 2) << 8 | 255;

        System.out.println(mask >> 22);//X
        System.out.println(mask >> 19 & 0x7);//5
        System.out.println(mask >> 16 & 0x7);//6
        System.out.println(mask >> 14 & 0x3);//3
        System.out.println(mask >> 10 & 0xf);//14
        System.out.println(mask >> 8 & 0x3);//2
        System.out.println(mask & 0xff);//255

        int whatever = 652 << 1 | 1;

        System.out.println(whatever & 1);
        System.out.println(whatever >> 1);


    }
}
