public class UserData {
    private String fullName;
    private String selectedState;
    private String selectedCity;
    private String selectedBloodGroup;
    private String email;

    // Constructor
    public UserData() {
        // Initialize the object
    }

    // Getter methods (if needed)
    public String getFullName() {
        return fullName;
    }

    // Setter methods
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSelectedState() {
        return selectedState;
    }

    public void setSelectedState(String selectedState) {
        this.selectedState = selectedState;
    }

    public String getSelectedCity() {
        return selectedCity;
    }

    public void setSelectedCity(String selectedCity) {
        this.selectedCity = selectedCity;
    }

    public String getSelectedBloodGroup() {
        return selectedBloodGroup;
    }

    public void setSelectedBloodGroup(String selectedBloodGroup) {
        this.selectedBloodGroup = selectedBloodGroup;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
