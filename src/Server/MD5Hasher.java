package Server;

public class MD5Hasher implements Runnable{
    private long begin;
    public static volatile boolean is_password_found = false;
    private long end;
    private static String hex_password;
    private static String foundPassword="Unavailable to decrypt it. Check, if it 7 letters in decrypted form.";

    public MD5Hasher(long begin, long end, String hex_password) {
        this.begin = begin;
        this.end = end;
        this.hex_password = hex_password;
    }

    public static void makeDefault()
    {
        foundPassword="Unavailable to decrypt it. Check, if it 7 letters in decrypted form.";
        is_password_found=false;
    }

    public void run() {
        for (long i = begin; i <= end && !is_password_found; i++) {
            String password = createPassword(i).toString();
            if (ClientHandler.hashPassword(password).equals(hex_password)) {
            is_password_found = true;
              foundPassword=password;
            }
        }
    }

    public static String getFoundPassword() {
        return foundPassword;
    }

    private static String createPassword(long current_combination) {
        int[] passwordInt = new int[7];
        for (int i = 0; i < 7; i++) {
            passwordInt[i] = (int) current_combination % 26;
            current_combination /= 26;
        }
        String password = "";
        for (int i = 0; i < 7; i++) {
            password += (char) ('a' + passwordInt[i]);
        }

        //Server.textArea.append(password+"\n");
        return password;
    }
}