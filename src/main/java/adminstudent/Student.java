package adminstudent;

public class Student {
    private String name;
    private String nim;
    private String faculty;
    private String major;
    private String email;
    private String pic;

    public Student(String name, String nim, String faculty, String major, String email, String pic) {
        this.name = name;
        this.nim = nim;
        this.faculty = faculty;
        this.major = major;
        this.email = email;
        this.pic = pic;
    }

    // Getters for all fields
    public String getName() {
        return name;
    }

    public String getNim() {
        return nim;
    }

    public String getFaculty() {
        return faculty;
    }

    public String getMajor() {
        return major;
    }

    public String getEmail() {
        return email;
    }

    public String getPic() {
        return pic;
    }
}
