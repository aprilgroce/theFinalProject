import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    private static HashMap<String, User> users = new HashMap<>();
    private static User mainUser;
    private static String matchFile;

    public static void createNewUser() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        String newUsername = null;
        System.out.println("Create a new account");
        System.out.print("Please create a username: ");
        Scanner in = new Scanner(System.in);
        newUsername = in.nextLine();
        for (String id : users.keySet()) {
            if (id.equals(newUsername)) {
                while (id.equals(newUsername)) {
                    System.out.print("This username is taken. Please enter a different username: ");
                    Scanner newIn = new Scanner(System.in);
                    newUsername = newIn.nextLine();
                }
            }
        }
        File file = new File("./users/" + newUsername + ".txt");
        file.createNewFile();

        PrintWriter output = new PrintWriter("./users/" + newUsername + ".txt");
        output.println(newUsername);
        System.out.print("Please create a password: ");
        Scanner in2 = new Scanner(System.in);
        String password = in2.nextLine();
        //next 3 lines creates salt
        //password hashing - "the salt" is what is used to transform the password so each salt would need to be stored with password, so added a new variable to the user object to include this
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        System.out.println(salt);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = f.generateSecret(spec).getEncoded();
        Base64.Encoder enc = Base64.getEncoder();
        String theSalt = enc.encodeToString(salt);
        password = enc.encodeToString(hash);
        output.println(password);
        output.println(salt);

        System.out.println("First name:");
        Scanner in3 = new Scanner(System.in);
        String fname = in3.nextLine();
        output.println(fname);
        System.out.println("Last name:");
        Scanner in4 = new Scanner(System.in);
        String lname = in4.nextLine();
        output.println(lname);
        mainUser = new User(newUsername, password, theSalt, fname, lname, 0.0, 0.0);
        users.put(newUsername,/*new User(newUsername,password,theSalt,fname,lname,0.0,0.0)*/mainUser);
        System.out.println("Your account has been successfully created.");
        System.out.println("");
        output.println(0.00);
        output.println(0.00);
        output.println(0.00);
        output.print(0.00);
        output.close();
    }

    public static void addToHashFromFile() throws FileNotFoundException {
        User user = null;
        String filename;
        File dir = new File("./users");
        File[] directoryListing = dir.listFiles();
        for (File aFile : directoryListing) {
            filename = aFile.getName();
            filename = filename.substring(0, filename.length() - 4);
            FileReader reader = new FileReader(aFile);
            Scanner in = new Scanner(reader);
            while (in.hasNext()) {
                user = new User(in);
                if (in.hasNext()) {
                    in.nextLine();
                }
            }
            users.put(filename, user);
        }
    }

    public static boolean login() throws NoSuchAlgorithmException, InvalidKeySpecException, FileNotFoundException {
        String filename;
        String fileUsername = null;
        String matchFile = null;
        System.out.println("Username: ");
        Scanner in = new Scanner(System.in);
        String loginUsername = in.nextLine();
        File dir = new File("./users");
        File[] directoryListing = dir.listFiles();
        for (File aFile : directoryListing) {
            filename = aFile.getName();
            fileUsername = filename.substring(0, filename.length() - 4);
            if (fileUsername.equals(loginUsername)) {
                matchFile = aFile.getName();
            }
        }
        if (matchFile == null) {
            System.out.println("Invalid Username.");
            return false;
        } else {
            System.out.println("Password: ");
            Scanner in2 = new Scanner(System.in);
            String pass = in2.nextLine();
            for (String userName : users.keySet()) {
                if (userName.equals(fileUsername)) {
                    mainUser = users.get(userName);
                }
            }
            String salt = mainUser.getSalt();
            byte[] theSalt = salt.getBytes(StandardCharsets.UTF_8);
            System.out.println(theSalt);
            KeySpec spec = new PBEKeySpec(pass.toCharArray(), theSalt, 65536, 128);
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = f.generateSecret(spec).getEncoded();
            Base64.Encoder enc = Base64.getEncoder();
            String newPass = enc.encodeToString(hash);
            System.out.println(newPass);
            String userPass = mainUser.getPassword();
            System.out.println(userPass);
            if (pass.equals(userPass)) {
                return true;
            } else {
                return false;
            }
        }
    }


    public static void balance() {
        System.out.println("Which account balance would you like to see?");
        System.out.println("1. Checking");
        System.out.println("2. Savings");
        System.out.println("3. Money Market");
        System.out.println("4. Certificate Deposit");
        System.out.println("5. All");
        Scanner in = new Scanner(System.in);
        int choice = in.nextInt();
        if (choice == 1) {
            mainUser.balanceChecking();
        } else if (choice == 2) {
            mainUser.balanceSavings();
        } else if (choice == 3) {
            mainUser.balanceMoneyMarket();
        } else if (choice == 4) {
            mainUser.balanceCertificateDeposit();
        } else if (choice == 5) {
            mainUser.balance();
        } else {
            System.out.println("You did not select a valid account");
            System.out.println("");
        }
    }

    public static void deposit() throws IOException {
        System.out.println("Which account would you like to deposit in to?");
        System.out.println("1. Checking");
        System.out.println("2. Savings");
        System.out.println("3. Money Market");
        System.out.println("4. Certificate Deposit");
        Scanner in = new Scanner(System.in);
        int choice = in.nextInt();

        System.out.println("How much would you like to deposit?");
        double depositAmount = in.nextDouble();

        if (choice == 1) {
            mainUser.depositChecking(depositAmount);
        } else if (choice == 2) {
            mainUser.depositSavings(depositAmount);
        } else if (choice == 3) {
            mainUser.depositMoneyMarket(depositAmount);
        } else if (choice == 4) {
            mainUser.depositCertificateDeposit(depositAmount);
        } else {
            System.out.println("You did not select a valid account");
            System.out.println("");
        }


    }

    public static void withdraw() throws IOException {
        System.out.println("Which account would you like to withdraw from?");
        System.out.println("1. Checking");
        System.out.println("2. Savings");
        System.out.println("3. Money Market");
        System.out.println("4. Certificate Deposit");
        Scanner in = new Scanner(System.in);
        int choice = in.nextInt();

        System.out.println("How much would you like to withdraw?");
        double withdrawAmount = in.nextDouble();

        if (choice == 1) {
            mainUser.withdrawChecking(withdrawAmount);
        } else if (choice == 2) {
            mainUser.withdrawSavings(withdrawAmount);
        } else if (choice == 3) {
            mainUser.withdrawMoneyMarket(withdrawAmount);
        } else if (choice == 4) {
            mainUser.withdrawCertificateDeposit(withdrawAmount);
        }

        File fnew = new File("./users/" + mainUser.getUsername() + ".txt");
        FileWriter f = new FileWriter(fnew, false);
        f.write(mainUser.getUsername() + "\n");
        f.write(mainUser.getPassword() + "\n");
        f.write(mainUser.getSalt() + "\n");
        f.write(mainUser.getFname() + "\n");
        f.write(mainUser.getLname() + "\n");
        f.write(String.valueOf(mainUser.getChecking()) + "\n");
        f.write(String.valueOf(mainUser.getSavings()) + "\n");
        f.write(String.valueOf(mainUser.getMM()) + "\n");
        f.write(String.valueOf(mainUser.getCD()) + "\n");
        f.close();


    }

    public static void transfer() throws IOException {
        Scanner in = new Scanner(System.in);
        System.out.println("Which account would you like to transfer money from?");
        System.out.println("1. Checking");
        System.out.println("2. Savings");
        System.out.println("3. Money Market");
        System.out.println("4. Certificate Deposit");
        int account1 = in.nextInt();

        Scanner in2 = new Scanner(System.in);
        System.out.println("Which account would you like to transfer money to?");
        System.out.println("1. Checking");
        System.out.println("2. Savings");
        System.out.println("3. Money Market");
        System.out.println("4. Certificate Deposit");
        int account2 = in.nextInt();

        System.out.println("How much money would you like to transfer?");
        Scanner in3 = new Scanner(System.in);
        double transferAmount = in.nextDouble();
        if (account1 == 1 && account2 == 2) {
            mainUser.transferCheckingSaving(transferAmount);
        } else if (account1 == 1 && account2 == 3) {
            mainUser.transferCheckingMM(transferAmount);
        } else if (account1 == 1 && account2 == 4) {
            mainUser.transferCheckingCD(transferAmount);
        } else if (account1 == 2 && account2 == 1) {
            mainUser.transferSavingsChecking(transferAmount);
        } else if (account1 == 2 && account2 == 3) {
            mainUser.transferSavingsMM(transferAmount);
        } else if (account1 == 2 && account2 == 4) {
            mainUser.transferSavingsCD(transferAmount);
        } else if (account1 == 3 && account2 == 1) {
            mainUser.transferMMChecking(transferAmount);
        } else if (account1 == 3 && account2 == 2) {
            mainUser.transferMMSavings(transferAmount);
        } else if (account1 == 3 && account2 == 4) {
            mainUser.transferMMCD(transferAmount);
        } else if (account1 == 4 && account2 == 1) {
            mainUser.transferCDChecking(transferAmount);
        } else if (account1 == 4 && account2 == 2) {
            mainUser.transferCDSavings(transferAmount);
        } else if (account1 == 4 && account2 == 3) {
            mainUser.transferCDMM(transferAmount);
        } else {
            System.out.println("Unable to transfer, please try again");
        }

        File fnew = new File("./users/" + mainUser.getUsername() + ".txt");
        FileWriter f = new FileWriter(fnew, false);
        f.write(mainUser.getUsername() + "\n");
        f.write(mainUser.getPassword() + "\n");
        f.write(mainUser.getSalt() + "\n");
        f.write(mainUser.getFname() + "\n");
        f.write(mainUser.getLname() + "\n");
        f.write(String.valueOf(mainUser.getChecking()) + "\n");
        f.write(String.valueOf(mainUser.getSavings()) + "\n");
        f.write(String.valueOf(mainUser.getMM()) + "\n");
        f.write(String.valueOf(mainUser.getCD()) + "\n");
        f.close();
    }

    public static void loan() {
        Scanner in = new Scanner(System.in);
        System.out.println("How much would you like to request a loan for?");
        double loanAmount = in.nextDouble();

        Scanner in2 = new Scanner(System.in);
        System.out.println("What is the reason you are requesting a loan?");
        String loanReason = in2.nextLine();

        Scanner in3 = new Scanner(System.in);
        System.out.println("What is your current salary?");
        double salary = in3.nextDouble();

        System.out.println("Thank you for your request. It will be reviewed and you will be notified when it has been approved");
        System.out.println("");
    }

    public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        //Line below initializes all the windows and displays the login window.
