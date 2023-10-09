package comp5216.sydney.edu.au.mentalhealth.entities;

public class UserProfile {

    private String userName;
    private boolean doc;
    private boolean hidden;
    private String userbirth;
    private String userEmall;
    private String userHobbies;
    private String userMajor;
    private String userDes;

    public UserProfile(){}
    public UserProfile(String UserName, boolean IsDoc, boolean Hidden,String userbirth, String userEmall, String userHobbies, String userMajor, String userDes){

        this.userName = UserName;
        this.doc = IsDoc;
        this.hidden = Hidden;
        this.userbirth = userbirth;
        this.userEmall = userEmall;
        this.userHobbies = userHobbies;
        this.userMajor = userMajor;
        this.userDes = userDes;
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

    public String getUserbirth() {
        return userbirth;
    }

    public void setUserbirth(String userbirth) {
        this.userbirth = userbirth;
    }

    public String getUserEmall() {
        return userEmall;
    }

    public void setUserEmall(String userEmall) {
        this.userEmall = userEmall;
    }

    public String getUserHobbies() {
        return userHobbies;
    }

    public void setUserHobbies(String userHobbies) {
        this.userHobbies = userHobbies;
    }

    public String getUserMajor() {
        return userMajor;
    }

    public void setUserMajor(String userMajor) {
        this.userMajor = userMajor;
    }

    public String getUserDes() {
        return userDes;
    }

    public void setUserDes(String userDes) {
        this.userDes = userDes;
    }
}
