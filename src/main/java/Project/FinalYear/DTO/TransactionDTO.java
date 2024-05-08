package Project.FinalYear.DTO;

public class TransactionDTO {
    private String type;
    private double amount;
    private double oldbalanceOrg;
    private double newbalanceOrig;
    private double oldbalanceDest;
    private double newbalanceDest;
    private String nameOrig;
    private String nameDest;

    // Default constructor
    public TransactionDTO() {
    }

    // Parameterized constructor
    public TransactionDTO(String type, double amount, double oldbalanceOrg, double newbalanceOrig, double oldbalanceDest, double newbalanceDest, String nameOrig, String nameDest) {
        this.type = type;
        this.amount = amount;
        this.oldbalanceOrg = oldbalanceOrg;
        this.newbalanceOrig = newbalanceOrig;
        this.oldbalanceDest = oldbalanceDest;
        this.newbalanceDest = newbalanceDest;
        this.nameOrig = nameOrig;
        this.nameDest = nameDest;


    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNameDest(){return nameDest;};

    public void setNameDest(String nameDest){this.nameDest= nameDest;}

    public String getNameOrig(){return nameOrig;};

    public void setNameOrig(String nameOrig){this.nameOrig= nameOrig;}

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getOldbalanceOrg() {
        return oldbalanceOrg;
    }

    public void setOldbalanceOrg(double oldbalanceOrg) {
        this.oldbalanceOrg = oldbalanceOrg;
    }

    public double getNewbalanceOrig() {
        return newbalanceOrig;
    }

    public void setNewbalanceOrig(double newbalanceOrig) {
        this.newbalanceOrig = newbalanceOrig;
    }

    public double getOldbalanceDest() {
        return oldbalanceDest;
    }

    public void setOldbalanceDest(double oldbalanceDest) {
        this.oldbalanceDest = oldbalanceDest;
    }

    public double getNewbalanceDest() {
        return newbalanceDest;
    }

    public void setNewbalanceDest(double newbalanceDest) {
        this.newbalanceDest = newbalanceDest;
    }

}


