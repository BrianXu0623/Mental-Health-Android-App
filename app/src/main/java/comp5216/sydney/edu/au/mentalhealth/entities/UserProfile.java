package comp5216.sydney.edu.au.mentalhealth.entities;

public class UserProfile {

    private String userName;
    private boolean doc;
    private boolean hidden;
    private String phone;
    private String email;
    private String hobbies;

    private String info;


    public UserProfile(){}
    public UserProfile(String UserName, boolean IsDoc, boolean Hidden,String phone, String email,
                       String hobbies, String info){

        this.userName = UserName;
        this.doc = IsDoc;
        this.hidden = Hidden;
        this.phone = phone;
        this.email =email;
        this.hobbies = hobbies;
        this.info = info;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isDoc() {
        return doc;
    }

    public void setDoc(boolean doc) {
        this.doc = doc;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getphone() {
        return phone;
    }

    public void setphone(String phone) {
        this.phone = phone;
    }

    public String getemail() {
        return email;
    }

    public void setemail(String email) {
        this.email = email;
    }

    public String gethobbies() {
        return hobbies;
    }

    public void sethobbies(String hobbies) {
        this.hobbies = hobbies;
    }


    public String getinfo() {
        return info;
    }

    public void setinfo(String info) {
        this.info = info;
    }


}