//        Window win = new Window();
        addToHashFromFile();
        System.out.println(users);
        JFrame frame = new JFrame("Welcome");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 200);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JButton button1 = new JButton("Login");
        button1.addActionListener(new ButtonListener());
        JButton button2 = new JButton("Create account");
        button2.addActionListener(new ButtonListener2());
        JButton button3 = new JButton("Exit");
        button3.addActionListener(new ButtonListener3());
        label.setText("Welcome to your mobile banking account!");
        panel.setSize(400, 100);
        panel.add(label);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel2 = new JPanel();
        panel2.setSize(400, 100);
        panel2.add(button1);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel3 = new JPanel();
        panel3.setSize(400, 100);
        panel3.add(button2);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel4 = new JPanel();
        panel4.setSize(400, 100);
        panel4.add(button3);
        panel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        frame.setVisible(true);
//        System.out.println("Welcome to your mobile bank account!");
        int choice = 0;
        while (choice != 3) {
            System.out.println("1. Login to existing account");
            System.out.println("2. Create an account");
            System.out.println("3. Exit");
            System.out.print(": ");
            Scanner in = new Scanner(System.in);
            choice = in.nextInt();
            if (choice == 1) {
                if (login() == true) {
                    int choice1 = 0;
                    while (choice1 != 6) {
                        System.out.println("Main Menu");
                        System.out.println("Manage your bank accounts");
                        System.out.println("1. See account balance");
                        System.out.println("2. Make a deposit");
                        System.out.println("3. Withdraw money");
                        System.out.println("4. Transfer money");
                        System.out.println("5. Request a loan");
                        System.out.println("6. Log out");
                        System.out.print(": ");
                        Scanner in2 = new Scanner(System.in);
                        choice1 = in.nextInt();
                        if (choice1 == 1) {
                            balance();
                        } else if (choice1 == 2) {
                            deposit();
                        } else if (choice1 == 3) {
                            withdraw();
                        } else if (choice1 == 4) {
                            transfer();
                        } else if (choice1 == 5) {
                            loan();
                        } else if (choice1 == 6) {
                            continue;
                        }
                    }
                } else {
                    continue;
                }
            } else if (choice == 2) {
                createNewUser();
                int choice2 = 0;
                while (choice2 != 6) {
                    System.out.println("Main Menu");
                    System.out.println("Manage your bank accounts");
                    System.out.println("1. See account balance");
                    System.out.println("2. Make a deposit");
                    System.out.println("3. Withdraw money");
                    System.out.println("4. Transfer money");
                    System.out.println("5. Request a loan");
                    System.out.println("6. Log out");
                    System.out.print(": ");
                    Scanner in2 = new Scanner(System.in);
                    choice2 = in.nextInt();
                    if (choice2 == 1) {
                        balance();
                    } else if (choice2 == 2) {
                        deposit();
                    } else if (choice2 == 3) {
                        withdraw();
                    } else if (choice2 == 4) {
                        transfer();
                    } else if (choice2 == 5) {
                        loan();
                    } else if (choice2 == 6) {
                        continue;
                    }
                }
            } else if (choice == 3) {
                System.exit(0);
            }
        }
    }


    public static User getMainUser() {
        return mainUser;
    }

    public static HashMap<String, User> getUsers() {
        return users;
    }

    public static String getMatchFile() {
        return matchFile;
    }
}

class ButtonListener implements ActionListener {
    static JTextField loginUserName;
    static JTextField loginPassword;
    static JFrame loginFrame;

    @Override
    public void actionPerformed(ActionEvent e) {
        loginFrame = new JFrame("Login");
        loginFrame.getContentPane().setLayout(new BoxLayout(loginFrame.getContentPane(), BoxLayout.Y_AXIS));
        loginFrame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        label.setText("Login to account");
        panel.setSize(400, 50);
        panel.add(label);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel2 = new JPanel();
        panel2.setSize(400, 100);
        JLabel label2 = new JLabel();
        label2.setText("Username:");
        panel2.add(label2);
        loginUserName = new JTextField(20);
        panel2.add(loginUserName);
        JLabel label3 = new JLabel();
        label3.setText("Password:");
        panel2.add(label3);
        loginPassword = new JPasswordField(20);
        panel2.add(loginPassword);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginFrame.add(panel);
        loginFrame.add(panel2);
        JPanel panel3 = new JPanel();
        JButton button4 = new JButton("Login");
        button4.addActionListener(new ButtonListener23());
        panel3.add(button4);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginFrame.add(panel3);
        loginFrame.setVisible(true);
    }

    public static JTextField getloginUsername () {
        return loginUserName;
    }

    public static JTextField getLoginPassword () {
        return loginPassword;
    }

    public static JFrame getLoginFrame() {
        return loginFrame;
    }

}

class ButtonListener2 implements ActionListener {
    static JTextField createUserName;
    static JTextField createPassword;
    static JTextField createFname;
    static JTextField createLname;
    static JFrame createAccount;

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            Main.addToHashFromFile();
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        createAccount = new JFrame("Register User");
        createAccount.getContentPane().setLayout(new BoxLayout(createAccount.getContentPane(), BoxLayout.Y_AXIS));
        createAccount.setSize(350, 600);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        label.setText("Create an account");
        panel.setSize(400, 50);
        panel.add(label);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel2 = new JPanel();
        panel2.setSize(400, 100);
        JLabel label2 = new JLabel();
        label2.setText("Username:");
        panel2.add(label2);
        createUserName = new JTextField(20);
        panel2.add(createUserName);
        JLabel label3 = new JLabel();
        label3.setText("Password:");
        panel2.add(label3);
        createPassword = new JPasswordField(20);
        panel2.add(createPassword);
        JLabel label4 = new JLabel();
        label4.setText("First Name:");
        panel2.add(label4);
        createFname = new JTextField(20);
        panel2.add(createFname);
        JLabel label5 = new JLabel();
        label5.setText("Last Name:");
        panel2.add(label5);
        createLname = new JTextField(20);
        panel2.add(createLname);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        createAccount.add(panel);
        createAccount.add(panel2);
        JPanel panel3 = new JPanel();
        JButton button4 = new JButton("Create Account");
        button4.addActionListener(new ButtonListener24());
        panel3.add(button4);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        createAccount.add(panel3);
        createAccount.setVisible(true);
    }


    public static JTextField getCreateUsername() {
        return createUserName;
    }

    public static JTextField getCreatePassword() {
        return createPassword;
    }

    public static JTextField getCreateFname() {
        return createFname;
    }

    public static JTextField getCreateLname() {
        return createLname;
    }

    public static JFrame getCreateAccount() {
        return createAccount;
    }
}

class ButtonListener3 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        System.exit(0);
    }

}

class ButtonListener4 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame("Login");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JButton button1 = new JButton("Check Balance");
        button1.addActionListener(new ButtonListener5());
        JButton button2 = new JButton("Deposit");
        button2.addActionListener(new ButtonListener6());
        JButton button3 = new JButton("Withdraw");
        button3.addActionListener(new ButtonListener7());
        JButton button4 = new JButton("Transfer");
        button4.addActionListener(new ButtonListener8());
        JButton button5 = new JButton("Request a Loan");
        button5.addActionListener(new ButtonListener9());
        JButton button6 = new JButton("Exit");
        button5.addActionListener(new ButtonListener3());
        label.setText("Main Menu");
        panel.setSize(400, 100);
        panel.add(label);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel2 = new JPanel();
        panel2.setSize(400, 100);
        panel2.add(button1);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel3 = new JPanel();
        panel3.setSize(400, 100);
        panel3.add(button2);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel4 = new JPanel();
        panel4.setSize(400, 100);
        panel4.add(button3);
        panel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel5 = new JPanel();
        panel5.setSize(400, 100);
        panel5.add(button4);
        panel5.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel6 = new JPanel();
        panel6.setSize(400, 100);
        panel6.add(button5);
        panel6.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel7 = new JPanel();
        panel7.setSize(400, 100);
        panel7.add(button6);
        panel7.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        frame.add(panel5);
        frame.add(panel6);
        frame.add(panel7);
        frame.setVisible(true);
    }

}

