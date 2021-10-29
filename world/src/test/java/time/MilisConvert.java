package time;


public class MilisConvert {

    public static void main(String[] args) throws InterruptedException {

        int minutes = 1;//1hour
        long offlineTime = System.currentTimeMillis() - (1000 * 60 * minutes);//5 minutes ago
        long timeOffline = System.currentTimeMillis() - offlineTime;
        long hours = (timeOffline / 3_600_000);
        long minutesRemaining = (hours == 0 ? (timeOffline / 60_000) : ((timeOffline - 3_600_000 * hours) / 60_000));
        System.out.println("Offline for " + hours + " hours");
        System.out.println("Offline for " + minutesRemaining + " minutes");
    }
}
