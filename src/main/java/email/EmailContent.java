package email;

public class EmailContent {

    public String emailVisitor(String username, String email) {
        return "Dear " + username + ",\n\n" +
                "Selamat bergabung dengan komunitas kami! Akun Anda telah berhasil dibuat.\n" +
                "Detail Akun Anda:\n" +
                "- Nama Pengguna: " + username + "\n" +
                "- Email: " + email + "\n\n" +
                "Jangan ragu untuk menghubungi kami jika Anda memiliki pertanyaan atau membutuhkan bantuan lebih lanjut.\n" +
                "Kami siap membantu Anda dengan segala yang Anda butuhkan.\n\n" +
                "Terima kasih atas kepercayaan Anda kepada kami. Selamat menikmati layanan dan pengalaman yang kami tawarkan!\n\n" +
                "Salam hangat,\n" +
                "Tim UMM Library";
    }

    public String emailStudent(String fullName, String nim) {
        return "Dear " + fullName + ",\n\n" +
                "Welcome to our community! Your account with NIM " + nim + " has been successfully created.\n" +
                "Feel free to explore our services and resources.\n\n" +
                "If you have any questions, don't hesitate to reach out to us.\n" +
                "Enjoy your experience with us!\n\n" +
                "Best regards,\n" +
                "The UMM Library Team";
    }

    public String borrowBookEmail(String fullName, String nim, String bookTitle, String returnDate) {
        return "Dear " + fullName + ",\n\n" +
                "You have successfully borrowed the book '" + bookTitle + "'.\n" +
                "Please remember to return it by " + returnDate + ".\n\n" +
                "Thank you for using our library services!\n\n" +
                "Best regards,\n" +
                "The UMM Library Team";
    }
}