class ButtonListener5 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame("Balance");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JButton button1 = new JButton("Checking Account");
        button1.addActionListener(new ButtonListener10());
        JButton button2 = new JButton("Savings Account");
        button2.addActionListener(new ButtonListener11());
        JButton button3 = new JButton("Money Market Account");
        button3.addActionListener(new ButtonListener12());
        JButton button4 = new JButton("Certificate of Deposit Account");
        button4.addActionListener(new ButtonListener13());
        JButton button5 = new JButton("All Accounts");
        button5.addActionListener(new ButtonListener14());
        JButton button6 = new JButton("Exit to Main Menu");
        button6.addActionListener(new ButtonListener25());
        label.setText("Select the account balance you want to see:");
        panel.setSize(400, 100);
        panel.add(label);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel2 = new JPanel();
        panel2.setSize(400, 100);
        panel2.add(button1);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel3 = new JPanel();
        panel3.setSize(400, 100);
        panel3.add(button2);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel4 = new JPanel();
        panel4.setSize(400, 100);
        panel4.add(button3);
        panel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel5 = new JPanel();
        panel5.setSize(400, 100);
        panel5.add(button4);
        panel5.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel6 = new JPanel();
        panel6.setSize(400, 100);
        panel6.add(button5);
        panel6.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel7 = new JPanel();
        panel7.setSize(400, 100);
        panel7.add(button6);
        panel7.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        frame.add(panel5);
        frame.add(panel6);
        frame.add(panel7);
        frame.setVisible(true);
    }

}

class ButtonListener6 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame("Deposit");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JButton button1 = new JButton("Checking Account");
        button1.addActionListener(new ButtonListener15());
        JButton button2 = new JButton("Savings Account");
        button2.addActionListener(new ButtonListener16());
        JButton button3 = new JButton("Money Market Account");
        button3.addActionListener(new ButtonListener17());
        JButton button4 = new JButton("Certificate of Deposit Account");
        button4.addActionListener(new ButtonListener18());
        label.setText("Select an account to deposit in to:");
        panel.setSize(400, 100);
        panel.add(label);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel2 = new JPanel();
        panel2.setSize(400, 100);
        panel2.add(button1);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel3 = new JPanel();
        panel3.setSize(400, 100);
        panel3.add(button2);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel4 = new JPanel();
        panel4.setSize(400, 100);
        panel4.add(button3);
        panel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel5 = new JPanel();
        panel5.setSize(400, 100);
        panel5.add(button4);
        panel5.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        frame.add(panel5);
        frame.setVisible(true);
    }

}


class ButtonListener7 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame("Withdraw");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JButton button1 = new JButton("Checking Account");
        button1.addActionListener(new ButtonListener19());
        JButton button2 = new JButton("Savings Account");
        button2.addActionListener(new ButtonListener20());
        JButton button3 = new JButton("Money Market Account");
        button3.addActionListener(new ButtonListener21());
        JButton button4 = new JButton("Certificate of Deposit Account");
        button4.addActionListener(new ButtonListener22());
        label.setText("Select an account to withdraw from:");
        panel.setSize(400, 100);
        panel.add(label);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel2 = new JPanel();
        panel2.setSize(400, 100);
        panel2.add(button1);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel3 = new JPanel();
        panel3.setSize(400, 100);
        panel3.add(button2);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel4 = new JPanel();
        panel4.setSize(400, 100);
        panel4.add(button3);
        panel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel5 = new JPanel();
        panel5.setSize(400, 100);
        panel5.add(button4);
        panel5.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        frame.add(panel5);
        frame.setVisible(true);
    }

}

class ButtonListener8 implements ActionListener {
    static JTextField amount;
    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame("Transfer");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JButton button1 = new JButton("Checking Account");
        button1.addActionListener(new ButtonListener26());
        JButton button2 = new JButton("Savings Account");
        button2.addActionListener(new ButtonListener28());
        JButton button3 = new JButton("Money Market Account");
        button3.addActionListener(new ButtonListener29());
        JButton button4 = new JButton("Certificate of Deposit Account");
        button4.addActionListener(new ButtonListener30());
        label.setText("Select an account to transfer from:");
        panel.setSize(400, 100);
        panel.add(label);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel2 = new JPanel();
        panel2.setSize(400, 100);
        panel2.add(button1);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel3 = new JPanel();
        panel3.setSize(400, 100);
        panel3.add(button2);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel4 = new JPanel();
        panel4.setSize(400, 100);
        panel4.add(button3);
        panel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel5 = new JPanel();
        panel5.setSize(400, 100);
        panel5.add(button4);
        panel5.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel label2 = new JLabel();
        label2.setText("How much would you like to transfer?");
        JPanel panel6 = new JPanel();
        panel6.add(label2);
        amount = new JTextField(20);
        JPanel panel7 = new JPanel();
        panel7.add(amount);
        frame.add(panel6);
        frame.add(panel7);
        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        frame.add(panel5);
        frame.setVisible(true);
    }

    public static JTextField getAmount () {
        return amount;
    }

}

class ButtonListener9 implements ActionListener {
    static JTextField amount;
    static JTextField reason;
    static JTextField salary;

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame("Loan Request");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        label.setText("How much would you like to request a loan for?");
        panel.setPreferredSize(new Dimension(100, 100));
        panel.add(label);
        amount = new JTextField(8);
        JPanel panel2 = new JPanel();
        panel2.setPreferredSize(new Dimension(100, 100));
        amount.setSize(50, 50);
        panel2.add(amount);

        JPanel panel3 = new JPanel();
        JLabel label2 = new JLabel();
        label2.setText("What is the reason you are requesting a loan?");
        panel3.setPreferredSize(new Dimension(100, 100));
        panel3.add(label2);
        reason = new JTextField(8);
        JPanel panel4 = new JPanel();
        panel4.setPreferredSize(new Dimension(100, 100));
        reason.setSize(50, 50);
        panel4.add(reason);

        JPanel panel5 = new JPanel();
        JLabel label3 = new JLabel();
        label3.setText("What is your current salary?");
        panel5.setPreferredSize(new Dimension(100, 100));
        panel5.add(label3);
        salary = new JTextField(8);
        JPanel panel6 = new JPanel();
        panel6.setPreferredSize(new Dimension(100, 100));
        salary.setSize(50, 50);
        panel6.add(salary);

        JPanel panel7 = new JPanel();
        panel7.setPreferredSize(new Dimension(100, 100));
        JButton button = new JButton("Submit Request");
        panel7.add(button);

        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        frame.add(panel5);
        frame.add(panel6);
        frame.add(panel7);
        frame.setVisible(true);

    }

}

class ButtonListener10 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        JFrame frame = new JFrame("Account Balance");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JLabel label = new JLabel();
        label.setText("Checking account balance: $" + user.getChecking());
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(label);
        frame.setVisible(true);
    }
}

class ButtonListener11 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        JFrame frame = new JFrame("Account Balance");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JLabel label = new JLabel();
        label.setText("Savings account balance: $" + user.getSavings());
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(label);
        frame.setVisible(true);
    }
}

class ButtonListener12 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        JFrame frame = new JFrame("Account Balance");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JLabel label = new JLabel();
        label.setText("Money market account balance: $" + user.getMM());
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(label);
        frame.setVisible(true);
    }
}

class ButtonListener13 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        JFrame frame = new JFrame("Account Balance");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JLabel label = new JLabel();
        label.setText("Certificate of Deposit account balance: $" + user.getCD());
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(label);
        frame.setVisible(true);
    }
}

class ButtonListener14 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        JFrame frame = new JFrame("Account Balance");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JLabel label = new JLabel();
        JPanel panel = new JPanel();
        label.setText("Checking account balance: $" + user.getChecking());
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        JLabel label2 = new JLabel();
        JPanel panel2 = new JPanel();
        label2.setText("Savings account balance: $" + user.getSavings());
        label2.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel2.add(label2);
        JLabel label3 = new JLabel();
        JPanel panel3 = new JPanel();
        label3.setText("Money Market account balance: $" + user.getMM());
        label3.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel3.add(label3);
        JLabel label4 = new JLabel();
        JPanel panel4 = new JPanel();
        label4.setText("Certificate of Deposit account balance: $" + user.getCD());
        label4.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel4.add(label4);

        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        frame.setVisible(true);
    }
}

class ButtonListener15 implements ActionListener {
    static JTextField amount;

    @Override
    public void actionPerformed(ActionEvent e) {
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        JFrame frame = new JFrame("Account Deposit");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        label.setText("How much would you like to deposit?");
        panel.setPreferredSize(new Dimension(100, 100));
        panel.add(label);
        amount = new JTextField(8);
        JPanel panel2 = new JPanel();
        panel2.setPreferredSize(new Dimension(100, 100));
        amount.setSize(50, 50);
        panel2.add(amount);
        JPanel panel3 = new JPanel();
        panel3.setPreferredSize(new Dimension(100, 100));
        JButton button = new JButton("Deposit");
        button.addActionListener(new ButtonListener27());
        panel3.add(button);

        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.setVisible(true);
    }

    public static JTextField getAmount() {
        return amount;
    }


}

class ButtonListener16 implements ActionListener {
    static JTextField amount;

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame("Account Deposit");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        label.setText("How much would you like to deposit?");
        panel.setPreferredSize(new Dimension(100, 100));
        panel.add(label);
        amount = new JTextField(8);
        JPanel panel2 = new JPanel();
        panel2.setPreferredSize(new Dimension(100, 100));
        amount.setSize(50, 50);
        panel2.add(amount);
        JPanel panel3 = new JPanel();
        panel3.setPreferredSize(new Dimension(100, 100));
        JButton button = new JButton("Deposit");
        button.addActionListener(new ButtonListener43());
        panel3.add(button);

        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.setVisible(true);
    }

    public static JTextField getAmount() {
        return amount;
    }

}

class ButtonListener17 implements ActionListener {
    static JTextField amount;

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame("Account Deposit");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        label.setText("How much would you like to deposit?");
        panel.setPreferredSize(new Dimension(100, 100));
        panel.add(label);
        amount = new JTextField(8);
        JPanel panel2 = new JPanel();
        panel2.setPreferredSize(new Dimension(100, 100));
        amount.setSize(50, 50);
        panel2.add(amount);
        JPanel panel3 = new JPanel();
        panel3.setPreferredSize(new Dimension(100, 100));
        JButton button = new JButton("Deposit");
        button.addActionListener(new ButtonListener44());
        panel3.add(button);

        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.setVisible(true);
    }

    public static JTextField getAmount() {
        return amount;
    }
}

