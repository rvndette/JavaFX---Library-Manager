package adminbook;

public class Book {
    private String id;
    private String title;
    private String author;
    private String genre;
    private int stock;
    private byte[] cover;

    public Book(String id, String title, String author, String genre, int stock, byte[] cover) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.stock = stock;
        this.cover = cover;
    }

    public Book(String id, String title) {
        this.id = id;
        this.title = title;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public byte[] getCover() {
        return cover;
    }

    public void setCover(byte[] cover) {
        this.cover = cover;
    }
}
