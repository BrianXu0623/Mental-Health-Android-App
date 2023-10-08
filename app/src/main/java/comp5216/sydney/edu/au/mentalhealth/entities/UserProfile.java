package comp5216.sydney.edu.au.mentalhealth.entities;

public class UserProfile {
    private String userId;
    private String userName;
    private boolean Doc;
    private boolean Hidden;
    private String userbirth;
    private String userEmall;
    private String userHobbies;
    private String userMajor;
    private String userDes;

    public UserProfile(){}
    public UserProfile(String UserId, String UserName, boolean IsDoc, boolean Hidden,String UserEmall, String UserHobbies, String UserMajor, String UserDes){
        this.userId = UserId;
        this.userName = UserName;
        this.Doc = IsDoc;
        this.Hidden = Hidden;
        this.userEmall = UserEmall;
        this.userHobbies = UserHobbies;
        this.userMajor = UserMajor;
        this.userDes = UserDes;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isDoc() {
        return Doc;
    }

    public void setDoc(boolean doc) {
        Doc = doc;
    }

    public boolean isHidden() {
        return Hidden;
    }

    public void setHidden(boolean hidden) {
        Hidden = hidden;
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