class ButtonListener18 implements ActionListener {
    static JTextField amount;

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame("Account Deposit");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        label.setText("How much would you like to deposit?");
        panel.setPreferredSize(new Dimension(100, 100));
        panel.add(label);
        amount = new JTextField(8);
        JPanel panel2 = new JPanel();
        panel2.setPreferredSize(new Dimension(100, 100));
        amount.setSize(50, 50);
        panel2.add(amount);
        JPanel panel3 = new JPanel();
        panel3.setPreferredSize(new Dimension(100, 100));
        JButton button = new JButton("Deposit");
        button.addActionListener(new ButtonListener45());
        panel3.add(button);

        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.setVisible(true);
    }

    public static JTextField getAmount() {
        return amount;
    }
}

class ButtonListener19 implements ActionListener {
    static JTextField amount;

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame("Account Withdraw");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        label.setText("How much would you like to withdraw?");
        panel.setPreferredSize(new Dimension(100, 100));
        panel.add(label);
        amount = new JTextField(8);
        JPanel panel2 = new JPanel();
        panel2.setPreferredSize(new Dimension(100, 100));
        amount.setSize(50, 50);
        panel2.add(amount);
        JPanel panel3 = new JPanel();
        panel3.setPreferredSize(new Dimension(100, 100));
        JButton button = new JButton("Withdraw");
        button.addActionListener(new ButtonListener46());
        panel3.add(button);

        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.setVisible(true);
    }

    public static JTextField getAmount() {
        return amount;
    }
}

class ButtonListener20 implements ActionListener {
    static JTextField amount;

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame("Account Withdraw");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        label.setText("How much would you like to withdraw?");
        panel.setPreferredSize(new Dimension(100, 100));
        panel.add(label);
        amount = new JTextField(8);
        JPanel panel2 = new JPanel();
        panel2.setPreferredSize(new Dimension(100, 100));
        amount.setSize(50, 50);
        panel2.add(amount);
        JPanel panel3 = new JPanel();
        panel3.setPreferredSize(new Dimension(100, 100));
        JButton button = new JButton("Withdraw");
        button.addActionListener(new ButtonListener47());
        panel3.add(button);

        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.setVisible(true);
    }

    public static JTextField getAmount() {
        return amount;
    }
}

class ButtonListener21 implements ActionListener {
    static JTextField amount;

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame("Account Withdraw");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        label.setText("How much would you like to withdraw?");
        panel.setPreferredSize(new Dimension(100, 100));
        panel.add(label);
        amount = new JTextField(8);
        JPanel panel2 = new JPanel();
        panel2.setPreferredSize(new Dimension(100, 100));
        amount.setSize(50, 50);
        panel2.add(amount);
        JPanel panel3 = new JPanel();
        panel3.setPreferredSize(new Dimension(100, 100));
        JButton button = new JButton("Withdraw");
        button.addActionListener(new ButtonListener48());
        panel3.add(button);

        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.setVisible(true);
    }

    public static JTextField getAmount() {
        return amount;
    }
}

class ButtonListener22 implements ActionListener {
    static JTextField amount;

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame("Account Withdraw");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        label.setText("How much would you like to withdraw?");
        panel.setPreferredSize(new Dimension(100, 100));
        panel.add(label);
        amount = new JTextField(8);
        JPanel panel2 = new JPanel();
        panel2.setPreferredSize(new Dimension(100, 100));
        amount.setSize(50, 50);
        panel2.add(amount);
        JPanel panel3 = new JPanel();
        panel3.setPreferredSize(new Dimension(100, 100));
        JButton button = new JButton("Withdraw");
        button.addActionListener(new ButtonListener49());
        panel3.add(button);

        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.setVisible(true);
    }

    public static JTextField getAmount() {
        return amount;
    }
}

class ButtonListener23 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        User user = Main.getMainUser();
        String filename;
        String fileUsername = null;
        String matchFile = null;
        boolean login = false;
        String loginUsername = ButtonListener.loginUserName.getText();
        String loginPassword = ButtonListener.loginPassword.getText();
        File dir = new File("./users");
        File[] directoryListing = dir.listFiles();
        for (File aFile : directoryListing) {
            filename = aFile.getName();
            fileUsername = filename.substring(0, filename.length() - 4);
            if (fileUsername.equals(loginUsername)) {
                matchFile = aFile.getName();
            }
        }
        if (matchFile != null) {
            HashMap<String, User> users = Main.getUsers();
            for (String userName : users.keySet()) {
                if (userName.equals(fileUsername)) {
                    user = users.get(userName);
                }
            }
            MessageDigest alg = null;
            try {
                alg = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
            }
            alg.reset();
            alg.update(loginPassword.getBytes());
            byte[] digest = alg.digest();
            StringBuffer hashedpasswd = new StringBuffer();
            String hx;
            for (int i = 0; i < digest.length; i++) {
                hx = Integer.toHexString(0xFF & digest[i]);
                if (hx.length() == 1) {
                    hx = "0" + hx;
                }
                hashedpasswd.append(hx);
            }
            String password = null;
            try {
                password = Files.readAllLines(Paths.get("./users/" + loginUsername + ".txt")).get(1);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            if (hashedpasswd.toString().equals(password)) {
                login = true;
                JFrame frame = new JFrame("Login");
                frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
                frame.setSize(300, 400);
                JPanel panel = new JPanel();
                JLabel label = new JLabel();
                JButton button1 = new JButton("Check Balance");
                button1.addActionListener(new ButtonListener5());
                JButton button2 = new JButton("Deposit");
                button2.addActionListener(new ButtonListener6());
                JButton button3 = new JButton("Withdraw");
                button3.addActionListener(new ButtonListener7());
                JButton button4 = new JButton("Transfer");
                button4.addActionListener(new ButtonListener8());
                JButton button5 = new JButton("Request a Loan");
                button5.addActionListener(new ButtonListener9());
                JButton button6 = new JButton("Exit");
                button6.addActionListener(new ButtonListener3());
                label.setText("Main Menu");
                panel.setSize(400, 100);
                panel.add(label);
                panel.setAlignmentX(Component.CENTER_ALIGNMENT);
                JPanel panel2 = new JPanel();
                panel2.setSize(400, 100);
                panel2.add(button1);
                panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
                JPanel panel3 = new JPanel();
                panel3.setSize(400, 100);
                panel3.add(button2);
                panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
                JPanel panel4 = new JPanel();
                panel4.setSize(400, 100);
                panel4.add(button3);
                panel4.setAlignmentX(Component.CENTER_ALIGNMENT);
                JPanel panel5 = new JPanel();
                panel5.setSize(400, 100);
                panel5.add(button4);
                panel5.setAlignmentX(Component.CENTER_ALIGNMENT);
                JPanel panel6 = new JPanel();
                panel6.setSize(400, 100);
                panel6.add(button5);
                panel6.setAlignmentX(Component.CENTER_ALIGNMENT);
                JPanel panel7 = new JPanel();
                panel7.setSize(400, 100);
                panel7.add(button6);
                panel7.setAlignmentX(Component.CENTER_ALIGNMENT);
                frame.add(panel);
                frame.add(panel2);
                frame.add(panel3);
                frame.add(panel4);
                frame.add(panel5);
                frame.add(panel6);
                frame.add(panel7);
                frame.setVisible(true);
                ButtonListener.getLoginFrame().setVisible(false);
            } else {
                login = false;
            }
        } else {
            JOptionPane.showMessageDialog(null,"Invalid login");
            ButtonListener.loginUserName.setText("");
            ButtonListener.loginPassword.setText("");
        }
    }
}

class ButtonListener24 implements ActionListener {
    static JTextField amount;

