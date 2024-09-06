import java.sql.*;
import java.util.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Main {
  static final String url = "jdbc:mysql://localhost:3306/library";
  static final String userName = "root";
  static final String passWord = "2004";

  public static void main(String[] args) throws Exception {
    Scanner sc = new Scanner(System.in);
    System.out.println("Welcome to Library");
    boolean state = true;
    while (state) {
      System.out.println("\n1. Explore books\n2. Add book\n3. Update book\n4. Delete book\n5. Exit");
      int mode = sc.nextInt();
      switch (mode) {
        case 1:
          readBook();
          break;
        case 2:
          insertBook();
          break;
        case 3:
          readBook();
          updateBook();
          break;
        case 4:
          deleteBook();
          break;
        case 5:
          System.out.println("Quiting");
          state = false;
          break;
        default:
          System.out.println("Invalid");
      }
    }
    sc.close();
  }

  static ArrayList<String> getBooksNameFromDB() throws Exception {
    ArrayList<String> booksInDB = new ArrayList<>();
    String query = "SELECT * FROM books";

    Connection connection = DriverManager.getConnection(url, userName, passWord);
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(query);

    while (resultSet.next()) {
      booksInDB.add(resultSet.getString(2));
    }

    connection.close();
    return booksInDB;
  }

  static ArrayList<Integer> getBooksIdFromDB() throws Exception {
    ArrayList<Integer> booksIdInDB = new ArrayList<>();
    String query = "SELECT * FROM books";

    Connection connection = DriverManager.getConnection(url, userName, passWord);
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(query);

    while (resultSet.next()) {
      booksIdInDB.add(resultSet.getInt(1));
    }

    connection.close();
    return booksIdInDB;
  }

  static void readBook() throws Exception {
    String query = "SELECT * FROM books";

    Connection connection = DriverManager.getConnection(url, userName, passWord);
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(query);
    boolean isEmpty = true;

    System.out.println("-------------------------------------------------------------------------");
    System.out.printf("| %-3s | %-25s | %-20s | %-12s |\n", "Id", "Book Name", "Book Author", "Added Date");
    System.out.println("-------------------------------------------------------------------------");

    while (resultSet.next()) {
      isEmpty = false;
      int id = resultSet.getInt(1);
      String bookName = resultSet.getString(2);
      String bookAuthor = resultSet.getString(3);
      String addedDate = resultSet.getDate(4).toString();
      System.out.printf("| %-3d | %-25s | %-20s | %-12s |\n", id, bookName, bookAuthor, addedDate);
    }
    if (isEmpty) {
      System.out.println("| Library is empty                                                      |");
    }
    System.out.println("-------------------------------------------------------------------------");
    connection.close();
  }

  static void insertBook() throws Exception {
    Scanner sc = new Scanner(System.in);
    String query = "INSERT INTO books VALUE(?,?,?,?)";

    Connection connection = DriverManager.getConnection(url, userName, passWord);
    PreparedStatement ps = connection.prepareStatement(query);

    System.out.println("Enter book id : ");
    int bookid = sc.nextInt();
    sc.nextLine();
    System.out.println("Enter book name : ");
    String bookname = sc.nextLine();
    System.out.println("Enter book author : ");
    String bookAuthor = sc.nextLine();
    System.out.println("Enter date (YYYY-MM-DD) : ");
    String dateString = sc.nextLine();

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date date = dateFormat.parse(dateString);
    java.sql.Date sqlDate = new java.sql.Date(date.getTime());

    ps.setInt(1, bookid);
    ps.setString(2, bookname);
    ps.setString(3, bookAuthor);
    ps.setDate(4, sqlDate);
    ps.executeUpdate();

    System.out.println("Book successfully added. Enter 1 to see books");
    connection.close();
  }

  static void updateBook() throws Exception {
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter book id to update ('0'-exit update): ");
    int idToUpdate = sc.nextInt();

    if (idToUpdate > 0) {
      System.out.println("A. Update book name\nB. Update book author\nC. Update added date\nD. Cancel");
      char option = sc.next().charAt(0);
      switch (option) {
        case 'A':
        case 'a':
          updateBookAt(idToUpdate, "bookname");
          break;
        case 'B':
        case 'b':
          updateBookAt(idToUpdate, "bookauthor");
          break;
        case 'C' | 'c':
          updateBookAddedDate(idToUpdate);
          break;
        case 'D' | 'd':
          System.out.println("Update canceled");
          return;
        default:
          System.out.println("Invalid selection");
      }

    }
  }

  static void updateBookAt(int id, String columnName) throws Exception {
    Scanner sc = new Scanner(System.in);

    System.out.println("Enter " + columnName + " to update : ");
    String update = sc.nextLine();

    String query = "UPDATE books SET " + columnName + "=? WHERE bookid=?";
    Connection connection = DriverManager.getConnection(url, userName, passWord);
    PreparedStatement ps = connection.prepareStatement(query);
    ps.setString(1, update);
    ps.setInt(2, id);
    ps.executeUpdate();

    System.out.println(columnName + " updated successfully");
    connection.close();
  }

  static void updateBookAddedDate(int id) throws Exception {
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter date to update (YYYY-MM-DD) : ");
    String dateString = sc.next();

    String query = "UPDATE books SET addeddate='" + dateString + "' WHERE bookid=" + id + ";";
    Connection connection = DriverManager.getConnection(url, userName, passWord);
    Statement statement = connection.createStatement();
    statement.executeUpdate(query);

    System.out.println("Date updated successfully");
    connection.close();
  }

  static void deleteBook() throws Exception {
    Scanner sc = new Scanner(System.in);
    System.out.println("A. Delete by book name\nB. Delete by book id\nC. Clear all books");
    char mode = sc.nextLine().charAt(0);

    if (mode == 'A' || mode == 'a') {
      System.out.println("Enter bookname to delete : ");
      String booknameToDelete = sc.nextLine();

      ArrayList<String> booksInDB = getBooksNameFromDB();

      if (booksInDB.contains(booknameToDelete)) {
        String query = "DELETE FROM books WHERE bookname='" + booknameToDelete + "';";
        Connection connection = DriverManager.getConnection(url, userName, passWord);
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);

        System.out.println("Book deleted");
        connection.close();
      } else {
        System.out.println("Book not found");
      }
    } else if (mode == 'B' || mode == 'b') {
      System.out.println("Enter book id to delete : ");
      int bookidToDelete = sc.nextInt();

      ArrayList<Integer> booksIdInDB = getBooksIdFromDB();

      if (booksIdInDB.contains(bookidToDelete)) {
        String query = "DELETE FROM books WHERE bookid=" + bookidToDelete + ";";
        Connection connection = DriverManager.getConnection(url, userName, passWord);
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);

        System.out.println("Book deleted");
        connection.close();
      } else {
        System.out.println("Entered book id not found");
      }
    } else if (mode == 'C' || mode == 'c') {
      String query = "DELETE FROM books;";
      Connection connection = DriverManager.getConnection(url, userName, passWord);
      Statement statement = connection.createStatement();
      statement.executeUpdate(query);

      System.out.println("Books cleared");
      connection.close();
    } else {
      System.out.println("Invalid selection");
    }
  }
}