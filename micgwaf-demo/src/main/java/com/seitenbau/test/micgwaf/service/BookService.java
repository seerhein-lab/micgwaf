package com.seitenbau.test.micgwaf.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Stores books.
 */
public class BookService
{
  /**
   * An instance of this service.
   */
  public static BookService instance = new BookService();
  
  private static List<Book> bookList;

  static
  {
    instance.resetBooks();
  }
  
  public void resetBooks()
  {
    bookList = new ArrayList<Book>();
    {
      Book book = new Book(0);
      book.setTitle("Effective Java");
      book.setAuthor("Joshua Bloch");
      book.setPublisher("Addison-Wesley");
      book.setIsbn("978-0321356680");
      bookList.add(book);
    }
    {
      Book book = new Book(1);
      book.setTitle("Design Patterns");
      book.setAuthor("Erich Gamma et al");
      book.setPublisher("Addison-Wesley");
      book.setIsbn("978-0201633610");
      bookList.add(book);
    }
    {
      Book book = new Book(2);
      book.setTitle("Clean Code");
      book.setAuthor("Robert C. Martin");
      book.setPublisher("Prentice Hall");
      book.setIsbn("978-0132350884");
      bookList.add(book);
    }    
  }
  
  /**
   * Saves a Book.
   * 
   * @param toSave the book to save.
   */
  public void save(Book toSave)
  {
    Iterator<Book> bookIt = bookList.iterator();
    int position = 0;
    while (bookIt.hasNext())
    {
      Book candidate = bookIt.next();
      if (candidate.getId() == toSave.getId())
      {
        bookIt.remove();
        break;
      }
      position++;
    }
    bookList.add(position, toSave);
  }
  
  /**
   * Returns the list of books.
   * 
   * @return the book list, not null.
   */
  public List<Book> getBookList()
  {
    // Copy list so the own list remains unchanged
    List<Book> result = new ArrayList<Book>();
    for (Book book : bookList)
    {
      result.add(new Book(book, book.getId()));
    }
    return result;
  }

  /**
   * Returns a new unused id.
   * 
   * @return an unused in, not null.
   */
  public Integer getNewListNumber()
  {
    Iterator<Book> adressenIt = bookList.iterator();
    int position = 0;
    while (adressenIt.hasNext())
    {
      Book candidate = adressenIt.next();
      if (candidate.getId() > position)
      {
        position = candidate.getId();
      }
    }
    return position + 1;
  }
}