    @Override
    public void actionPerformed(ActionEvent e) {
        String newUsername = null;
        boolean valid = true;
        newUsername = ButtonListener2.createUserName.getText();
        System.out.println(newUsername);
        for (String id : Main.getUsers().keySet()) {
            if (id.equals(newUsername)) {
                JOptionPane.showMessageDialog(null, "This username is already taken");
                ButtonListener2.createUserName.setText("");
                ButtonListener2.createPassword.setText("");
                ButtonListener2.createFname.setText("");
                ButtonListener2.createLname.setText("");
                valid = false;
            }
        }
        if (valid == true) {
            File file = new File("./users/" + newUsername + ".txt");
            try {
                file.createNewFile();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            PrintWriter output = null;
            try {
                output = new PrintWriter("./users/" + newUsername + ".txt");
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            output.println(newUsername);
            String password = null;
            password = ButtonListener2.createPassword.getText();
            String theSalt = "salt";
            MessageDigest alg = null;
            try {
                alg = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
            }
            alg.reset();
            alg.update(password.getBytes());
            byte[] digest = alg.digest();
            StringBuffer hashedpasswd = new StringBuffer();
            String hx;
            for (int i = 0; i < digest.length; i++) {
                hx = Integer.toHexString(0xFF & digest[i]);
                if (hx.length() == 1) {
                    hx = "0" + hx;
                }
                hashedpasswd.append(hx);
            }
            output.println(hashedpasswd);
            output.println(digest);

            String fname = null;
            fname = ButtonListener2.createFname.getText();
            output.println(fname);
            String lname = null;
            lname = ButtonListener2.createLname.getText();
            output.println(lname);
            User mainUser = new User(newUsername, password, theSalt, fname, lname, 0.0, 0.0);
            Main.getUsers().put(newUsername,new User(newUsername,password,theSalt,fname,lname,0.0,0.0));
            output.println(0.00);
            output.println(0.00);
            output.println(0.00);
            output.print(0.00);
            output.close();
            JOptionPane.showMessageDialog(null, "Your account has been successfully created.");
            ButtonListener2.getCreateAccount().setVisible(false);
        }
    }
}

class ButtonListener25 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame("Login");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JButton button1 = new JButton("Check Balance");
        button1.addActionListener(new ButtonListener5());
        JButton button2 = new JButton("Deposit");
        button2.addActionListener(new ButtonListener6());
        JButton button3 = new JButton("Withdraw");
        button3.addActionListener(new ButtonListener7());
        JButton button4 = new JButton("Transfer");
        button4.addActionListener(new ButtonListener8());
        JButton button5 = new JButton("Request a Loan");
        button5.addActionListener(new ButtonListener9());
        JButton button6 = new JButton("Exit");
        button6.addActionListener(new ButtonListener3());
        label.setText("Main Menu");
        panel.setSize(400, 100);
        panel.add(label);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel2 = new JPanel();
        panel2.setSize(400, 100);
        panel2.add(button1);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel3 = new JPanel();
        panel3.setSize(400, 100);
        panel3.add(button2);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel4 = new JPanel();
        panel4.setSize(400, 100);
        panel4.add(button3);
        panel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel5 = new JPanel();
        panel5.setSize(400, 100);
        panel5.add(button4);
        panel5.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel6 = new JPanel();
        panel6.setSize(400, 100);
        panel6.add(button5);
        panel6.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel7 = new JPanel();
        panel7.setSize(400, 100);
        panel7.add(button6);
        panel7.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        frame.add(panel5);
        frame.add(panel6);
        frame.add(panel7);
        frame.setVisible(true);
        ButtonListener.getLoginFrame().setVisible(false);
    }

}

class ButtonListener26 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        JFrame frame = new JFrame("Transfer");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JButton button2 = new JButton("Savings Account");
        button2.addActionListener(new ButtonListener31());
        JButton button3 = new JButton("Money Market Account");
        button3.addActionListener(new ButtonListener32());
        JButton button4 = new JButton("Certificate of Deposit Account");
        button4.addActionListener(new ButtonListener33());
        label.setText("Select an account to transfer to:");
        panel.setSize(400, 100);
        panel.add(label);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel2 = new JPanel();
        panel2.setSize(400, 100);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel3 = new JPanel();
        panel3.setSize(400, 100);
        panel3.add(button2);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel4 = new JPanel();
        panel4.setSize(400, 100);
        panel4.add(button3);
        panel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel5 = new JPanel();
        panel5.setSize(400, 100);
        panel5.add(button4);
        panel5.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        frame.add(panel5);
        frame.setVisible(true);
    }

}

class ButtonListener27 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        PrintWriter output;
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        String strAmount = ButtonListener15.getAmount().getText();
        Double amount = Double.parseDouble(strAmount);
        user.depositChecking(amount);
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        String balance = "Checking account balance: $" + user.getChecking();
        JLabel label = new JLabel(balance);
        panel.add(label);
        frame.add(panel);
        frame.setVisible(true);
        try {
            output = new PrintWriter("./users/" + user.getUsername() + ".txt");
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        output.println(user.getUsername());
        output.println(user.getPassword());
        output.println(user.getSalt());
        output.println(user.getFname());
        output.println(user.getLname());
        output.println(user.getChecking());
        output.println(user.getSavings());
        output.println(user.getMM());
        output.println(user.getCD());
        output.close();

    }

}

class ButtonListener28 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        JFrame frame = new JFrame("Transfer");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JButton button2 = new JButton("Checking Account");
        button2.addActionListener(new ButtonListener34());
        JButton button3 = new JButton("Money Market Account");
        button3.addActionListener(new ButtonListener35());
        JButton button4 = new JButton("Certificate of Deposit Account");
        button4.addActionListener(new ButtonListener36());
        label.setText("Select an account to transfer to:");
        panel.setSize(400, 100);
        panel.add(label);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel2 = new JPanel();
        panel2.setSize(400, 100);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel3 = new JPanel();
        panel3.setSize(400, 100);
        panel3.add(button2);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel4 = new JPanel();
        panel4.setSize(400, 100);
        panel4.add(button3);
        panel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel5 = new JPanel();
        panel5.setSize(400, 100);
        panel5.add(button4);
        panel5.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        frame.add(panel5);
        frame.setVisible(true);
    }

}

class ButtonListener29 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        JFrame frame = new JFrame("Transfer");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JButton button2 = new JButton("Checking Account");
        button2.addActionListener(new ButtonListener37());
        JButton button3 = new JButton("Savings Account");
        button3.addActionListener(new ButtonListener38());
        JButton button4 = new JButton("Certificate of Deposit Account");
        button4.addActionListener(new ButtonListener39());
        label.setText("Select an account to transfer to:");
        panel.setSize(400, 100);
        panel.add(label);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel2 = new JPanel();
        panel2.setSize(400, 100);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel3 = new JPanel();
        panel3.setSize(400, 100);
        panel3.add(button2);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel4 = new JPanel();
        panel4.setSize(400, 100);
        panel4.add(button3);
        panel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel5 = new JPanel();
        panel5.setSize(400, 100);
        panel5.add(button4);
        panel5.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        frame.add(panel5);
        frame.setVisible(true);
    }

}

class ButtonListener30 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        JFrame frame = new JFrame("Transfer");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JButton button2 = new JButton("Checking Account");
        button2.addActionListener(new ButtonListener40());
        JButton button3 = new JButton("Savings Account");
        button3.addActionListener(new ButtonListener41());
        JButton button4 = new JButton("Money Market Account");
        button4.addActionListener(new ButtonListener42());
        label.setText("Select an account to transfer to:");
        panel.setSize(400, 100);
        panel.add(label);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel2 = new JPanel();
        panel2.setSize(400, 100);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel3 = new JPanel();
        panel3.setSize(400, 100);
        panel3.add(button2);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel4 = new JPanel();
        panel4.setSize(400, 100);
        panel4.add(button3);
        panel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel5 = new JPanel();
        panel5.setSize(400, 100);
        panel5.add(button4);
        panel5.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        frame.add(panel5);
        frame.setVisible(true);
    }

}

class ButtonListener31 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        user.transferCheckingSaving(Double.parseDouble(ButtonListener8.getAmount().getText()));
        File fnew = new File("./users/" + ButtonListener.getloginUsername().getText() + ".txt");
        FileWriter f = null;
        try {
            f = new FileWriter(fnew, false);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        try {
            f.write(user.getUsername() + "\n");
            f.write(user.getPassword() + "\n");
            f.write(user.getSalt() + "\n");
            f.write(user.getFname() + "\n");
            f.write(user.getLname() + "\n");
            f.write(String.valueOf(user.getChecking()) + "\n");
            f.write(String.valueOf(user.getSavings()) + "\n");
            f.write(String.valueOf(user.getMM()) + "\n");
            f.write(String.valueOf(user.getCD()) + "\n");
            f.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        JFrame frame = new JFrame("Login");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JButton button1 = new JButton("Check Balance");
        button1.addActionListener(new ButtonListener5());
        JButton button2 = new JButton("Deposit");
        button2.addActionListener(new ButtonListener6());
        JButton button3 = new JButton("Withdraw");
        button3.addActionListener(new ButtonListener7());
        JButton button4 = new JButton("Transfer");
        button4.addActionListener(new ButtonListener8());
        JButton button5 = new JButton("Request a Loan");
        button5.addActionListener(new ButtonListener9());
        JButton button6 = new JButton("Exit");
        button6.addActionListener(new ButtonListener3());
        label.setText("Main Menu");
        panel.setSize(400, 100);
        panel.add(label);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel2 = new JPanel();
        panel2.setSize(400, 100);
        panel2.add(button1);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel3 = new JPanel();
        panel3.setSize(400, 100);
        panel3.add(button2);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel4 = new JPanel();
        panel4.setSize(400, 100);
        panel4.add(button3);
        panel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel5 = new JPanel();
        panel5.setSize(400, 100);
        panel5.add(button4);
        panel5.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel6 = new JPanel();
        panel6.setSize(400, 100);
        panel6.add(button5);
        panel6.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel7 = new JPanel();
        panel7.setSize(400, 100);
        panel7.add(button6);
        panel7.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        frame.add(panel5);
        frame.add(panel6);
        frame.add(panel7);
        frame.setVisible(true);
        ButtonListener.getLoginFrame().setVisible(false);
    }
}

class ButtonListener32 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        user.transferCheckingMM(Double.parseDouble(ButtonListener8.getAmount().getText()));
        File fnew = new File("./users/" + ButtonListener.getloginUsername().getText() + ".txt");
        FileWriter f = null;
        try {
            f = new FileWriter(fnew, false);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        try {
            f.write(user.getUsername() + "\n");
            f.write(user.getPassword() + "\n");
            f.write(user.getSalt() + "\n");
            f.write(user.getFname() + "\n");
            f.write(user.getLname() + "\n");
            f.write(String.valueOf(user.getChecking()) + "\n");
            f.write(String.valueOf(user.getSavings()) + "\n");
            f.write(String.valueOf(user.getMM()) + "\n");
            f.write(String.valueOf(user.getCD()) + "\n");
            f.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        JFrame frame = new JFrame("Login");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JButton button1 = new JButton("Check Balance");
        button1.addActionListener(new ButtonListener5());
        JButton button2 = new JButton("Deposit");
        button2.addActionListener(new ButtonListener6());
        JButton button3 = new JButton("Withdraw");
        button3.addActionListener(new ButtonListener7());
        JButton button4 = new JButton("Transfer");
        button4.addActionListener(new ButtonListener8());
        JButton button5 = new JButton("Request a Loan");
        button5.addActionListener(new ButtonListener9());
        JButton button6 = new JButton("Exit");
        button6.addActionListener(new ButtonListener3());
        label.setText("Main Menu");
        panel.setSize(400, 100);
        panel.add(label);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel2 = new JPanel();
        panel2.setSize(400, 100);
        panel2.add(button1);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel3 = new JPanel();
        panel3.setSize(400, 100);
        panel3.add(button2);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel4 = new JPanel();
        panel4.setSize(400, 100);
        panel4.add(button3);
        panel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel5 = new JPanel();
        panel5.setSize(400, 100);
        panel5.add(button4);
        panel5.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel6 = new JPanel();
        panel6.setSize(400, 100);
        panel6.add(button5);
        panel6.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel7 = new JPanel();
        panel7.setSize(400, 100);
        panel7.add(button6);
        panel7.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        frame.add(panel5);
        frame.add(panel6);
        frame.add(panel7);
        frame.setVisible(true);
        ButtonListener.getLoginFrame().setVisible(false);
    }
}

