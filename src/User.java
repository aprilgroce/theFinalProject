import java.util.Scanner;

/**
 * @author James Amador, Kaylie Curran, April Groce
 */
public class User {
    private String username;
    private String password;
    private String fname;
    private String lname;
    private String salt;
    private double checking;
    private double savings;
    private double moneyMarket;
    private double certificateDeposit;

    /**
     * This constructor is for new users. All users start with no money
     * in their money market and CDs, but the amount in their checking
     * and savings account is subject to whatever the user says they start
     * with on account creation.
     *
     * @param uname    Stores the username of the user
     * @param fname    Stores the user's first name
     * @param checkAct Stores the amount of money in the user's checking account
     * @param saveAct  Stores the amount of money in the user's savings account
     */
    public User(String uname, String pass, String salt, String fname, String lname, double checkAct, double saveAct) {
        this.username = uname;
        this.password = pass;
        this.salt = salt;
        this.fname = fname;
        this.lname = lname;
        this.checking = checkAct;
        this.savings = saveAct;
        this.moneyMarket = 0.00;
        this.certificateDeposit = 0.00;
    }

    /**
     * This constructor is for existing users. The information for each user is stored
     * in one file per user, with the filename being the user's username. This, of course,
     * is subject to change if another efficient method is found for storing user info.
     * This is based off of the Card constructor found in the SRS, with some heavy modifications.
     *
     * @param read Scanner reads a file and parses each line for user information.
     */
    public User(Scanner read) {
        this.username = read.nextLine();
        this.password = read.nextLine();
        this.salt = read.nextLine();
        this.fname = read.nextLine();
        this.lname = read.nextLine();
        this.checking = read.nextDouble();
        this.savings = read.nextDouble();
        this.moneyMarket = read.nextDouble();
        this.certificateDeposit = read.nextDouble();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    /**
     * Deposits a user specified amount into the user's checking account
     *
     * @param balance the amount to be deposited into the checking account
     */
    public double depositChecking(double balance) {

        this.checking += balance;
        return this.checking;
    }

    public void depositSavings(double balance) {
        this.savings += balance;
    }

    public void depositMoneyMarket(double balance) {
        this.moneyMarket += balance;
    }

    public void depositCertificateDeposit(double balance) {
        this.certificateDeposit += balance;
    }

    /**
     * Withdraws a user specified amount from the user's checking account
     *
     * @param balance the amount to be withdrawn from the checking account
     */
    public void withdrawChecking(double balance) {
        if ((this.checking - balance) >= 0) {
            this.checking -= balance;
        } else {
            this.checking -= 0;
        }
    }

    public void withdrawSavings(double balance) {
        if ((this.savings - balance) >= 0) {
            this.savings -= balance;
        } else {
            this.savings -= 0;
        }
    }

    public void withdrawMoneyMarket(double balance) {
        if ((this.moneyMarket - balance) >= 0) {
            this.moneyMarket -= balance;
        } else {
            this.moneyMarket -= 0;
        }
    }

    public void withdrawCertificateDeposit(double balance) {
        if ((this.certificateDeposit - balance) >= 0) {
            this.certificateDeposit -= balance;
        } else {
            this.certificateDeposit -= 0;
        }
    }

    public void balanceChecking() {
        System.out.println("Checking Account: $" + this.checking);
        System.out.println("");
    }

    public void balanceSavings() {
        System.out.println("Savings Account: $" + this.savings);
        System.out.println("");
    }

    public void balanceMoneyMarket() {
        System.out.println("Money Market Account: $" + this.moneyMarket);
        System.out.println("");
    }

    public void balanceCertificateDeposit() {
        System.out.println("Certificate of Deposit Account: $" + this.certificateDeposit);
        System.out.println("");
    }

    public void balance() {
        System.out.println("Checking Account: $" + this.checking);
        System.out.println("Savings Account: $" + this.savings);
        System.out.println("Money Market Account: $" + this.moneyMarket);
        System.out.println("Certificate of Deposit Account: $" + this.certificateDeposit);
        System.out.println("");
    }

    public void transferCheckingSaving(double balance) {
        withdrawChecking(balance);
        depositSavings(balance);
    }

    public void transferCheckingMM(double balance) {
        withdrawChecking(balance);
        depositMoneyMarket(balance);
    }

    public void transferCheckingCD(double balance) {
        withdrawChecking(balance);
        depositCertificateDeposit(balance);
    }

    public void transferSavingsChecking(double balance) {
        withdrawSavings(balance);
        depositChecking(balance);
    }

    public void transferSavingsMM(double balance) {
        withdrawSavings(balance);
        depositMoneyMarket(balance);
    }

    public void transferSavingsCD(double balance) {
        withdrawSavings(balance);
        depositCertificateDeposit(balance);
    }

    public void transferMMChecking(double balance) {
        withdrawMoneyMarket(balance);
        depositChecking(balance);
    }

    public void transferMMSavings(double balance) {
        withdrawMoneyMarket(balance);
        depositSavings(balance);
    }

    public void transferMMCD(double balance) {
        withdrawMoneyMarket(balance);
        depositCertificateDeposit(balance);
    }

    public void transferCDChecking(double balance) {
        withdrawCertificateDeposit(balance);
        depositChecking(balance);
    }

    public void transferCDSavings(double balance) {
        withdrawCertificateDeposit(balance);
        depositSavings(balance);
    }

    public void transferCDMM(double balance) {
        withdrawCertificateDeposit(balance);
        depositMoneyMarket(balance);
    }

    /**
     * Retrieve's the amount of money in the user's checking account
     *
     * @return amount of money in user's checking account
     */
    public double getChecking() {
        return this.checking;
    }

    /**
     * Retrieve's the amount of money in hte user's savings account
     *
     * @return amount of money in user's savings account
     */
    public double getSavings() {
        return this.savings;
    }

    /**
     * Retrieve's the amount of money in the user's money market
     *
     * @return amount of money in the user's money market
     */
    public double getMM() {
        return this.moneyMarket;
    }

    /**
     * Retrieve's the amount of money that is in the user's Certificate Deposit
     *
     * @return the amount of money in the user's Certificate Deposit
     */
    public double getCD() {
        return this.certificateDeposit;
    }
}