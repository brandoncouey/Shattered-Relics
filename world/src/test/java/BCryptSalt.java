import com.shattered.utilities.cryptography.BCrypt;

public class BCryptSalt {


    public static void main(String[] args) {
        System.out.println(BCrypt.hashpw("testing", BCrypt.gensalt()));
    }
}