class ButtonListener33 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        user.transferCheckingCD(Double.parseDouble(ButtonListener8.getAmount().getText()));
        File fnew = new File("./users/" + ButtonListener.getloginUsername().getText() + ".txt");
        FileWriter f = null;
        try {
            f = new FileWriter(fnew, false);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        try {
            f.write(user.getUsername() + "\n");
            f.write(user.getPassword() + "\n");
            f.write(user.getSalt() + "\n");
            f.write(user.getFname() + "\n");
            f.write(user.getLname() + "\n");
            f.write(String.valueOf(user.getChecking()) + "\n");
            f.write(String.valueOf(user.getSavings()) + "\n");
            f.write(String.valueOf(user.getMM()) + "\n");
            f.write(String.valueOf(user.getCD()) + "\n");
            f.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        JFrame frame = new JFrame("Login");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JButton button1 = new JButton("Check Balance");
        button1.addActionListener(new ButtonListener5());
        JButton button2 = new JButton("Deposit");
        button2.addActionListener(new ButtonListener6());
        JButton button3 = new JButton("Withdraw");
        button3.addActionListener(new ButtonListener7());
        JButton button4 = new JButton("Transfer");
        button4.addActionListener(new ButtonListener8());
        JButton button5 = new JButton("Request a Loan");
        button5.addActionListener(new ButtonListener9());
        JButton button6 = new JButton("Exit");
        button6.addActionListener(new ButtonListener3());
        label.setText("Main Menu");
        panel.setSize(400, 100);
        panel.add(label);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel2 = new JPanel();
        panel2.setSize(400, 100);
        panel2.add(button1);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel3 = new JPanel();
        panel3.setSize(400, 100);
        panel3.add(button2);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel4 = new JPanel();
        panel4.setSize(400, 100);
        panel4.add(button3);
        panel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel5 = new JPanel();
        panel5.setSize(400, 100);
        panel5.add(button4);
        panel5.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel6 = new JPanel();
        panel6.setSize(400, 100);
        panel6.add(button5);
        panel6.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel7 = new JPanel();
        panel7.setSize(400, 100);
        panel7.add(button6);
        panel7.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        frame.add(panel5);
        frame.add(panel6);
        frame.add(panel7);
        frame.setVisible(true);
        ButtonListener.getLoginFrame().setVisible(false);
    }
}

class ButtonListener34 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        user.transferSavingsChecking(Double.parseDouble(ButtonListener8.getAmount().getText()));
        File fnew = new File("./users/" + ButtonListener.getloginUsername().getText() + ".txt");
        FileWriter f = null;
        try {
            f = new FileWriter(fnew, false);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        try {
            f.write(user.getUsername() + "\n");
            f.write(user.getPassword() + "\n");
            f.write(user.getSalt() + "\n");
            f.write(user.getFname() + "\n");
            f.write(user.getLname() + "\n");
            f.write(String.valueOf(user.getChecking()) + "\n");
            f.write(String.valueOf(user.getSavings()) + "\n");
            f.write(String.valueOf(user.getMM()) + "\n");
            f.write(String.valueOf(user.getCD()) + "\n");
            f.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        JFrame frame = new JFrame("Login");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JButton button1 = new JButton("Check Balance");
        button1.addActionListener(new ButtonListener5());
        JButton button2 = new JButton("Deposit");
        button2.addActionListener(new ButtonListener6());
        JButton button3 = new JButton("Withdraw");
        button3.addActionListener(new ButtonListener7());
        JButton button4 = new JButton("Transfer");
        button4.addActionListener(new ButtonListener8());
        JButton button5 = new JButton("Request a Loan");
        button5.addActionListener(new ButtonListener9());
        JButton button6 = new JButton("Exit");
        button6.addActionListener(new ButtonListener3());
        label.setText("Main Menu");
        panel.setSize(400, 100);
        panel.add(label);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel2 = new JPanel();
        panel2.setSize(400, 100);
        panel2.add(button1);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel3 = new JPanel();
        panel3.setSize(400, 100);
        panel3.add(button2);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel4 = new JPanel();
        panel4.setSize(400, 100);
        panel4.add(button3);
        panel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel5 = new JPanel();
        panel5.setSize(400, 100);
        panel5.add(button4);
        panel5.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel6 = new JPanel();
        panel6.setSize(400, 100);
        panel6.add(button5);
        panel6.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel7 = new JPanel();
        panel7.setSize(400, 100);
        panel7.add(button6);
        panel7.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        frame.add(panel5);
        frame.add(panel6);
        frame.add(panel7);
        frame.setVisible(true);
        ButtonListener.getLoginFrame().setVisible(false);
    }
}

class ButtonListener35 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        user.transferSavingsMM(Double.parseDouble(ButtonListener8.getAmount().getText()));
        File fnew = new File("./users/" + ButtonListener.getloginUsername().getText() + ".txt");
        FileWriter f = null;
        try {
            f = new FileWriter(fnew, false);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        try {
            f.write(user.getUsername() + "\n");
            f.write(user.getPassword() + "\n");
            f.write(user.getSalt() + "\n");
            f.write(user.getFname() + "\n");
            f.write(user.getLname() + "\n");
            f.write(String.valueOf(user.getChecking()) + "\n");
            f.write(String.valueOf(user.getSavings()) + "\n");
            f.write(String.valueOf(user.getMM()) + "\n");
            f.write(String.valueOf(user.getCD()) + "\n");
            f.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        JFrame frame = new JFrame("Login");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JButton button1 = new JButton("Check Balance");
        button1.addActionListener(new ButtonListener5());
        JButton button2 = new JButton("Deposit");
        button2.addActionListener(new ButtonListener6());
        JButton button3 = new JButton("Withdraw");
        button3.addActionListener(new ButtonListener7());
        JButton button4 = new JButton("Transfer");
        button4.addActionListener(new ButtonListener8());
        JButton button5 = new JButton("Request a Loan");
        button5.addActionListener(new ButtonListener9());
        JButton button6 = new JButton("Exit");
        button6.addActionListener(new ButtonListener3());
        label.setText("Main Menu");
        panel.setSize(400, 100);
        panel.add(label);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel2 = new JPanel();
        panel2.setSize(400, 100);
        panel2.add(button1);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel3 = new JPanel();
        panel3.setSize(400, 100);
        panel3.add(button2);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel4 = new JPanel();
        panel4.setSize(400, 100);
        panel4.add(button3);
        panel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel5 = new JPanel();
        panel5.setSize(400, 100);
        panel5.add(button4);
        panel5.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel6 = new JPanel();
        panel6.setSize(400, 100);
        panel6.add(button5);
        panel6.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel7 = new JPanel();
        panel7.setSize(400, 100);
        panel7.add(button6);
        panel7.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        frame.add(panel5);
        frame.add(panel6);
        frame.add(panel7);
        frame.setVisible(true);
        ButtonListener.getLoginFrame().setVisible(false);
    }
}

class ButtonListener36 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        user.transferSavingsCD(Double.parseDouble(ButtonListener8.getAmount().getText()));
        File fnew = new File("./users/" + ButtonListener.getloginUsername().getText() + ".txt");
        FileWriter f = null;
        try {
            f = new FileWriter(fnew, false);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        try {
            f.write(user.getUsername() + "\n");
            f.write(user.getPassword() + "\n");
            f.write(user.getSalt() + "\n");
            f.write(user.getFname() + "\n");
            f.write(user.getLname() + "\n");
            f.write(String.valueOf(user.getChecking()) + "\n");
            f.write(String.valueOf(user.getSavings()) + "\n");
            f.write(String.valueOf(user.getMM()) + "\n");
            f.write(String.valueOf(user.getCD()) + "\n");
            f.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        JFrame frame = new JFrame("Login");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JButton button1 = new JButton("Check Balance");
        button1.addActionListener(new ButtonListener5());
        JButton button2 = new JButton("Deposit");
        button2.addActionListener(new ButtonListener6());
        JButton button3 = new JButton("Withdraw");
        button3.addActionListener(new ButtonListener7());
        JButton button4 = new JButton("Transfer");
        button4.addActionListener(new ButtonListener8());
        JButton button5 = new JButton("Request a Loan");
        button5.addActionListener(new ButtonListener9());
        JButton button6 = new JButton("Exit");
        button6.addActionListener(new ButtonListener3());
        label.setText("Main Menu");
        panel.setSize(400, 100);
        panel.add(label);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel2 = new JPanel();
        panel2.setSize(400, 100);
        panel2.add(button1);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel3 = new JPanel();
        panel3.setSize(400, 100);
        panel3.add(button2);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel4 = new JPanel();
        panel4.setSize(400, 100);
        panel4.add(button3);
        panel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel5 = new JPanel();
        panel5.setSize(400, 100);
        panel5.add(button4);
        panel5.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel6 = new JPanel();
        panel6.setSize(400, 100);
        panel6.add(button5);
        panel6.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel7 = new JPanel();
        panel7.setSize(400, 100);
        panel7.add(button6);
        panel7.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        frame.add(panel5);
        frame.add(panel6);
        frame.add(panel7);
        frame.setVisible(true);
        ButtonListener.getLoginFrame().setVisible(false);
    }
}

