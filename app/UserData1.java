public class UserData1 {
    private String name;
    private String email;
    private String phoneNumber;
    private String aadharNumber;
    private String age;
    private String weight;
    private String illness;
    private String addiction;

    public UserData1() {
        // Default constructor required for Firebase Realtime Database
    }

    public UserData1(String name, String email, String phoneNumber, String aadharNumber,
                     String age, String weight, String illness, String addiction) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.aadharNumber = aadharNumber;
        this.age = age;
        this.weight = weight;
        this.illness = illness;
        this.addiction = addiction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getIllness() {
        return illness;
    }

    public void setIllness(String illness) {
        this.illness = illness;
    }

    public String getAddiction() {
        return addiction;
    }

    public void setAddiction(String addiction) {
        this.addiction = addiction;
    }
}
