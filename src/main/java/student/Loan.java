package student;

import adminbook.Book;

import java.time.LocalDate;

public class Loan {
    private String nim;
    private String bookId;
    private String title;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status;

    // Constructors
    public Loan(String nim, String bookId, String title, LocalDate loanDate, LocalDate dueDate, LocalDate returnDate) {
        this.nim = nim;
        this.bookId = bookId;
        this.title = title;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = calculateStatus();
    }

    public Loan(String id, String bookId, String title, LocalDate loanDate, LocalDate dueDate) {
        this.bookId = bookId;
        this.title = title;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.status = calculateStatus();
    }

    public Loan(String nimStudent, String bookId, LocalDate loanDate, LocalDate dueDate, LocalDate returnDate, Book book) {
        this.nim = nimStudent;
        this.bookId = bookId;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.title = book.getTitle();
        this.status = calculateStatus();
    }

    // Getters and Setters
    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
        this.status = calculateStatus();
    }

    public String getStatus() {
        return status;
    }

    private String calculateStatus() {
        if (returnDate == null) {
            if (dueDate != null && LocalDate.now().isAfter(dueDate)) {
                return "Terlambat";
            } else {
                return "Belum Mengembalikan";
            }
        } else {
            if (dueDate != null && returnDate.isAfter(dueDate)) {
                return "Sudah Mengembalikan dengan Denda";
            } else {
                return "Sudah Mengembalikan";
            }
        }
    }
}