class ButtonListener37 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        user.transferMMChecking(Double.parseDouble(ButtonListener8.getAmount().getText()));
        File fnew = new File("./users/" + ButtonListener.getloginUsername().getText() + ".txt");
        FileWriter f = null;
        try {
            f = new FileWriter(fnew, false);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        try {
            f.write(user.getUsername() + "\n");
            f.write(user.getPassword() + "\n");
            f.write(user.getSalt() + "\n");
            f.write(user.getFname() + "\n");
            f.write(user.getLname() + "\n");
            f.write(String.valueOf(user.getChecking()) + "\n");
            f.write(String.valueOf(user.getSavings()) + "\n");
            f.write(String.valueOf(user.getMM()) + "\n");
            f.write(String.valueOf(user.getCD()) + "\n");
            f.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        JFrame frame = new JFrame("Login");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JButton button1 = new JButton("Check Balance");
        button1.addActionListener(new ButtonListener5());
        JButton button2 = new JButton("Deposit");
        button2.addActionListener(new ButtonListener6());
        JButton button3 = new JButton("Withdraw");
        button3.addActionListener(new ButtonListener7());
        JButton button4 = new JButton("Transfer");
        button4.addActionListener(new ButtonListener8());
        JButton button5 = new JButton("Request a Loan");
        button5.addActionListener(new ButtonListener9());
        JButton button6 = new JButton("Exit");
        button6.addActionListener(new ButtonListener3());
        label.setText("Main Menu");
        panel.setSize(400, 100);
        panel.add(label);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel2 = new JPanel();
        panel2.setSize(400, 100);
        panel2.add(button1);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel3 = new JPanel();
        panel3.setSize(400, 100);
        panel3.add(button2);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel4 = new JPanel();
        panel4.setSize(400, 100);
        panel4.add(button3);
        panel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel5 = new JPanel();
        panel5.setSize(400, 100);
        panel5.add(button4);
        panel5.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel6 = new JPanel();
        panel6.setSize(400, 100);
        panel6.add(button5);
        panel6.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel7 = new JPanel();
        panel7.setSize(400, 100);
        panel7.add(button6);
        panel7.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        frame.add(panel5);
        frame.add(panel6);
        frame.add(panel7);
        frame.setVisible(true);
        ButtonListener.getLoginFrame().setVisible(false);
    }
}

class ButtonListener38 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        user.transferMMSavings(Double.parseDouble(ButtonListener8.getAmount().getText()));
        File fnew = new File("./users/" + ButtonListener.getloginUsername().getText() + ".txt");
        FileWriter f = null;
        try {
            f = new FileWriter(fnew, false);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        try {
            f.write(user.getUsername() + "\n");
            f.write(user.getPassword() + "\n");
            f.write(user.getSalt() + "\n");
            f.write(user.getFname() + "\n");
            f.write(user.getLname() + "\n");
            f.write(String.valueOf(user.getChecking()) + "\n");
            f.write(String.valueOf(user.getSavings()) + "\n");
            f.write(String.valueOf(user.getMM()) + "\n");
            f.write(String.valueOf(user.getCD()) + "\n");
            f.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        JFrame frame = new JFrame("Login");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JButton button1 = new JButton("Check Balance");
        button1.addActionListener(new ButtonListener5());
        JButton button2 = new JButton("Deposit");
        button2.addActionListener(new ButtonListener6());
        JButton button3 = new JButton("Withdraw");
        button3.addActionListener(new ButtonListener7());
        JButton button4 = new JButton("Transfer");
        button4.addActionListener(new ButtonListener8());
        JButton button5 = new JButton("Request a Loan");
        button5.addActionListener(new ButtonListener9());
        JButton button6 = new JButton("Exit");
        button6.addActionListener(new ButtonListener3());
        label.setText("Main Menu");
        panel.setSize(400, 100);
        panel.add(label);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel2 = new JPanel();
        panel2.setSize(400, 100);
        panel2.add(button1);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel3 = new JPanel();
        panel3.setSize(400, 100);
        panel3.add(button2);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel4 = new JPanel();
        panel4.setSize(400, 100);
        panel4.add(button3);
        panel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel5 = new JPanel();
        panel5.setSize(400, 100);
        panel5.add(button4);
        panel5.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel6 = new JPanel();
        panel6.setSize(400, 100);
        panel6.add(button5);
        panel6.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel7 = new JPanel();
        panel7.setSize(400, 100);
        panel7.add(button6);
        panel7.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        frame.add(panel5);
        frame.add(panel6);
        frame.add(panel7);
        frame.setVisible(true);
        ButtonListener.getLoginFrame().setVisible(false);
    }
}

class ButtonListener39 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        user.transferMMCD(Double.parseDouble(ButtonListener8.getAmount().getText()));
        File fnew = new File("./users/" + ButtonListener.getloginUsername().getText() + ".txt");
        FileWriter f = null;
        try {
            f = new FileWriter(fnew, false);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        try {
            f.write(user.getUsername() + "\n");
            f.write(user.getPassword() + "\n");
            f.write(user.getSalt() + "\n");
            f.write(user.getFname() + "\n");
            f.write(user.getLname() + "\n");
            f.write(String.valueOf(user.getChecking()) + "\n");
            f.write(String.valueOf(user.getSavings()) + "\n");
            f.write(String.valueOf(user.getMM()) + "\n");
            f.write(String.valueOf(user.getCD()) + "\n");
            f.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        JFrame frame = new JFrame("Login");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JButton button1 = new JButton("Check Balance");
        button1.addActionListener(new ButtonListener5());
        JButton button2 = new JButton("Deposit");
        button2.addActionListener(new ButtonListener6());
        JButton button3 = new JButton("Withdraw");
        button3.addActionListener(new ButtonListener7());
        JButton button4 = new JButton("Transfer");
        button4.addActionListener(new ButtonListener8());
        JButton button5 = new JButton("Request a Loan");
        button5.addActionListener(new ButtonListener9());
        JButton button6 = new JButton("Exit");
        button6.addActionListener(new ButtonListener3());
        label.setText("Main Menu");
        panel.setSize(400, 100);
        panel.add(label);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel2 = new JPanel();
        panel2.setSize(400, 100);
        panel2.add(button1);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel3 = new JPanel();
        panel3.setSize(400, 100);
        panel3.add(button2);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel4 = new JPanel();
        panel4.setSize(400, 100);
        panel4.add(button3);
        panel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel5 = new JPanel();
        panel5.setSize(400, 100);
        panel5.add(button4);
        panel5.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel6 = new JPanel();
        panel6.setSize(400, 100);
        panel6.add(button5);
        panel6.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel7 = new JPanel();
        panel7.setSize(400, 100);
        panel7.add(button6);
        panel7.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        frame.add(panel5);
        frame.add(panel6);
        frame.add(panel7);
        frame.setVisible(true);
        ButtonListener.getLoginFrame().setVisible(false);
    }
}

class ButtonListener40 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        user.transferCDChecking(Double.parseDouble(ButtonListener8.getAmount().getText()));
        File fnew = new File("./users/" + ButtonListener.getloginUsername().getText() + ".txt");
        FileWriter f = null;
        try {
            f = new FileWriter(fnew, false);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        try {
            f.write(user.getUsername() + "\n");
            f.write(user.getPassword() + "\n");
            f.write(user.getSalt() + "\n");
            f.write(user.getFname() + "\n");
            f.write(user.getLname() + "\n");
            f.write(String.valueOf(user.getChecking()) + "\n");
            f.write(String.valueOf(user.getSavings()) + "\n");
            f.write(String.valueOf(user.getMM()) + "\n");
            f.write(String.valueOf(user.getCD()) + "\n");
            f.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        JFrame frame = new JFrame("Login");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JButton button1 = new JButton("Check Balance");
        button1.addActionListener(new ButtonListener5());
        JButton button2 = new JButton("Deposit");
        button2.addActionListener(new ButtonListener6());
        JButton button3 = new JButton("Withdraw");
        button3.addActionListener(new ButtonListener7());
        JButton button4 = new JButton("Transfer");
        button4.addActionListener(new ButtonListener8());
        JButton button5 = new JButton("Request a Loan");
        button5.addActionListener(new ButtonListener9());
        JButton button6 = new JButton("Exit");
        button6.addActionListener(new ButtonListener3());
        label.setText("Main Menu");
        panel.setSize(400, 100);
        panel.add(label);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel2 = new JPanel();
        panel2.setSize(400, 100);
        panel2.add(button1);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel3 = new JPanel();
        panel3.setSize(400, 100);
        panel3.add(button2);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel4 = new JPanel();
        panel4.setSize(400, 100);
        panel4.add(button3);
        panel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel5 = new JPanel();
        panel5.setSize(400, 100);
        panel5.add(button4);
        panel5.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel6 = new JPanel();
        panel6.setSize(400, 100);
        panel6.add(button5);
        panel6.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel7 = new JPanel();
        panel7.setSize(400, 100);
        panel7.add(button6);
        panel7.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        frame.add(panel5);
        frame.add(panel6);
        frame.add(panel7);
        frame.setVisible(true);
        ButtonListener.getLoginFrame().setVisible(false);
    }
}

class ButtonListener41 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        user.transferCDSavings(Double.parseDouble(ButtonListener8.getAmount().getText()));
        File fnew = new File("./users/" + ButtonListener.getloginUsername().getText() + ".txt");
        FileWriter f = null;
        try {
            f = new FileWriter(fnew, false);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        try {
            f.write(user.getUsername() + "\n");
            f.write(user.getPassword() + "\n");
            f.write(user.getSalt() + "\n");
            f.write(user.getFname() + "\n");
            f.write(user.getLname() + "\n");
            f.write(String.valueOf(user.getChecking()) + "\n");
            f.write(String.valueOf(user.getSavings()) + "\n");
            f.write(String.valueOf(user.getMM()) + "\n");
            f.write(String.valueOf(user.getCD()) + "\n");
            f.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        JFrame frame = new JFrame("Login");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JButton button1 = new JButton("Check Balance");
        button1.addActionListener(new ButtonListener5());
        JButton button2 = new JButton("Deposit");
        button2.addActionListener(new ButtonListener6());
        JButton button3 = new JButton("Withdraw");
        button3.addActionListener(new ButtonListener7());
        JButton button4 = new JButton("Transfer");
        button4.addActionListener(new ButtonListener8());
        JButton button5 = new JButton("Request a Loan");
        button5.addActionListener(new ButtonListener9());
        JButton button6 = new JButton("Exit");
        button6.addActionListener(new ButtonListener3());
        label.setText("Main Menu");
        panel.setSize(400, 100);
        panel.add(label);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel2 = new JPanel();
        panel2.setSize(400, 100);
        panel2.add(button1);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel3 = new JPanel();
        panel3.setSize(400, 100);
        panel3.add(button2);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel4 = new JPanel();
        panel4.setSize(400, 100);
        panel4.add(button3);
        panel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel5 = new JPanel();
        panel5.setSize(400, 100);
        panel5.add(button4);
        panel5.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel6 = new JPanel();
        panel6.setSize(400, 100);
        panel6.add(button5);
        panel6.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel7 = new JPanel();
        panel7.setSize(400, 100);
        panel7.add(button6);
        panel7.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        frame.add(panel5);
        frame.add(panel6);
        frame.add(panel7);
        frame.setVisible(true);
        ButtonListener.getLoginFrame().setVisible(false);
    }
}

class ButtonListener42 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        user.transferCDMM(Double.parseDouble(ButtonListener8.getAmount().getText()));
        JFrame frame = new JFrame("Login");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JButton button1 = new JButton("Check Balance");
        button1.addActionListener(new ButtonListener5());
        JButton button2 = new JButton("Deposit");
        button2.addActionListener(new ButtonListener6());
        JButton button3 = new JButton("Withdraw");
        button3.addActionListener(new ButtonListener7());
        JButton button4 = new JButton("Transfer");
        button4.addActionListener(new ButtonListener8());
        JButton button5 = new JButton("Request a Loan");
        button5.addActionListener(new ButtonListener9());
        JButton button6 = new JButton("Exit");
        button6.addActionListener(new ButtonListener3());
        label.setText("Main Menu");
        panel.setSize(400, 100);
        panel.add(label);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel2 = new JPanel();
        panel2.setSize(400, 100);
        panel2.add(button1);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel3 = new JPanel();
        panel3.setSize(400, 100);
        panel3.add(button2);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel4 = new JPanel();
        panel4.setSize(400, 100);
        panel4.add(button3);
        panel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel5 = new JPanel();
        panel5.setSize(400, 100);
        panel5.add(button4);
        panel5.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel6 = new JPanel();
        panel6.setSize(400, 100);
        panel6.add(button5);
        panel6.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel7 = new JPanel();
        panel7.setSize(400, 100);
        panel7.add(button6);
        panel7.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(panel);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        frame.add(panel5);
        frame.add(panel6);
        frame.add(panel7);
        frame.setVisible(true);
        ButtonListener.getLoginFrame().setVisible(false);
    }
}

class ButtonListener43 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        PrintWriter output;
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        String strAmount = ButtonListener16.getAmount().getText();
        Double amount = Double.parseDouble(strAmount);
        user.depositSavings(amount);
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        String balance = "Savings account balance: $" + user.getSavings();
        JLabel label = new JLabel(balance);
        panel.add(label);
        frame.add(panel);
        frame.setVisible(true);
        try {
            output = new PrintWriter("./users/" + user.getUsername() + ".txt");
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        output.println(user.getUsername());
        output.println(user.getPassword());
        output.println(user.getSalt());
        output.println(user.getFname());
        output.println(user.getLname());
        output.println(user.getChecking());
        output.println(user.getSavings());
        output.println(user.getMM());
        output.println(user.getCD());
        output.close();

    }

}

class ButtonListener44 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        PrintWriter output;
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        String strAmount = ButtonListener17.getAmount().getText();
        Double amount = Double.parseDouble(strAmount);
        user.depositMoneyMarket(amount);
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        String balance = "Money Market account balance: $" + user.getMM();
        JLabel label = new JLabel(balance);
        panel.add(label);
        frame.add(panel);
        frame.setVisible(true);
        try {
            output = new PrintWriter("./users/" + user.getUsername() + ".txt");
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        output.println(user.getUsername());
        output.println(user.getPassword());
        output.println(user.getSalt());
        output.println(user.getFname());
        output.println(user.getLname());
        output.println(user.getChecking());
        output.println(user.getSavings());
        output.println(user.getMM());
        output.println(user.getCD());
        output.close();

    }


}

class ButtonListener45 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        PrintWriter output;
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        String strAmount = ButtonListener18.getAmount().getText();
        Double amount = Double.parseDouble(strAmount);
        user.depositCertificateDeposit(amount);
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        String balance = "Certificate of Deposit account balance: $" + user.getCD();
        JLabel label = new JLabel(balance);
        panel.add(label);
        frame.add(panel);
        frame.setVisible(true);
        try {
            output = new PrintWriter("./users/" + user.getUsername() + ".txt");
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        output.println(user.getUsername());
        output.println(user.getPassword());
        output.println(user.getSalt());
        output.println(user.getFname());
        output.println(user.getLname());
        output.println(user.getChecking());
        output.println(user.getSavings());
        output.println(user.getMM());
        output.println(user.getCD());
        output.close();

    }

}

class ButtonListener46 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        PrintWriter output;
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        String strAmount = ButtonListener19.getAmount().getText();
        Double amount = Double.parseDouble(strAmount);
        user.withdrawChecking(amount);
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        String balance = "Checking account balance: $" + user.getChecking();
        JLabel label = new JLabel(balance);
        panel.add(label);
        frame.add(panel);
        frame.setVisible(true);
        try {
            output = new PrintWriter("./users/" + user.getUsername() + ".txt");
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        output.println(user.getUsername());
        output.println(user.getPassword());
        output.println(user.getSalt());
        output.println(user.getFname());
        output.println(user.getLname());
        output.println(user.getChecking());
        output.println(user.getSavings());
        output.println(user.getMM());
        output.println(user.getCD());
        output.close();

    }

}

class ButtonListener47 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        PrintWriter output;
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        String strAmount = ButtonListener20.getAmount().getText();
        Double amount = Double.parseDouble(strAmount);
        user.withdrawSavings(amount);
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        String balance = "Savings account balance: $" + user.getSavings();
        JLabel label = new JLabel(balance);
        panel.add(label);
        frame.add(panel);
        frame.setVisible(true);
        try {
            output = new PrintWriter("./users/" + user.getUsername() + ".txt");
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        output.println(user.getUsername());
        output.println(user.getPassword());
        output.println(user.getSalt());
        output.println(user.getFname());
        output.println(user.getLname());
        output.println(user.getChecking());
        output.println(user.getSavings());
        output.println(user.getMM());
        output.println(user.getCD());
        output.close();

    }

}

class ButtonListener48 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        PrintWriter output;
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        String strAmount = ButtonListener21.getAmount().getText();
        Double amount = Double.parseDouble(strAmount);
        user.withdrawMoneyMarket(amount);
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        String balance = "Money Market account balance: $" + user.getMM();
        JLabel label = new JLabel(balance);
        panel.add(label);
        frame.add(panel);
        frame.setVisible(true);
        try {
            output = new PrintWriter("./users/" + user.getUsername() + ".txt");
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        output.println(user.getUsername());
        output.println(user.getPassword());
        output.println(user.getSalt());
        output.println(user.getFname());
        output.println(user.getLname());
        output.println(user.getChecking());
        output.println(user.getSavings());
        output.println(user.getMM());
        output.println(user.getCD());
        output.close();

    }

}

class ButtonListener49 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        PrintWriter output;
        User user = null;
        String username = ButtonListener.getloginUsername().getText();
        for (String name : Main.getUsers().keySet()) {
            if (username.equals(name)) {
                user = Main.getUsers().get(name);
            }
        }
        String strAmount = ButtonListener22.getAmount().getText();
        Double amount = Double.parseDouble(strAmount);
        user.withdrawCertificateDeposit(amount);
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(300, 400);
        JPanel panel = new JPanel();
        String balance = "Certificate of Deposit account balance: $" + user.getCD();
        JLabel label = new JLabel(balance);
        panel.add(label);
        frame.add(panel);
        frame.setVisible(true);
        try {
            output = new PrintWriter("./users/" + user.getUsername() + ".txt");
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        output.println(user.getUsername());
        output.println(user.getPassword());
        output.println(user.getSalt());
        output.println(user.getFname());
        output.println(user.getLname());
        output.println(user.getChecking());
        output.println(user.getSavings());
        output.println(user.getMM());
        output.println(user.getCD());
        output.close();

    }

}